package Commands;

import Reservations.Itinerary;
import Reservations.ReservationsDB;

import java.util.ArrayList;

/**
 * Command block that handles Reserve Flight Request
 *
 * Legend: {keyword literal}, [optional input], data
 * Make reservation request: {reserve},id,passenger;
 *      id: identifier in the last flight information request for the itinerary being reserved.
 *          The position number, starting with 1 (0), of the itinerary in the last AFRS response to a Flight Info Query
 *      passenger: name of the passenger making the reservation
 */
public class ReserveFlight extends UndoableCommand{
    /* constants */
    private static final int ID = 1;
    private static final int PASSENGER = 2;

    /* attributes */
    private ReservationsDB reservationsDB = ReservationsDB.getInstance();
    private String currPassenger = "";
    private Itinerary currItinerary = null;
    private static final String SUCCESSFUL_RESERVATION_MSG = "reserve,successful";

    public ReserveFlight(ArrayList<String> input){
        super(input);
    }

    @Override
    public String execute() throws Exception{
        int id = Integer.parseInt(input.get(ID)) - 1;
        currPassenger = input.get(PASSENGER);
        // only update currItinerary on new command obj (set to null)
        if(currItinerary == null){
            currItinerary = reservationsDB.getCurrItineraryWithID(id);
        }
        return reservationsDB.reserveItinerary(currItinerary, currPassenger);
    }

    /**
     * delete: Delete reservation request: {delete},passenger,origin,destination;
     */
    @Override
    public void undo() throws Exception{
        reservationsDB.deleteItinerary(currPassenger, currItinerary);
    }
}
