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

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

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

        button = new Button("Continue");
        button.setOnAction(e -> adrenalineGUI.willChooseConnection());
        boxButton = new HBox(button);
        boxButton.setAlignment(Pos.CENTER);
        boxButton.setMargin(button, new Insets(0, 0, 50, 0));

        root = new BorderPane();
        BackgroundImage myBI= new BackgroundImage(new Image(new FileInputStream("src/main/resources/images/adrenaline.jpg")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        root.setBackground(new Background(myBI));
        root.setBottom(boxButton);

        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public void chooseConnection(){

        root.getChildren().clear();
        root.setStyle("-fx-background-color: white");

        CheckBox checkBoxRMI = new CheckBox("RMI");
        CheckBox checkBoxTCP = new CheckBox("TCP");
        Label label = new Label("Select a connection");
        button.setOnAction(e -> handleConnectionOptions(checkBoxRMI, checkBoxTCP));
        checkBoxRMI.setOnAction(e -> handleOptionsRMI(checkBoxRMI, checkBoxTCP));
        checkBoxTCP.setOnAction(e -> handleOptionsTCP(checkBoxRMI, checkBoxTCP));

        HBox boxRMI = new HBox(checkBoxRMI);
        boxRMI.setAlignment(Pos.CENTER);

        HBox boxTCP = new HBox(checkBoxTCP);
        boxTCP.setAlignment(Pos.CENTER);

        HBox boxLabel = new HBox(label);
        boxLabel.setAlignment(Pos.CENTER);
        boxLabel.setMargin(label, new Insets(50, 0, 0, 0));

        HBox center = new HBox(boxRMI, boxTCP);
        center.setAlignment(Pos.CENTER);
        center.setSpacing(150);
        root.setCenter(center);
        root.setBottom(boxButton);
        root.setTop(boxLabel);
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

            button.setOnAction(e -> handlePortAddressOptions(portField, addressField));

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

    public void handlePortAddressOptions(TextField portTextfield, TextField addressTextfield){
        String port = portTextfield.getText();
        Integer portNumber = Integer.parseInt(port);
        String address = addressTextfield.getText();
        this.adrenalineGUI.didChooseConnection(connectionType, portNumber, address);
        //login();
        try {
            chooseGameMap();
        } catch (FileNotFoundException e){

        }
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
        adrenalineGUI.createUser(username);
    }

    public void handleLoginFailure() {
        Text text = new Text("Username already in use");
        VBox box = new VBox(text, boxButton);
        box.setAlignment(Pos.CENTER);
        root.setBottom(box);
    }

    public void characterChoice(ArrayList<String> availableCharacters) throws FileNotFoundException{
        root.getChildren().clear();

        Text text = new Text();
        HBox boxText = new HBox(text);
        boxText.setAlignment(Pos.CENTER);

        Text tBlue = new Text("BANSHEE:");
        Image imageBlue;
        if(availableCharacters.contains("blue")) {
            imageBlue = new Image(new FileInputStream("src/main/resources/images/characters/blue_character_taken.png"));
        }
        else {
            imageBlue = new Image(new FileInputStream("src/main/resources/images/characters/blue_character.png"));
        }
        ImageView ivBlue = new ImageView();
        ivBlue.setImage(imageBlue);
        VBox boxBlue = new VBox(tBlue, ivBlue);
        boxBlue.setAlignment(Pos.CENTER);

        Text tGreen = new Text("SPROG:");
        Image imageGreen;
        if(availableCharacters.contains("green")) {
            imageGreen = new Image(new FileInputStream("src/main/resources/images/characters/green_character_taken.png"));
        }
        else {
            imageGreen = new Image(new FileInputStream("src/main/resources/images/characters/green_character.png"));
        }
        ImageView ivGreen = new ImageView();
        ivGreen.setImage(imageGreen);
        VBox boxGreen = new VBox(tGreen, ivGreen);
        boxGreen.setAlignment(Pos.CENTER);

        Text tGrey = new Text("DOZER:");
        Image imageGrey;
        if(availableCharacters.contains("grey")) {
            imageGrey = new Image(new FileInputStream("src/main/resources/images/characters/grey_character_taken.png"));
        }
        else {
            imageGrey = new Image(new FileInputStream("src/main/resources/images/characters/grey_character.png"));
        }
        ImageView ivGrey = new ImageView();
        ivGrey.setImage(imageGrey);
        VBox boxGrey = new VBox(tGrey, ivGrey);
        boxGrey.setAlignment(Pos.CENTER);

        Text tPurple = new Text("VIOLET:");
        Image imagePurple;
        if(availableCharacters.contains("purple")) {
            imagePurple = new Image(new FileInputStream("src/main/resources/images/characters/purple_character_taken.png"));
        }
        else {
            imagePurple = new Image(new FileInputStream("src/main/resources/images/characters/purple_character.png"));
        }
        ImageView ivPurple = new ImageView();
        ivPurple.setImage(imagePurple);
        VBox boxPurple = new VBox(tPurple, ivPurple);
        boxPurple.setAlignment(Pos.CENTER);

        Text tYellow = new Text("D-STRUCT-OR:");
        Image imageYellow;
        if(availableCharacters.contains("yellow")) {
            imageYellow = new Image(new FileInputStream("src/main/resources/images/characters/yellow_character_taken.png"));
        }
        else {
            imageYellow = new Image(new FileInputStream("src/main/resources/images/characters/yellow_character.png"));
        }
        ImageView ivYellow = new ImageView();
        ivYellow.setImage(imageYellow);
        VBox boxYellow = new VBox(tYellow, ivYellow);
        boxYellow.setAlignment(Pos.CENTER);

        HBox imagesBox = new HBox(boxBlue, boxGreen, boxGrey, boxPurple, boxYellow);
        VBox generalBox = new VBox(imagesBox, boxText);
        root.getChildren().add(generalBox);
        root.setBottom(boxButton);



        ivBlue.setOnMouseClicked(e -> {
            text.setText("You selected BANSHEE!");
            button.setOnAction(ev -> handleCharactersOptions("blue"));
        });

        ivGreen.setOnMouseClicked(e -> {
            text.setText("You selected SPROG!");
            button.setOnAction(ev -> handleCharactersOptions("green"));
        });

        ivGrey.setOnMouseClicked(e -> {
            text.setText("You selected DOZER!");
            button.setOnAction(ev -> handleCharactersOptions("grey"));
        });

        ivPurple.setOnMouseClicked(e -> {
            text.setText("You selected VIOLET!");
            button.setOnAction(ev -> handleCharactersOptions("purple"));
        });

        ivYellow.setOnMouseClicked(e -> {
            text.setText("You selected D-STRUCT-OR!");
            button.setOnAction(ev -> handleCharactersOptions("yellow"));
        });
    }

    public void handleCharactersOptions(String selectedCharacter){
        adrenalineGUI.didChooseCharacter(selectedCharacter);
    }

    public void chooseGameMap() throws FileNotFoundException{
        root.getChildren().clear();

        Text tFirst = new Text("First configuration, good for 3 or 4 players:");
        Image firstLeft = new Image(new FileInputStream("src/main/resources/images/characters/board1.png"));
        ImageView ivFirstLeft = new ImageView();
        ivFirstLeft.setImage(firstLeft);
        Image firstRight = new Image(new FileInputStream("src/main/resources/images/characters/board4.png"));
        ImageView ivFirstRight = new ImageView();
        ivFirstRight.setImage(firstRight);
        HBox boxFirst = new HBox(ivFirstLeft, ivFirstRight);
        VBox vBoxFirst = new VBox(tFirst, boxFirst);

        Text tSecond = new Text("Second configuration, good for any number of players:");
        Image secondLeft = new Image(new FileInputStream("src/main/resources/images/characters/board1.png"));
        ImageView ivSecondLeft = new ImageView();
        ivSecondLeft.setImage(secondLeft);
        Image secondRight = new Image(new FileInputStream("src/main/resources/images/characters/board2.png"));
        ImageView ivSecondRight = new ImageView();
        ivSecondRight.setImage(secondRight);
        HBox boxSecond = new HBox(ivSecondLeft, ivSecondRight);
        VBox vBoxSecond = new VBox(tSecond, boxSecond);

        Text tThird = new Text("Third configuration, good for any number of players:");
        Image thirdLeft = new Image(new FileInputStream("src/main/resources/images/characters/board3.png"));
        ImageView ivThirdLeft = new ImageView();
        ivThirdLeft.setImage(thirdLeft);
        Image thirdRight = new Image(new FileInputStream("src/main/resources/images/characters/bard2.png"));
        ImageView ivThirdRight = new ImageView();
        ivThirdRight.setImage(thirdRight);
        HBox boxThird = new HBox(ivThirdLeft, ivThirdRight);
        VBox vBoxThird = new VBox(tThird, boxThird);

        Text tFourth = new Text("Fourth configuration, good for 4 or 5 players:");
        Image fourthLeft = new Image(new FileInputStream("src/main/resources/images/characters/board3.png"));
        ImageView ivFourthLeft = new ImageView();
        ivThirdLeft.setImage(fourthLeft);
        Image fourthRight = new Image(new FileInputStream("src/main/resources/images/characters/bard4.png"));
        ImageView ivFourthRight = new ImageView();
        ivFourthRight.setImage(fourthRight);
        HBox boxFourth = new HBox(ivFourthLeft, ivFourthRight);
        VBox vBoxFourth = new VBox(tFourth, boxFourth);

        HBox generalBox = new HBox(vBoxFirst, vBoxSecond, vBoxThird, vBoxFourth);
        generalBox.setMargin(vBoxFirst, new Insets(10,10,10,10));
        generalBox.setMargin(vBoxSecond, new Insets(10,10,10,10));
        generalBox.setMargin(vBoxThird, new Insets(10,10,10,10));
        generalBox.setMargin(vBoxFourth, new Insets(10,10,10,10));

        root.getChildren().add(generalBox);
    }




}
