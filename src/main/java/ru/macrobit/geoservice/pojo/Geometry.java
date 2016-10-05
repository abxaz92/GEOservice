package ru.macrobit.geoservice.pojo;

import ru.macrobit.geoservice.service.RoutingService;

import java.io.IOException;
import java.util.List;

/**
 * Created by [david] on 05.10.16.
 */
public class Geometry {
    private String type;
    private List<List<List<Double>>> coordinates;

    public Geometry() {
        this.type = "Polygon";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<List<Double>>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<List<Double>>> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        try {
            return RoutingService.MAPPER.writeValueAsString(this);
        } catch (IOException e) {
            return null;
        }
    }
}