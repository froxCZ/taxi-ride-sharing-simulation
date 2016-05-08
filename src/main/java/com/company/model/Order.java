package com.company.model;

import com.company.simulator.Coordinator;
import org.joda.time.DateTime;

/**
 * Created by frox on 7.5.16.
 */
public class Order {
    private final DurationAndDistance directRouteDurationAndDistance;
    long orderId,rideId;
    Coordinate pickup,destination;
    DateTime orderedAt, latestPickup;

    public Order(long orderId, Coordinate pickup, Coordinate destination, DateTime orderedAt, DurationAndDistance durationAndDistance) {
        this.orderId = orderId;
        this.pickup = pickup;
        this.destination = destination;
        this.orderedAt = orderedAt;
        this.latestPickup = orderedAt.plusSeconds(Coordinator.MAX_PICKUP_DURATION);
        this.directRouteDurationAndDistance = durationAndDistance;
    }

    public long getOrderId() {
        return orderId;
    }


    public Coordinate getPickup() {
        return pickup;
    }

    public Coordinate getDestination() {
        return destination;
    }

    public DateTime getOrderedAt() {
        return orderedAt;
    }

    public DateTime getLatestPickup() {
        return latestPickup;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", pickup=" + pickup +
                ", destination=" + destination +
                ", orderedAt=" + orderedAt +
                '}';
    }


    public int getDirectRouteDurationAndDistance() {
        return directRouteDurationAndDistance.duration;
    }

    public int getDirectRouteDistance() {
        return directRouteDurationAndDistance.distance;
    }
}
