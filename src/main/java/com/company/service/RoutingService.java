package com.company.service;

import com.company.routing.MapGridCache;
import com.company.model.Coordinate;
import com.company.model.DurationAndDistance;
import com.company.routing.OsrmClient;
import com.company.routing.vo.Route;

/**
 * This service returns route or duration and distance between specified coordinates. It either uses grid cache or OSRM
 */
public class RoutingService {
    MapGridCache mapGridCache = MapGridCache.getInstance();
    public static int COUNTER = 0;
    private static RoutingService instance = new RoutingService();

    public RoutingService() {

    }

    public static RoutingService getInstance() {
        return instance;
    }

    /**
     * uses OSRM
     * @param coordinates
     * @return
     */
    public Route getRoute(Coordinate... coordinates) {
        COUNTER++;
        return OsrmClient.getRoute(coordinates);
    }

    /**
     * uses OSRM
     * @param from
     * @param to
     * @return
     */
    public DurationAndDistance getDurationAndDistance(Coordinate from, Coordinate to) {
        COUNTER++;
        return OsrmClient.getDurationAndDistance(from, to);
    }

    /**
     * uses grid cache
     * @param coordinates
     * @return
     */
    public DurationAndDistance getDurationAndDistanceFast(Coordinate... coordinates) {
        COUNTER++;
        if (coordinates.length <= 1) {
            throw new RuntimeException("at least 2 args");
        } else {
            int totalDuration = 0, totalDistance = 0;
            Coordinate previous = coordinates[0];
            for (int i = 0; i < coordinates.length; i++) {
                DurationAndDistance durationAndDistance = mapGridCache.getDurationAndDistance(previous, coordinates[i]);
                totalDistance += durationAndDistance.distance;
                totalDuration += durationAndDistance.duration;
                previous = coordinates[i];
            }
            return new DurationAndDistance(totalDuration, totalDistance);
        }

    }

}
