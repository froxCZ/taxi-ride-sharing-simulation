package com.company.model;

/**
 * Created by frox on 5.5.16.
 */
public class PlanPoint {
    private Coordinate coordinate;

    public PlanPoint(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public PlanPoint() {
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public String toString() {
        return coordinate.toString();
    }
}
