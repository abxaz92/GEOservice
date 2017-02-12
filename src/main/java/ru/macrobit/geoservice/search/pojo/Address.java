package ru.macrobit.geoservice.search.pojo;

import ru.macrobit.geoservice.common.GraphUtils;

import java.io.IOException;

public class Address {
    private String name = "";
    private double x;
    private double y;
    private String comment = "";
    private String area;
    private String town = "";
    private String street = "";
    private String house = "";
    private String entrance = "";
    private String region = "";
    private String housing = "";
    private String mapId;

    public Address() {

    }

    public Address(double[] coors) {
        this();
        this.x = coors[0];
        this.y = coors[1];
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean equals(Object o) {
        Address address = (Address) o;
        return address.getArea().equals(this.area)
                && address.getStreet().equals(this.street)
                && (address.getX() == this.x && address.getY() == this.y
                || address.getX() == 0 && address.getY() == 0 || 0 == this.x
                && 0 == this.y);
    }

    public int hashCode() {
        return this.street.hashCode();
    }

    public String toString() {
        try {
            return GraphUtils.MAPPER.writeValueAsString(this);
        } catch (IOException e) {
            return null;
        }
    }

    public String getHousing() {
        return housing;
    }

    public void setHousing(String housing) {
        this.housing = housing;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }
}
