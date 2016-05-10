package com.company.statistics;

import com.company.routing.MapGrid;
import com.company.model.Coordinate;
import com.company.model.DurationAndDistance;
import com.company.model.Order;
import com.company.routing.OsrmClient;
import com.company.service.OrderProvider;
import com.company.util.Util;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frox on 10.5.16.
 */
public class GridStats {


    public static void testGrid() {
        OrderProvider orderProvider = new OrderProvider();
        orderProvider.getAllOrders();
        testWithOrders(orderProvider.getAllOrders());
        List<Order> randomList = new ArrayList<>();
        for (int i = 0; i < orderProvider.getAllOrders().size(); i++) {
            Order order = new Order(0, new Coordinate(Util.randomInRange(MapGrid.latMin, MapGrid.latMax), Util.randomInRange(MapGrid.lonMin, MapGrid.lonMax)), new Coordinate(Util.randomInRange(MapGrid.latMin, MapGrid.latMax), Util.randomInRange(MapGrid.lonMin, MapGrid.lonMax)), new DateTime(0), null, 0);
            randomList.add(order);
        }
        System.out.println("random:");
        testWithOrders(randomList);
        measureTime(orderProvider.getAllOrders());
        MapGrid.getInstance().saveGridMap();;
    }

    private static void measureTime(List<Order> orderList) {
        MapGrid mapGrid = MapGrid.getInstance();
        Util.timeMeasureStart();
        for (Order order : orderList) {
            OsrmClient.getDurationAndDistance(order.getPickup(),order.getDestination());
        }
        Util.timeMeasureStop();
        Util.timeMeasureStart();
        for (Order order : orderList) {
            mapGrid.getDurationAndDistance(order.getPickup(),order.getDestination());
        }
        Util.timeMeasureStop();
    }

    private static void testWithOrders(List<Order> orderList) {
        MapGrid mapGrid = MapGrid.getInstance();

        DurationAndDistance gridDurationAndDistance;
        DurationAndDistance osrmDurationAndDistance;
        int sumDurDiff = 0, sumDistDiff = 0;
        int maxDurationDiff = 0;
        int maxDistanceDiff = 0;
        int diffOverKm = 0;
        int diffOverMinute = 0;
        for (Order order : orderList) {
            //System.out.println(a + " " + b);
            gridDurationAndDistance = mapGrid.getDurationAndDistance(order.getPickup(), order.getDestination());
            osrmDurationAndDistance = OsrmClient.getDurationAndDistance(order.getPickup(), order.getDestination());
            int diffDur = (int) Math.abs(gridDurationAndDistance.duration - osrmDurationAndDistance.duration);
            int diffDist = (int) Math.abs(gridDurationAndDistance.distance - osrmDurationAndDistance.distance);
            sumDurDiff += diffDur;
            sumDistDiff += diffDist;
            if (diffDur > maxDurationDiff) {
                maxDurationDiff = diffDur;
            }
            if (diffDist > maxDistanceDiff) {
                maxDistanceDiff = diffDist;
            }
            if (diffDist > 1000) {
                diffOverKm++;
            }
            if (diffDur > 60) {

                diffOverMinute++;
            }
        }
        int ordersCount = orderList.size();
        System.out.println("maxDuration difference: " + maxDurationDiff);
        System.out.println("avg duration difference: " + sumDurDiff * 1.0 / ordersCount);
        System.out.println("difference over minute: " + diffOverMinute);
        System.out.println("maxDistance difference: " + maxDistanceDiff);
        System.out.println("avg distance difference: " + sumDistDiff * 1.0 / ordersCount);
        System.out.println("difference over 1km: " + diffOverKm);
        System.out.println("orders count: " + ordersCount);
    }
}
