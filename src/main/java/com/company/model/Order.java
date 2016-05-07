package com.company.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by frox on 7.5.16.
 */
public class Order {
    BigDecimal orderId,rideId;
    Coordinate pickup,destination;
    DateTime orderedAt;

    public Order(BigDecimal orderId, BigDecimal rideId, Coordinate pickup, Coordinate destination, DateTime orderedAt) {
        this.orderId = orderId;
        this.rideId = rideId;
        this.pickup = pickup;
        this.destination = destination;
        this.orderedAt = orderedAt;
    }

    public BigDecimal getOrderId() {
        return orderId;
    }

    public BigDecimal getRideId() {
        return rideId;
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

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", rideId=" + rideId +
                ", pickup=" + pickup +
                ", destination=" + destination +
                ", orderedAt=" + orderedAt +
                '}';
    }
}
