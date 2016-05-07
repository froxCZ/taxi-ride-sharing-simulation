package com.company.model;

import com.company.simulator.Coordinator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frox on 5.5.16.
 */
public class RoutePlan {
    private List<PlanPoint> points = new ArrayList<>();
    private long startTime;
    int pointsStartTime = 0;

    public RoutePlan() {
        startTime = Coordinator.TIME_FROM_START;
    }

    public List<PlanPoint> getPoints() {
        return points;
    }

    public void setPoints(List<PlanPoint> points) {
        pointsStartTime = Coordinator.TIME_FROM_START;
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
        return (int) Math.floor((Coordinator.TIME_FROM_START - pointsStartTime) / Coordinator.TIME_DELTA);
    }

    public boolean hasStopsAhead() {
        int routePlanIndex = getRoutePlanIndex();
        return points.size() > routePlanIndex;
    }
}
