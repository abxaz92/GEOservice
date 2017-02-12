package ru.macrobit.geoservice.search.pojo;

import ru.macrobit.geoservice.common.Entity;

public class City extends Entity {
    private int weight;
    private String regionId;

    public City() {
    }

    public City(String name, String regionId) {
        this();
        this.setName(name);
        this.regionId = regionId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
