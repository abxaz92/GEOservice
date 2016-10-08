package ru.macrobit.geoservice.pojo;

/**
 * Created by [david] on 05.10.16.
 */
public class TaximeterRequest {
    private String tarif;
    private String orderId;
    private int index;
    private boolean build;

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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isBuild() {
        return build;
    }

    public void setBuild(boolean build) {
        this.build = build;
    }
}
