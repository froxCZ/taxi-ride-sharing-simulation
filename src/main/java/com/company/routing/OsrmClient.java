package com.company.routing;

import com.company.model.Coordinate;
import com.company.routing.vo.OsrmResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;

/**
 * Created by frox on 5.5.16.
 */
public class OsrmClient {
    public OsrmClient() {
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

    public void test(Coordinate from, Coordinate to) {
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
            for(Coordinate c:osrmResponse.getRoute().getLeg().getRoutePlanByDeltaSeconds(deltaSec)){
                System.out.println("time: "+time+" "+c.toString());
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
}
