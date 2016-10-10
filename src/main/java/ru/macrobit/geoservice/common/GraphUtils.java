package ru.macrobit.geoservice.common;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.macrobit.geoservice.service.RoutingService;

import javax.ws.rs.WebApplicationException;
import java.util.Locale;

/**
 * Created by [David] on 22.09.16.
 */
public class GraphUtils {
    private static final Logger logger = LoggerFactory.getLogger(RoutingService.class);
    private static final double ROUND_CONST = 180;
    private static final double RAD = 6372795;

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

    public static double getDistance(double fromLat, double fromLon, double toLat, double toLon, GraphHopper hopper) {
        GHResponse rsp = hopper.route(GraphUtils.createRequest(fromLat, fromLon, toLat, toLon));
        if (rsp.hasErrors()) {
            logger.error("{}", rsp.getErrors());
            return -1;
        }
        PathWrapper path = rsp.getBest();
        return path.getDistance();
    }

    public static double getDummyDist(double[] from, double[] to) {
        return getDummyDist(from[0], from[1], to[0], to[1]);
    }

    public static double getDummyDist(double llat1, double llong1, double llat2, double llong2) {

        double lat1 = llat1 * Math.PI / ROUND_CONST;
        double lat2 = llat2 * Math.PI / ROUND_CONST;
        double long1 = llong1 * Math.PI / ROUND_CONST;
        double long2 = llong2 * Math.PI / ROUND_CONST;

        double cl1 = Math.cos(lat1);
        double cl2 = Math.cos(lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(lat2);

        double delta = long2 - long1;
        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
        double x = sl1 * sl2 + cl1 * cl2 * cdelta;
        double ad = Math.atan2(y, x);
        return ad * RAD;
    }
}
