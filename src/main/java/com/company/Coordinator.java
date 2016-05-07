package com.company;

import com.company.model.Coordinate;
import com.company.model.Order;
import com.company.model.Ride;
import com.company.model.Taxi;
import com.company.service.OrderProvider;
import com.company.util.Util;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frox on 5.5.16.
 */
public class Coordinator {
    private static final Integer MAX_PICKUP_DISTANCE = 2000;
    public static int TIME_FROM_START = 0;
    public static final int TIME_DELTA = 20;
    public static DateTime START_TIME = Util.getDateTimeFormatter().parseDateTime("2016-04-29 17:00:00");
    public static DateTime CURRENT_TIME = START_TIME;
    public static DateTime END_TIME = Util.getDateTimeFormatter().parseDateTime("2016-04-30 03:00:00");
    List<Taxi> taxiList = new ArrayList<>();
    OrderProvider orderProvider;
    public Coordinator() {
        orderProvider = new OrderProvider(this);
    }

    public void runSimulation() {
        while (START_TIME.plusSeconds(TIME_FROM_START).isBefore(END_TIME)) {
            nextSimulationStep();
        }

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

    private void nextSimulationStep() {
        moveTime(TIME_DELTA);
    }

    private void moveTime(int seconds) {
        TIME_FROM_START += seconds;
        CURRENT_TIME = CURRENT_TIME.plusSeconds(seconds);
        orderProvider.onTimeChanged(CURRENT_TIME, CURRENT_TIME.plusSeconds(seconds));
    }

    public void onNewRideRequest(Order order) {
        System.out.println(order);
//        for (Taxi nearestTaxi : findNearestTaxis(order.getPickup(), Coordinator.MAX_PICKUP_DISTANCE)) {
//            System.out.println(nearestTaxi.toString());
//        }
    }

    public static interface CoordinatorTimeListener {
        void onTimeChanged(DateTime oldTime, DateTime newTime);
    }
}
