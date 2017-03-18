package ru.macrobit.geoservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.macrobit.geoservice.common.GraphUtils;
import ru.macrobit.geoservice.common.exception.InvalidRequestException;
import ru.macrobit.geoservice.pojo.AvoidEdge;
import ru.macrobit.geoservice.pojo.BatchRequest;
import ru.macrobit.geoservice.pojo.TaximeterRequest;
import ru.macrobit.geoservice.service.RoutingService;
import ru.macrobit.geoservice.service.TaximeterService;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by [david] on 15.09.16.
 */
@Path("/route")
@Produces(MediaType.APPLICATION_JSON)
public class RouteController {
    private static final Logger logger = LoggerFactory.getLogger(RouteController.class);

    @EJB
    private RoutingService routingService;
    @EJB
    private TaximeterService taximeterService;

    @GET
    @Path("/search")
    public Object getRouteInfo(@QueryParam("from") String from, @QueryParam("to") String to) {
        double[] fromLocs = GraphUtils.parseLocations(from);
        double[] toLocs = GraphUtils.parseLocations(to);
        return routingService.getRoute(fromLocs[0], fromLocs[1], toLocs[0], toLocs[1]);
    }

    @GET
    @Path("/distance")
    public Object getRouteDistance(@QueryParam("from") String from, @QueryParam("to") String to) {
        double[] fromLocs = GraphUtils.parseLocations(from);
        double[] toLocs = GraphUtils.parseLocations(to);
        return routingService.getDistanceAndTime(fromLocs[0], fromLocs[1], toLocs[0], toLocs[1]);
    }

    @POST
    @Path("/batch")
    @Consumes(MediaType.APPLICATION_JSON)
    public Object postBatchDistanceRequest(BatchRequest batchRequest) throws InvalidRequestException {
        return routingService.calcDistances(batchRequest);
    }

    @POST
    @Path("/calculate")
    @Consumes(MediaType.APPLICATION_JSON)
    public Object calculateTaximeter(TaximeterRequest taximeterRequest) throws Exception {
        return taximeterService.calculate(taximeterRequest);
    }

    @POST
    @Path("/build/{orderId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void buildLogs(@PathParam("orderId") String orderId, @QueryParam("maxdist") Double maxDist, @QueryParam("maxtimeout") Long maxTimeout) throws Exception {
        logger.info("{}, dist {}, time {}", orderId, maxDist, maxTimeout);
        taximeterService.buildLogs(orderId, maxDist, maxTimeout);
    }


    @POST
    @Path("avoid/")
    public void addEdge(AvoidEdge avoidEdge) {
        routingService.setEdgeSpeed(avoidEdge);
    }

    @PUT
    @Path("avoid/")
    public void putEdge(AvoidEdge avoidEdge) {
        if (!avoidEdge.isActive()) {
            routingService.reloadAvoidEdges();
        } else
            routingService.setEdgeSpeed(avoidEdge);
    }

    @DELETE
    @Path("avoid/")
    public void deleteEdge() {
        routingService.reloadAvoidEdges();
    }
}
