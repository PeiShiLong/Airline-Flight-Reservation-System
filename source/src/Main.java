import Airports.AirportInfoService;
import Airports.AirportsDBProxy;
import Commands.InputParser;
import Parser.CSVParser;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The main function to start the AFRS
 * Created by Joshua Ehling, Ethan Della Posta, PeiShi Long
 */
public class Main {

    public static void help(){
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("Input should follow one of the following formats (Anything in brackets are optional and all commands are ended with semi-colons): ");
        System.out.println("Flight information request: info,origin,destination[,connections[,sort-order]];");
        System.out.println("Make reservation request: reserve,id,passenger;");
        System.out.println("Retrieve reservations request: retrieve,passenger[,origin[,destination]];");
        System.out.println("Delete reservation request: delete,passenger,origin,destination;");
        System.out.println("Airport information request: airport,airport;");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("Please input a command (Type 'HELP' to see commands again, Type 'QUIT' to exit, Type 'SERVER' to toggle which airport service you are using): ");
    }

    public static void main(String[] args) throws Exception{
        helper();
    }

    public static void helper(){
        CSVParser csvp = new CSVParser();
        csvp.createHashes();
        InputParser parser = new InputParser();
        Scanner scanner = new Scanner(System.in);
        String input = "";
        AtomicInteger id = new AtomicInteger(10000);

        System.out.println("************************************** Welcome to Airline Flight Reservation Server (AFRS)! **************************************");
        help();

        while(true){
            input += scanner.next();
            if(input.trim().toLowerCase().contains("quit")){
                System.out.println("Thank you for using AFRS!");

                //save any reservations upon quitting
                csvp.writeToCSV();
                return;
            }

            if(input.trim().toLowerCase().contains("server")){
                AirportsDBProxy.getInstance().toggleService();
                AirportInfoService airportInfoService = AirportsDBProxy.getInstance();
                System.out.println("You are now using the " + airportInfoService + " airport information service");
                input = "";
                continue;
            }
            if(input.trim().toLowerCase().contains("help")){
                help();
                input = "";
                continue;
            }
            if(input.trim().endsWith(";")){
                try{
                    parser.parseInput(input.substring(0, input.length() - 1));
                    System.out.println(parser.executeRequest());
                    input = "";
                }catch(Exception e){
                    input = "";
                    System.out.println(e.getMessage());
                }
            }
        }
    }

}
