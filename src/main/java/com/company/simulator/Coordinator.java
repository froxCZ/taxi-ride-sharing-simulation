package com.company.simulator;

import com.company.model.Coordinate;
import com.company.model.Order;
import com.company.model.Taxi;
import com.company.service.OrderProvider;
import com.company.service.RoutingService;
import com.company.util.Util;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frox on 5.5.16.
 */
public class Coordinator {
    public static final Integer MAX_PICKUP_DURATION = 60 * 10;//6min.. cca 5km
    public static final Integer TAXI_STOP_DELAY = 60 * 1;//
    public static final double MAX_DETOUR_MULTIPLICATION = 1.5;
    public static final int TAXI_CAPACITY = 4;
    public static final int TIME_DELTA = 20;
    public static final double PRICE_PER_KM = 28.0;
    public static int TIME_FROM_START = 0;
    public static DateTime START_TIME = Util.getDateTimeFormatter().parseDateTime("2016-04-29 17:00:00");
    public static DateTime CURRENT_TIME = START_TIME;
    public static DateTime END_TIME = Util.getDateTimeFormatter().parseDateTime("2016-04-30 00:02:00");
    public static int TAXI_COUNT = InitialData.getTaxiPositions().size();
    public Statistics statistics = new Statistics(this);
    List<Taxi> taxiList = new ArrayList<>();
    OrderProvider orderProvider;
    OrderTaxiMatcher orderTaxiMatcher;
    public Coordinator() {
        orderProvider = new OrderProvider(this);
        //orderTaxiMatcher = new SimpleOrderTaxiMatcher(this);
        orderTaxiMatcher = new RideShareOrderTaxiMatcher(this);
    }

    public void runSimulation() {
        beforeSimulation();
        //printSnapshot();
        printSimulatorDetails();
        while (START_TIME.plusSeconds(TIME_FROM_START).isBefore(END_TIME)) {
            nextSimulationStep();
        }
        afterSimulation();
    }

    private void afterSimulation() {
        statistics.createStatisticsData();

    }

    private void printSimulatorDetails() {
        System.out.println("********");
        System.out.println("Starting simulation");
        System.out.println("Taxis: " + TAXI_COUNT);
        System.out.println("OrderTaximatcher: " + orderTaxiMatcher.getClass().getSimpleName());
        System.out.println("********\n");
    }

    public void printSnapshot() {
        System.out.println("*** snapshot at " + CURRENT_TIME + " ****");
        for (Taxi t : taxiList) {
            System.out.println(t);
        }
        System.out.println("*****");
    }

    private void beforeSimulation() {
        createTaxis();
    }

    private void createTaxis() {
        List<double[]> taxiPositionsData = InitialData.getTaxiPositions();
        for (int i = 0; i < TAXI_COUNT; i++) {
            Coordinate position = new Coordinate(taxiPositionsData.get(i)[0], taxiPositionsData.get(i)[1]);
            taxiList.add(new Taxi(position));
        }
    }

    private void nextSimulationStep() {
        moveTime(TIME_DELTA);
    }

    private void moveTime(int seconds) {
        orderProvider.onTimeChanged(CURRENT_TIME, CURRENT_TIME.plusSeconds(seconds));
        TIME_FROM_START += seconds;
        CURRENT_TIME = CURRENT_TIME.plusSeconds(seconds);
    }

    public void onNewRideRequest(Order order) {

        orderTaxiMatcher.matchOrderToTaxi(order);
    }

    public List<Taxi> getTaxiList() {
        return taxiList;
    }

    public static void printCurrentTime() {
        System.out.println("**** current time is " + CURRENT_TIME);
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public static interface CoordinatorTimeListener {

        void onTimeChanged(DateTime oldTime, DateTime newTime);
    }

    public void test() {
//        Taxi taxi;
//        Ride[] rides = {
//                new Ride(new Coordinate(50.098704, 14.366922), new Coordinate(50.093772, 14.438834)),
//                new Ride(new Coordinate(50.055881, 14.425281), new Coordinate(50.093986, 14.456501)),
//                new Ride(new Coordinate(50.087113, 14.428889), new Coordinate(50.088880, 14.467193)),
//        };
//        for (Ride ride : rides) {
//            taxi = new Taxi();
//            taxi.addRide(ride);
//            taxiList.add(taxi);
//        }
    }
}
