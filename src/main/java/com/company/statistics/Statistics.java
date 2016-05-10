package com.company.statistics;

import com.company.model.DurationAndDistance;
import com.company.model.Order;
import com.company.model.PassengerStop;
import com.company.model.Taxi;
import com.company.service.RoutingService;
import com.company.simulator.Simulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class which collects statistics after running the simulation
 */
public class Statistics {
    private int TOTAL_EARNINGS = 0;
    public int SINGLE_RIDES = 0;
    public int NOT_SERVED_RIDES = 0;
    public int TOTAL_DETOUR = 0;
    Simulator simulator;
    private RoutingService routingService = RoutingService.getInstance();
    private int RIDES = 0;
    private int PICKUP_DURATION;
    private int RIDE_DURATION = 0;
    private int RIDE_DIRECT_DISTANCE;
    private int RIDE_REAL_DISTANCE = 0;
    private int NUMBER_OF_RIDES = 0;
    private double AVG_PAS_ON_BOARD;
    private int TRAVELED_TOTAL_DISTANCE = 0;

    public Statistics(Simulator simulator) {
        this.simulator = simulator;
    }

    public void createStatisticsData() {
        int usedTaxis = 0;
        for (Taxi taxi : simulator.getTaxiList()) {
            createStatisticsDataForTaxi(taxi);
        }
        System.out.println("route query counter: "+RoutingService.COUNTER);
        System.out.println("\nrides(shared): " + RIDES + "(" + (RIDES - SINGLE_RIDES) + ") not served:" + NOT_SERVED_RIDES +
                        "\ntotalEarning: " + TOTAL_EARNINGS +
                        "\npaidDistance/totalDistance: " + RIDE_DIRECT_DISTANCE *1.0/TRAVELED_TOTAL_DISTANCE +
                        "\navgDistancePerKm: " + (RIDE_REAL_DISTANCE * 1.0) / NUMBER_OF_RIDES + " " +
                        "\nearning/km: " + ((RIDE_DIRECT_DISTANCE * Simulator.PRICE_PER_KM * 1.0) / TRAVELED_TOTAL_DISTANCE) +
                        "\navgTimeToPickup: " + (PICKUP_DURATION / RIDES) + "sec" +
                        "\naverageDetourExtension: " + ((RIDE_REAL_DISTANCE * 1.0) / RIDE_DIRECT_DISTANCE) + "sec" +
                        "\navgPasOnBoard: " + AVG_PAS_ON_BOARD + "" +
                        "\nnumOfRides" + NUMBER_OF_RIDES
        );
    }

    private void createStatisticsDataForTaxi(Taxi taxi) {
        List<PassengerStop> stopsHistory = taxi.getAllStops();
        PassengerStop stop, nextStop = null;
        int distanceFromBeginning = 0;
        HashMap<Long, Integer> distanceFromBeginningMap = new HashMap<>();
        HashMap<Long, Order> ordersEnRoute = new HashMap<>();
        //System.out.println("taxi " + taxi.getId());
        int earnings = 0;
        int sumOfPasOnBoard = 0;
        int sumOfPasOnBoardOccurences = 0;
        if (stopsHistory.size() > 0) {
            int distance;
            distance = routingService.getDurationAndDistance(taxi.getInitialPosition(), stopsHistory.get(0).getCoordinate()).distance;
            TRAVELED_TOTAL_DISTANCE += distance;
            DurationAndDistance durationAndDistance = null;
            for (int i = 0; i < stopsHistory.size(); i++) {//loop through all stops of taxi and calculate the stats
                stop = stopsHistory.get(i);
                if (i < stopsHistory.size() - 1) {
                    nextStop = stopsHistory.get(i + 1);
                    durationAndDistance = routingService.getDurationAndDistance(stop.getCoordinate(), nextStop.getCoordinate());
                    distance = durationAndDistance.distance;
                } else {
                    distance = 0;//it is last stop.
                }
                TRAVELED_TOTAL_DISTANCE += distance;
                if (stop.getType() == PassengerStop.Type.PICKUP) {
                    ordersEnRoute.put(stop.getOrder().getOrderId(), stop.getOrder());
                    RIDES++;
                    if (stop.getDestinationStop().equals(nextStop)) {
                        SINGLE_RIDES++;
                    }
                    distanceFromBeginningMap.put(stop.getOrder().getOrderId(), distanceFromBeginning);
                    PICKUP_DURATION += durationAndDistance.duration;

                } else {
                    int realDistance = distanceFromBeginning - distanceFromBeginningMap.get(stop.getOrder().getOrderId());
                    int detour = realDistance - stop.getOrder().getDirectRouteDistance();
                    TOTAL_EARNINGS += (stop.getOrder().getDirectRouteDistance() / 1000.0) * Simulator.PRICE_PER_KM;
                    TOTAL_DETOUR += detour;
                    RIDE_REAL_DISTANCE += realDistance;
                    RIDE_DIRECT_DISTANCE += stop.getOrder().getDirectRouteDistance();
                    ordersEnRoute.remove(stop.getOrder().getOrderId());
                    NUMBER_OF_RIDES++;
                }
                for (Map.Entry<Long, Order> orderEnRoute : ordersEnRoute.entrySet()) {
                    sumOfPasOnBoard += orderEnRoute.getValue().getPassengersCount();
                    sumOfPasOnBoardOccurences++;
                }
                distanceFromBeginning += distance;
                if (ordersEnRoute.size() > Simulator.TAXI_CAPACITY) {
                    System.out.println("BIG RIDE SHARE: " + ordersEnRoute.size() + " " + ordersEnRoute);
                }
            }
            AVG_PAS_ON_BOARD = (sumOfPasOnBoard * 1.0) / sumOfPasOnBoardOccurences;
        }
    }
}
