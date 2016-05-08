package com.company.simulator;

import com.company.model.Coordinate;
import com.company.model.Order;
import com.company.model.PassengerStop;
import com.company.model.Taxi;
import com.company.routing.OsrmClient;
import com.company.routing.vo.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frox on 7.5.16.
 */
public class RideShareOrderTaxiMatcher extends OrderTaxiMatcher {

    public RideShareOrderTaxiMatcher(Coordinator coordinator) {
        super(coordinator);
    }

    /**
     * finds the nearest available taxi and add this order to its plan
     *
     * @param order
     */
    @Override
    public void matchOrderToTaxi(Order order) {
        DetourInfo bestDetour = DetourInfo.createDefault();
        for (Taxi taxi : findNearestTaxis(order.getPickup(), Coordinator.MAX_PICKUP_DURATION)) {
            DetourInfo detour = DetourInfo.createDefault();
            if (taxi.isServing()) {
                detour = findMinimalValidDetour(taxi, order);
                if (detour.isValid() && detour.duration < bestDetour.duration) {
                    bestDetour = detour;
                }
            } else {
                int duration = routingService.getDurationAndDistanceFast(taxi.getPosition(), order.getPickup(), order.getDestination()).duration;
                detour.duration = duration;
                detour.taxi = taxi;
                if (detour.duration < bestDetour.duration) {
                    bestDetour = detour;
                }
            }

        }
        if (bestDetour.taxi == null) {
            System.out.println("did not find available taxi");
        } else {
            if (bestDetour.taxi.isServing()) {
                Taxi taxi = bestDetour.taxi;
                List<PassengerStop> stops = taxi.getStops();
                PassengerStop pickup = new PassengerStop(order, PassengerStop.Type.PICKUP);
                PassengerStop destination = new PassengerStop(order, PassengerStop.Type.DESTINATION);
                pickup.setDestinationStop(destination);
                stops.add(bestDetour.destinationPreviousStop + 1, destination);//first must add destination in cases pickup and destination have same previous stop
                stops.add(bestDetour.pickupPreviousStop + 1, pickup);
                taxi.setStops(stops);
                System.out.println("best is RIDE SHARE!!");
            } else {
                addOrderToEmptyTaxi(order, bestDetour.taxi);
            }
        }
    }

    private DetourInfo findMinimalValidDetour(Taxi taxiInNeighbourghood, Order order) {
        List<PassengerStop> stopPlan = taxiInNeighbourghood.getStops();
        PassengerStop previousStop = null;
        PassengerStop nextStop = stopPlan.get(0);
        Coordinate taxiPosition = taxiInNeighbourghood.getPosition();
        int currentDuration = routingService.getDurationAndDistanceFast(taxiPosition, nextStop.getCoordinate()).duration;
        int toPickupDuration = routingService.getDurationAndDistanceFast(taxiPosition, order.getPickup()).duration;
        int fromPickupToNextDuration = routingService.getDurationAndDistanceFast(order.getPickup(), nextStop.getCoordinate()).duration;
        int totalDetourIncrement = toPickupDuration + fromPickupToNextDuration - currentDuration;
        DetourInfo bestDetour = DetourInfo.createDefault();
        DetourInfo detourInfo;
        if (isValidArrivalToLaterStops(0, stopPlan, totalDetourIncrement)) {
            detourInfo = findMinimalValidDestinationDetour(0, stopPlan, order, totalDetourIncrement);
            if (detourInfo.duration < bestDetour.duration) {
                bestDetour = detourInfo;
                bestDetour.pickupPreviousStop = -1;
            }
        }
        pickupSearchLoop:
        for (int i = 1; i < stopPlan.size(); i++) {
            previousStop = nextStop;
            nextStop = stopPlan.get(i);
            currentDuration = routingService.getDurationAndDistanceFast(previousStop.getCoordinate(), nextStop.getCoordinate()).duration;
            toPickupDuration = routingService.getDurationAndDistanceFast(previousStop.getCoordinate(), order.getPickup()).duration;
            fromPickupToNextDuration = routingService.getDurationAndDistanceFast(order.getPickup(), nextStop.getCoordinate()).duration;
            totalDetourIncrement = toPickupDuration + fromPickupToNextDuration - currentDuration;

            if (previousStop.getPlannedArrival().plusSeconds(toPickupDuration).isAfter(order.getLatestPickup())) {
                //too late pickup
                continue pickupSearchLoop;
            }
            if (!isValidArrivalToLaterStops(i, stopPlan, totalDetourIncrement)) {
                //too late arrival to some point after -> not a valid pickup again!
                continue pickupSearchLoop;
            }
            //pickup seems valid
            detourInfo = findMinimalValidDestinationDetour(i, stopPlan, order, totalDetourIncrement);
            if (detourInfo.duration < bestDetour.duration) {
                bestDetour = detourInfo;
                bestDetour.pickupPreviousStop = i - 1;
            }

        }
        bestDetour.taxi = taxiInNeighbourghood;
        return bestDetour;
    }

