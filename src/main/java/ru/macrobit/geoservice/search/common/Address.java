package ru.macrobit.geoservice.search.common;

import ru.macrobit.geoservice.search.pojo.City;
import ru.macrobit.geoservice.search.pojo.GeoAddress;
import ru.macrobit.geoservice.search.pojo.Region;
import ru.macrobit.geoservice.search.pojo.Street;

/**
 * @author Georgy Davityan.
 */
public class Address {

    private Region region;
    private City city;
    private Street street;
    private GeoAddress address;

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    public GeoAddress getAddress() {
        return address;
    }

    public void setAddress(GeoAddress address) {
        this.address = address;
    }
}
