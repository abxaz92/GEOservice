package ru.macrobit.geoservice.search.pojo;

import ru.macrobit.geoservice.common.Entity;

public class Street extends Entity {
    private String cityId;
    private String areaId;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
}
