package com.company.model;

import com.company.simulator.Simulator;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of route points which are delta seconds from each other. Used for simulating taxi movement and quick calculation of taxi at given time
 */
public class RoutePlan {
    private List<PlanPoint> points = new ArrayList<>();
    private long startTime;
    int pointsStartTime = 0;

    public RoutePlan() {
        startTime = Simulator.TIME_FROM_START;
    }

    public List<PlanPoint> getPoints() {
        return points;
    }

    public void setPoints(List<PlanPoint> points) {
        pointsStartTime = Simulator.TIME_FROM_START;
        this.points = points;
    }

    public long getStartTime() {
        return startTime;
    }

    public Coordinate getPositionAtTime(long time) {
        if (!hasStopsAhead()) {
            return points.get(points.size() - 1).getCoordinate();
        } else {
            return points.get(getRoutePlanIndex()).getCoordinate();
        }
    }

    private int getRoutePlanIndex() {
        return (int) Math.floor((Simulator.TIME_FROM_START - pointsStartTime) / Simulator.TIME_DELTA);
    }

    public boolean hasStopsAhead() {
        int routePlanIndex = getRoutePlanIndex();
        return points.size() > routePlanIndex;
    }
}
