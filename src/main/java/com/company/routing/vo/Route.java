package com.company.routing.vo;

import com.company.model.Coordinate;
import com.company.model.PlanPoint;
import com.company.model.RoutePoint;
import com.company.simulator.Simulator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frox on 5.5.16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Route {
    public double duration;
    public double distance;
    public List<Leg> legs;

    /**
     * creates a list of future taxi positions. Positions at index i and i+1 are delta seconds apart.
     * @param secondsDelta
     * @return
     */
    public List<PlanPoint> getRoutePlanByDeltaSeconds(int secondsDelta) {
        List<PlanPoint> coordinatesByDeltaSeconds = new ArrayList<PlanPoint>();
        double time = 0;
        double lastRecordedCoordinateTime = Integer.MIN_VALUE + 100;
        for (Leg leg : legs) {//one leg is a route between two stops
            List<Step> steps = leg.steps;
            for (int i = 0; i < steps.size(); i++) {
                Step step = steps.get(i);
                double coordinateDelta = (step.duration / step.getCoordinates().size());
                for (Double[] c : step.getCoordinates()) {
                    if ((time - lastRecordedCoordinateTime) > secondsDelta ||
                            (lastRecordedCoordinateTime - (coordinatesByDeltaSeconds.size() - 1) * secondsDelta) > secondsDelta //for smoothing the planned route in time so its lenght corresponds to its duration
                            ) {
                        lastRecordedCoordinateTime = time;
                        coordinatesByDeltaSeconds.add(new RoutePoint(new Coordinate(c[1], c[0])));
                    }
                    time += coordinateDelta;
                }
            }
            if (steps.size() > 0) {
                //add route points for waiting time at each stop
                RoutePoint lastRoutePoint = (RoutePoint) coordinatesByDeltaSeconds.get(coordinatesByDeltaSeconds.size() - 1);
                for (int i = 0; i < Math.floor(Simulator.TAXI_STOP_DELAY / Simulator.TIME_DELTA); i++) {
                    coordinatesByDeltaSeconds.add(lastRoutePoint);
                }
            }
        }
        return coordinatesByDeltaSeconds;

    }
}
