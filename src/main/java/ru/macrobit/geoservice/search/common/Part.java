package ru.macrobit.geoservice.search.common;

import java.util.List;

/**
 * @author Georgy Davityan.
 */
public class Part {

    String name;
    List<String> ids;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
