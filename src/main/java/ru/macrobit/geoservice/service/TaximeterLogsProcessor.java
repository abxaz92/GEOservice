package ru.macrobit.geoservice.service;

import com.graphhopper.GraphHopper;
import com.graphhopper.matching.EdgeMatch;
import com.graphhopper.matching.LocationIndexMatch;
import com.graphhopper.matching.MapMatching;
import com.graphhopper.matching.MatchResult;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.index.LocationIndexTree;
import com.graphhopper.util.GPXEntry;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint3D;
import ru.macrobit.geoservice.pojo.LogEntry;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by [david] on 18.03.17.
 */
public class TaximeterLogsProcessor {
    private static final String DEFAULT_ACCURACY = "10";
    private static final String DEFAULT_LOCATIONS_PROVIDER = "gps";
    private GraphHopper hopper;
    private CarFlagEncoder encoder;
    List<LogEntry> logs;
    RouteCalculator routeCalculator;

    public static Builder newBuilder() {
        return new TaximeterLogsProcessor().new Builder();
    }

    public List<LogEntry> makeSmooth(Long maxTimeout) {
        logs = bindLocationsToRoad();
        sortLocationsList();
        return interpolateLocationTrack(maxTimeout);
    }

    private void sortLocationsList() {
        Collections.sort(logs, Comparator.comparingLong(LogEntry::getTimestamp));
    }

    private List<LogEntry> interpolateLocationTrack(Long maxPermitedTimeout) {
        int index = 1;
        List<LogEntry> taximeterLogs = new ArrayList<>(logs);
        for (int i = 0; i < logs.size() - 1; i++) {
            long timeoutBetweenCurrentLocations = logs.get(i + 1).getTimestamp() - logs.get(i).getTimestamp();
            if (timeoutBetweenCurrentLocations > maxPermitedTimeout) {
                PointList pointList = routeCalculator.getPointListForPoints(logs.get(i), logs.get(i + 1));
                Iterator<GHPoint3D> iterator = pointList.iterator();
                long timestamp = logs.get(i).getTimestamp();
                long incremet = (timeoutBetweenCurrentLocations / pointList.size());
                while (iterator.hasNext()) {
                    GHPoint3D point = iterator.next();
                    timestamp += incremet;
                    LogEntry logEntry = new LogEntry();
                    logEntry.setLat(point.getLat());
                    logEntry.setLon(point.getLon());
                    logEntry.setTimestamp(timestamp);
                    logEntry.setError(DEFAULT_ACCURACY);
                    logEntry.setSrc(DEFAULT_LOCATIONS_PROVIDER);
                    logEntry.setBuilded(true);
                    taximeterLogs.add(index, logEntry);
                    index++;
                }
                index++;
            } else {
                index++;
            }
        }
        return taximeterLogs;
    }

    private List<LogEntry> bindLocationsToRoad() {
        GraphHopperStorage graph = hopper.getGraphHopperStorage();
        LocationIndexMatch locationIndex = new LocationIndexMatch(graph,
                (LocationIndexTree) hopper.getLocationIndex());
        MapMatching mapMatching = new MapMatching(graph, locationIndex, encoder);
        List<GPXEntry> gpxEntries = logs.stream().map(log -> new GPXEntry(log.getLat(), log.getLon(), log.getTimestamp())).collect(Collectors.toList());
        mapMatching.setForceRepair(true);
        MatchResult mr = mapMatching.doWork(gpxEntries);
        List<EdgeMatch> matches = mr.getEdgeMatches();
        logs = new ArrayList<>();
        for (EdgeMatch match : matches) {
            logs.addAll(match.getGpxExtensions().stream().map(gpx -> {
                LogEntry log = new LogEntry();
                log.setLat(gpx.getQueryResult().getSnappedPoint().getLat());
                log.setLon(gpx.getQueryResult().getSnappedPoint().getLon());
                log.setTimestamp(gpx.getEntry().getTime());
                return log;
            }).collect(Collectors.toSet()));
        }
        return logs;
    }

    public class Builder {
        private Builder() {

        }

        public Builder setTaximeterLogs(List<LogEntry> taximeterLogs) {
            TaximeterLogsProcessor.this.logs = taximeterLogs;
            return this;
        }

        public Builder setGraphHopper(GraphHopper graphHopper) {
            TaximeterLogsProcessor.this.hopper = graphHopper;
            return this;
        }

        public Builder setEncoder(CarFlagEncoder encoder) {
            TaximeterLogsProcessor.this.encoder = encoder;
            return this;
        }

        public Builder setRouteCalculator(RouteCalculator routeCalculator) {
            TaximeterLogsProcessor.this.routeCalculator = routeCalculator;
            return this;
        }

        public TaximeterLogsProcessor build() {
            return TaximeterLogsProcessor.this;
        }
    }
}
