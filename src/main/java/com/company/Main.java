package com.company;

import com.company.grid.MapGrid;
import com.company.model.Coordinate;
import com.company.routing.OsrmClient;
import com.company.scripts.CsvToSqlInsert;
import com.company.simulator.Coordinator;
import com.company.statistics.GridStats;

public class Main {

    public static void main(String[] args) {

        //new Coordinator().runSimulation();

        GridStats.testGrid();
        //MapGrid.getInstance().saveGridMap();

    }
}
