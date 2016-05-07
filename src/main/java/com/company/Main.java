package com.company;

import com.company.grid.MapGrid;
import com.company.model.Coordinate;
import com.company.routing.OsrmClient;
import com.company.simulator.Coordinator;

public class Main {

    public static void main(String[] args) {

        new Coordinator().runSimulation();


        MapGrid.getInstance().saveGridMap();
    }
}
