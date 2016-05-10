package com.company.simulator;

import com.company.model.Order;
import com.company.model.Taxi;

/**
 * Created by frox on 7.5.16.
 */
public class SimpleOrderTaxiMatcher extends OrderTaxiMatcher {

    public SimpleOrderTaxiMatcher(Simulator simulator) {
        super(simulator);
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
        for (Taxi taxiInNeighbourghood : findNearestTaxis(order.getPickup(), Simulator.MAX_PICKUP_DURATION)) {
            if (!taxiInNeighbourghood.isServing()) {
                int distance = (int) routingService.getDurationAndDistanceFast(order.getPickup(), taxiInNeighbourghood.getPosition()).distance;
                if (distance < nearestTaxiDistance) {
                    nearestTaxiDistance = distance;
                    nearestTaxi = taxiInNeighbourghood;
                }
            }
        }
        if (nearestTaxi == null) {
            noAvailableTaxi(order);
        } else {
            addOrderToEmptyTaxi(order, nearestTaxi);
        }
    }
}
