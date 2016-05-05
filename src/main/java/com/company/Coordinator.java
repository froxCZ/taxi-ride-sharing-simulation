package com.company;

import com.company.model.Coordinate;
import com.company.model.Ride;
import com.company.model.Taxi;

/**
 * Created by frox on 5.5.16.
 */
public class Coordinator {
    public static long TIME = 0;
    public static final int TIME_DELTA = 20;

    public Coordinator() {

    }

    public void test() {
        Taxi a = new Taxi();
        a.addRide(new Ride(new Coordinate(50.098704, 14.366922), new Coordinate(50.093772, 14.438834)));
        moveTime(120);
        System.out.println(a.getPosition());

    }

    private void moveTime(int seconds) {
        TIME += seconds;
    }
}
