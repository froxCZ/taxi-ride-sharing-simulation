package com.company.simulator;

import com.company.grid.MapGrid;
import com.company.model.Order;
import com.company.model.PassengerStop;
import com.company.model.Taxi;
import com.company.service.RoutingService;

import java.util.HashMap;
import java.util.List;

/**
 * Created by frox on 8.5.16.
 */
public class Statistics {
    private int TOTAL_EARNINGS = 0;
    public int RIDE_SHARES = 0;
    public int NOT_SERVED_RIDES = 0;
    public int TOTAL_DETOUR = 0;
    Coordinator coordinator;
    private RoutingService routingService = RoutingService.getInstance();
    private int RIDES = 0;

    public Statistics(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public void createStatisticsData() {
        int paidMeters = 0, nonPaidMeters = 0;
        int usedTaxis = 0;
        for (Taxi taxi : coordinator.getTaxiList()) {
            createStatisticsDataForTaxi(taxi);
            System.out.println(taxi);
            paidMeters += taxi.getPaidMeters();
            nonPaidMeters += taxi.getNonPaidMeters();
            if (taxi.getPaidMeters() > 0) {
                usedTaxis++;
            }
        }
        System.out.println("usedTaxis: " + usedTaxis + " rides(shared): " + RIDES + "(" + RIDE_SHARES + ") not served:" + NOT_SERVED_RIDES +
                "\npaid/nonPaid: " + ((paidMeters * 1.0) / nonPaidMeters) +
                "\ntotalEarning: " + TOTAL_EARNINGS +
                "\ntotalKm: " + ((paidMeters + nonPaidMeters) / 1000.0 + " ") +
                "\nearning/km: " + (TOTAL_EARNINGS / ((paidMeters + nonPaidMeters) / 1000.0)));
    }

    private void createStatisticsDataForTaxi(Taxi taxi) {
        taxi.getStopsHistory().addAll(taxi.getStops());
        List<PassengerStop> stopsHistory = taxi.getStopsHistory();
        PassengerStop stop, nextStop;
        int distanceFromBeginning = 0;
        HashMap<Long, Integer> distanceFromBeginningMap = new HashMap<>();
        HashMap<Long, Order> ordersEnRoute = new HashMap<>();
        int earnings = 0;
        if (stopsHistory.size() > 0) {
            int distance;
            distance = routingService.getDurationAndDistance(taxi.getInitialPosition(), stopsHistory.get(0).getCoordinate()).distance;
            taxi.addNonPaidMeters(distance);
            for (int i = 0; i < stopsHistory.size() - 1; i++) {
                stop = stopsHistory.get(i);
                //System.out.println(stop.printForPlot());
                nextStop = stopsHistory.get(i + 1);
                if (stop.getType() == PassengerStop.Type.PICKUP) {
                    ordersEnRoute.put(stop.getOrder().getOrderId(), stop.getOrder());
                    RIDES++;
                    if (ordersEnRoute.size() > 1) {
                        RIDE_SHARES++;
                    }
                    distanceFromBeginningMap.put(stop.getOrder().getOrderId(), distanceFromBeginning);
                } else {
                    int realDistance = distanceFromBeginning - distanceFromBeginningMap.get(stop.getOrder().getOrderId());
                    int detour = realDistance - stop.getOrder().getDirectRouteDistance();
                    earnings += (stop.getOrder().getDirectRouteDistance() / 1000.0) * Coordinator.PRICE_PER_KM;
                    TOTAL_DETOUR += detour;
                    ordersEnRoute.remove(stop.getOrder().getOrderId());
                }
                distance = routingService.getDurationAndDistance(stop.getCoordinate(), nextStop.getCoordinate()).distance;
                distanceFromBeginning += distance;
                if (ordersEnRoute.size() > 0) {
                    taxi.addPaidMeters(distance);
                } else {
                    taxi.addNonPaidMeters(distance);
                }
                if (ordersEnRoute.size() > 3) {
                    System.out.println("BIG RIDE SHARE: " + ordersEnRoute);
                }
            }
            TOTAL_EARNINGS += earnings;
            //System.out.println(stopsHistory.get(stopsHistory.size() - 1).printForPlot());
        }
    }
}
