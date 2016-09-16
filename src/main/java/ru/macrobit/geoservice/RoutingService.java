package ru.macrobit.geoservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.macrobit.geoservice.pojo.BatchRequest;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by [david] on 15.09.16.
 */
@Singleton
@Startup
@Lock(LockType.READ)
public class RoutingService {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(RoutingService.class);
    private GraphHopper hopper;
    private ExecutorService pool = Executors.newFixedThreadPool(100, new ThreadFactory() {
        private AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("RoutingServiceThreadPool " + counter.addAndGet(1));
            logger.info(thread.getName());
            return thread;
        }
    });

    @PostConstruct
    public void init() {
        try {
            hopper = new GraphHopper().forServer();
            hopper.setOSMFile(PropertiesFileReader.getOsmFilePath());
            hopper.setGraphHopperLocation(PropertiesFileReader.getGraphFolder());
            hopper.setEncodingManager(new EncodingManager("car"));
            hopper.importOrLoad();
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    public Object calcDistances(BatchRequest batchRequest) {
        int batchSize = batchRequest.getDests().values().size();
        if (batchSize == 0) {
            return null;
        }
        if (batchSize == 1) {
            Map<String, Double> resMap = new HashMap<>();
            Map.Entry<String, double[]> entry = batchRequest.getDests().entrySet().iterator().next();
            resMap.put(entry.getKey(), getDistance(batchRequest.getSrc(), entry.getValue()));
            return resMap;
        }
        Map<String, Future<Double>> futureMap = new HashMap<>();
/*        batchRequest.getDests().forEach((key, location) -> futureMap.put(key, pool.submit(() -> {
            Thread.sleep(10);
            return 0d;
        })));*/
        batchRequest.getDests().forEach((key, location) -> futureMap.put(key, pool.submit(() -> getDistance(batchRequest.getSrc(), location))));
        Map<String, Double> response = new HashMap<>();
        futureMap.forEach((key, val) -> {
            try {
                response.put(key, val.get(15, TimeUnit.MILLISECONDS));
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                response.put(key, null);
            }
        });
        return response;
    }

    public double getDistance(double[] from, double[] to) {
        return getDistance(from[0], from[1], to[0], to[1]);
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
