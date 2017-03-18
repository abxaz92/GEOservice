package ru.macrobit.geoservice.service.taximeter;

import ru.macrobit.drivertaxi.taximeter.TaximeterLocation;
import ru.macrobit.drivertaxi.taximeter.ordersdata.TaximeterInterval;

import java.util.ArrayList;

/**
 * Created by [david] on 18.03.17.
 */
public class TaximeterLogger implements ru.macrobit.drivertaxi.taximeter.logs.TaximeterLogger {
    @Override
    public void logNewTaximeterLocation(TaximeterLocation taximeterLocation) {

    }

    @Override
    public void logNewIntervalData(TaximeterInterval taximeterInterval, int i) {

    }

    @Override
    public void logTaximeterIntervalsOnFinish(ArrayList<TaximeterInterval> arrayList) {

    }

    @Override
    public void clearTaximeterLocations() {

    }

    @Override
    public ArrayList<TaximeterLocation> getOrderLoggedTaximeterLocations() {
        return null;
    }
}
