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

//    /**
//     * add the order to taxis plan
//     *
//     * @param order
//     * @param taxi
//     */
//    private void addOrderToTaxi(Order order, Taxi taxi) {
//        if (taxi.isServing()) throw new RuntimeException("can only add to empty taxis!");
//        Route r = OsrmClient.getRoute(taxi.getPosition(), order.getPickup(), order.getDestination());
//        taxi.getRoutePlan().setPoints(r.getRoutePlanByDeltaSeconds(Simulator.TIME_DELTA));
//        //first leg is to pickup, second is to destination
//        taxi.addNonPaidMeters((int) r.legs.get(0).distance);
//        taxi.addPaidMeters((int) r.legs.get(1).distance);
//        System.out.println("taxi " + taxi.getId() + " will server " + r.duration + " going from " + taxi.getPosition() + " to " + order);
//    }
}
