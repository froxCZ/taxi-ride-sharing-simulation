package com.company.model;

import java.io.Serializable;

/**
 * Created by frox on 5.5.16.
 */
public class DurationAndDistance  implements Serializable{
    public int duration, distance;
    private static final long serialVersionUID = 1L;

    public DurationAndDistance(int duration, int distance) {
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
