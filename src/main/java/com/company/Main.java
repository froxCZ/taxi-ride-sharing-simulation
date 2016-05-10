package com.company;

import com.company.routing.MapGrid;
import com.company.simulator.Simulator;

public class Main {

    public static void main(String[] args) {

        new Simulator().runSimulation();

        MapGrid.getInstance().saveGridMap();

    }
}
