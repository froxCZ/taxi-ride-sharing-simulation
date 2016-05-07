package com.company.grid;

import com.company.model.Coordinate;
import com.company.model.DurationAndDistance;
import com.company.model.Ride;
import com.company.routing.OsrmClient;
import com.company.service.RoutingService;
import com.company.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Created by frox on 5.5.16.
 */
public class MapGrid {
    static double latMin = 49.98;
    static double latMax = 50.15;
    static double lonMin = 14.23;
    static double lonMax = 14.61;
    //    static double latMin = 50.05;
//    static double latMax = 50.09;
//    static double lonMin = 14.40;
//    static double lonMax = 14.50;
    static Coordinate NORTH_WEST = new Coordinate(latMax, lonMin);
    static Coordinate NORTH_EAST = new Coordinate(latMax, lonMax);
    static Coordinate SOUTH_WEST = new Coordinate(latMin, lonMin);
    static Coordinate SOUTH_EAST = new Coordinate(latMin, lonMax);
    static int DECIMAL_PRECISION = 3;
    static int PRECISION_MULTIPLIER = (int) Math.pow(10, DECIMAL_PRECISION);
    static double PRECISION_DIFF_STEP = 1 / PRECISION_MULTIPLIER;
    File file = new File("mapgrid_precision_" + DECIMAL_PRECISION + ".file");
    HashMap<Integer, HashMap<Integer, DurationAndDistance>> distanceHashmap;
    private static MapGrid instance = new MapGrid();

    public MapGrid() {
        loadGridMap();
    }

    public static MapGrid getInstance() {
        return instance;
    }

    public DurationAndDistance getDurationAndDistance(Coordinate from, Coordinate to) {
        int fromHash = latLonToHash(from.getLatitude(), from.getLongitude());
        int toHash = latLonToHash(to.getLatitude(), to.getLongitude());
        HashMap<Integer, DurationAndDistance> fromMap = distanceHashmap.get(fromHash);
        DurationAndDistance durationAndDistance = null;
        if (fromMap == null) {
            fromMap = new HashMap<>();
            durationAndDistance = OsrmClient.getDurationAndDistance(from, to);
            fromMap.put(toHash, durationAndDistance);
            distanceHashmap.put(fromHash, fromMap);
        } else {
            durationAndDistance = fromMap.get(toHash);
            if (durationAndDistance == null) {
                durationAndDistance = OsrmClient.getDurationAndDistance(from, to);
                fromMap.put(toHash, durationAndDistance);
            }
        }
        return durationAndDistance;
    }

    private static int latLonToHash(double lat, double lon) {
        int gridLat = (int) Math.round((lat - latMin) * PRECISION_MULTIPLIER);
        int gridLon = (int) Math.round((lon - lonMin) * PRECISION_MULTIPLIER);
        int hash = (gridLat << 16);
        hash |= gridLon;
        return hash;
    }

    private static double[] hashToLatLong(int hash) {
        int lat = (hash >> 16);
        int lon = hash & 0xFF;
        return new double[]{latMin + lat / PRECISION_MULTIPLIER, lon};
    }

    private double roundCoordinate(double coordinate) {
        return ((Math.round(coordinate * PRECISION_MULTIPLIER)) * 1.0) / PRECISION_MULTIPLIER;

    }

