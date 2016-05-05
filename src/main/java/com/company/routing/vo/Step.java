package com.company.routing.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frox on 5.5.16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Step {
    public Geometry geometry;
    public double duration;
    public double distance;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Double[] coordinate : geometry.coordinates) {
            sb.append(coordinate[1] + "," + coordinate[0] + ";");
        }
        return sb.toString();
    }

    public List<Double[]> getCoordinates() {
        return geometry.coordinates;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Geometry {
        public List<Double[]> coordinates;
    }
}
