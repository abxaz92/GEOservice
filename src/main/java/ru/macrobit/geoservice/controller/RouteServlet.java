package ru.macrobit.geoservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.macrobit.geoservice.service.RoutingService;
import ru.macrobit.geoservice.pojo.BatchRequest;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by [david] on 16.09.16.
 */
@WebServlet("/batch")
public class RouteServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RouteServlet.class);

    @EJB
    private RoutingService routingService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long a = System.currentTimeMillis();
        BatchRequest batchRequest = RoutingService.MAPPER.readValue(req.getInputStream(), BatchRequest.class);
        resp.setContentType(MediaType.APPLICATION_JSON);
        resp.getWriter().print(RoutingService.MAPPER.writeValueAsString(routingService.calcDistances(batchRequest)));
        logger.info("{}", System.currentTimeMillis() - a);
    }
}
