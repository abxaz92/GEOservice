package ru.macrobit.geoservice;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by [david] on 15.09.16.
 */
@Path("/route")
@Produces(MediaType.APPLICATION_JSON)
public class RouteController {
    @EJB
    private RoutingService routingService;

    @GET
    @Path("/search")
    public Object getRouteInfo(@QueryParam("from") String from, @QueryParam("to") String to) {
        double[] fromLocs = parseLocations(from);
        double[] toLocs = parseLocations(to);
        return routingService.getRoute(fromLocs[0], fromLocs[1], toLocs[0], toLocs[1]);
    }

    @GET
    @Path("/distance")
    public Object getRouteDistance(@QueryParam("from") String from, @QueryParam("to") String to) {
        double[] fromLocs = parseLocations(from);
        double[] toLocs = parseLocations(to);
        return routingService.getDistance(fromLocs[0], fromLocs[1], toLocs[0], toLocs[1]);
    }

    private double[] parseLocations(String loc) {
        String[] locs = loc.split(",");
        if (locs.length != 2)
            throw new WebApplicationException("Illegal location param", 406);
        return new double[]{Double.parseDouble(locs[0]), Double.parseDouble(locs[1])};
    }
}