    public void saveGridMap() {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file, false));
            oos.writeObject(distanceHashmap);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGridMap() {
        ObjectInputStream ois = null;
        if (file.exists()) {
            try {
                ois = new ObjectInputStream(new FileInputStream(file));
                distanceHashmap = (HashMap) ois.readObject();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            distanceHashmap = new HashMap<>();
        }
    }


    public void testSmallMap() {
        DurationAndDistance gridDurationAndDistance, osrmDurationAndDistance;

        Ride[] testRides = {
                new Ride(new Coordinate(50.087287, 14.432453), new Coordinate(50.081424, 14.412906)),
                new Ride(new Coordinate(50.090120, 14.424682), new Coordinate(50.083761, 14.413401)),
        };
        for (Ride ride : testRides) {
            gridDurationAndDistance = getDurationAndDistance(ride.getPickup(), ride.getDestination());
            osrmDurationAndDistance = OsrmClient.getDurationAndDistance(ride.getPickup(), ride.getDestination());
            System.out.print(" direct distance: " + Math.abs(osrmDurationAndDistance.distance - Util.distance(ride.getPickup(), ride.getDestination())) + " ");
            printDifference(gridDurationAndDistance, osrmDurationAndDistance);
        }
        int x = 50;
        while (x-- > 0) {
            Coordinate a = new Coordinate(Util.randomInRange(latMin, latMax), Util.randomInRange(lonMin, lonMax));
            Coordinate b = new Coordinate(Util.randomInRange(latMin, latMax), Util.randomInRange(lonMin, lonMax));
            //System.out.println(a + " " + b);
            gridDurationAndDistance = getDurationAndDistance(a, b);
            osrmDurationAndDistance = OsrmClient.getDurationAndDistance(a, b);
            printDifference(gridDurationAndDistance, osrmDurationAndDistance);
        }
    }

    public void printDifference(DurationAndDistance a, DurationAndDistance b) {
        System.out.print("duration: " + Math.abs(a.duration - b.duration));
        System.out.println(" distance: " + Math.abs(a.distance - b.distance));

    }

    public void testBigMap() {
        int testCount;
        testCount = 300;
        DurationAndDistance gridDurationAndDistance, osrmDurationAndDistance;
        int maxDist, maxDurationDiff = Integer.MIN_VALUE;
        int sumDurDiff = 0;
        for (int i = 0; i < testCount; i++) {
            Coordinate a = new Coordinate(Util.randomInRange(latMin, latMax), Util.randomInRange(lonMin, lonMax));
            Coordinate b = new Coordinate(Util.randomInRange(latMin, latMax), Util.randomInRange(lonMin, lonMax));
            //System.out.println(a + " " + b);
            gridDurationAndDistance = getDurationAndDistance(a, b);
            osrmDurationAndDistance = OsrmClient.getDurationAndDistance(a, b);
            int diffDur = (int) Math.abs(gridDurationAndDistance.duration - osrmDurationAndDistance.duration);
            sumDurDiff += diffDur;
            if (diffDur > maxDurationDiff) {
                maxDurationDiff = diffDur;
                System.out.println("max: " + a + " " + b);
            }
        }
        System.out.println("max dur diff:" + maxDurationDiff + " avg:" + sumDurDiff / testCount);
    }

    public void create() {
//        int latFields = (int) ((latMax - latMin) * PRECISION_MULTIPLIER);
//        int lonFields = (int) ((lonMax - lonMin) * PRECISION_MULTIPLIER);
//        distanceHashmap = new HashMap<>();
//        int i = 0;
//        for (double lat = latMin; lat < latMax; lat += PRECISION_DIFF_STEP) {
//            for (double lon = lonMin; lon < lonMax; lon += PRECISION_DIFF_STEP) {
//                int hashFrom = latLonToHash(lat, lon);
//                HashMap<Integer, DurationAndDistance> fromHashmap = new HashMap<>();
//                distanceHashmap.put(hashFrom, fromHashmap);
//                for (double latx = latMin; latx < latMax; latx += PRECISION_DIFF_STEP) {
//                    int gridLatx = (int) Math.ceil((latx - latMin) * PRECISION_MULTIPLIER);
//                    for (double lonx = lonMin; lonx < lonMax; lonx += PRECISION_DIFF_STEP) {
//                        int hashTo = latLonToHash(latx, lonx);
//                        fromHashmap.put(hashTo, OsrmClient.getDurationAndDistance(lat, lon, latx, lonx));
//                        i++;
//                    }
//                }
//                System.out.println(i);
//            }
//        }
//        System.out.println(i);
//        ObjectOutputStream oos = null;
//        try {
//            oos = new ObjectOutputStream(new FileOutputStream(file, false));
//            oos.writeObject(distanceHashmap);
//            oos.flush();
//            oos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
