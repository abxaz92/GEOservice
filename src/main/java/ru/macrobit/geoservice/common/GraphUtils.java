package ru.macrobit.geoservice.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.macrobit.geoservice.service.RoutingService;

import javax.ws.rs.WebApplicationException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by [David] on 22.09.16.
 */
public class GraphUtils {
    private static final Logger logger = LoggerFactory.getLogger(RoutingService.class);
    private static final double ROUND_CONST = 180;
    private static final double RAD = 6372795;
    public static ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Create Graph Hopper Request for 2 location points
     *
     * @param fromLat from latitude
     * @param fromLon form longtitude
     * @param toLat   to latitude
     * @param toLon   to longtitude
     * @return
     */
    public static GHRequest createRequest(double fromLat, double fromLon, double toLat, double toLon) {
        return new GHRequest(fromLat, fromLon, toLat, toLon).
                setWeighting("fastest").
                setVehicle("car").
                setLocale(Locale.US);
    }

    /**
     * Parse location array from string like"42.512,41.356"
     *
     * @param loc
     * @return array of double with size 2
     */
    public static double[] parseLocations(String loc) {
        String[] locs = loc.split(",");
        if (locs.length != 2)
            throw new WebApplicationException("Illegal location param", 406);
        return new double[]{Double.parseDouble(locs[0]), Double.parseDouble(locs[1])};
    }

    /**
     * Calculate distance between 2 locations by route
     *
     * @param fromLat from latitude
     * @param fromLon form longtitude
     * @param toLat   to latitude
     * @param toLon   to longtitude
     * @param hopper  Graph hopper location index object
     * @return
     */
    public static double getDistance(double fromLat, double fromLon, double toLat, double toLon, GraphHopper hopper) {
        GHResponse rsp = hopper.route(GraphUtils.createRequest(fromLat, fromLon, toLat, toLon));
        if (rsp.hasErrors()) {
            logger.error("{}", rsp.getErrors());
            return -1;
        }
        PathWrapper path = rsp.getBest();
        return path.getDistance();
    }

    /**
     * Calculate distance between 2 locations by straight line
     *
     * @param from
     * @param to
     * @return distance
     */
    public static double getDummyDist(double[] from, double[] to) {
        return getDummyDist(from[0], from[1], to[0], to[1]);
    }

    /**
     * Generate Map by HTTP get request
     *
     * @param query
     * @return Map with request params
     */
    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : params) {
            String[] splitedParam = param.split("=");
            String name = splitedParam[0];
            String value = splitedParam.length == 2 ? splitedParam[1] : "";
            map.put(name, value);
        }
        return map;
    }

    /**
     * @param fromLat  from latitude
     * @param fromLong form longtitude
     * @param toLat    to latitude
     * @param toLong   to longtitude
     * @return
     */
    public static double getDummyDist(double fromLat, double fromLong, double toLat, double toLong) {

        double lat1 = fromLat * Math.PI / ROUND_CONST;
        double lat2 = toLat * Math.PI / ROUND_CONST;
        double long1 = fromLong * Math.PI / ROUND_CONST;
        double long2 = toLong * Math.PI / ROUND_CONST;

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

    public static boolean isaResponseSuccess(HttpResponse response) {
        int status = response.getStatusLine().getStatusCode();
        return status >= 200 && status < 300;
    }
}
