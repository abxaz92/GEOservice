package ru.macrobit.geoservice.service;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.*;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.GHUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by david on 19.09.16.
 */
public class CustomGraphHopper extends GraphHopper {
    private static final Logger logger = LoggerFactory.getLogger(RoutingService.class);

    Set<EdgeIteratorState> forbiddenEdges = new HashSet<>();

    public void determineForbiddenEdges() {
        double lat = 43.045042;
        double lon = 44.661595;
        QueryResult qr = this.getLocationIndex().findClosest(lat, lon, EdgeFilter.ALL_EDGES);
        if (!qr.isValid()) {
            // logger.info("no matching road found for entry " + entry.getId() + " at " + point);
            return;
        }
        EdgeIteratorState edge = qr.getClosestEdge();
        logger.warn("ignoring {}", edge.getEdge());
        forbiddenEdges.add(edge);
    }

    @Override
    public Weighting createWeighting(HintsMap weightingMap, FlagEncoder encoder) {
        logger.warn("create createWeighting");
        String weighting = weightingMap.getWeighting();

        if ("BLOCKING".equalsIgnoreCase(weighting)) {
            AvoidEdgesWeighting w = new AvoidEdgesWeighting(new CustomWeighting(encoder, forbiddenEdges));
            w.addEdges(forbiddenEdges);
            return w;
        } else {
            return super.createWeighting(weightingMap, encoder);
        }
    }

    class CustomWeighting implements Weighting {

        private final FlagEncoder encoder;
        private final double maxSpeed;
        private Set<EdgeIteratorState> forbiddenEdges;

        public CustomWeighting(FlagEncoder encoder, Set<EdgeIteratorState> forbiddenEdges) {
            this.encoder = encoder;
            this.maxSpeed = encoder.getMaxSpeed();
            this.forbiddenEdges = forbiddenEdges;
        }

        @Override
        public double getMinWeight(double distance) {
            return distance / maxSpeed;
        }

        @Override
        public double calcWeight(EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId) {
            if (forbiddenEdges.contains(edgeState.getEdge()))
                return Double.POSITIVE_INFINITY;

            double speed = reverse ? encoder.getReverseSpeed(edgeState.getFlags()) : encoder.getSpeed(edgeState.getFlags());
            if (speed == 0)
                return Double.POSITIVE_INFINITY;
            return edgeState.getDistance() / speed;
        }

        @Override
        public FlagEncoder getFlagEncoder() {
            return encoder;
        }

        @Override
        public String getName() {
            return "CustomWeighting";
        }

        @Override
        public boolean matches(HintsMap hintsMap) {
            return false;
        }

        @Override
        public String toString() {
            return "BLOCKING";
        }
    }
}
