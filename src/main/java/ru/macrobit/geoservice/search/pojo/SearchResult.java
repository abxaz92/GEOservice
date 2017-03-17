package ru.macrobit.geoservice.search.pojo;

import com.fasterxml.jackson.annotation.JsonRawValue;
import ru.macrobit.geoservice.search.common.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Georgy Davityan.
 */
public class SearchResult {

    @JsonRawValue
    private String suggestion;
    private List<GeoObject> objects = new ArrayList<>();
    private List<ru.macrobit.geoservice.search.common.Address> addresses = new ArrayList<>();

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public List<GeoObject> getObjects() {
        return objects;
    }

    public void setObjects(List<GeoObject> objects) {
        this.objects = objects;
    }

    public List<ru.macrobit.geoservice.search.common.Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<ru.macrobit.geoservice.search.common.Address> addresses) {
        this.addresses = addresses;
    }
}
