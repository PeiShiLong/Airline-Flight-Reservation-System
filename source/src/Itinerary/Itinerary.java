package src.Itinerary;

import src.TTARouteNetwork.Flight;

import java.util.ArrayList;
import java.util.List;

public class Itinerary{

    private List<src.TTARouteNetwork.Flight> flightList = new ArrayList<>();
    private int totalPrice = 0;
    private int numberFlightIn = 0;
    private Flight flight;

    public Itinerary(int totalPrice, int numberFlightIn, Flight fl) {
        this.totalPrice = totalPrice;
        this.numberFlightIn = numberFlightIn;
        this.flight = fl;
    }

    public void addFlight(src.TTARouteNetwork.Flight fl){
        flightList.add(fl);
        totalPrice += fl.getAirfare();
        numberFlightIn += 1;
    }

    public int getTotalPrice(){
        return totalPrice;
    }

    public int getNumberFlightIn(){
        return numberFlightIn;
    }

    /**
     * Returns the 3-letter code representing the origin airport of the first flight in the itinerary.
     * @return
     */
    public String getOrigin(){
        return flightList.get(0).getOriginAirport();
    }

    /**
     * Returns the 3-letter code representing the destination airport of the last flight in the itinerary.
     * @return
     */
    public String getDestination(){
        return flightList.get(flightList.size()-1).getDestinationAirport();
    }

    @Override
    public String toString(){
        return getTotalPrice() + "," + getNumberFlightIn() + "[,"
                + flight.getFlightNumber() + "," + getOrigin() + ","
                + flight.getDepatureTime() + "," + getDestination()
                + "," + flight.getArrivalTime();
    }
}
