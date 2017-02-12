package ru.macrobit.geoservice.search.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.macrobit.geoservice.common.Entity;

public class GeoObject extends Entity {
    private LinkedAddres address;
    private String geoGroupId;
    @JsonIgnore
    private String zloebuchijMaximaId;
    private double distance;
    private double factor;
    private String cityId;
    private String metaAreaId;
    private double[] location;

    public LinkedAddres getAddress() {
        return address;
    }

    public void setAddress(LinkedAddres address) {
        this.address = address;
    }

    public String getGeoGroupId() {
        return geoGroupId;
    }

    public void setGeoGroupId(String geoGroupId) {
        this.geoGroupId = geoGroupId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getZloebuchijMaximaId() {
        return zloebuchijMaximaId;
    }

    public void setZloebuchijMaximaId(String zloebuchijMaximaId) {
        this.zloebuchijMaximaId = zloebuchijMaximaId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getMetaAreaId() {
        return metaAreaId;
    }

    public void setMetaAreaId(String metaAreaId) {
        this.metaAreaId = metaAreaId;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }
}
