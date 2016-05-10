package com.company.model;

import com.company.routing.vo.Leg;
import com.company.service.RoutingService;
import com.company.simulator.Simulator;
import com.company.routing.OsrmClient;
import com.company.routing.vo.Route;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by frox on 5.5.16.
 */
public class Taxi {
    private final int id;
    private static int TAXI_ID_COUNTER = 0;
    private final Coordinate initialPosition;
    private Coordinate lastRoutePosition;
    private RoutePlan routePlan = new RoutePlan();//List of route stops which are delta seconds from each other. used for calculating taxis position at given time
    private List<PassengerStop> stops = new ArrayList<>();//a future stops taxi is going to visit. New stops can be inserted
    private List<PassengerStop> stopsHistory = new ArrayList<>();//keeps track of all stops taxi visited for statistics data collection
    private RoutingService routingService = RoutingService.getInstance();
    private int passengersOnBoard = 0;

    public Taxi(Coordinate initialPosition) {
        this.initialPosition = initialPosition;
        this.id = TAXI_ID_COUNTER++;
    }

    public Coordinate getInitialPosition() {
        return initialPosition;
    }

    public RoutePlan getRoutePlan() {
        return routePlan;
    }

    /**
     * returns only stops ahead
     *
     * @return
     */
    public List<PassengerStop> getStops() {
        removedVisitedStops();
        return stops;
    }

    private void removedVisitedStops() {
        Iterator<PassengerStop> it = stops.iterator();
        while (it.hasNext()) {
            PassengerStop passengerStop = it.next();
            if (Simulator.CURRENT_TIME.isAfter(passengerStop.getPlannedArrival())) {
                passengersOnBoard += passengerStop.getPassengerChange();
                stopsHistory.add(passengerStop);
                it.remove();
            } else {
                break;
            }
        }
    }

    /**
     * set new order of stops taxi needs to visit. This methods also changes route points, because the route changed.
     * @param stops
     */
    public void setStops(List<PassengerStop> stops) {
        Coordinate[] coordinates = new Coordinate[stops.size() + 1];
        coordinates[0] = getPosition();
        for (int i = 0; i < stops.size(); i++) {
            coordinates[i + 1] = stops.get(i).getCoordinate();
        }
        Route route = routingService.getRoute(coordinates);
        int durationFromStart = 0;
        for (int i = 0; i < stops.size(); i++) {
            PassengerStop stop = stops.get(i);
            Leg leg = route.legs.get(i);
            durationFromStart += leg.duration + Simulator.TAXI_STOP_DELAY;
            stop.setPlannedArrival(Simulator.CURRENT_TIME.plusSeconds(durationFromStart));
            stop.setPlannedDistance((int) leg.distance);
        }
        this.stops = stops;
        List<PlanPoint> routePlanPoints = route.getRoutePlanByDeltaSeconds(Simulator.TIME_DELTA);//get route points for the route
        this.routePlan.setPoints(routePlanPoints);
        lastRoutePosition = routePlanPoints.get(routePlanPoints.size() - 1).getCoordinate();
    }


    public Coordinate getPosition() {
        return getPositionAtTime(Simulator.TIME_FROM_START);
    }

    public Coordinate getPositionAtTime(int time) {
        if (routePlan.hasStopsAhead()) {
            return routePlan.getPositionAtTime(time);
        } else {
            return lastRoutePosition == null ? initialPosition : lastRoutePosition;
        }
    }

    public boolean isServing() {
        return getStops().size() > 0;
    }

    public int getPassengersOnBoard() {
        return passengersOnBoard;
    }

    @Override
    public String toString() {
        return "Taxi{" +
                "id=" + id +
                " position: " + getPosition() +
                " serving: " + isServing() +
                " passengersOnBoard: " + passengersOnBoard +
                '}';
    }


    public int getId() {
        return id;
    }

    public List<PassengerStop> getAllStops() {
        stopsHistory.addAll(stops);
        return stopsHistory;
    }
}
