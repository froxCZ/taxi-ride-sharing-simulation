package com.company.model;

import com.company.Coordinator;
import com.company.routing.OsrmClient;
import com.company.routing.vo.Route;

import java.util.List;

/**
 * Created by frox on 5.5.16.
 */
public class Taxi {
    private RoutePlan routePlan = new RoutePlan();

    public RoutePlan getRoutePlan() {
        return routePlan;
    }

    public void addRide(Ride ride) {
        Route r = OsrmClient.getRoute(ride.getPickup(), ride.getDestination());
        routePlan.setPoints(r.getRoutePlanByDeltaSeconds(Coordinator.TIME_DELTA));
    }

    public Coordinate getPosition() {
        return routePlan.getPositionAtTime(Coordinator.TIME);
    }

    public Coordinate getPositionAtTime(int time) {
        return routePlan.getPositionAtTime(Coordinator.TIME);
    }
}
