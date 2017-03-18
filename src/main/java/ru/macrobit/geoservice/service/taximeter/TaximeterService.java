package ru.macrobit.geoservice.service.taximeter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.macrobit.drivertaxi.taximeter.api.TaximeterAPI;
import ru.macrobit.drivertaxi.taximeter.api.TaximeterAPIImpl;
import ru.macrobit.drivertaxi.taximeter.api.TaximeterAPIResult;
import ru.macrobit.drivertaxi.taximeter.api.TaximeterRequest;
import ru.macrobit.drivertaxi.taximeter.taximeterParams.TaximeterParams;
import ru.macrobit.drivertaxi.taximeter.taximeterParams.TaximeterParamsImpl;
import ru.macrobit.drivertaxi.taximeter.taximeterParams.polygons.PolygonWithDataImpl;
import ru.macrobit.drivertaxi.taximeter.taximeterParams.polygons.PolygonsImpl;
import ru.macrobit.drivertaxi.taximeter.taximeterParams.polygons.polygon.Point;
import ru.macrobit.geoservice.common.GraphUtils;
import ru.macrobit.geoservice.common.PropertiesFileReader;
import ru.macrobit.geoservice.pojo.Area;
import ru.macrobit.geoservice.pojo.LogEntry;
import ru.macrobit.geoservice.service.RoutingService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by [david] on 05.10.16.
 */
@Singleton
@Startup
public class TaximeterService {
    private static final Logger logger = LoggerFactory.getLogger(TaximeterService.class);
    private TypeReference<List<Area>> typeReference = new TypeReference<List<Area>>() {
    };
    private PolygonsImpl polygons = new PolygonsImpl();
    private GraphHopper hopper;
    private static int POOL_SIZE = 300;
    CarFlagEncoder encoder = new CarFlagEncoder();
    private ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE, new ThreadFactory() {
        private AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("RoutingServiceThreadPool " + counter.addAndGet(1));
            return thread;
        }
    });
    private ScheduledExecutorService areasFetcherExecutor = Executors.newSingleThreadScheduledExecutor();
    private static final int POLYGON_RELOAD_DELAY = 10;


    @Inject
    private TaximeterLogDAO taximeterLogDAO;

    @PostConstruct
    public void init() {
        hopper = new GraphHopper().forServer();
        hopper.setOSMFile(PropertiesFileReader.getOsmFilePath());
        hopper.setGraphHopperLocation(PropertiesFileReader.getSecondGraphFolder());
        hopper.setEncodingManager(new EncodingManager(encoder));
        hopper.importOrLoad();
        startAreaFetcher();
        initThreadPool();
    }

    private void startAreaFetcher() {
        areasFetcherExecutor.schedule(new PolygonsReloadRunnable(), POLYGON_RELOAD_DELAY, TimeUnit.SECONDS);
    }

    class PolygonsReloadRunnable implements Runnable {
        @Override
        public void run() {
            try {
                logger.warn("start reloading ...");
                reloadPolygons();
                logger.warn("reloaded!");
            } catch (IOException e) {
                startAreaFetcher();
                logger.error("{}", e);
            }
        }
    }

    private void initThreadPool() {
        for (int i = 0; i < POOL_SIZE; i++) {
            pool.submit(() -> null);
        }
    }

    public void buildLogs(String orderId, Double maxDist, Long maxTimeout) {
        List<LogEntry> logs = taximeterLogDAO.getLogsNotBuildedLogs(orderId, null, true);
        TaximeterLogsProcessor taximeterLogsProcessor = TaximeterLogsProcessor
                .newBuilder()
                .setTaximeterLogs(logs)
                .setEncoder(encoder)
                .setGraphHopper(hopper)
                .setRouteCalculator((from, to) -> calcRoute(from, to))
                .build();
        taximeterLogsProcessor.makeSmooth(maxTimeout);
        logger.warn("{}", logs);
        taximeterLogDAO.bulkInsert(logs.stream()
                .collect(Collectors.toSet()), orderId);
    }

    public TaximeterAPIResult calculate(ru.macrobit.geoservice.pojo.TaximeterRequest taximeterRequest) throws Exception {
        TaximeterParams params = new TaximeterParamsImpl(taximeterRequest.getTarif());
        List<LogEntry> logs = taximeterLogDAO.getLogsNotBuildedLogs(taximeterRequest.getOrderId(), taximeterRequest.getIndex(), taximeterRequest.isBuild());
        TaximeterRequest request = new TaximeterRequest.Builder(params)
                .setConstantInterval(20000)
                .setLocations(logs)
                .setPolygons(polygons)
                .setTaximeterLogger(new TaximeterLogger())
                .build();
        TaximeterAPI api = new TaximeterAPIImpl(request);
        return api.calculate();
    }

    public PointList calcRoute(LogEntry from, LogEntry to) {
        GHResponse rsp = hopper.route(GraphUtils.createRequest(from.getLat(), from.getLon(), to.getLat(), to.getLon()));
        if (rsp.hasErrors()) {
            logger.error("{}", rsp.getErrors());
            return null;
        }

        PathWrapper path = rsp.getBest();
        return path.getPoints();
    }

    public synchronized void reloadPolygons() throws IOException {
        logger.warn("downloading areas...");
        List<Area> areas = fetchAreas();
        buildPolygonWithDataObj(areas);
        logger.warn("dowloaded!");
    }

    private void buildPolygonWithDataObj(List<Area> areas) {
        areas.stream().forEach(area -> {
            try {
                if (area.isValid()) {
                    List<Point> points = convertToPoints(area);
                    PolygonWithDataImpl polygonWithData =
                            new PolygonWithDataImpl(points, area.getName(), (float) area.getFactor(), area.getDescription());
                    polygons.addPolygon(polygonWithData);
                }
            } catch (Exception e) {
                logger.error("{} : {}", area.getName(), e);
            }
        });
    }

    private List<Point> convertToPoints(Area area) {
        if (area.getLoc() == null)
            return null;
        if (area.getLoc().getCoordinates() == null)
            return null;
        if (area.getLoc().getCoordinates().isEmpty())
            return null;
        return area.getLoc()
                .getCoordinates()
                .get(0)
                .stream().map(p -> new Point(p.get(0).floatValue(), p.get(1).floatValue())).collect(Collectors.toList());
    }

    private List<Area> fetchAreas() throws java.io.IOException {
        CloseableHttpClient client = HttpClients.createMinimal();
        HttpGet httpGet = new HttpGet("http://db/taxi/rest/area/with/factors");
        httpGet.setHeader("Authorization", "Basic " + new String(Base64.getEncoder().encode("route:!23456".getBytes())));
        ResponseHandler<List<Area>> responseHandler = response -> getAreas(response);
        return client.execute(httpGet, responseHandler);
    }

    private List<Area> getAreas(HttpResponse response) throws java.io.IOException {
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            return entity != null ? RoutingService.MAPPER.readValue(entity.getContent(), typeReference) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    }

    @PreDestroy
    public void preDestroy() {
        pool.shutdown();
    }

}
