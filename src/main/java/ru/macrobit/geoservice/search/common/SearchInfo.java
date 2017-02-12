package ru.macrobit.geoservice.search.common;

/**
 * @author Georgy Davityan.
 */
public class SearchInfo {

    private String text;
    private Boolean exists = false;

    public SearchInfo(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }
}
