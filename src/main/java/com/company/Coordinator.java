package com.company;

import com.company.model.Coordinate;
import com.company.model.Ride;
import com.company.model.Taxi;
import com.company.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frox on 5.5.16.
 */
public class Coordinator {
    private static final Integer MAX_PICKUP_DISTANCE = 2000;
    public static long TIME = 0;
    public static final int TIME_DELTA = 20;
    List<Taxi> taxiList = new ArrayList<>();

    public Coordinator() {

    }

    public void test() {
        Taxi taxi;
        Ride[] rides = {
                new Ride(new Coordinate(50.098704, 14.366922), new Coordinate(50.093772, 14.438834)),
                new Ride(new Coordinate(50.055881, 14.425281), new Coordinate(50.093986, 14.456501)),
                new Ride(new Coordinate(50.087113, 14.428889), new Coordinate(50.088880, 14.467193)),
        };
        for (Ride ride : rides) {
            taxi = new Taxi();
            taxi.addRide(ride);
            taxiList.add(taxi);
        }
        serverIncomingRideRequest(new Ride(new Coordinate(50.056079, 14.420554), new Coordinate(50.071360, 14.384753)));
        moveTime(1200);

    }

    private void serverIncomingRideRequest(Ride ride) {
        for (Taxi nearestTaxi : findNearestTaxis(ride.getPickup(), Coordinator.MAX_PICKUP_DISTANCE)) {
            System.out.println(nearestTaxi.toString());
        }

    }

    /**
     * find all taxis up to maxDistance at current time
     *
     * @param coordinate
     * @return
     */
    private List<Taxi> findNearestTaxis(Coordinate coordinate, int maxDistance) {
        List<Taxi> availableTaxis = new ArrayList<>();
        for (Taxi taxi : taxiList) {
            if (Util.distance(taxi.getPosition(), coordinate) < maxDistance) {
                availableTaxis.add(taxi);
            }
        }
        return availableTaxis;
    }

    private void moveTime(int seconds) {
        TIME += seconds;
    }
}
