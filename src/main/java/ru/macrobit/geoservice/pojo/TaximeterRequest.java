package ru.macrobit.geoservice.pojo;

import java.util.List;

/**
 * Created by [david] on 05.10.16.
 */
public class TaximeterRequest {
    private List<LogEntry> logs;
    private String tarif;
    private String orderId;
    private double maxDist = 300;
    private long maxTimeout = 20000;
    private int index;

    public List<LogEntry> getLogs() {
        return logs;
    }

    public void setLogs(List<LogEntry> logs) {
        this.logs = logs;
    }

    public String getTarif() {
        return tarif;
    }

    public void setTarif(String tarif) {
        this.tarif = tarif;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getMaxDist() {
        return maxDist;
    }

    public void setMaxDist(double maxDist) {
        this.maxDist = maxDist;
    }

    public long getMaxTimeout() {
        return maxTimeout;
    }

    public void setMaxTimeout(long maxTimeout) {
        this.maxTimeout = maxTimeout;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
