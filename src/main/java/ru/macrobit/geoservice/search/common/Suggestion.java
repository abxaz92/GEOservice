package ru.macrobit.geoservice.search.common;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * @author Georgy Davityan.
 */
public class Suggestion {

    private Weight weight;
    private Part region;
    private Part city;
    private Part street;
    private String house;
    private String building;

    public Suggestion() {
        this.weight = new Weight();
        this.region = new Part();
        this.city = new Part();
        this.street = new Part();
    }

    public Suggestion(Suggestion suggestion) {
        this.weight = suggestion.weight;
        this.region = suggestion.region;
        this.city = suggestion.city;
        this.street = suggestion.street;
        this.house = suggestion.house;
        this.building = suggestion.building;
    }

    public Weight getWeight() {
        return weight;
    }

    public void setWeight(Weight weight) {
        this.weight = weight;
    }

    public Part getRegion() {
        return region;
    }

    public void setRegion(Part region) {
        this.region = region;
    }

    public Part getCity() {
        return city;
    }

    public void setCity(Part city) {
        this.city = city;
    }

    public Part getStreet() {
        return street;
    }

    public void setStreet(Part street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public JsonObject toJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (region.name == null)
            builder.addNull("region");
        else
            builder.add("region", region.name);
        if (city.name == null)
            builder.addNull("city");
        else
            builder.add("city", city.name);
        if (street.name == null)
            builder.addNull("street");
        else
            builder.add("street", street.name);
        if (house == null)
            builder.addNull("house");
        else
            builder.add("house", house);
        return builder.build();
    }
}
