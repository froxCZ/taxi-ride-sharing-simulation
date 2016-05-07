package com.company;

import com.company.service.OrderProvider;

public class Main {

    public static void main(String[] args) {

        new Coordinator().runSimulation();
//        Coordinator coordinator = new Coordinator();
//        //coordinator.test();
//        MapGrid mapGrid = new MapGrid();
//        mapGrid.loadMap();
//        mapGrid.testBigMap();
//        System.out.println("start");
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 1; i++) {
//            new OsrmClient().test(new Coordinate(50.047425, 14.426246), new Coordinate(50.099360, 14.364335));
//        }
//        System.out.println("stop: " + (System.currentTimeMillis() - start));
//        System.out.println(Util.distance(new Coordinate(50.046919, 14.425125), new Coordinate(50.046861, 14.424823)));
    }
}
