package com.company.model;

import com.company.simulator.Coordinator;
import org.joda.time.DateTime;

/**
 * Created by frox on 5.5.16.
 */
public class PassengerStop extends PlanPoint{
    private DateTime latestArrival;
    private Type type;
    private Order order;
    private DateTime plannedArrival;
    private PassengerStop destinationStop,pickupStop;
    private int plannedDistanceFromPreviousStop;

    public void setDestinationStop(PassengerStop destinationStop) {
        this.destinationStop = destinationStop;
    }

    public Type getType() {
        return type;
    }

    public Order getOrder() {
        return order;
    }

    public int getPassengerChange(){
        if(type == Type.PICKUP){
            return order.getPassengersCount();
        }else{
            return -1 * order.getPassengersCount();
        }
    }

    public String printForPlot() {
        return getCoordinate() + "," + order.getOrderId() + type;
    }

    public void setPlannedDistance(int distance) {
        plannedDistanceFromPreviousStop = distance;
    }

    public int getPlannedDistanceFromPreviousStop() {
        return plannedDistanceFromPreviousStop;
    }

    public PassengerStop getDestinationStop() {
        return destinationStop;
    }

    public PassengerStop getPickupStop() {
        return pickupStop;
    }

    public void setPickupStop(PassengerStop pickupStop) {
        this.pickupStop = pickupStop;
    }

    public enum Type {
        PICKUP, DESTINATION;
    }

    public PassengerStop(Order order, Type type) {
        this.type = type;
        this.order = order;
        setCoordinate(type == Type.PICKUP ? order.getPickup() : order.getDestination());
        if (type == Type.PICKUP) {
            latestArrival = Coordinator.CURRENT_TIME.plusSeconds(Coordinator.MAX_PICKUP_DURATION);
        }
        //latestArrival = order.getLatestArrival();//Coordinator.CURRENT_TIME.plusSeconds((int) (order.getLenght() * Coordinator.MAX_DETOUR_MULTIPLICATION));
    }

    public DateTime getLatestArrival() {
        return latestArrival;
    }

    public void setLatestArrival(DateTime latestArrival) {
        this.latestArrival = latestArrival;
    }

    public DateTime getPlannedArrival() {
        return plannedArrival;
    }

    public void setPlannedArrival(DateTime plannedArrival) {
        this.plannedArrival = plannedArrival;
        if (type == Type.PICKUP) {
            destinationStop.setLatestArrival(plannedArrival.plusSeconds((int) (Math.max(order.getDirectRouteDurationAndDistance(), 3 * 60) * Coordinator.MAX_DETOUR_MULTIPLICATION)));
        }
    }

    @Override
    public String toString() {
        return "PassengerStop{" +
                "order=" + order.getOrderId() +
                ", type=" + type +
                ", coordinate=" + getCoordinate() +
                ", plannedArrival=" + plannedArrival +
                ", latestArrival=" + latestArrival +
                '}';
    }
}
