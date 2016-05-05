package com.company.model;

/**
 * Created by frox on 5.5.16.
 */
public class Ride {
    private Coordinate pickup;
    private Coordinate destination;

    public Ride(Coordinate pickup, Coordinate destination) {
        this.pickup = pickup;
        this.destination = destination;
    }

    public Coordinate getPickup() {
        return pickup;
    }

    public Coordinate getDestination() {
        return destination;
    }
}
