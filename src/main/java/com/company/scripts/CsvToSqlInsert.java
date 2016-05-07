package com.company.scripts;

import com.company.util.Util;
import com.sun.deploy.util.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by frox on 7.5.16.
 */
public class CsvToSqlInsert {

    public static void run() {


        String csvFile = "data/liftago_my_export.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        PrintWriter writer;
        try {
            writer = new PrintWriter("data/liftago_insert_script.sql", "UTF-8");
            br = new BufferedReader(new FileReader(csvFile));
            br.readLine();
            writer.println("INSERT INTO orders (orderId,rideId,orderedAt,avgDistanceTariffOffered,requestedPickupLat,requestedPickupLon,requestedDestinationLat,requestedDestinationLon,completionState) VALUES");
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.replace("\"", "").split(cvsSplitBy);
                ArrayList<String> values = new ArrayList<>();
                values.add(data[0]);
                values.add(data[1]);
                values.add("\"" + convertTime(data[2]) + "\"");
                values.add(data[3]);
                String[] pickup = data[4].split(",");
                values.add(pickup[0]);
                values.add(pickup[1]);
                String[] destination = data[5].split(",");
                values.add(destination[0]);
                values.add(destination[1]);
                values.add("\""+data[6]+"\"");
                writer.println("(" + StringUtils.join(values, ",") + "),");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static String convertTime(String dateTimeStr) {
        DateTimeFormatter formatter = Util.getDateTimeFormatter();
        DateTime dt = formatter.withZone(DateTimeZone.UTC).parseDateTime(dateTimeStr).withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Prague")));
        return formatter.print(dt);
    }


}
