package com.company.simulator;

import com.company.model.Order;
import com.company.model.RoutePlan;
import com.company.model.Taxi;
import com.company.routing.OsrmClient;
import com.company.routing.vo.Route;
import com.company.service.RoutingService;

/**
 * Created by frox on 7.5.16.
 */
public class SimpleOrderTaxiMatcher extends OrderTaxiMatcher {

    public SimpleOrderTaxiMatcher(Coordinator coordinator) {
        super(coordinator);
    }

    /**
     * finds the nearest available taxi and add this order to its plan
     *
     * @param order
     */
    @Override
    public void matchOrderToTaxi(Order order) {
        Taxi nearestTaxi = null;
        int nearestTaxiDistance = Integer.MAX_VALUE;
        for (Taxi taxiInNeighbourghood : findNearestTaxis(order.getPickup(), Coordinator.MAX_PICKUP_DISTANCE)) {
            if (!taxiInNeighbourghood.isServing()) {
                int distance = (int) routingService.getDurationAndDistanceFast(order.getPickup(), taxiInNeighbourghood.getPosition()).distance;
                if (distance < nearestTaxiDistance) {
                    nearestTaxiDistance = distance;
                    nearestTaxi = taxiInNeighbourghood;
                }
            }
        }
        if (nearestTaxi == null) {
            System.out.println("did not find available taxi");
        } else {
            addOrderToTaxi(order, nearestTaxi);
        }
    }

    /**
     * add the order to taxis plan
     *
     * @param order
     * @param taxi
     */
    private void addOrderToTaxi(Order order, Taxi taxi) {
        if (taxi.isServing()) throw new RuntimeException("can only add to empty taxis!");
        Route r = OsrmClient.getRoute(taxi.getPosition(), order.getPickup(), order.getDestination());
        taxi.getRoutePlan().setPoints(r.getRoutePlanByDeltaSeconds(Coordinator.TIME_DELTA));
        System.out.println("taxi " + taxi.getId() + " will server " + r.duration + " going from " + taxi.getPosition() + " to " + order);
    }
}
