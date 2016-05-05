package com.company.routing.vo;

import com.company.model.Coordinate;
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

    public Leg getLeg() {
        return legs.get(0);
    }

    public List<Coordinate> getRoutePlanByDeltaSeconds(int secondsDelta) {
        List<Coordinate> coordinatesByDeltaSeconds = new ArrayList<Coordinate>();
        double time = 0;
        double lastRecordedCoordinateTime = Integer.MIN_VALUE + 100;
        List<Step> steps = getLeg().steps;
        for (int i = 0; i < steps.size(); i++) {
            Step step = steps.get(i);
            double coordinateDelta = (step.duration / step.getCoordinates().size());
            for (Double[] c : step.getCoordinates()) {
                if ((time - lastRecordedCoordinateTime) > secondsDelta ||
                        (lastRecordedCoordinateTime - (coordinatesByDeltaSeconds.size() - 1) * secondsDelta) > secondsDelta //for smoothing the planned route in time so its lenght corresponds to its duration
                        ) {
                    lastRecordedCoordinateTime = time;
                    coordinatesByDeltaSeconds.add(new Coordinate(c[1], c[0]));
                    //System.out.println("realt: " + (int) lastRecordedCoordinateTime + " \t calculatedTime: " + (coordinatesByDeltaSeconds.size() - 1) * secondsDelta);
                }
                time += coordinateDelta;
            }

        }
        return coordinatesByDeltaSeconds;

    }
}
