package com.company.simulator;

import com.company.model.Coordinate;
import com.company.model.Order;
import com.company.model.Taxi;
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
    protected List<Taxi> findNearestTaxis(Coordinate coordinate, int maxDistance) {
        List<Taxi> availableTaxis = new ArrayList<>();
        for (Taxi taxi : coordinator.getTaxiList()) {
            if (routingService.getDurationAndDistanceFast(taxi.getPosition(), coordinate).distance < maxDistance) {
                availableTaxis.add(taxi);
            }
        }
        return availableTaxis;
    }
}
