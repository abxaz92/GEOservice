package ru.macrobit.geoservice.service.taximeter;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.macrobit.geoservice.pojo.LogEntry;
import ru.macrobit.geoservice.service.RouteCalculator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by [david] on 18.03.17.
 */
public class TaximeterLogsProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TaximeterLogsProcessor.class);

    private GraphHopper hopper;
    private CarFlagEncoder encoder;
    private List<LogEntry> logs;
    private RouteCalculator routeCalculator;

    public static Builder newBuilder() {
        return new TaximeterLogsProcessor().new Builder();
    }

    public List<LogEntry> makeSmooth(Long maxTimeout) {
        doBindLocationsToRoad();
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
                long timestamp = logs.get(i).getTimestamp();
                long incremet = (timeoutBetweenCurrentLocations / pointList.size());
                Iterator<GHPoint3D> iterator = pointList.iterator();
                while (iterator.hasNext()) {
                    timestamp += incremet;
                    GHPoint3D ghPoint3D = iterator.next();
                    LogEntry logEntry = LogEntry.createFromGHPoint3D(ghPoint3D, timestamp);
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

    private void doBindLocationsToRoad() {
        GraphHopperStorage graph = hopper.getGraphHopperStorage();
        LocationIndexMatch locationIndex = new LocationIndexMatch(graph, (LocationIndexTree) hopper.getLocationIndex());
        MapMatching mapMatching = new MapMatching(graph, locationIndex, encoder);
        List<GPXEntry> gpxEntries = convertLogsToGpxEntries();
        mapMatching.setForceRepair(true);
        MatchResult mr = mapMatching.doWork(gpxEntries);
        List<EdgeMatch> matches = mr.getEdgeMatches();
        this.logs = new ArrayList<>();
        for (EdgeMatch match : matches) {
            Set<LogEntry> taximeterLogs = extractTaximeterLogsFromMatch(match);
            logs.addAll(taximeterLogs);
        }
    }

    private static Set<LogEntry> extractTaximeterLogsFromMatch(EdgeMatch match) {
        return match
                .getGpxExtensions()
                .stream()
                .map(gpxExtension -> LogEntry.createFromGpxExtension(gpxExtension))
                .collect(Collectors.toSet());
    }

    private List<GPXEntry> convertLogsToGpxEntries() {
        return logs.stream().map(log -> new GPXEntry(log.getLat(), log.getLon(), log.getTimestamp())).collect(Collectors.toList());
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
