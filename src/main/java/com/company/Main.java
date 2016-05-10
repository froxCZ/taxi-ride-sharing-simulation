package com.company;

import com.company.routing.MapGridCache;
import com.company.simulator.Simulator;

public class Main {

    public static void main(String[] args) {

        new Simulator().runSimulation();

        MapGridCache.getInstance().saveCache();

    }
}
