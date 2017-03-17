package ru.macrobit.geoservice.search.common;

/**
 * @author Georgy Davityan.
 */
public class Weight {

    Integer region = 0;
    Integer city = 0;
    Integer street = 0;
    Integer house = 0;
    Integer building = 0;

    public Integer getRegion() {
        return region;
    }

    public void setRegion(Integer region) {
        this.region = region;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public Integer getStreet() {
        return street;
    }

    public void setStreet(Integer street) {
        this.street = street;
    }

    public Integer getHouse() {
        return house;
    }

    public void setHouse(Integer house) {
        this.house = house;
    }

    public Integer getBuilding() {
        return building;
    }

    public void setBuilding(Integer building) {
        this.building = building;
    }

    public int sum() {
        return region + city + street + house + building;
    }
}
