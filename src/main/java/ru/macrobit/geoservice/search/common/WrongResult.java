package ru.macrobit.geoservice.search.common;

/**
 * @author Georgy Davityan.
 */
public class WrongResult extends SearchResult {

    private SearchInfo region;
    private SearchInfo city;
    private SearchInfo street;
    private SearchInfo house;

    public SearchInfo getRegion() {
        return region;
    }

    public void setRegion(SearchInfo region) {
        this.region = region;
    }

    public SearchInfo getCity() {
        return city;
    }

    public void setCity(SearchInfo city) {
        this.city = city;
    }

    public SearchInfo getStreet() {
        return street;
    }

    public void setStreet(SearchInfo street) {
        this.street = street;
    }

    public SearchInfo getHouse() {
        return house;
    }

    public void setHouse(SearchInfo house) {
        this.house = house;
    }
}
