package TTARouteNetwork;

import java.util.HashMap;

/**
 * @author Joshua Ehling
 * Enforces Singleton Pattern
 * Database used to map flight number --> ArrayList <Flights>
 */
public class FlightsDB {
    /* attributes */
    private HashMap<Integer, Flight> flightsHashMap;

    /* Enforce Singleton Pattern */
    private static FlightsDB instance;

    /* Singleton Pattern Accessor */
    public static FlightsDB getInstance(){
        if(instance == null) instance = new FlightsDB();
        return instance;
    }

    /**
     * Constructor
     */
    private FlightsDB(){
        flightsHashMap = new HashMap<>();
    }

    /**
     * Accessor for total set of Flight objects. Maps Flight # --> Flight Object
     * @param flightsKey - origin destination flights key
     * @return ArrayList of Flight objects that share the origin airport
     */
    public Flight getFlight(int flightsKey){
        return flightsHashMap.get(flightsKey);
    }


    public HashMap<Integer, Flight> getFlightsHashMap(){
        return this.flightsHashMap;
    }

    /**
     * Mutator method. Adds Flight objects to general HashMap based on Flight Number
     * @param flightsKey - origin detsination flights key
     * @param flight - Flight object
     */
      public void addFlight(int flightsKey, Flight flight){
         flightsHashMap.put(flightsKey, flight);
      }

    /**
     * Mutator method. Removes flight objects from general HashMap
     * @param flightsKey  origin detsination flights key
     */
     public void removeFlight(int flightsKey, Flight flight){
         flightsHashMap.put(flightsKey, flight);
      }
}
