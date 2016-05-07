package com.company.service;

import com.company.simulator.Coordinator;
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
 * Created by frox on 7.5.16.
 */
public class OrderProvider implements Coordinator.CoordinatorTimeListener {
    Coordinator coordinator;
    private Connection conn;
    List<Order> allOrders = new ArrayList<>();
    int lastProvidedOrderIndex = 0;

    public OrderProvider(Coordinator coordinator) {
        this.coordinator = coordinator;
        init();
    }

    public OrderProvider() {
        init();
    }

    private void init() {
        initDb();
        loadAllOrdersForSimulation();
    }

    private void loadAllOrdersForSimulation() {
        try {
            // our SQL SELECT query.
            // if you only need a few columns, specify them by name instead of using "*"
            String query = "SELECT * FROM orders " +
                    "WHERE orderedAt BETWEEN '" + Util.getDateTimeFormatter().print(Coordinator.START_TIME) + "' AND '" + Util.getDateTimeFormatter().print(Coordinator.END_TIME) + "' " +
                    "AND completionState IS NOT NULL ORDER BY orderedAt";
            System.out.println(query);
            // create the java statement
            Statement st = conn.createStatement();

            // execute the query, and get a java resultset
            ResultSet rs = st.executeQuery(query);

            // iterate through the java resultset
            while (rs.next()) {
                Order order;
                order = new Order(rs.getBigDecimal("orderId"), rs.getBigDecimal("rideId"),
                        new Coordinate(rs.getDouble("requestedPickupLat"), rs.getDouble("requestedPickupLon")),
                        new Coordinate(rs.getDouble("requestedDestinationLat"), rs.getDouble("requestedDestinationLon")),
                        new DateTime(rs.getTimestamp("orderedAt"))
                );
                allOrders.add(order);
            }
            st.close();
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

    @Override
    public void onTimeChanged(DateTime fromTime, DateTime toTime) {
        List<Order> ordersInTimeRange = getOrdersInTimeRange(fromTime, toTime);
        if (coordinator != null && ordersInTimeRange.size() > 0) {
            Coordinator.printCurrentTime();
            for (Order order : ordersInTimeRange) {
                coordinator.onNewRideRequest(order);
            }
        }

    }

    public List<Order> getOrdersInTimeRange(DateTime fromTime, DateTime toTime) {
        int orderIndex = lastProvidedOrderIndex;
        Order order = allOrders.get(orderIndex);
        List<Order> ordersInRange = new ArrayList<>();
        while (order.getOrderedAt().isBefore(toTime)) {
            if (order.getOrderedAt().isAfter(fromTime)) {
                ordersInRange.add(order);
                lastProvidedOrderIndex = orderIndex - 1;
            }
            if(orderIndex < allOrders.size()) {
                order = allOrders.get(orderIndex++);
            }else{
                break;
            }
        }
        return ordersInRange;
    }
}
