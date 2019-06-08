package it.polimi.ingsw.view.gui;
import it.polimi.ingsw.model.utility.PlayerColor;
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

        if (!tcp.isSelected() && !rmi.isSelected()){
            System.out.println("Seleziona qualcosa!");
        }

        else {
            root.getChildren().clear();

            VBox generalBox = new VBox();

            Text text = new Text("Please provide a port number and an address");
            HBox boxText = new HBox(text);
            boxText.setAlignment(Pos.CENTER);

            Text portText = new Text("port number:  ");
            TextField portField = new TextField();
            HBox boxPort = new HBox(portText, portField);
            boxPort.setAlignment(Pos.CENTER_LEFT);
            boxPort.setMargin(portText, new Insets(10, 0, 10, 50));
            boxPort.setMargin(portField, new Insets(10, 50, 10, 0));

            Text addressText = new Text("address:         ");
            TextField addressField = new TextField();
            HBox boxAddress = new HBox(addressText, addressField);
            boxAddress.setAlignment(Pos.CENTER_LEFT);

            button.setOnAction(e -> handlePortOptions(portField, addressField));

            generalBox.getChildren().add(boxPort);
            generalBox.getChildren().add(boxAddress);
            generalBox.setFillWidth(true);
            generalBox.setAlignment(Pos.CENTER);

            boxText.setMargin(text, new Insets(50, 0, 0, 0));
            boxAddress.setMargin(addressText, new Insets(10, 0, 10, 50));
            boxAddress.setMargin(addressField, new Insets(10, 50, 10, 0));
            root.setCenter(generalBox);
            root.setTop(boxText);
            root.setBottom(boxButton);

            if (rmi.isSelected()) {
                connectionType = ConnectionType.RMI;
            }
            if (tcp.isSelected()) {
                connectionType = ConnectionType.SOCKET;
            }
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
        this.adrenalineGUI.didChooseConnection(connectionType, portNumber, address);
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

    public void handleLoginOptions(TextField usernameTextfield) {
        String username = usernameTextfield.getText();
        adrenalineGUI.getClient().createUser(username);
        try {
            characterChoice();
        } catch (FileNotFoundException e){
            System.err.println("ClientRMI exception: " + e.toString());
        }
    }

    public void characterChoice() throws FileNotFoundException{
        root.getChildren().clear();

        Text text = new Text();
        HBox boxText = new HBox(text);
        boxText.setAlignment(Pos.CENTER);


        /*
        HBox generalBox = new HBox();
        for (PlayerColor playerColor : PlayerColor.values()){
            Image image = new Image(new FileInputStream("src/main/resources/images/characters/" + playerColor+ "_character.png"));
            ImageView iv = new ImageView();
            iv.setImage(image);
            generalBox.getChildren().add(iv);
        }
        */
        Text tBlue = new Text("BANSHEE:");
        Image imageBlue = new Image(new FileInputStream("src/main/resources/images/characters/blue_character.png"));
        ImageView ivBlue = new ImageView();
        ivBlue.setImage(imageBlue);
        VBox boxBlue = new VBox(tBlue, ivBlue);
        boxBlue.setAlignment(Pos.CENTER);

        Text tGreen = new Text("SPROG:");
        Image imageGreen = new Image(new FileInputStream("src/main/resources/images/characters/green_character_taken.png"));
        ImageView ivGreen = new ImageView();
        ivGreen.setImage(imageGreen);
        VBox boxGreen = new VBox(tGreen, ivGreen);
        boxGreen.setAlignment(Pos.CENTER);

        Text tGrey = new Text("DOZER:");
        Image imageGrey = new Image(new FileInputStream("src/main/resources/images/characters/grey_character.png"));
        ImageView ivGrey = new ImageView();
        ivGrey.setImage(imageGrey);
        VBox boxGrey = new VBox(tGrey, ivGrey);
        boxGrey.setAlignment(Pos.CENTER);

        Text tPurple = new Text("VIOLET:");
        Image imagePurple = new Image(new FileInputStream("src/main/resources/images/characters/purple_character.png"));
        ImageView ivPurple = new ImageView();
        ivPurple.setImage(imagePurple);
        VBox boxPurple = new VBox(tPurple, ivPurple);
        boxPurple.setAlignment(Pos.CENTER);

        Text tYellow = new Text("D-STRUCT-OR:");
        Image imageYellow = new Image(new FileInputStream("src/main/resources/images/characters/yellow_character_taken.png"));
        ImageView ivYellow = new ImageView();
        ivYellow.setImage(imageYellow);
        VBox boxYellow = new VBox(tYellow, ivYellow);
        boxYellow.setAlignment(Pos.CENTER);

        HBox imagesBox = new HBox(boxBlue, boxGreen, boxGrey, boxPurple, boxYellow);
        VBox generalBox = new VBox(imagesBox, boxText);
        root.getChildren().add(generalBox);

        ivBlue.setOnMouseClicked(e -> {
            text.setText("You selected BANSHEE!");
        });

        ivGreen.setOnMouseClicked(e -> {
            text.setText("You selected SPROG!");
        });

        ivGrey.setOnMouseClicked(e -> {
            text.setText("You selected DOZER!");
        });

        ivPurple.setOnMouseClicked(e -> {
            text.setText("You selected VIOLET!");
        });

        ivYellow.setOnMouseClicked(e -> {
            text.setText("You selected D-STRUCT-OR!");
        });
    }







}
