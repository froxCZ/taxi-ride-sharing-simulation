package com.company.service;

import com.company.grid.MapGrid;
import com.company.model.Coordinate;
import com.company.model.DurationAndDistance;
import com.company.routing.OsrmClient;

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

    public DurationAndDistance getDurationAndDistance(Coordinate from, Coordinate to) {
        return OsrmClient.getDurationAndDistance(from, to);
    }

    public DurationAndDistance getDurationAndDistanceFast(Coordinate from, Coordinate to) {
        return mapGrid.getDurationAndDistance(from, to);
    }

}
