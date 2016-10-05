package ru.macrobit.geoservice.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by [david] on 27.09.16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvoidEdge {
    protected String id;
    protected String name;
    private double[] loc;
    private double speed;
    private boolean active;
    private double oldSpeed;
    private Integer edgeId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getLoc() {
        return loc;
    }

    public void setLoc(double[] loc) {
        this.loc = loc;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getOldSpeed() {
        return oldSpeed;
    }

    public void setOldSpeed(double oldSpeed) {
        this.oldSpeed = oldSpeed;
    }

    public Integer getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(Integer edgeId) {
        this.edgeId = edgeId;
    }
}
