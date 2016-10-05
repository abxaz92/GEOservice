package ru.macrobit.geoservice.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.macrobit.drivertaxi.taximeter.TaximeterLocation;
import ru.macrobit.drivertaxi.taximeter.TaximeterLocationString;

/**
 * Created by [david] on 05.10.16.
 */
public class LogEntry implements TaximeterLocation {
    private double lat;
    private double lon;
    private long timestamp;
    private String src;
    private String error;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @JsonIgnore
    public String toLocationString() {
        return lat + "," + lon;
    }

    @Override
    @JsonIgnore
    public double getLatitude() {
        return this.lat;
    }

    @Override
    @JsonIgnore
    public double getLongitude() {
        return this.lon;
    }

    @Override
    @JsonIgnore
    public long getTime() {
        return this.timestamp;
    }

    @Override
    @JsonIgnore
    public double distanceTo(TaximeterLocation taximeterLocation) {
        return TaximeterLocationString.HaversineAlgorithm.HaversineInM(getLatitude(), getLongitude(), taximeterLocation.getLatitude(), taximeterLocation.getLongitude());
    }

    @Override
    @JsonIgnore
    public boolean isNetworkProvider() {
        return "network".equals(this.src);
    }

    @Override
    @JsonIgnore
    public String getProvider() {
        return this.src;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogEntry that = (LogEntry) o;

        if (Double.compare(that.lat, lat) != 0) return false;
        if (Double.compare(that.lon, lon) != 0) return false;
        return timestamp == that.timestamp;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}
