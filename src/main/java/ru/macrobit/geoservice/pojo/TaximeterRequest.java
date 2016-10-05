package ru.macrobit.geoservice.pojo;

import java.util.List;

/**
 * Created by [david] on 05.10.16.
 */
public class TaximeterRequest {
    private List<LogEntry> logs;
    private String tarif;
    private boolean prepare;
    private String orderId;
    private double maxDist;
    private long maxTimeout;

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

    public boolean isPrepare() {
        return prepare;
    }

    public void setPrepare(boolean prepare) {
        this.prepare = prepare;
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
}
