package ru.macrobit.geoservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.graphhopper.GHRequest;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.shapes.GHPoint;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.macrobit.drivertaxi.taximeter.TaximeterLocation;
import ru.macrobit.drivertaxi.taximeter.api.TaximeterAPI;
import ru.macrobit.drivertaxi.taximeter.api.TaximeterAPIImpl;
import ru.macrobit.drivertaxi.taximeter.api.TaximeterAPIResult;
import ru.macrobit.drivertaxi.taximeter.api.TaximeterRequest;
import ru.macrobit.drivertaxi.taximeter.ordersdata.TaximeterInterval;
import ru.macrobit.drivertaxi.taximeter.taximeterParams.TaximeterParams;
import ru.macrobit.drivertaxi.taximeter.taximeterParams.TaximeterParamsImpl;
import ru.macrobit.drivertaxi.taximeter.taximeterParams.polygons.PolygonWithDataImpl;
import ru.macrobit.drivertaxi.taximeter.taximeterParams.polygons.PolygonsImpl;
import ru.macrobit.drivertaxi.taximeter.taximeterParams.polygons.polygon.Point;
import ru.macrobit.geoservice.common.PropertiesFileReader;
import ru.macrobit.geoservice.pojo.Area;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
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
    private volatile boolean ready = false;
    private Thread areaFetcher;
    private GraphHopper hopper;

    @PostConstruct
    public void init() {
        hopper = new GraphHopper().forServer();
        hopper.setOSMFile(PropertiesFileReader.getOsmFilePath());
        hopper.setGraphHopperLocation(PropertiesFileReader.getSecondGraphFolder());
        hopper.setEncodingManager(new EncodingManager("car"));
        hopper.importOrLoad();

        areaFetcher = new Thread(() -> {
            while (!ready) {
                try {
                    reloadPolygons();
                    ready = true;
                } catch (Exception e) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        });
        areaFetcher.start();
    }

    public TaximeterAPIResult calculate(ru.macrobit.geoservice.pojo.TaximeterRequest taximeterRequest) throws Exception {
        TaximeterParams params = new TaximeterParamsImpl(taximeterRequest.getTarif());
        if (taximeterRequest.isPrepare()) {
            List<GHPoint> points = taximeterRequest.getLogs().stream().map(logEntry ->
                    new GHPoint(logEntry.getLat(), logEntry.getLon())
            ).collect(Collectors.toList());
            GHRequest request = new GHRequest(points).
                    setWeighting("fastest").
                    setVehicle("car").
                    setLocale(Locale.US);
        }
        /*int maxGpsLostTimeout = 20000;
        for (int i = 0; i < logs.size() - 1; i++) {
            if (logs.get(i).getTimestamp() - logs.get(i + 1).getTimestamp() > maxGpsLostTimeout) {
                routingService.calculateTrack(logs.get(i), logs.get(i + 1));
            }
        }*/

        TaximeterRequest request = new TaximeterRequest.Builder(params)
                .setConstantInterval(20000)
                .setLocations(taximeterRequest.getLogs())
                .setPolygons(polygons)
                .setTaximeterLogger(new TaximeterLogger())
                .build();
        TaximeterAPI api = new TaximeterAPIImpl(request);
        return api.calculate();
    }

    public synchronized void reloadPolygons() throws Exception {
        logger.warn("downloading areas...");
        CloseableHttpClient client = HttpClients.createMinimal();
        HttpGet httpGet = new HttpGet("http://db/taxi/rest/area/with/factors");
        httpGet.setHeader("Authorization", "Basic " + new String(Base64.getEncoder().encode("route:!23456".getBytes())));
        ResponseHandler<List<Area>> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? RoutingService.MAPPER.readValue(entity.getContent(), typeReference) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };
        List<Area> areas = client.execute(httpGet, responseHandler);
        areas.stream().forEach(area -> {
            List<Point> points = new ArrayList<>();
            try {
                if (area.getLoc() != null &&
                        area.getLoc().getCoordinates() != null &&
                        area.getLoc().getCoordinates().size() > 0 &&
                        area.getLoc().getCoordinates().get(0).size() > 0) {
                    area.getLoc().getCoordinates().get(0).stream().forEach(p -> points.add(new Point(p.get(0).floatValue(), p.get(1).floatValue())));
                }
                PolygonWithDataImpl polygonWithData = new PolygonWithDataImpl(points, area.getName(), (float) area.getFactor(), area.getDescription());
                polygons.addPolygon(polygonWithData);
            } catch (Exception e) {
                logger.error("{} : {}", area.getName(), e);
            }
        });
        logger.warn("dowloaded!");
    }

    @PreDestroy
    public void preDestroy() {
        areaFetcher.interrupt();
    }

    class TaximeterLogger implements ru.macrobit.drivertaxi.taximeter.logs.TaximeterLogger {
        @Override
        public void logNewTaximeterLocation(TaximeterLocation taximeterLocation) {

        }

        @Override
        public void logTaximeterIntervalsOnFinish(ArrayList<TaximeterInterval> arrayList) {

        }

        @Override
        public void logNewIntervalData(TaximeterInterval taximeterInterval, int i) {
        }

        @Override
        public void clearTaximeterLocations() {

        }

        @Override
        public ArrayList<TaximeterLocation> getOrderLoggedTaximeterLocations() {
            return null;
        }
    }
}
