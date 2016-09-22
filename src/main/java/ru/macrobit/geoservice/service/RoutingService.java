package ru.macrobit.geoservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.macrobit.geoservice.common.GraphUtils;
import ru.macrobit.geoservice.common.PropertiesFileReader;
import ru.macrobit.geoservice.pojo.BatchRequest;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by [David] on 15.09.16.
 */
@Singleton
@Startup
@Lock(LockType.READ)
public class RoutingService {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(RoutingService.class);
    private GraphHopper hopper;
    private static int POOL_SIZE = 100;
    private ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE, new ThreadFactory() {
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
        hopper = new GraphHopper().forServer();
        hopper.setOSMFile(PropertiesFileReader.getOsmFilePath());
        hopper.setCHEnabled(false);
        hopper.setGraphHopperLocation(PropertiesFileReader.getGraphFolder());
        hopper.setEncodingManager(new EncodingManager("car"));
        hopper.importOrLoad();
        for (int i = 0; i < POOL_SIZE; i++) {
            pool.submit(() -> null);
        }
    }

    public Object getRoute(double fromLat, double fromLon, double toLat, double toLon) {
        GHResponse rsp = hopper.route(GraphUtils.createRequest(fromLat, fromLon, toLat, toLon));
        if (rsp.hasErrors()) {
            logger.error("{}", rsp.getErrors());
            return null;
        }

        PathWrapper path = rsp.getBest();
        PointList pointList = path.getPoints();
        return pointList.toGeoJson();
    }

    public Object calcDistances(BatchRequest batchRequest) {
        long a = System.currentTimeMillis();
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
        batchRequest.getDests().forEach((key, location) -> futureMap.put(key, pool.submit(() -> getDistance(location, batchRequest.getSrc()))));
        Map<String, Double> response = new HashMap<>();
        futureMap.forEach((key, val) -> {
            try {
                response.put(key, val.get(7, TimeUnit.MILLISECONDS));
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                response.put(key, -1d);
            }
        });
        return response;
    }

    private double getDistance(double[] from, double[] to) {
        return getDistance(from[0], from[1], to[0], to[1]);
    }

    private double getDistance(double fromLat, double fromLon, double toLat, double toLon) {
        GHResponse rsp = hopper.route(GraphUtils.createRequest(fromLat, fromLon, toLat, toLon));
        if (rsp.hasErrors()) {
            logger.error("{}", rsp.getErrors());
            return -1;
        }
        PathWrapper path = rsp.getBest();
        return path.getDistance();
    }

    public Object getDistanceAndTime(double fromLat, double fromLon, double toLat, double toLon) {
        GHResponse rsp = hopper.route(GraphUtils.createRequest(fromLat, fromLon, toLat, toLon));
        if (rsp.hasErrors()) {
            logger.error("{}", rsp.getErrors());
            return -1;
        }
        PathWrapper path = rsp.getBest();
        Map<String, Object> res = new HashMap<>();
        res.put("dist", path.getDistance());
        res.put("time", path.getTime() / 1000);

        return res;
    }

    public void setEdgeSpeed(double lat, double lon, double speed) {
        Graph graph = hopper.getGraphHopperStorage();
        FlagEncoder carEncoder = hopper.getEncodingManager().getEncoder("car");
        LocationIndex locationIndex = hopper.getLocationIndex();
        QueryResult qr = locationIndex.findClosest(lat, lon, EdgeFilter.ALL_EDGES);
        if (!qr.isValid()) {
            logger.warn("query not valid for : {}, {}", lat, lon);
            return;
        }

        int edgeId = qr.getClosestEdge().getEdge();
        EdgeIteratorState edge = graph.getEdgeIteratorState(edgeId, Integer.MIN_VALUE);
        double oldSpeed = carEncoder.getSpeed(edge.getFlags());
        if (oldSpeed != speed) {
            logger.info("Speed change [{}] at ({}, {}). Old: {}, new: {}", edge.getName(), lat, lon, oldSpeed, speed);
            edge.setFlags(carEncoder.setSpeed(edge.getFlags(), speed));
        }
    }

}
