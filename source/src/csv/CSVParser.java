package csv;

import Airports.AirportsDB;
import Reservations.ReservationsDB;
import TTARouteNetwork.Flight;
import TTARouteNetwork.FlightsDB;
import csv.parseTypes.*;
import Reservations.Itinerary;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


/**
*   this class is the "context" of the strategy pattern I have implemented here. This class is instantiated in the Main
*   and creates the HashMap objects for both flights and airports
 *   Author: Ethan Della Posta
*/
public class CSVParser {
    public static String AIRPORTS_NAME_PATH =  "CSVFiles/airports.csv";
    public static String AIRPORTS_DELAY_PATH = "CSVFiles/delay.csv";
    public static String AIRPORTS_TIME_PATH = "CSVFiles/min_connection_time.csv";
    public static String AIRPORTS_WEATHER_PATH = "CSVFiles/weather.csv";
    public static String FLIGHTS_PATH = "CSVFiles/route_network.csv";
    public static String RESERVATIONS_PATH = "CSVFiles/reservations.csv";

    private CSVParse nameParse;
    private CSVParse delayParse;
    private CSVParse timeParse;
    private CSVParse weatherParse;
    private CSVParse flightParse;
    private CSVParse reservationParse;
    private AirportsDB airports;
    private FlightsDB flights;
    private ReservationsDB reservations;


    public CSVParser(){
        // all the parse types the parser will use to generate the HashMaps
        this.nameParse = new AirportNameParse();
        this.delayParse = new AirportDelayParse();
        this.timeParse = new AirportTimeParse();
        this.weatherParse = new AirportWeatherParse();
        this.flightParse = new FlightParse();
        this.reservationParse = new ReservationParse();

        //airports and flights HashMaps will be filled with their data in the createHashes function call
        this.airports = AirportsDB.getInstance();
        this.flights = FlightsDB.getInstance();
        this.reservations = ReservationsDB.getInstance();
    }

    public void createHashes(){

        //read in all the airport data and adds to flights HashMap
        //check if files don't exist
        this.nameParse.parseCSV(AIRPORTS_NAME_PATH);
        this.delayParse.parseCSV(AIRPORTS_DELAY_PATH);
        this.timeParse.parseCSV(AIRPORTS_TIME_PATH);
        this.weatherParse.parseCSV(AIRPORTS_WEATHER_PATH);


        //read in all the flight data and adds to flights HashMap
        this.flightParse.parseCSV(FLIGHTS_PATH);

        //read in all the reservation data and adds to reservation HashMap
        this.reservationParse.parseCSV(RESERVATIONS_PATH);
    }

    /**
     * Accessor method for airports
     * @return airports
     */
    public AirportsDB getAirports() {
        return airports;
    }


    /**
     * Accessor method for flights
     * @return flights
     */
    public FlightsDB getFlights() {
        return flights;
    }

    /**
     * Accessor method for reservations
     * @return reservations
     */
    public ReservationsDB getReservations() {
        return reservations;
    }

    /**
     * Singular Purpose
     * Writes reservations to reservations.csv in the following format:
     * Passenger Name, Airfare, Flight Number, Origin Airport, Departure Time, Destination Airport, Arrival Time, ....
     */
    public void writeToCSV(){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(RESERVATIONS_PATH));
            for (String passengerName : this.reservations.parserGetReservationKeySet()) {
                for (Itinerary itinerary : this.reservations.parserGetReservations( passengerName)) {
                    writer.write(passengerName + ",");
                    for(Flight flight : itinerary.getFlights()){
                        writer.write(flight.toString() + ",");
                    }
                }
                writer.write("\n");
            }
            writer.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
