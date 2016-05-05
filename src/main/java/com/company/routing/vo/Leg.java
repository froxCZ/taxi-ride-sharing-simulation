package com.company.routing.vo;

import com.company.model.Coordinate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frox on 5.5.16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Leg {
    public List<Step> steps;
    public double duration;
    public double distance;



    public double sumDuration() {
        double sum = 0;
        for (Step s : steps) {
            sum += s.duration;
        }
        return sum;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Step s : steps) {
            stringBuilder.append(s.toString() + ";");
        }
        return stringBuilder.toString();
    }
}
