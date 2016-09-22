package ru.macrobit.geoservice.common;

import com.graphhopper.GHRequest;

import java.util.Locale;

/**
 * Created by [David] on 22.09.16.
 */
public class GraphUtils {

    public static GHRequest createRequest(double fromLat, double fromLon, double toLat, double toLon) {
        return new GHRequest(fromLat, fromLon, toLat, toLon).
                setWeighting("fastest").
                setVehicle("car").
                setLocale(Locale.US);
    }
}
