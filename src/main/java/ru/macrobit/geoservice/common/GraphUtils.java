package ru.macrobit.geoservice.common;

import com.graphhopper.GHRequest;

import javax.ws.rs.WebApplicationException;
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

    public static double[] parseLocations(String loc) {
        String[] locs = loc.split(",");
        if (locs.length != 2)
            throw new WebApplicationException("Illegal location param", 406);
        return new double[]{Double.parseDouble(locs[0]), Double.parseDouble(locs[1])};
    }
}
