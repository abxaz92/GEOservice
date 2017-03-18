package ru.macrobit.geoservice.service;

import com.graphhopper.util.PointList;
import ru.macrobit.geoservice.pojo.LogEntry;

/**
 * Created by [david] on 18.03.17.
 */
public interface RouteCalculator {
    PointList getPointListForPoints(LogEntry from, LogEntry to);
}
