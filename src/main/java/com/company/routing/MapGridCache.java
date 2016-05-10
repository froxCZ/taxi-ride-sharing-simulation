package com.company.routing;

import com.company.model.Coordinate;
import com.company.model.DurationAndDistance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * A class which rounds coordinates to 3 decimal places and caches duration and distances between them
 */
public class MapGridCache {
    /**
     * min and max lat and lon. It is a square around Prague.
     */
    public static double latMin = 49.98;
    public static double latMax = 50.15;
    public static double lonMin = 14.23;
    public static double lonMax = 14.61;
    static int DECIMAL_PRECISION = 3;
    static int PRECISION_MULTIPLIER = (int) Math.pow(10, DECIMAL_PRECISION);
    File file = new File("mapgrid_precision_" + DECIMAL_PRECISION + ".file");//file to store hashmaps
    HashMap<Integer, HashMap<Integer, DurationAndDistance>> distanceHashmap;
    private static MapGridCache instance = new MapGridCache();

    public MapGridCache() {
        loadCache();
    }

    public static MapGridCache getInstance() {
        return instance;
    }

    /**
     * rounds the from and to coordinates. If its not in cache it runs query at OSRM client and stores the result
     * @param from
     * @param to
     * @return
     */
    public DurationAndDistance getDurationAndDistance(Coordinate from, Coordinate to) {
        int fromHash = latLonToHash(from.getLatitude(), from.getLongitude());
        int toHash = latLonToHash(to.getLatitude(), to.getLongitude());
        HashMap<Integer, DurationAndDistance> fromMap = distanceHashmap.get(fromHash);
        DurationAndDistance durationAndDistance = null;
        if (fromMap == null) {
            //from hashmap does not exists
            fromMap = new HashMap<>();
            durationAndDistance = OsrmClient.getDurationAndDistance(roundCoordinate(from.getLatitude()),roundCoordinate(from.getLongitude()),
                    roundCoordinate(to.getLatitude()),roundCoordinate(to.getLongitude()));
            fromMap.put(toHash, durationAndDistance);
            distanceHashmap.put(fromHash, fromMap);
        } else {
            durationAndDistance = fromMap.get(toHash);
            if (durationAndDistance == null) {
                //to hashmap does not exists
                durationAndDistance = OsrmClient.getDurationAndDistance(roundCoordinate(from.getLatitude()),roundCoordinate(from.getLongitude()),
                        roundCoordinate(to.getLatitude()),roundCoordinate(to.getLongitude()));
                fromMap.put(toHash, durationAndDistance);
            }
        }
        return durationAndDistance;
    }

    /**
     * from lat and lon creates a hash which is used as a key to hashmap
     * @param lat
     * @param lon
     * @return
     */
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

    /**
     * rounding coordinates
     * @param coordinate
     * @return
     */
    public static double roundCoordinate(double coordinate) {
        return ((Math.round(coordinate * PRECISION_MULTIPLIER)) * 1.0) / PRECISION_MULTIPLIER;

    }

    /**
     * saves the hashmaps cache to file
     */
    public void saveCache() {
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

    /**
     * loads the hashmaps cache
     */
    public void loadCache() {
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
}
