package com.company.model;

import com.company.simulator.Coordinator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by frox on 7.5.16.
 */
public class StopPlan {
    private List<PassengerStop> stops = new ArrayList<>();

    /**
     * returns only stops ahead
     *
     * @return
     */
    public List<PassengerStop> getStops() {
        removedVisitedStops();
        return stops;
    }

    private void removedVisitedStops() {
        Iterator<PassengerStop> it = stops.iterator();
        while (it.hasNext()) {
            PassengerStop passengerStop = it.next();
            if (Coordinator.CURRENT_TIME.isAfter(passengerStop.getPlannedArrival())) {
                it.remove();
            } else {
                break;
            }
        }


    }

    public void setStops(List<PassengerStop> stops) {
        this.stops = stops;
    }
}
