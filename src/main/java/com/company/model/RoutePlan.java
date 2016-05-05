package com.company.model;

import com.company.Coordinator;

import java.util.List;

/**
 * Created by frox on 5.5.16.
 */
public class RoutePlan {
    private List<PlanPoint> points;
    private long startTime;

    public RoutePlan() {
        startTime = Coordinator.TIME;
    }

    public List<PlanPoint> getPoints() {
        return points;
    }

    public void setPoints(List<PlanPoint> points) {
        this.points = points;
    }

    public long getStartTime() {
        return startTime;
    }

    public Coordinate getPositionAtTime(long time) {
        int index = (int) Math.floor(Coordinator.TIME / Coordinator.TIME_DELTA);
        if (points.size() <= index) {
            return points.get(points.size() - 1).getCoordinate();
        } else {
            return points.get((int) Math.floor(Coordinator.TIME / Coordinator.TIME_DELTA)).getCoordinate();
        }

    }
}
