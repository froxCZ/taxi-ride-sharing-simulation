package com.company.model;

import com.company.simulator.Simulator;
import org.joda.time.DateTime;

/**
 * Order, which is usually transformed into a ride
 */
public class Order {
    private final DurationAndDistance directRouteDurationAndDistance;
    long orderId;
    Coordinate pickup,destination;
    DateTime orderedAt, latestPickup;
    private int passengersCount;

    public Order(long orderId, Coordinate pickup, Coordinate destination, DateTime orderedAt, DurationAndDistance durationAndDistance, int passengersCount) {
        this.orderId = orderId;
        this.pickup = pickup;
        this.destination = destination;
        this.orderedAt = orderedAt;
        this.latestPickup = orderedAt.plusSeconds(Simulator.MAX_PICKUP_DURATION);
        this.directRouteDurationAndDistance = durationAndDistance;
        this.passengersCount= passengersCount;
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

    public void setPassengersCount(int passengersCount) {
        this.passengersCount = passengersCount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", pickup=" + pickup +
                ", destination=" + destination +
                ", orderedAt=" + orderedAt +
                ", passengerCount=" + passengersCount +
                '}';
    }


    public int getDirectRouteDuration() {
        return directRouteDurationAndDistance.duration;
    }

    public int getDirectRouteDistance() {
        return directRouteDurationAndDistance.distance;
    }

    public int getPassengersCount() {
        return passengersCount;
    }
}
