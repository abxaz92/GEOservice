package ru.macrobit.geoservice.pojo;

import java.util.List;

/**
 * Created by [david] on 05.10.16.
 */
public class TaximeterRequest {
    private List<LogEntry> logs;
    private String tarif;

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
}
