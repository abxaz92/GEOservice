package ru.macrobit.geoservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.macrobit.geoservice.common.GraphUtils;
import ru.macrobit.geoservice.common.PropertiesFileReader;
import ru.macrobit.geoservice.common.exception.InvalidRequestException;
import ru.macrobit.geoservice.pojo.AvoidEdge;
import ru.macrobit.geoservice.pojo.BatchRequest;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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
    private static int POOL_SIZE = 300;
    private ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE, new ThreadFactory() {
        private AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("RoutingServiceThreadPool " + counter.addAndGet(1));
            return thread;
        }
    });
    private CloseableHttpClient client;
    private TypeReference<List<AvoidEdge>> typeReference = new TypeReference<List<AvoidEdge>>() {
    };
    private List<AvoidEdge> avoidEdges = null;
    private static final String URL_AVOID_EDGES = "http://db/taxi/rest/mapnode?query=%7Bactive%3Atrue%7D";
    private static final Header AUTH_HEADER = new BasicHeader("Authorization", "Basic " + new String(Base64.getEncoder().encode("route:!23456".getBytes())));

    @PostConstruct
    public void init() {
        hopper = new GraphHopper().forServer();
        hopper.setOSMFile(PropertiesFileReader.getOsmFilePath());
        hopper.setCHEnabled(false);
        hopper.setGraphHopperLocation(PropertiesFileReader.getGraphFolder());
        hopper.setEncodingManager(new EncodingManager("car"));
        hopper.importOrLoad();
        initThreadPool();
        reloadAvoidEdges();
    }

    /**
     * Init every Thread in pool on startup
     */
    private void initThreadPool() {
        for (int i = 0; i < POOL_SIZE; i++) {
            pool.submit(() -> null);
        }
    }

    public void reloadAvoidEdges() {
        logger.warn("reloading...");
        try {
            avoidEdges = fetchAvoidEdges();
            reAssignSpeedForEdges();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.warn("reloaded!");
    }

    private void reAssignSpeedForEdges() {
        avoidEdges.forEach(this::setEdgeSpeed);
    }

    private List<AvoidEdge> fetchAvoidEdges() throws IOException {
        client = HttpClients.createMinimal();
        HttpGet httpGet = new HttpGet(URL_AVOID_EDGES);

        httpGet.setHeader(AUTH_HEADER);
        ResponseHandler<List<AvoidEdge>> responseHandler = response -> getAvoidEdgesFromResponse(response);
        return client.execute(httpGet, responseHandler);
    }

    private List<AvoidEdge> getAvoidEdgesFromResponse(HttpResponse response) throws java.io.IOException {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            return entity != null ? MAPPER.readValue(entity.getContent(), typeReference) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
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

    public Object calcDistances(BatchRequest batchRequest) throws InvalidRequestException {
        long a = System.currentTimeMillis();
        if (batchRequest == null)
            throw new InvalidRequestException();
        if (!batchRequest.isValid())
            throw new InvalidRequestException();

        int batchSize = batchRequest.getDests().values().size();
        if (batchSize == 0) {
            return null;
        }
        if (batchSize == 1) {
            return calcDistanceForSingleBatch(batchRequest);
        }

//        Map<String, Future<Double>> futureMap = new HashMap<>();
        Map<String, Future<Double>> futureMap = new HashMap<>();

        batchRequest.getDests().forEach((key, location) -> futureMap.put(key, pool.submit(() -> calcDistance(location, batchRequest.getSrc()))));
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

    private Object calcDistanceForSingleBatch(BatchRequest batchRequest) {
        Map<String, Double> resMap = new HashMap<>();
        Map.Entry<String, double[]> entry = batchRequest.getDests().entrySet().iterator().next();
        resMap.put(entry.getKey(), calcDistance(batchRequest.getSrc(), entry.getValue()));
        return resMap;
    }

    private double calcDistance(double[] from, double[] to) {
        return GraphUtils.getDistance(from[0], from[1], to[0], to[1], hopper);
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

    public void setEdgeSpeed(AvoidEdge avoidEdge) {
        if (avoidEdge.getLoc() == null) {
            throw new RuntimeException("location for " + avoidEdge.getId() + " is null");
        }
        if (avoidEdge.getLoc().length != 2) {
            throw new RuntimeException("location for " + avoidEdge.getId() + " is illegal");
        }
        Graph graph = hopper.getGraphHopperStorage();
        FlagEncoder carEncoder = hopper.getEncodingManager().getEncoder("car");
        LocationIndex locationIndex = hopper.getLocationIndex();
        QueryResult qr = locationIndex.findClosest(avoidEdge.getLoc()[0], avoidEdge.getLoc()[1], EdgeFilter.ALL_EDGES);
        if (!qr.isValid()) {
            logger.warn("query not valid for :{} {}, {}", avoidEdge.getId(), avoidEdge.getLoc()[0], avoidEdge.getLoc()[0]);
            return;
        }

        int edgeId = qr.getClosestEdge().getEdge();
        EdgeIteratorState edge = graph.getEdgeIteratorState(edgeId, Integer.MIN_VALUE);
        double oldSpeed = carEncoder.getSpeed(edge.getFlags());
        avoidEdge.setOldSpeed(oldSpeed);
        avoidEdge.setEdgeId(edgeId);
        logger.info(avoidEdge.getId());
        if (oldSpeed != avoidEdge.getSpeed()) {
            logger.info("Speed change [{}] at ({}, {}). Old: {}, new: {}", edge.getName(), avoidEdge.getId(), avoidEdge.getLoc()[0], oldSpeed, avoidEdge.getSpeed());
            edge.setFlags(carEncoder.setSpeed(edge.getFlags(), avoidEdge.getSpeed()));
        }
    }

    @PreDestroy
    public void preDestroy() {
        pool.shutdown();
    }

}
