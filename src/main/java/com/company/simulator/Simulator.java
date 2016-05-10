package com.company.simulator;

import com.company.model.Coordinate;
import com.company.model.Order;
import com.company.model.Taxi;
import com.company.service.OrderProvider;
import com.company.statistics.Statistics;
import com.company.util.Util;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Main simulator calss
 */
public class Simulator {
    public static final Integer MAX_PICKUP_DURATION = 60 * 10;//6min.. cca 5km
    public static final Integer TAXI_STOP_DELAY = 60 * 2;//
    public static final double MAX_DETOUR_MULTIPLICATION = 1.9;
    public static final int TAXI_CAPACITY = 4;
    public static final int TIME_DELTA = 20;
    public static final double PRICE_PER_KM = 28;
    OrderTaxiMatcher orderTaxiMatcher = new RideShareOrderTaxiMatcher(this);//new SimpleOrderTaxiMatcher(this)
    public static int TIME_FROM_START = 0;
    public static DateTime START_TIME = Util.getDateTimeFormatter().parseDateTime("2016-04-29 17:00:00");
    public static DateTime CURRENT_TIME = START_TIME;
    public static DateTime END_TIME = Util.getDateTimeFormatter().parseDateTime("2016-04-30 00:02:00");

    public static int TAXI_COUNT = InitialData.getTaxiPositions().size();
    public Statistics statistics = new Statistics(this);
    List<Taxi> taxiList = new ArrayList<>();
    OrderProvider  orderProvider = new OrderProvider(this);;

    public void runSimulation() {
        createTaxis();
        printSimulatorDetails();
        while (START_TIME.plusSeconds(TIME_FROM_START).isBefore(END_TIME)) {
            nextSimulationStep();
        }
        statistics.createStatisticsData();
    }

    private void printSimulatorDetails() {
        System.out.println("********");
        System.out.println("Starting simulation");
        System.out.println("Taxis: " + TAXI_COUNT);
        System.out.println("OrderTaximatcher: " + orderTaxiMatcher.getClass().getSimpleName());
        System.out.println("********\n");
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

    /**
     * moves the time forward and calls order provider about the time change
     * @param seconds
     */
    private void moveTime(int seconds) {
        orderProvider.onTimeChanged(CURRENT_TIME, CURRENT_TIME.plusSeconds(seconds));
        TIME_FROM_START += seconds;
        CURRENT_TIME = CURRENT_TIME.plusSeconds(seconds);
    }

    /**
     * let the order taxi matcher handle the order
     * @param order
     */
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
}
