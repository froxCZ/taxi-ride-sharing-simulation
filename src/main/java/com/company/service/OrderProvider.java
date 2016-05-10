package com.company.service;

import com.company.simulator.Simulator;
import com.company.model.Coordinate;
import com.company.model.Order;
import com.company.util.Util;
import org.joda.time.DateTime;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which provides rides in given time
 */
public class OrderProvider implements Simulator.CoordinatorTimeListener {
    Simulator simulator;
    private Connection conn;
    List<Order> allOrders = new ArrayList<>();
    int lastProvidedOrderIndex = 0;
    private RoutingService routingService = RoutingService.getInstance();

    public OrderProvider(Simulator simulator) {
        this.simulator = simulator;
        init();
    }

    public OrderProvider() {
        init();
    }

    public List<Order> getAllOrders() {
        return allOrders;
    }

    private void init() {
        initDb();
        loadAllOrdersForSimulation();
        //createTestOrders();
    }

    private void createTestOrders() {
        Order order;
        Coordinate pickup = new Coordinate(50.048756, 14.431567);
        Coordinate destination = new Coordinate(50.047213, 14.439527);
        order = new Order(1, pickup, destination, Simulator.START_TIME.plusSeconds(5), routingService.getDurationAndDistance(pickup, destination), 1);
        allOrders.add(order);

        pickup = new Coordinate(50.048811, 14.434184);
        destination = new Coordinate(50.045539, 14.439227);
        order = new Order(2, pickup, destination, Simulator.START_TIME.plusSeconds(15), routingService.getDurationAndDistance(pickup, destination), 1);
        allOrders.add(order);
    }

    /**
     * load all data at one time. Its much faster.
     */
    private void loadAllOrdersForSimulation() {
        try {
            // our SQL SELECT query.
            // if you only need a few columns, specify them by name instead of using "*"
            String query = "SELECT * FROM orders " +
                    "WHERE orderedAt BETWEEN '" + Util.getDateTimeFormatter().print(Simulator.START_TIME) + "' AND '" + Util.getDateTimeFormatter().print(Simulator.END_TIME) + "' " +
                    "AND completionState IS NOT NULL ORDER BY orderedAt";
            System.out.println(query);
            // create the java statement
            Statement st = conn.createStatement();

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            // iterate through the java resultset
            while (rs.next()) {
                Order order;
                Coordinate pickup = new Coordinate(rs.getDouble("requestedPickupLat"), rs.getDouble("requestedPickupLon"));
                Coordinate destination = new Coordinate(rs.getDouble("requestedDestinationLat"), rs.getDouble("requestedDestinationLon"));
                order = new Order(rs.getBigDecimal("orderId").longValue(),
                        pickup,
                        destination,
                        new DateTime(rs.getTimestamp("orderedAt")),
                        routingService.getDurationAndDistance(pickup, destination),
                        rs.getInt("passengersCount")
                );
                allOrders.add(order);
            }
            st.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initDb() {
        // create our mysql database connection
        String myDriver = "com.mysql.jdbc.Driver";
        String myUrl = "jdbc:mysql://localhost/ridesharing?characterEncoding=UTF-8";
        try {
            Class.forName(myDriver);
            conn = DriverManager.getConnection(myUrl, "root", "mysql");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * when time changes, the order provider sends all rides at that time to simulator
     * @param fromTime
     * @param toTime
     */
    @Override
    public void onTimeChanged(DateTime fromTime, DateTime toTime) {
        List<Order> ordersInTimeRange = getOrdersInTimeRange(fromTime, toTime);
        if (simulator != null && ordersInTimeRange.size() > 0) {
            for (Order order : ordersInTimeRange) {
                simulator.onNewRideRequest(order);
            }
        }

    }

    public List<Order> getOrdersInTimeRange(DateTime fromTime, DateTime toTime) {
        int orderIndex = lastProvidedOrderIndex;
        Order order;
        List<Order> ordersInRange = new ArrayList<>();
        while (true) {
            if (orderIndex < allOrders.size()) {
                order = allOrders.get(orderIndex++);
            } else {
                break;
            }
            if (order.getOrderedAt().isAfter(toTime)) {
                break;
            }
            if (order.getOrderedAt().isAfter(fromTime)) {
                ordersInRange.add(order);
                lastProvidedOrderIndex = orderIndex;
            }

        }
        return ordersInRange;
    }
}
