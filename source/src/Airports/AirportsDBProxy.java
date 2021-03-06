package Airports;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * @author Ethan Della Posta
 *
 *
 * If faaToggled:
 *      Gets Airport json data from faa api,
 *      and returns an Airport object
 * else if ! faaToggled:
 *      Uses the AirportsDB object to get airport information
 */
public class AirportsDBProxy implements AirportInfoService {

    /* Have we specified to use the FAA web service? */
    private Boolean faaToggled = false;

    /* Instance of the singleton AirportsDB to be used by default for airport info */
    private AirportInfoService airportsDB = AirportsDB.getInstance();

    /* Root url for getting airport information through the FAA web service */
    private static final String urlPreface =  "https://soa.smext.faa.gov/asws/api/airport/status/";

    /* Enforce Singleton Pattern */
    private static AirportsDBProxy instance;

    /* Singleton Pattern accessor */
    public static AirportsDBProxy getInstance() {
        if(instance == null) instance = new AirportsDBProxy();
        return instance;
    }

    /**
     *
     * Depending on faaToggled, get airport information from the specified source
     *
     * @param airportCode - 3 letter code
     * @return Airport object either from FAA or local AirportsDB
     */
    public Airport getAirport(String airportCode){
        if(faaToggled) {
            JSONObject json = getJsonFromApi(airportCode);
            Airport airport = generateAirport(json, airportCode);
            return airport;
        }

        return this.airportsDB.getAirport(airportCode);
    }

    /**
     * change which service is being used for airport information
     */
    public void toggleService(){
        faaToggled = !faaToggled;
    }

    /**
     * Get the json object for a specified airport
     *
     * @param airportCode to be searched for
     * @return json object that gets returned from api call
     */
    private JSONObject getJsonFromApi(String airportCode){

        JSONObject json;

        try {
            String url = urlPreface + airportCode;
            URL FAAURL = new URL(url);
            HttpURLConnection urlConnection =
                    (HttpURLConnection) FAAURL.openConnection();

            // Set the paramters for the HTTP Request
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Accept", "application/json");

            // Create an input stream and wrap in a BufferedReader to read the
            // response.
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            // MAKE SURE TO CLOSE YOUR CONNECTION!
            in.close();

            JSONParser parser = new JSONParser();
            json = (JSONObject) parser.parse(response.toString());
            return json;
        }
        catch (FileNotFoundException e) {
            System.out.print("URL not found: ");
            System.out.println(e.getMessage());
        }
        catch (MalformedURLException e) {
            System.out.print("Malformed URL: ");
            System.out.println(e.getMessage());
        }
        catch (ProtocolException e) {
            System.out.print("Unsupported protocol: ");
            System.out.println(e.getMessage());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        catch(ParseException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * generate an Airport object from json data
     *
     * @param json returned from api call
     * @return newly generated airport
     */
    private Airport generateAirport(JSONObject json, String airportCode){
        String name = json.get("Name").toString();

        int delay = getDelay(json);

        WeatherList weathers = new WeatherList();
        //is the min connection time going to be zero here?
        Airport airport = new  Airport(name, airportCode, weathers, delay, 0);
        Weather weather = getWeather(json);
        airport.addWeather(weather);

        return airport;
    }

    /**
     *
     * @param json returned from api call
     * @return integer value for delay
     */
    private int getDelay(JSONObject json){
        Object delayObj = json.get("Delay");
        Boolean isDelay = (Boolean) delayObj;
        if(isDelay){
            JSONArray statusArr = (JSONArray) json.get("Status");
            JSONObject statusObj = (JSONObject) statusArr.get(0);
            if(statusObj.containsKey("AvgDelay")){
                String delay = statusObj.get("AvgDelay").toString();
                return Integer.parseInt(delay.substring(0,delay.indexOf(" ")));
            }
            else if(statusObj.containsKey("MinDelay")){
                String delay = statusObj.get("MinDelay").toString();
                return Integer.parseInt(delay.substring(0,delay.indexOf(" ")));
            }
            else if(statusObj.containsKey("MaxDelay")){
                String delay = statusObj.get("MaxDelay").toString();
                return Integer.parseInt(delay.substring(0,delay.indexOf(" ")));
            }
        }

        return 0;
    }

    /**
     *
     * @param json returned from api call
     * @return new Weather object
     */
    private Weather getWeather(JSONObject json){
        JSONObject wObj = (JSONObject) json.get("Weather");

        //temperate
        String tempStr = wObj.get("Temp").toString();
        int temp = (int) Double.parseDouble(tempStr.substring(2,tempStr.indexOf("F")-1));

        //condition
        JSONArray wArray = (JSONArray) wObj.get("Weather");
        JSONObject firstWeather = (JSONObject) wArray.get(0);
        String condition = firstWeather.get("Temp").toString().replace("\\", "");
        condition = condition.substring(2,condition.length()-2);

        return new Weather(temp, condition);
    }

    @Override
    public String toString() {
        if(faaToggled){
            return "FAA";
        }
        return "local";
    }

}