package com.company.model;

import java.io.Serializable;

/**
 * Created by frox on 5.5.16.
 */
public class DurationAndDistance  implements Serializable{
    public double duration, distance;
    private static final long serialVersionUID = 1L;

    public DurationAndDistance(double duration, double distance) {
        this.duration = duration;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "DurationAndDistance{" +
                "duration=" + duration +
                ", distance=" + distance +
                '}';
    }
}
