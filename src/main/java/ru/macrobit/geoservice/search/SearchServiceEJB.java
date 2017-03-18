package ru.macrobit.geoservice.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.macrobit.geoservice.common.TaxiProperties;
import ru.macrobit.geoservice.dao.*;
import ru.macrobit.geoservice.search.common.Address;
import ru.macrobit.geoservice.search.common.*;
import ru.macrobit.geoservice.search.pojo.*;
import ru.macrobit.geoservice.search.pojo.SearchResult;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Georgy Davityan.
 */
@Stateless
public class SearchServiceEJB implements SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchServiceEJB.class);

    private static final List<GeoObject> EMPTY_GEOOBJECTS = new ArrayList();
    @EJB
    private TaxiProperties PROPERTIES;

    @Inject
    private GeoAddressDAO addressDAO;
    @Inject
    private GeoObjectDAO geoObjectDAO;
    @Inject
    private StreetDAO streetDAO;
    @Inject
    private CityDAO cityDAO;
    @Inject
    private RegionDAO regionDAO;

    @Override
    public SearchResult search(String pattern, String city, String disp, String filterCity) throws Exception {
        SearchResult searchResult = new SearchResult();

        if (pattern.isEmpty())
            return searchResult;
        if (disp != null || !PROPERTIES.isExcludeGeoObjectsFromSearch()) {
            searchResult.setObjects(geoObjectDAO
                    .getCache()
                    .values()
                    .stream()
                    .parallel()
                    .filter(geoObject -> {
                        if (filterCity != null) {
                            if (geoObject.getAddress() != null
                                    && geoObject.getAddress().getTown() != null
                                    && !geoObject.getAddress().getTown().toLowerCase().equals(filterCity)) {
                                return false;
                            }
                        }
                        return geoObject.getName() != null &&
                                geoObject.getName().toLowerCase().contains(pattern.toLowerCase());
                    })
                    .sorted((o1, o2) -> {
                        // Если оба объекта из Владика или оба не из Владика,
                        // то сортировать по релевантности
                        String pat = pattern.toLowerCase();
                        int firstObjPatternIndex = o1.getName().toLowerCase().indexOf(pat);
                        int secondObjPatternIndex = o2.getName().toLowerCase().indexOf(pat);

                        if (firstObjPatternIndex < secondObjPatternIndex) {
                            return -1;
                        }

                        if (firstObjPatternIndex > secondObjPatternIndex) {
                            return 1;
                        }

                        return 0;
                    })
                    .limit(10)
                    .collect(Collectors.toList()));
            searchResult
                    .getObjects()
                    .stream()
                    .parallel()
                    .filter(geoObject -> geoObject.getAddress().getStreetId() != null &&
                            geoObject.getAddress().getStreet() == null)
                    .forEach(geoObject -> {
                        geoObject.getAddress().setStreet(
                                streetDAO.getCache()
                                        .get(geoObject.getAddress().getStreetId())
                                        .getName());
                    });
        } else {
            searchResult.setObjects(EMPTY_GEOOBJECTS);
        }

        List<Suggestion> suggestions = makeSuggestions(pattern)
                .stream()
                .parallel()
                .filter(address -> address.getWeight().sum() > 0)
                .collect(Collectors.toList());
        Suggestion suggestion = suggestions.isEmpty() ? null : suggestions.iterator().next();
        searchResult.setSuggestion(suggestion == null ? null : suggestion.toJson().toString());
        searchResult.setAddresses(
                sort(suggestion,
                        suggestions
                                .stream()
                                .parallel()
                                .map(this::makeAddress)
                                .flatMap(Collection::stream)
                                .filter(address -> {
                                    if (filterCity != null) {
                                        if (address.getCity() != null
                                                && address.getCity().getName() != null
                                                && !address.getCity().getName().toLowerCase().equals(filterCity)) {
                                            return false;
                                        }
                                    }
                                    return true;
                                })
                                .collect(Collectors.toList()),
                        city,
                        pattern));

        if (searchResult.getAddresses().isEmpty()
                && searchResult.getObjects().isEmpty() && suggestion != null && suggestion.getHouse() != null) {
            Suggestion dummy = new Suggestion(suggestion);
            dummy.setHouse(null);
            searchResult.setAddresses(sort(dummy, makeAddress(dummy), city, pattern));
        }

//        if (results.isEmpty() && !suggestions.isEmpty())
//            results.add(makeWrongResult(suggestions.iterator().next()));

        return searchResult;
    }

    private List<Address> sort(Suggestion suggestion, List<Address> addresses, String city, String pattern) {
        if (suggestion != null) return addresses
                .stream()
                .parallel()
                .sorted((a, b) -> {
                    int comparator = 0;

                    if (b.getCity() == null)
                        return -1;
                    if (a.getCity() == null)
                        return -1;
                    if (city == null) {
                        comparator = Integer.compare(b.getCity().getWeight(), a.getCity().getWeight());
                    } else {
                        if (a.getCity() != null && b.getCity() != null) {
                            if (a.getCity().getName().toLowerCase().indexOf(city) >= 0) {
                                comparator = -1;
                            } else if (b.getCity().getName().toLowerCase().indexOf(city) >= 0) {
                                comparator = 1;
                            } else {
                                comparator = a.getCity().getName().compareToIgnoreCase(b.getCity().getName());
                            }
                        }
                    }

                    if (a.getAddress() != null && b.getAddress() != null) {
                        if (suggestion.getHouse() != null) {
                            if (a.getAddress().getHouse().equals(suggestion.getHouse())) {
                                comparator = -1;
                            } else if (b.getAddress().getHouse().equals(suggestion.getHouse())) {
                                comparator = 1;
                            } else {
                                comparator = a.getAddress().getHouse().compareToIgnoreCase(b.getAddress().getHouse());
                            }
                        }
                    }
                    if (comparator != 0) {
                        return comparator;
                    }
                    if (a.getStreet() != null && b.getStreet() != null) {
                        String aStreetLower = a.getStreet().getName().toLowerCase();
                        String bStreetLower = b.getStreet().getName().toLowerCase();
                        if (aStreetLower.startsWith(pattern)) {
                            return -1;
                        } else if (bStreetLower.startsWith(pattern)) {
                            return 1;
                        }
                    }
                    return comparator;
                })
                .limit(10)
                .collect(Collectors.toList());
        return addresses;
    }

    private List<Address> makeAddress(Suggestion suggestion) {
        List<Address> addressList = new ArrayList<>();
        if (suggestion.getStreet().getIds() != null) {
            List<Street> streets = suggestion.getStreet().getIds()
                    .stream()
                    .map(streetDAO.getCache()::get)
                    .collect(Collectors.toList());
            for (Street street : streets) {
                Address address = new Address();
                address.setStreet(street);
                address.setCity(cityDAO.getCache().get(street.getCityId()));
                address.setRegion(regionDAO.getCache().get(address.getCity().getRegionId()));

                if (suggestion.getHouse() != null) {
                    getAddress(street.getId(), suggestion.getHouse())
                            .stream()
                            .forEach(o -> {
                                Address addr = new Address();
                                addr.setRegion(address.getRegion());
                                addr.setCity(address.getCity());
                                addr.setStreet(address.getStreet());
                                addr.setAddress(o);
                                addressList.add(addr);
                            });
                } else {
                    addressList.add(address);
                }
            }
        } else if (suggestion.getCity().getIds() != null) {
            List<City> cities = suggestion.getCity().getIds()
                    .stream()
                    .map(cityDAO.getCache()::get)
                    .collect(Collectors.toList());
            for (City city : cities) {
                Address address = new Address();
                address.setCity(city);
                address.setRegion(regionDAO.getCache().get(city.getRegionId()));

                addressList.add(address);
            }
        } else if (suggestion.getRegion().getIds() != null) {
            List<Region> regions = suggestion.getRegion().getIds()
                    .stream()
                    .map(regionDAO.getCache()::get)
                    .collect(Collectors.toList());
            for (Region region : regions) {
                Address address = new Address();
                address.setRegion(region);
                addressList.add(address);
            }
        }

        return addressList;
    }

    private List<Suggestion> makeSuggestions(String pattern) {
        List<String> words = ArrayUtils.split(pattern.toLowerCase(), Arrays.asList(" ", ",", ";"));
        List<List<String>> combinations = initWords(words, 0);
        combinations.add(words);
        Set<List<String>> set = new HashSet<>(combinations);
        combinations.clear();
        combinations.addAll(set);
        List<List<String>> addresses = new ArrayList<>();
        combinations.forEach(w -> {
            if (w.size() < 5)
                addresses.addAll(makeAddressVariants(Arrays.asList(null, null, null, null, null), 4, w.size() - 1, w));
        });
        return make(addresses);
    }

    private WrongResult makeWrongResult(Suggestion suggestion) {
        WrongResult result = new WrongResult();
        result.setRegion(new SearchInfo(suggestion.getRegion().getName()));
        result.setCity(new SearchInfo(suggestion.getCity().getName()));
        result.setStreet(new SearchInfo(suggestion.getStreet().getName()));
        result.setHouse(new SearchInfo(suggestion.getHouse()));

        List<Street> streets = null;
        List<City> cities = null;
        if (result.getStreet().getText() != null && result.getHouse().getText() != null) {
            streets = streetDAO.getCache().values()
                    .stream()
                    .parallel()
                    .filter(o -> (" " + o.getName().toLowerCase()).contains(" " + result.getStreet().getText()))
                    .collect(Collectors.toList());
            if (!streets.isEmpty()) {
                result.getStreet().setExists(true);
            }
            List<String> streetIds = streets
                    .stream()
                    .map(Street::getId)
                    .collect(Collectors.toList());
            List<String> addressStreetIds = addressDAO.getCache().values()
                    .stream()
                    .parallel()
                    .filter(o -> o.getHouse().contains(result.getHouse().getText())
                            && streetIds.contains(o.getStreetId()))
                    .map(o -> o.getStreetId())
                    .collect(Collectors.toList());
            if (!addressStreetIds.isEmpty()) {
                result.getHouse().setExists(true);
                streets = streets
                        .stream()
                        .parallel()
                        .filter(o -> addressStreetIds.contains(o.getId()))
                        .collect(Collectors.toList());
            }
        }

        if (result.getCity().getText() != null) {
            if (streets != null) {
                cities = streets
                        .stream()
                        .parallel()
                        .map(o -> cityDAO.getCache().get(o.getCityId()))
                        .filter(o -> (" " + o.getName().toLowerCase()).contains(" " + result.getCity().getText()))
                        .collect(Collectors.toList());
            } else {
                cities = cityDAO.getCache().values()
                        .stream()
                        .parallel()
                        .filter(o -> (" " + o.getName().toLowerCase()).contains(" " + result.getCity().getText()))
                        .collect(Collectors.toList());
            }

            if (!cities.isEmpty()) {
                result.getCity().setExists(true);
            }
        }

        if (result.getRegion().getText() != null) {
            List<Region> regions;
            if (cities != null) {
                regions = cities
                        .stream()
                        .parallel()
                        .map(o -> regionDAO.getCache().get(o.getRegionId()))
                        .filter(o -> (" " + o.getName().toLowerCase()).contains(" " + result.getRegion().getText()))
                        .collect(Collectors.toList());
            } else {
                regions = regionDAO.getCache().values()
                        .stream()
                        .parallel()
                        .filter(o -> (" " + o.getName().toLowerCase()).contains(" " + result.getRegion().getText()))
                        .collect(Collectors.toList());
            }

            if (!regions.isEmpty()) {
                result.getRegion().setExists(true);
            }
        }


        return result;
    }

    private List<List<String>> initWords(List<String> words, int pos) {
        List<List<String>> result = new ArrayList<>();
        for (int i = pos; i < words.size(); i++) {
            if (pos != i) {
                List<String> merged = ArrayUtils.merge(words, pos, i);
                result.add(merged);
                if (merged.size() > 2) {
                    result.addAll(initWords(merged, 0));
                }
            }
        }
        pos++;
        if (pos < words.size()) {
            result.addAll(initWords(words, pos));
        }
        return result;
    }

    private List<List<String>> makeAddressVariants(List<String> address, int index, int wordIndex, List<String> words) {
        List<List<String>> addresses = new ArrayList<>();
        while (index >= wordIndex) {
            List<String> o = new ArrayList<>(address);
            if (wordIndex == 0) {
                addresses.add(o);
            }
            o.set(index, words.get(wordIndex));
            index = index - 1;
            if (wordIndex > 0) {
                addresses.addAll(makeAddressVariants(o, index, wordIndex - 1, words));
            }
        }
        return addresses;
    }

    private void preProcess(Suggestion suggestion) {
        if (suggestion.getHouse() != null) {
            if (Character.isDigit(suggestion.getHouse().charAt(0))) {
                suggestion.getWeight().setHouse(suggestion.getWeight().getHouse() + PROPERTIES.getSearchWeights()
                        .getHouseStartsWithDigit());
            } else {
                suggestion.getWeight().setHouse(suggestion.getWeight().getHouse() + PROPERTIES.getSearchWeights()
                        .getHouseStartsWithNoneDigit());
            }
        }
        if (suggestion.getBuilding() != null) {
            if (suggestion.getHouse() == null) {
                suggestion.getWeight().setBuilding(suggestion.getWeight().getBuilding() + PROPERTIES.getSearchWeights()
                        .getBuildingWithoutHouse());
            } else if (Character.isDigit(suggestion.getBuilding().charAt(0))) {
                suggestion.getWeight().setBuilding(suggestion.getWeight().getBuilding() +
                        PROPERTIES.getSearchWeights().getBuildingWithHouseStartsWithDigit());
            } else {
                suggestion.getWeight().setBuilding(suggestion.getWeight().getBuilding() +
                        PROPERTIES.getSearchWeights().getBuildingWithHouseStartsWithNoneDigit());
            }
        }
    }

    private List<Suggestion> make(List<List<String>> addresses) {
        return addresses
                .stream()
                .parallel()
                .map(addr -> {
                    Suggestion suggestion = new Suggestion();
                    suggestion.getRegion().setName(addr.get(0));
                    suggestion.getCity().setName(addr.get(1));
                    suggestion.getStreet().setName(addr.get(2));
                    suggestion.setHouse(addr.get(3));
                    suggestion.setBuilding(addr.get(4));

                    preProcess(suggestion);

                    if (suggestion.getRegion().getName() != null) {
                        List<String> regionIds = regionDAO.getCache().values()
                                .stream()
                                .parallel()
                                .filter(o -> (" " + o.getName().toLowerCase()).contains(" " + suggestion.getRegion().getName()))
                                .map(Region::getId)
                                .collect(Collectors.toList());
                        if (regionIds.isEmpty()) {
                            suggestion.getWeight().setRegion(suggestion.getWeight().getRegion() + PROPERTIES.getSearchWeights()
                                    .getRegionDoesNotExist());
                        } else {
                            suggestion.getRegion().setIds(regionIds);
                            suggestion.getWeight().setRegion(suggestion.getWeight().getRegion() + PROPERTIES.getSearchWeights()
                                    .getRegionExists());
                        }
                    }

                    if (suggestion.getCity().getName() != null) {
                        List<String> cityIds = cityDAO.getCache().values()
                                .stream()
                                .parallel()
                                .filter(o -> {
                                    if (suggestion.getRegion().getIds() == null || suggestion.getRegion().getIds().isEmpty()) {
                                        return (" " + o.getName().toLowerCase()).contains(" " + suggestion.getCity().getName());
                                    } else {
                                        return suggestion.getRegion().getIds() != null &&
                                                suggestion.getRegion().getIds().contains(o.getRegionId()) &&
                                                (" " + o.getName().toLowerCase()).contains(" " + suggestion.getCity().getName());
                                    }
                                })
                                .map(City::getId)
                                .collect(Collectors.toList());
                        if (cityIds.isEmpty()) {
                            suggestion.getWeight().setCity(suggestion.getWeight().getCity() + PROPERTIES.getSearchWeights()
                                    .getCityDoesNotExist());
                        } else {
                            suggestion.getCity().setIds(cityIds);
                            if (suggestion.getWeight().getRegion() < 0) {
                                suggestion.getWeight().setCity(suggestion.getWeight().getCity() + PROPERTIES.getSearchWeights()
                                        .getCityExists());
                            } else {
                                suggestion.getWeight().setCity(suggestion.getWeight().getCity() + PROPERTIES.getSearchWeights()
                                        .getCityExistsInRegion());
                            }
                        }
                    }

                    if (suggestion.getStreet().getName() != null) {
                        List<String> streetIds = streetDAO.getCache().values()
                                .stream()
                                .parallel()
                                .filter(o -> {
                                    if (suggestion.getCity().getIds() == null || suggestion.getCity().getIds().isEmpty()) {
                                        return (" " + o.getName().toLowerCase()).contains(" " + suggestion.getStreet().getName());
                                    } else {
                                        return suggestion.getCity().getIds() != null &&
                                                suggestion.getCity().getIds().contains(o.getCityId()) &&
                                                (" " + o.getName().toLowerCase()).contains(" " + suggestion.getStreet().getName());
                                    }
                                })
                                .map(Street::getId)
                                .collect(Collectors.toList());
                        if (streetIds.isEmpty()) {
                            suggestion.getWeight().setStreet(suggestion.getWeight().getStreet() + PROPERTIES.getSearchWeights()
                                    .getStreetDoesNotExist());
                        } else {
                            suggestion.getStreet().setIds(streetIds);
                            if (suggestion.getWeight().getCity() < PROPERTIES.getSearchWeights().getCityExistsInRegion()) {
                                suggestion.getWeight().setStreet(suggestion.getWeight().getStreet() + PROPERTIES
                                        .getSearchWeights().getStreetExists());
                            } else {
                                suggestion.getWeight().setStreet(suggestion.getWeight().getStreet() + PROPERTIES
                                        .getSearchWeights().getStreetExistsInCity());
                            }
                        }
                    }

                    if (suggestion.getHouse() != null &&
                            suggestion.getStreet().getIds() != null && !suggestion.getStreet().getIds().isEmpty()) {
                        GeoAddress house = addressDAO.getCache().values()
                                .stream()
                                .parallel()
                                .filter(o -> o.getHouse() != null &&
                                        o.getStreetId() != null &&
                                        o.getHouse().toLowerCase().contains(suggestion.getHouse()) &&
                                        suggestion.getStreet().getIds().contains(o.getStreetId()))
                                .findFirst()
                                .orElse(null);
                        if (house == null) {
                            suggestion.getWeight().setHouse(suggestion.getWeight().getHouse() + PROPERTIES.getSearchWeights()
                                    .getHouseDoesNotExist());
                        } else {
                            if (suggestion.getWeight().getStreet() < PROPERTIES.getSearchWeights().getHouseExistsInStreet() -
                                    PROPERTIES.getSearchWeights().getHouseStartsWithDigit()) {
                                suggestion.getWeight().setHouse(suggestion.getWeight().getHouse() + PROPERTIES.getSearchWeights()
                                        .getHouseExistsInStreet());
                            } else {
                                suggestion.getWeight().setHouse(suggestion.getWeight().getHouse() +
                                        PROPERTIES.getSearchWeights().getHouseExistsInStreetWithCity());
                            }
                        }
                    }
                    return suggestion;

                }).sorted((o1, o2) -> o2.getWeight().sum() - o1.getWeight().sum()).collect(Collectors.toList());
    }

    private List<GeoAddress> getAddress(String streetId, String house) {
        if (streetId == null)
            throw new NullPointerException("Parameter 'streetId' must be specified");

        return addressDAO.getCache().values()
                .stream()
                .parallel()
                .filter(o -> {
                    if (o.getStreetId() != null && o.getStreetId().equals(streetId)) {
                        if (house != null) {
                            return o.getHouse() != null && o.getHouse().toLowerCase().contains(house);
                        }
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
    }
}

