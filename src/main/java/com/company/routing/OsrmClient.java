package com.company.routing;

import com.company.model.Coordinate;
import com.company.model.DurationAndDistance;
import com.company.model.PlanPoint;
import com.company.routing.vo.OsrmResponse;
import com.company.routing.vo.Route;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;

/**
 * Created by frox on 5.5.16.
 */
public class OsrmClient {
    static {
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static Route getRoute(Coordinate from, Coordinate to) {
        String url = "http://127.0.0.1:5000/route/v1/driving/{fromLon},{fromLat};{toLon},{toLat}?overview=false&steps=true&geometries=geojson";
        try {
            OsrmResponse osrmResponse = Unirest.get(url)
                    .routeParam("fromLon", String.valueOf(from.getLongitude()))
                    .routeParam("fromLat", String.valueOf(from.getLatitude()))
                    .routeParam("toLon", String.valueOf(to.getLongitude()))
                    .routeParam("toLat", String.valueOf(to.getLatitude()))
                    .asObject(OsrmResponse.class).getBody();
            return osrmResponse.getRoute();
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void test(Coordinate from, Coordinate to) {
        String url = "http://127.0.0.1:5000/route/v1/driving/{fromLon},{fromLat};{toLon},{toLat}?overview=false&steps=true&geometries=geojson";
        try {
            OsrmResponse osrmResponse = Unirest.get(url)
                    .routeParam("fromLon", String.valueOf(from.getLongitude()))
                    .routeParam("fromLat", String.valueOf(from.getLatitude()))
                    .routeParam("toLon", String.valueOf(to.getLongitude()))
                    .routeParam("toLat", String.valueOf(to.getLatitude()))
                    .asObject(OsrmResponse.class).getBody();
            System.out.println(osrmResponse.getRoute().duration+" sum duration:"+osrmResponse.getRoute().getLeg().sumDuration()+" route:" + osrmResponse.getRoute().getLeg().toString());
            int deltaSec = 20;
            int time = 0;
            for (PlanPoint p : osrmResponse.getRoute().getRoutePlanByDeltaSeconds(deltaSec)) {
                System.out.println("time: " + time + " " + p.toString());
                time+=deltaSec;
            }
            System.out.println(osrmResponse.code);
            System.out.println(Unirest.get(url)
                    .routeParam("fromLon", String.valueOf(from.getLongitude()))
                    .routeParam("fromLat", String.valueOf(from.getLatitude()))
                    .routeParam("toLon", String.valueOf(to.getLongitude()))
                    .routeParam("toLat", String.valueOf(to.getLatitude())).asJson().getBody().toString());

        } catch (UnirestException e) {
            e.printStackTrace();
        }

    }

    public static DurationAndDistance getRouteFast(Coordinate from, Coordinate to) {
        return getRouteFast(from.getLatitude(),from.getLongitude(),to.getLatitude(),to.getLongitude());
    }
    public static DurationAndDistance getRouteFast(double lat, double lon, double latx, double lonx) {
        String url = "http://127.0.0.1:5000/route/v1/driving/" + lon + "," + lat + ";" + lonx + "," + latx + "?overview=false";
        try {
            //example: "routes":[{"duration":86.7,"distance":948.8,"legs":[{"summary":"","
            //get duration and distance
            String s = Unirest.get(url).asJson().getBody().toString();
            int firstCommaIndex = s.indexOf(",");
            int secondCommaIndex = s.indexOf(",", firstCommaIndex+1);
            double duration = Double.valueOf(s.substring("{\"routes\":[{\"duration\":".length(), firstCommaIndex));
            double distance = Double.valueOf(s.substring(firstCommaIndex+" \"distance\":".length(),secondCommaIndex));
            return new DurationAndDistance(duration,distance);

        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }
}
