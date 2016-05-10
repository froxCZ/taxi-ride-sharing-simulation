package com.company.model;

import com.company.simulator.Simulator;
import org.joda.time.DateTime;

/**
 * A pickup or destination stop
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
            latestArrival = Simulator.CURRENT_TIME.plusSeconds(Simulator.MAX_PICKUP_DURATION);
        }
        //latestArrival = order.getLatestArrival();//Simulator.CURRENT_TIME.plusSeconds((int) (order.getLenght() * Simulator.MAX_DETOUR_MULTIPLICATION));
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
            destinationStop.setLatestArrival(plannedArrival.plusSeconds((int) (Math.max(order.getDirectRouteDuration(), 3 * 60) * Simulator.MAX_DETOUR_MULTIPLICATION)));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PassengerStop)) return false;

        PassengerStop that = (PassengerStop) o;

        if (plannedDistanceFromPreviousStop != that.plannedDistanceFromPreviousStop) return false;
        if (latestArrival != null ? !latestArrival.equals(that.latestArrival) : that.latestArrival != null)
            return false;
        if (type != that.type) return false;
        if (order != null ? !order.equals(that.order) : that.order != null) return false;
        if (plannedArrival != null ? !plannedArrival.equals(that.plannedArrival) : that.plannedArrival != null)
            return false;
        if (destinationStop != null ? !destinationStop.equals(that.destinationStop) : that.destinationStop != null)
            return false;
        return !(pickupStop != null ? !pickupStop.equals(that.pickupStop) : that.pickupStop != null);

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
