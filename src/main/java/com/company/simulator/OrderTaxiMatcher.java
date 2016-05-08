package com.company.simulator;

import com.company.model.Coordinate;
import com.company.model.Order;
import com.company.model.PassengerStop;
import com.company.model.Taxi;
import com.company.routing.vo.Route;
import com.company.service.RoutingService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frox on 7.5.16.
 */
public abstract class OrderTaxiMatcher {
    private final Coordinator coordinator;
    protected RoutingService routingService = RoutingService.getInstance();

    public OrderTaxiMatcher(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public abstract void matchOrderToTaxi(Order order);

    /**
     * find all taxis up to maxDistance at current time
     *
     * @param coordinate
     * @return
     */
    protected List<Taxi> findNearestTaxis(Coordinate coordinate, int maxDuration) {
        List<Taxi> availableTaxis = new ArrayList<>();
        for (Taxi taxi : coordinator.getTaxiList()) {
            if (routingService.getDurationAndDistanceFast(taxi.getPosition(), coordinate).duration < maxDuration) {
                availableTaxis.add(taxi);
            }
        }
        return availableTaxis;
    }

    protected void noAvailableTaxi(Order order) {
        System.out.println("did not find available taxi for order " + order);
        coordinator.getStatistics().NOT_SERVED_RIDES++;
    }

    protected void addOrderToEmptyTaxi(Order order, Taxi taxi) {
        if (taxi.isServing()) throw new RuntimeException("can only add to empty taxis!");
        List<PassengerStop> stops = taxi.getStops();
        PassengerStop pickup = new PassengerStop(order, PassengerStop.Type.PICKUP);
        PassengerStop destination = new PassengerStop(order, PassengerStop.Type.DESTINATION);
        pickup.setDestinationStop(destination);
        destination.setPickupStop(pickup);
        stops.add(0, pickup);
        stops.add(1, destination);
        taxi.setStops(stops);
//        //first leg is to pickup, second is to destination
//        taxi.addNonPaidMeters((int) r.legs.get(0).distance);
//        taxi.addPaidMeters((int) r.legs.get(1).distance);
    }

}
