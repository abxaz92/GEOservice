package ru.macrobit.geoservice.pojo;

import java.util.Map;

/**
 * Created by [david] on 16.09.16.
 */
public class BatchRequest {
    private double[] src;
    private Map<String, double[]> dests;

    public Map<String, double[]> getDests() {
        return dests;
    }

    public void setDests(Map<String, double[]> dests) {
        this.dests = dests;
    }

    public double[] getSrc() {
        return src;
    }

    public void setSrc(double[] src) {
        this.src = src;
    }

    public boolean isValid() {
        return this.getDests() != null && this.getDests().values() != null;
    }
}
