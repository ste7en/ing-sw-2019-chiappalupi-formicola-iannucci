package it.polimi.ingsw.view.gui;
import it.polimi.ingsw.networking.utility.ConnectionType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GUIHandler extends Application  {

    private BorderPane root;
    private AdrenalineGUI adrenalineGUI;
    private HBox boxButton;
    private Button button;
    private ConnectionType connectionType;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.adrenalineGUI = new AdrenalineGUI(this);
        primaryStage.setTitle("Button test");
        CheckBox checkBoxRMI = new CheckBox("RMI");
        CheckBox checkBoxTCP = new CheckBox("TCP");
        button = new Button("Continue");
        Label label = new Label("Select a connection");
        button.setOnAction(e -> handleConnectionOptions(checkBoxRMI, checkBoxTCP));
        checkBoxRMI.setOnAction(e -> handleOptionsRMI(checkBoxRMI, checkBoxTCP));
        checkBoxTCP.setOnAction(e -> handleOptionsTCP(checkBoxRMI, checkBoxTCP));
        root = new BorderPane();

        HBox boxRMI = new HBox(checkBoxRMI);
        boxRMI.setAlignment(Pos.CENTER);

        HBox boxTCP = new HBox(checkBoxTCP);
        boxTCP.setAlignment(Pos.CENTER);

        boxButton = new HBox(button);
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

    public void handleConnectionOptions(CheckBox rmi, CheckBox tcp){
        root.getChildren().clear();

        Text portText = new Text("port number:  ");
        TextField portField = new TextField();
        HBox boxPort = new HBox(portText, portField);
        boxPort.setAlignment(Pos.CENTER_LEFT);
        boxPort.setMargin(portText,  new Insets(10, 0, 10, 50));
        boxPort.setMargin(portField,  new Insets(10, 50, 10, 0));

        if(rmi.isSelected()){
            connectionType = ConnectionType.RMI;
            Text text = new Text("Please provide a port number");
            HBox boxTextRMI = new HBox(text);
            boxTextRMI.setAlignment(Pos.CENTER);
            boxTextRMI.setMargin(text, new Insets(50, 0, 0, 0));

            button.setOnAction(e -> handlePortOptionsRMI(portField));

            root.setCenter(boxPort);
            root.setTop(boxTextRMI);
            root.setBottom(boxButton);
        }
        if(tcp.isSelected()){
            connectionType = ConnectionType.SOCKET;
            VBox generalBox = new VBox();

            Text text = new Text("Please provide a port number and an address");
            HBox boxTextTCP = new HBox(text);
            boxTextTCP.setAlignment(Pos.CENTER);

            Text addressText = new Text("address:         ");
            TextField addressField = new TextField();
            HBox boxAddress = new HBox(addressText, addressField);
            boxAddress.setAlignment(Pos.CENTER_LEFT);

            button.setOnAction(e -> handlePortOptionsTCP(portField, addressField));

            generalBox.getChildren().add(boxPort);
            generalBox.getChildren().add(boxAddress);
            generalBox.setFillWidth(true);
            generalBox.setAlignment(Pos.CENTER);

            boxTextTCP.setMargin(text, new Insets(50, 0, 0, 0));
            boxAddress.setMargin(addressText, new Insets(10, 0, 10, 50));
            boxAddress.setMargin(addressField, new Insets(10, 50, 10, 0));
            root.setCenter(generalBox);
            root.setTop(boxTextTCP);
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

    public void handlePortOptionsTCP(TextField portTextfield, TextField addressTextfield){
        String port = portTextfield.getText();
        Integer portNumber = Integer.parseInt(port);
        String address = addressTextfield.getText();
        this.adrenalineGUI.didChooseConnection(ConnectionType.SOCKET, portNumber, address);
        login();
    }

    public void handlePortOptionsRMI(TextField portTextfield){
        String port = portTextfield.getText();
        Integer portNumber = Integer.parseInt(port);
        adrenalineGUI.didChooseConnection(ConnectionType.RMI, portNumber, null);
        login();
    }

    public void login(){
        root.getChildren().clear();
        Text text = new Text("Please provide a username");
        HBox boxTextlogin = new HBox(text);
        boxTextlogin.setAlignment(Pos.CENTER);

        Text usernameText = new Text("username: ");
        TextField usernameField = new TextField();
        HBox boxUsername = new HBox(usernameText, usernameField);
        boxUsername.setAlignment(Pos.CENTER_LEFT);

        button.setOnAction(e -> handleLoginOptions(usernameField));

        boxTextlogin.setMargin(text, new Insets(50, 0, 0, 0));
        boxUsername.setMargin(usernameText,  new Insets(0, 0, 0, 50));
        boxUsername.setMargin(usernameField,  new Insets(0, 50, 0, 0));
        root.setCenter(boxUsername);
        root.setTop(boxTextlogin);
        root.setBottom(boxButton);
    }

    public void handleLoginOptions(TextField usernameTextfield){
        String username = usernameTextfield.getText();
        adrenalineGUI.getClient().login(username);
    }

    public void characterChoice() throws FileNotFoundException{
        root.getChildren().clear();
        Image image = new Image(new FileInputStream("src/main/resources/images/board/board1.png"));
        ImageView iv1 = new ImageView();
        iv1.setImage(image);
        root.getChildren().add(iv1);
    }





}
