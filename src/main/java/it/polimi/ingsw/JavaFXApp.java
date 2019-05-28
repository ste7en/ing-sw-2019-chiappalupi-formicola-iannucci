package it.polimi.ingsw;

import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.view.gui.AdrenalineGUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class JavaFXApp extends Application {

    private BorderPane root;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Button test");
        CheckBox checkBoxRMI = new CheckBox("RMI");
        CheckBox checkBoxTCP = new CheckBox("TCP");
        Button button = new Button("Continue");
        Label label = new Label("Select a connection");
        button.setOnAction(e -> handleOptions(checkBoxRMI, checkBoxTCP));
        checkBoxRMI.setOnAction(e -> handleOptionsRMI(checkBoxRMI, checkBoxTCP));
        checkBoxTCP.setOnAction(e -> handleOptionsTCP(checkBoxRMI, checkBoxTCP));
        root = new BorderPane();

        HBox boxRMI = new HBox(checkBoxRMI);
        boxRMI.setAlignment(Pos.CENTER);

        HBox boxTCP = new HBox(checkBoxTCP);
        boxTCP.setAlignment(Pos.CENTER);

        HBox boxButton = new HBox(button);
        boxButton.setAlignment(Pos.CENTER);
        boxButton.setMargin(button, new Insets(0, 0, 50, 0));

        HBox boxLabel = new HBox(label);
        boxLabel.setAlignment(Pos.CENTER);
        boxLabel.setMargin(label, new Insets(50, 0, 0, 0));

        HBox center = new HBox(boxRMI, boxTCP);
        center.setAlignment(Pos.CENTER);
        center.setSpacing(150);
        root.setCenter(center);
        root.setBottom(boxButton);
        root.setTop(boxLabel);

        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public void handleOptions(CheckBox rmi, CheckBox tcp){
        if(rmi.isSelected()){
            System.out.println();
        }
        if(tcp.isSelected()){
            System.out.println("Hai selezionato TCP!");
            root.getChildren().clear();
            VBox generalBox = new VBox();
            Text text = new Text("Please provide a port number and an address");
            HBox boxText = new HBox(text);
            boxText.setAlignment(Pos.CENTER);

            Text portText = new Text("port number:  ");
            TextField portField = new TextField();
            HBox boxPort = new HBox(portText, portField);
            boxPort.setAlignment(Pos.CENTER_LEFT);

            Text addressText = new Text("address:         ");
            TextField addressField = new TextField();
            HBox boxAddress = new HBox(addressText, addressField);
            boxAddress.setAlignment(Pos.CENTER_LEFT);

            Button button = new Button();
            button.setText("Continue");
            HBox boxButton = new HBox(button);
            boxButton.setAlignment(Pos.CENTER);
            button.setOnAction(e -> handlePortOptions(portField, addressField));

            generalBox.setFillWidth(true);
            generalBox.getChildren().add(boxPort);
            generalBox.getChildren().add(boxAddress);
            generalBox.setAlignment(Pos.CENTER);

            boxButton.setMargin(button, new Insets(0, 0, 50, 0));
            boxText.setMargin(text, new Insets(50, 0, 0, 0));
            generalBox.setMargin(boxPort, new Insets(10, 50, 10, 50));
            generalBox.setMargin(boxAddress, new Insets(10, 50, 10, 50));
            root.setCenter(generalBox);
            root.setTop(boxText);
            root.setBottom(boxButton);
        }
        if(!tcp.isSelected() && !rmi.isSelected()){
            System.out.println("Seleziona qualcosa!");
        }
    }



    public void handleOptionsRMI(CheckBox rmi, CheckBox tcp){
        if(rmi.isSelected()) {
            tcp.setSelected(false);
        }
    }

    public void handleOptionsTCP(CheckBox rmi, CheckBox tcp){
        if(tcp.isSelected()) {
            rmi.setSelected(false);
        }
    }

    public void handlePortOptions(TextField portTextfield, TextField addressTextfield){
        String port = portTextfield.getText();
        Integer portNumber = Integer.parseInt(port);
        String address = addressTextfield.getText();
        AdrenalineGUI connectionHandler = new AdrenalineGUI();
        connectionHandler.didChooseConnection(ConnectionType.SOCKET, portNumber, address);

    }
}
