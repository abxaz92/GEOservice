package ru.macrobit.geoservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.macrobit.geoservice.common.GraphUtils;
import ru.macrobit.geoservice.pojo.BatchRequest;
import ru.macrobit.geoservice.service.RoutingService;

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
    public Object postBatchDistanceRequest(BatchRequest batchRequest) {
        long a = System.currentTimeMillis();
        Object res = routingService.calcDistances(batchRequest);
        logger.info("{}", System.currentTimeMillis() - a);
        return res;
    }
}
