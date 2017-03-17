package ru.macrobit.geoservice.search.pojo;

/**
 * @author Georgy Davityan.
 */
public class Weights {

    private Integer houseStartsWithDigit  = 30;
    private Integer houseStartsWithNoneDigit  = -250;
    private Integer buildingWithoutHouse  = -500;
    private Integer buildingWithHouseStartsWithDigit  = 15;;
    private Integer buildingWithHouseStartsWithNoneDigit  = -250;
    private Integer regionExists  = 60;
    private Integer regionDoesNotExist  = -250;
    private Integer cityDoesNotExist  = -500;
    private Integer cityExists  = 25;
    private Integer cityExistsInRegion  = 50;
    private Integer streetDoesNotExist  = -500;
    private Integer streetExists  = 45;
    private Integer streetExistsInCity  = 90;
    private Integer houseDoesNotExist  = 10;
    private Integer houseExistsInStreetWithCity  = 200;
    private Integer houseExistsInStreet  = 80;

    public Integer getHouseStartsWithDigit() {
        return houseStartsWithDigit;
    }

    public void setHouseStartsWithDigit(Integer houseStartsWithDigit) {
        this.houseStartsWithDigit = houseStartsWithDigit;
    }

    public Integer getHouseStartsWithNoneDigit() {
        return houseStartsWithNoneDigit;
    }

    public void setHouseStartsWithNoneDigit(Integer houseStartsWithNoneDigit) {
        this.houseStartsWithNoneDigit = houseStartsWithNoneDigit;
    }

    public Integer getBuildingWithoutHouse() {
        return buildingWithoutHouse;
    }

    public void setBuildingWithoutHouse(Integer buildingWithoutHouse) {
        this.buildingWithoutHouse = buildingWithoutHouse;
    }

    public Integer getBuildingWithHouseStartsWithDigit() {
        return buildingWithHouseStartsWithDigit;
    }

    public void setBuildingWithHouseStartsWithDigit(Integer buildingWithHouseStartsWithDigit) {
        this.buildingWithHouseStartsWithDigit = buildingWithHouseStartsWithDigit;
    }

    public Integer getBuildingWithHouseStartsWithNoneDigit() {
        return buildingWithHouseStartsWithNoneDigit;
    }

    public void setBuildingWithHouseStartsWithNoneDigit(Integer buildingWithHouseStartsWithNoneDigit) {
        this.buildingWithHouseStartsWithNoneDigit = buildingWithHouseStartsWithNoneDigit;
    }

    public Integer getRegionExists() {
        return regionExists;
    }

    public void setRegionExists(Integer regionExists) {
        this.regionExists = regionExists;
    }

    public Integer getRegionDoesNotExist() {
        return regionDoesNotExist;
    }

    public void setRegionDoesNotExist(Integer regionDoesNotExist) {
        this.regionDoesNotExist = regionDoesNotExist;
    }

    public Integer getCityDoesNotExist() {
        return cityDoesNotExist;
    }

    public void setCityDoesNotExist(Integer cityDoesNotExist) {
        this.cityDoesNotExist = cityDoesNotExist;
    }

    public Integer getCityExists() {
        return cityExists;
    }

    public void setCityExists(Integer cityExists) {
        this.cityExists = cityExists;
    }

    public Integer getCityExistsInRegion() {
        return cityExistsInRegion;
    }

    public void setCityExistsInRegion(Integer cityExistsInRegion) {
        this.cityExistsInRegion = cityExistsInRegion;
    }

    public Integer getStreetDoesNotExist() {
        return streetDoesNotExist;
    }

    public void setStreetDoesNotExist(Integer streetDoesNotExist) {
        this.streetDoesNotExist = streetDoesNotExist;
    }

    public Integer getStreetExists() {
        return streetExists;
    }

    public void setStreetExists(Integer streetExists) {
        this.streetExists = streetExists;
    }

    public Integer getStreetExistsInCity() {
        return streetExistsInCity;
    }

    public void setStreetExistsInCity(Integer streetExistsInCity) {
        this.streetExistsInCity = streetExistsInCity;
    }

    public Integer getHouseDoesNotExist() {
        return houseDoesNotExist;
    }

    public void setHouseDoesNotExist(Integer houseDoesNotExist) {
        this.houseDoesNotExist = houseDoesNotExist;
    }

    public Integer getHouseExistsInStreetWithCity() {
        return houseExistsInStreetWithCity;
    }

    public void setHouseExistsInStreetWithCity(Integer houseExistsInStreetWithCity) {
        this.houseExistsInStreetWithCity = houseExistsInStreetWithCity;
    }

    public Integer getHouseExistsInStreet() {
        return houseExistsInStreet;
    }

    public void setHouseExistsInStreet(Integer houseExistsInStreet) {
        this.houseExistsInStreet = houseExistsInStreet;
    }
}
