import Airports.AirportInfoService;
import Airports.AirportsDBProxy;
import Commands.InputParser;
import Parser.CSVParser;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * The GUI class which handles building GUI elements as well as the GUI's interactions with the system
 * Created by PeiShi Long on 03/23/2018
 */
public class GUI extends Application{

    //New window button
    private Button newbutton = new Button("New Window");
    //Submit button
    private Button submitbutton = new Button("Submit");
    //A text area that show the input formats
    private TextArea input = new TextArea();
    //A text area that show the response info that user request
    private TextArea output = new TextArea();
    //Request label
    private Label requestLabel = new Label("Request: ");
    //connection status label
    private Label connectionStatus = new Label("Connection Status: Disconnected");

    //The TextField where the user will type and enter their commands.
    private TextField requestTextField = new TextField();

    //The inputted request text
    private String requestText;
    private static CSVParser csvp;

    //Text for input formats that display in Request Information text area
    public void helper(){
        String s = "Input should follow one of the following formats:\n" +
                "(Anything in brackets are optional and\n" +
                "all commands are ended with semi-colons)\n" +
                " \n" +
                "Flight information request: info,origin,destination[,connections[,sort-order]]\n" +
                " \n" +
                "Make reservation request: reserve,id,passenger\n" +
                " \n" +
                "Retrieve reservations request: retrieve,passenger[,origin[,destination]]\n" +
                " \n" +
                "Delete reservation request: delete,passenger,origin,destination\n" +
                " \n" +
                "Airport information request: airport,airport\n" +
                " \n"+
                "---------------------------------------------------------------------\n" +
                " \n" +
                "Type HELP to see commands again\n" +
                "Type SERVER to use FAA service\n" +
                "Type QUIT to exit\n";
        input.setText(s);
        input.setWrapText(true);
    }

    /**
     * The start method used by the parent application class
     * This class initializes the GUI and builds all of its components
     * @param primaryStage the GUI window
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane b = new BorderPane();
        b.setPadding(new Insets(10,10,10,10));

        helper();

        //Treat the return key the same as pressing submit
        requestTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER){
                    requestText = requestTextField.getText();
                    connectAFRS();
                    requestTextField.setText("");

                }
            }
        });

        b.setCenter(buildCenter());

        //Event handler for the submit key, sends command to parser
        submitbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                requestText = requestTextField.getText();
                connectAFRS();
                requestTextField.setText("");

            }
        });

        b.setTop(buildTop());

        //Event handler to start a new instance of GUI
        newbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = new Stage();
                try {
                    GUI foo = new GUI();
                    foo.start(stage);
                }
                catch(Exception e){

                }
            }
        });
        b.setBottom(buildBottom());
        Scene scene = new Scene(b, 950,450);
        primaryStage.setTitle("AFRS");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Builds the bottom of the border pane used by the GUI
     * @return HBox with the request label, along with text field and submit button
     */
    private HBox buildBottom(){
        HBox hb = new HBox();
        VBox vb = new VBox();
        HBox.setHgrow(requestTextField,Priority.ALWAYS);
        hb.getChildren().addAll(requestLabel,requestTextField,submitbutton);
        return hb;
    }

    /**
     * Build the center of the border pane
     * @return HBox with the 'Request Information' and 'Respond' label as well as two text boxes
     */
    private HBox buildCenter(){
        HBox hb = new HBox();
        VBox vbRequest = new VBox();
        VBox vbResponse = new VBox();
        Label requestInfo = new Label("Request Information");
        Label responseInfo = new Label("Response");
        //hb.setSpacing(2);
        HBox.setHgrow(input, Priority.ALWAYS);
        HBox.setHgrow(output,Priority.ALWAYS);
        input.setEditable(false);
        output.setEditable(false);
        VBox.setVgrow(input,Priority.ALWAYS);
        VBox.setVgrow(output,Priority.ALWAYS);
        vbRequest.getChildren().addAll(requestInfo,input);
        vbResponse.getChildren().addAll(responseInfo,output);
        hb.getChildren().addAll(vbRequest,vbResponse);
        return hb;
    }

    /**
     * Build the top of the BorderPane  with e New window button and connextion status
     * @return HBox
     */
    private HBox buildTop(){
        HBox hb = new HBox();
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        hb.getChildren().addAll(newbutton,region,connectionStatus);
        return hb;
    }

    //Sends commands to the system and displays the responses in the GUI
    private void connectAFRS(){
        InputParser parser;

        if(requestText.trim().toLowerCase().contains("quit")){
            output.setText("Thank you for using AFRS!");

            //save any reservations upon quitting
            csvp.writeToCSV();
            //System.exit(0);
            return;
        }
        if(requestText.trim().toLowerCase().contains("help")){
            requestText = "";
        }
        if(requestText.trim().toLowerCase().contains("server")) {

            //csvp.getAirports().switchAirportService();
            //String status = csvp.getAirports().getAirportService();

            AirportsDBProxy.getInstance().toggleService();
            AirportInfoService airportInfoService = AirportsDBProxy.getInstance();
            String status = airportInfoService.toString();
            if(status.equals("local")){
                connectionStatus.setText("Connection Status: Disconnected");
            }else if(status.equals("FAA")){
                connectionStatus.setText("Connection Status: Connected");
            }

        }

        if(requestText.trim().endsWith(";")){
            try{
                parser = new InputParser();
                parser.parseInput(requestText.substring(0, requestText.length() - 1));
                String cmdPrintout = parser.executeRequest();
                System.out.println(cmdPrintout);
                output.setText(cmdPrintout);
                requestText = "";
            }catch(Exception e){
                requestText = "";
                output.setText(e.getMessage());
            }
        }
    }

    /**
     * The main function to start the GUI
     * Create hashes in order to resets all the weather indexes every time
     * @param args unused
     */
    public static void main(String[] args){
        csvp = new CSVParser();
        csvp.createHashes();
        Application.launch(args);
    }
}
