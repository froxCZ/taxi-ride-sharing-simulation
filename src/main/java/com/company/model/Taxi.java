package com.company.model;

import com.company.Coordinator;
import com.company.routing.OsrmClient;
import com.company.routing.vo.Route;

import java.util.List;

/**
 * Created by frox on 5.5.16.
 */
public class Taxi {
    private final int id;
    private static int TAXI_COUNT = 0;
    private RoutePlan routePlan = new RoutePlan();

    public Taxi() {
        this.id = TAXI_COUNT++;
    }

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

    @Override
    public String toString() {
        return "Taxi{" +
                "id=" + id +
                '}';
    }

    public int getId() {
        return id;
    }
}
