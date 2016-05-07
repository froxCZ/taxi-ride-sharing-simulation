package com.company.model;

import com.company.simulator.Coordinator;
import com.company.routing.OsrmClient;
import com.company.routing.vo.Route;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by frox on 5.5.16.
 */
public class Taxi {
    private final int id;
    private static int TAXI_COUNT = 0;
    private final Coordinate initialPosition;
    private RoutePlan routePlan = new RoutePlan();
    int paidMeters = 0;
    int nonPaidMeters = 0;

    public Taxi(Coordinate initialPosition) {
        this.initialPosition = initialPosition;
        this.id = TAXI_COUNT++;
    }

    public Coordinate getInitialPosition() {
        return initialPosition;
    }

    public RoutePlan getRoutePlan() {
        return routePlan;
    }

    public void addRide(Ride ride) {
        Route r = OsrmClient.getRoute(ride.getPickup(), ride.getDestination());
        routePlan.setPoints(r.getRoutePlanByDeltaSeconds(Coordinator.TIME_DELTA));
    }

    public Coordinate getPosition() {
        return getPositionAtTime(Coordinator.TIME_FROM_START);
    }

    public Coordinate getPositionAtTime(int time) {
        if (routePlan.hasStopsAhead()) {
            return routePlan.getPositionAtTime(time);
        } else {
            return initialPosition;
        }
    }

    public boolean isServing() {
        return routePlan.hasStopsAhead();
    }

    @Override
    public String toString() {
        return "Taxi{" +
                "id=" + id +
                " position: " + getPosition() +
                " serving: " + isServing() +
                " paidMeters: " + paidMeters +
                " nonPaidMeters: " + nonPaidMeters +
                '}';
    }

    public int getPaidMeters() {
        return paidMeters;
    }

    public int getNonPaidMeters() {
        return nonPaidMeters;
    }

    public void addPaidMeters(int paidMeters) {
        this.paidMeters += paidMeters;
    }

    public void addNonPaidMeters(int nonPaidMeters) {
        this.nonPaidMeters += nonPaidMeters;
    }

    public int getId() {
        return id;
    }
}
