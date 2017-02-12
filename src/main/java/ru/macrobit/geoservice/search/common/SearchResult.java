package ru.macrobit.geoservice.search.common;

import com.fasterxml.jackson.annotation.JsonRawValue;
import ru.macrobit.geoservice.search.pojo.GeoObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Georgy Davityan.
 */
public class SearchResult {

    @JsonRawValue
    private String suggestion;
    private List<GeoObject> objects = new ArrayList<>();
    private List<Address> addresses = new ArrayList<>();

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

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
