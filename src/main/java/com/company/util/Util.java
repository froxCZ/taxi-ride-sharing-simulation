package com.company.util;


import com.company.model.Coordinate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Random;

/**
 * Created by frox on 5.5.16.
 */
public class Util {
    /**
     *
     * @param a
     * @param b
     * @return in meters
     */
    public static double distance(Coordinate a, Coordinate b) {
        double earthRadius = 6371.0d; // KM: use mile here if you want mile result

        double dLat = toRadian(b.getLatitude() - a.getLatitude());
        double dLng = toRadian(b.getLongitude() - a.getLongitude());

        double x = Math.pow(Math.sin(dLat / 2), 2) +
                Math.cos(toRadian(a.getLatitude())) * Math.cos(toRadian(b.getLatitude())) *
                        Math.pow(Math.sin(dLng / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1 - x));

        return earthRadius * c*1000; // returns result kilometers
    }

    public static double toRadian(double degrees) {
        return (degrees * Math.PI) / 180.0d;
    }

    static Random r = new Random();

    public static double randomInRange(double rangeMin, double rangeMax) {
        return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    }

    static long measureTime;

    public static void timeMeasureStart() {
        measureTime = System.currentTimeMillis();
    }

    public static void timeMeasureStop() {
        System.out.println(System.currentTimeMillis() - measureTime);
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 50% one passenger,30% 2 passengers, 11% 3 passengers, 9% 4 passengers
     * @return
     */
    public static int getRandomPassengerCount(){
        Double random =randomInRange(0,1);
        if(randomInRange(0,1) < 0.5){
            return 1;
        }else if(random <0.80){
            return 2;
        }else if(random < 0.91){
            return 3;
        }else{
            return 4;
        }
    }
}
