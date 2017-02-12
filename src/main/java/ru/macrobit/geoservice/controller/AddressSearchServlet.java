package ru.macrobit.geoservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.macrobit.geoservice.common.GraphUtils;
import ru.macrobit.geoservice.search.pojo.SearchResult;
import ru.macrobit.geoservice.search.SearchService;

import javax.ejb.EJB;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * @author Georgy Davityan.
 */
@WebServlet(value = "/public/search", asyncSupported = true)
public class AddressSearchServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AddressSearchServlet.class);

    @EJB
    private SearchService searchService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, String> querymap = GraphUtils.getQueryMap(URLDecoder.decode(req.getQueryString(), "UTF-8"));
        String pattern = querymap.get("pattern");
        String city = querymap.get("city");
        String disp = querymap.get("disp");
        String filterCity = querymap.get("filterCity");
        final AsyncContext context = req.startAsync();
        context.start(() -> {
            try {
                HttpServletResponse response = (HttpServletResponse) context.getResponse();
                response.setContentType(MediaType.APPLICATION_JSON);
                response.setHeader("Content-Type", "application/json");
                response.getWriter()
                        .print(GraphUtils.MAPPER.writeValueAsString(
                                searchService.search(pattern.trim().toLowerCase(),
                                        city == null ? null : city.toLowerCase(),
                                        disp,
                                        filterCity)));
                context.complete();
            } catch (Exception e) {
                try {
                    resp.getWriter().print(GraphUtils.MAPPER.writeValueAsString(new SearchResult()));
                } catch (IOException e1) {
                    context.complete();
                }
                logger.error("Unknown servlet IO exception", e);
                context.complete();
            }
        });
    }
}
