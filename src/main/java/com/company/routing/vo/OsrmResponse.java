package com.company.routing.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by frox on 5.5.16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OsrmResponse {
    public List<Route> routes;
    public String code;

    public Route getRoute() {
        return routes.get(0);
    }
}
