package ru.macrobit.geoservice.search.pojo;

import ru.macrobit.geoservice.common.Entity;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class GeoAddress extends Entity {
    @Min(-90)
    @Max(90)
    private double x;
    @Min(-180)
    @Max(180)
    private double y;
    private String house;
    private String streetId;
    private boolean invalid;
    private String areaId;

    public GeoAddress() {

    }

    public GeoAddress(double[] coors) {
        this();
        this.x = coors[0];
        this.y = coors[1];
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getStreetId() {
        return streetId;
    }

    public void setStreetId(String streetId) {
        this.streetId = streetId;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
}
