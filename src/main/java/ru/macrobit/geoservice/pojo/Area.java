package ru.macrobit.geoservice.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by [david] on 05.10.16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Area {
    private String id;
    private String name;
    private Geometry loc;
    private String meta;
    private double markup;
    private String description;
    /*Не учитывать ЦЗ при расчете стоимости*/
    private boolean excludeFromCosts;

    /*Коэффициент*/
    private double factor = 1;

    private Map<String, Double> firmFactors = new HashMap<>();
    private Map<String, Double> factors = new HashMap<>();

    public Geometry getLoc() {
        return loc;
    }

    public void setLoc(Geometry loc) {
        this.loc = loc;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public double getMarkup() {
        return markup;
    }

    public void setMarkup(double markup) {
        this.markup = markup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isExcludeFromCosts() {
        return excludeFromCosts;
    }

    public void setExcludeFromCosts(boolean excludeFromCosts) {
        this.excludeFromCosts = excludeFromCosts;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public Map<String, Double> getFirmFactors() {
        return firmFactors;
    }

    public void setFirmFactors(Map<String, Double> firmFactors) {
        this.firmFactors = firmFactors;
    }

    public Map<String, Double> getFactors() {
        return factors;
    }

    public void setFactors(Map<String, Double> factors) {
        this.factors = factors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
