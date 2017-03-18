package ru.macrobit.geoservice.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AvoidEdge avoidEdge = (AvoidEdge) o;

        if (Double.compare(avoidEdge.speed, speed) != 0) return false;
        if (active != avoidEdge.active) return false;
        if (Double.compare(avoidEdge.oldSpeed, oldSpeed) != 0) return false;
        if (id != null ? !id.equals(avoidEdge.id) : avoidEdge.id != null) return false;
        if (name != null ? !name.equals(avoidEdge.name) : avoidEdge.name != null) return false;
        if (!Arrays.equals(loc, avoidEdge.loc)) return false;
        return edgeId != null ? edgeId.equals(avoidEdge.edgeId) : avoidEdge.edgeId == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(loc);
        temp = Double.doubleToLongBits(speed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (active ? 1 : 0);
        temp = Double.doubleToLongBits(oldSpeed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (edgeId != null ? edgeId.hashCode() : 0);
        return result;
    }

    public boolean isValid() {
        return this.getLoc() != null && this.getLoc().length == 2;
    }
}
