package com.company;

import com.company.routing.MapGrid;
import com.company.simulator.Coordinator;

public class Main {

    public static void main(String[] args) {

        new Coordinator().runSimulation();

        MapGrid.getInstance().saveGridMap();

    }
}