    private DetourInfo findMinimalValidDestinationDetour(int indexAfterPickup, List<PassengerStop> stopPlan, Order order, int pickupDetourIncrement) {
        DetourInfo bestDetour = DetourInfo.createDefault();
        Coordinate pickup = order.getPickup();
        //try immediately after pickup
        PassengerStop previousStop;
        PassengerStop nextStop = stopPlan.get(indexAfterPickup);
        int currentDuration = routingService.getDurationAndDistanceFast(pickup, nextStop.getCoordinate()).duration;
        int fromPickupToDest = routingService.getDurationAndDistanceFast(pickup, order.getDestination()).duration;
        int fromDestToNext = routingService.getDurationAndDistanceFast(order.getDestination(), nextStop.getCoordinate()).duration;
        int totalDetourIncrement = pickupDetourIncrement + fromPickupToDest + fromDestToNext - currentDuration;

        if (isValidArrivalToLaterStops(indexAfterPickup, stopPlan, totalDetourIncrement)) {
            bestDetour = new DetourInfo(indexAfterPickup - 1, totalDetourIncrement);
        }
        destinationSearchLoop:
        for (int i = indexAfterPickup; i < stopPlan.size(); i++) {
            previousStop = nextStop;
            nextStop = stopPlan.get(i);
            currentDuration = routingService.getDurationAndDistanceFast(previousStop.getCoordinate(), nextStop.getCoordinate()).duration;
            int fromPrevToDest = routingService.getDurationAndDistanceFast(previousStop.getCoordinate(), order.getDestination()).duration;
            fromDestToNext = routingService.getDurationAndDistanceFast(order.getDestination(), nextStop.getCoordinate()).duration;
            totalDetourIncrement = pickupDetourIncrement + fromPrevToDest + fromDestToNext - currentDuration;
            if (!isValidArrivalToLaterStops(i, stopPlan, totalDetourIncrement)) {
                continue destinationSearchLoop;
            }
            if (totalDetourIncrement < bestDetour.duration) {
                bestDetour = new DetourInfo(i - 1, totalDetourIncrement);
            }
        }
        PassengerStop lastStop = stopPlan.get(stopPlan.size() - 1);
        totalDetourIncrement = pickupDetourIncrement + routingService.getDurationAndDistanceFast(lastStop.getCoordinate(), order.getDestination()).duration;
        if (totalDetourIncrement < bestDetour.duration) {
            bestDetour = new DetourInfo(stopPlan.size() - 1, totalDetourIncrement);
        }
        return bestDetour;
    }

    public boolean isValidArrivalToLaterStops(int from, List<PassengerStop> stopPlan, int totalDetourIncrement) {
        for (int j = from; j < stopPlan.size(); j++) {
            if (!isValidArrival(stopPlan.get(j), totalDetourIncrement)) {
                return false;
            }
        }
        return true;
    }

    /**
     * if arriving late by duration than planned is still valid time.
     *
     * @param stop
     * @param detourTime
     * @return
     */
    private boolean isValidArrival(PassengerStop stop, int detourTime) {
        return stop.getPlannedArrival().plusSeconds(detourTime).isBefore(stop.getLatestArrival());
    }

    private static class DetourInfo {
        int destinationPreviousStop = Integer.MIN_VALUE;
        int pickupPreviousStop = Integer.MIN_VALUE;
        int duration = Integer.MAX_VALUE;
        public Taxi taxi;

        public DetourInfo(int destinationPreviousStopIndex, int duration) {
            this.destinationPreviousStop = destinationPreviousStopIndex;
            this.duration = duration;
        }

        public boolean isValid() {
            return destinationPreviousStop >= -1 && pickupPreviousStop >= -1;
        }

        public static DetourInfo createDefault() {
            return new DetourInfo(Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
    }


}
