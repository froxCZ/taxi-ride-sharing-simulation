package com.company.service;

import com.company.grid.MapGrid;
import com.company.model.Coordinate;
import com.company.model.DurationAndDistance;
import com.company.routing.OsrmClient;
import com.company.routing.vo.Route;

/**
 * Created by frox on 7.5.16.
 */
public class RoutingService {
    MapGrid mapGrid = MapGrid.getInstance();
    private static RoutingService instance = new RoutingService();

    public RoutingService() {

    }

    public static RoutingService getInstance() {
        return instance;
    }

    public Route getRoute(Coordinate... coordinates) {
        return OsrmClient.getRoute(coordinates);
    }
    public DurationAndDistance getDurationAndDistance(Coordinate from, Coordinate to) {
        return OsrmClient.getDurationAndDistance(from, to);
    }

    public DurationAndDistance getDurationAndDistanceFast(Coordinate... coordinates) {
        if (coordinates.length <= 1) {
            throw new RuntimeException("at least 2 args");
        } else {
            int totalDuration = 0, totalDistance = 0;
            Coordinate previous = coordinates[0];
            for (int i = 0; i < coordinates.length; i++) {
                DurationAndDistance durationAndDistance = mapGrid.getDurationAndDistance(previous, coordinates[i]);
                totalDistance += durationAndDistance.distance;
                totalDuration += durationAndDistance.duration;
                previous = coordinates[i];
            }
            return new DurationAndDistance(totalDuration, totalDistance);
        }

    }

}
