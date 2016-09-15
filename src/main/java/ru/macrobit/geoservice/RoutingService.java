package ru.macrobit.geoservice;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.Locale;

/**
 * Created by [david] on 15.09.16.
 */
@Singleton
@Startup
@Lock(LockType.READ)
public class RoutingService {
    private static final Logger logger = LoggerFactory.getLogger(RoutingService.class);
    private GraphHopper hopper;

    @PostConstruct
    public void init() {
        hopper = new GraphHopper().forServer();
        hopper.setOSMFile("/home/david/north-caucasus-fed-district-latest.osm");
        hopper.setGraphHopperLocation("/home/david/graph/");
        hopper.setEncodingManager(new EncodingManager("car"));
        hopper.importOrLoad();
    }

    public Object getRoute(double fromLat, double fromLon, double toLat, double toLon) {
        GHResponse rsp = hopper.route(createRequest(fromLat, fromLon, toLat, toLon));
        if (rsp.hasErrors()) {
            logger.error("{}", rsp.getErrors());
            return null;
        }

        PathWrapper path = rsp.getBest();
        PointList pointList = path.getPoints();
        return pointList.toGeoJson();
    }

    public double getDistance(double fromLat, double fromLon, double toLat, double toLon) {
        GHResponse rsp = hopper.route(createRequest(fromLat, fromLon, toLat, toLon));
        if (rsp.hasErrors()) {
            logger.error("{}", rsp.getErrors());
            return -1;
        }
        PathWrapper path = rsp.getBest();
        return path.getDistance();
    }

    public static GHRequest createRequest(double fromLat, double fromLon, double toLat, double toLon) {
        return new GHRequest(fromLat, fromLon, toLat, toLon).
                setWeighting("fastest").
                setVehicle("car").
                setLocale(Locale.US);
    }

}
