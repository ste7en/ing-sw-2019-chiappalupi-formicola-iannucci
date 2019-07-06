package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.utility.MapType;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.utility.Loggable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

@SuppressWarnings("all")
public class GUIHandler extends Application {

    private static final String IMAGE_DIR_PATH = "images/";
    private static final String CHARACTERS_DIR_PATH = "characters/";
    private static final String CHARACTER_FILE_UNCLICKED_FORMAT = "_unclicked";
    private static final String BOARD_DIR_PATH = "board/";
    private static final String POWERUPS_DIR_PATH = "powerups/";
    private static final String ACTIONS_DIR_PATH = "actions/";
    private static final String WEAPONS_DIR_PATH = "weapons/";
    private static final String AMMOTILES_DIR_PATH = "ammotiles/";
    private static final String PLAYERBOARDS_DIR_PATH = "playerboards/";
    private static final String PLAYERBOARD_FILE_FORMAT = "playerboard_";
    private static final String PNG_FILE_EXT = ".png";


    //main attributes
    private AdrenalineGUI adrenalineGUI;
    private Stage primaryStage;
    private Scene mainScene;
    private Background background;
    private String conf;
    private static final Font andaleMonoFont = Font.font("Andale Mono");

    //game map
    private GridPane boardGrid;
    private GridPane mapGrid;
    private GridPane cardsContainer;
    private GridPane leftDeck;
    private GridPane upperDeck;
    private GridPane rightDeck;
    private List<List<StackPane>> cellsContainers;
    private List<List<Button>> cellsButtons;
    private List<StackPane> yellowAmmos;
    private List<StackPane> redAmmos;
    private List<StackPane> blueAmmos;
    private List<StackPane> weapons;
    private List<StackPane> powerups;
    private List<StackPane> deckLeft;
    private List<StackPane> leftBackup;
    private List<StackPane> deckRight;
    private List<StackPane> rightBackup;
    private List<StackPane> deckAbove;
    private List<StackPane> aboveBackup;
    private GridPane cardsDisplayer;
    Button mapButton;
    Button backToMapButton;


    //playerboard
    private HashMap<String ,StackPane> playerBoards;
    private HashMap<String, GridPane> damagesGrid;
    private HashMap<String, List <StackPane>> bloodTruck;
    private HashMap<String, List <StackPane>> marksTruck;
    private HashMap<String, List<StackPane>> killedPointsTruck;
    private Map<Integer, List<String>> weaponsInRespawn;

    private ColumnConstraints c;
    private RowConstraints r;
    private int counter;
    private Button button;
    private StackPane buttonContainer;
    private Button button2;
    private Button button3;
    private StackPane pickButtonContainer;

    private HBox boxButton;
    private VBox textContainer;
    private VBox textContainerSettingChoice;
    private VBox textSelectedContainer;
    private BorderPane firstRoot;
    private GridPane modesChoiceGrid;
    private GridPane messageGrid;

    private DoubleProperty fontSize = new SimpleDoubleProperty(10);
    private ConnectionType connectionType;
    private Scene secondScene;

    private int currentDeck;
    private int currentCard;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.adrenalineGUI = new AdrenalineGUI(this);
        primaryStage.setTitle("Adrenaline");
        textContainer = new VBox();
        textContainer.setAlignment(Pos.CENTER);
        textContainerSettingChoice = new VBox();
        textContainerSettingChoice.setAlignment(Pos.CENTER);
        textSelectedContainer = new VBox();
        textSelectedContainer.setAlignment(Pos.CENTER);
        modesChoiceGrid = new GridPane();

        pickButtonContainer = new StackPane();
        buttonContainer=new StackPane();
        button2 = new Button();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().add(button2);

        counter = 0;

        BackgroundImage backgroundImageAfter= new BackgroundImage(new Image(new FileInputStream("src/main/resources/images/background.png")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
        background = new Background(backgroundImageAfter);
        buildMessageGrid();
        button = new Button("Continue");
        button3 = new Button("Buy Card");
        button.setOnAction(e -> {try{chooseConnection();}catch (Exception ex){ex.printStackTrace();}});
        boxButton = new HBox(button);
        boxButton.setAlignment(Pos.CENTER);
        boxButton.setMargin(button, new Insets(0, 0, 50, 0));

        firstRoot = new BorderPane();
        BackgroundImage backgroundImage = new BackgroundImage(getImageFromPath(IMAGE_DIR_PATH+"adrenaline.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, true));
        firstRoot.setBackground(new Background(backgroundImage));
        firstRoot.setBottom(boxButton);

        weaponsInRespawn = new HashMap<>();

        //Main modesChoiceGrid creation
        boardGrid = new GridPane();
        ColumnConstraints column0 = new ColumnConstraints();
        column0.setPercentWidth(40);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(60);
        boardGrid.getColumnConstraints().addAll(column0, column1);
        RowConstraints rowtext = new RowConstraints();
        rowtext.setPercentHeight(7);
        RowConstraints rowbutton = new RowConstraints();
        rowbutton.setPercentHeight(6);
        RowConstraints row0 = new RowConstraints();
        row0.setPercentHeight(13);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(13);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(13);
        RowConstraints row3 = new RowConstraints();
        row3.setPercentHeight(13);
        RowConstraints row4 = new RowConstraints();
        row4.setPercentHeight(35);
        boardGrid.getRowConstraints().addAll(rowtext, rowbutton, row0, row1, row2, row3, row4);
        mapButton = new Button();
        backToMapButton = new Button();
        secondScene = new Scene(boardGrid, 1200, 675);

        mainScene = new Scene(firstRoot, 1200,800);
        primaryStage.setScene(mainScene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        AdrenalineLogger.setLogName("GUI");
        launch(args);
    }


    public void chooseConnection() {

        firstRoot.getChildren().clear();
        firstRoot.setBackground(background);
        firstRoot.setBottom(boxButton);

        CheckBox checkBoxRMI = new CheckBox("RMI");
        CheckBox checkBoxTCP = new CheckBox("TCP");
        checkBoxRMI.setTextFill(Color.WHITE);
        checkBoxRMI.setStyle("-fx-font-family: 'Andale Mono';");
        checkBoxTCP.setTextFill(Color.WHITE);
        checkBoxTCP.setStyle("-fx-font-family: 'Andale Mono';");


        button.setOnAction(e -> handleConnectionOptions(checkBoxRMI, checkBoxTCP));
        checkBoxRMI.setOnAction(e -> handleOptionsRMI(checkBoxRMI, checkBoxTCP));
        checkBoxTCP.setOnAction(e -> handleOptionsTCP(checkBoxRMI, checkBoxTCP));

        HBox boxRMI = new HBox(checkBoxRMI);
        boxRMI.setAlignment(Pos.CENTER);

        HBox boxTCP = new HBox(checkBoxTCP);
        boxTCP.setAlignment(Pos.CENTER);


        HBox center = new HBox(boxRMI, boxTCP);
        center.setAlignment(Pos.CENTER);
        center.setSpacing(150);
        firstRoot.setCenter(center);
        firstRoot.setBottom(boxButton);
        VBox connectionBox = new VBox();
        setText(connectionBox, "Please, select a connection");
        firstRoot.setTop(connectionBox);
    }

    public void handleConnectionOptions(CheckBox rmi, CheckBox tcp){

        if (!tcp.isSelected() && !rmi.isSelected()){
            System.out.println("Seleziona qualcosa!");
        }

        else {
            firstRoot.getChildren().clear();

            VBox generalBox = new VBox();

            Text text = new Text("Please provide a port number and an address");

            HBox boxText = new HBox(text);
            boxText.setAlignment(Pos.CENTER);

            Text portText = new Text("Port number");
            portText.setFont(andaleMonoFont);
            portText.setFill(Color.WHITE);
            TextField portField = new TextField();
            portText.setFill(Color.WHITE);
            portText.setStyle("-fx-font-family: 'Andale Mono';");


            HBox boxPort = new HBox(portText, portField);
            boxPort.setAlignment(Pos.CENTER_LEFT);
            HBox.setMargin(portText, new Insets(10, 0, 10, 50));
            HBox.setMargin(portField, new Insets(10, 50, 10, 0));

            Text addressText = new Text("address:      ");
            TextField addressField = new TextField();
            addressText.setFill(Color.WHITE);
            addressText.setStyle("-fx-font-family: 'Andale Mono';");

            HBox boxAddress = new HBox(addressText, addressField);
            boxAddress.setAlignment(Pos.CENTER_LEFT);
            HBox.setMargin(addressText, new Insets(10, 0, 10, 50));
            HBox.setMargin(addressField, new Insets(10, 50, 10, 0));

            var textVBox = new VBox(addressText, portText);
            textVBox.setSpacing(30);
            var fieldVBox = new VBox(addressField, portField);
            fieldVBox.setSpacing(15);

            var hbox = new HBox(textVBox, fieldVBox);
            HBox.setMargin(textVBox, new Insets(5, 0, 50, 20));

            hbox.setSpacing(8);

            button.setOnAction(e -> handlePortAddressOptions(portField, addressField));

            generalBox.getChildren().add(boxAddress);
            generalBox.getChildren().add(boxPort);
            generalBox.setFillWidth(true);
            generalBox.setAlignment(Pos.CENTER);

            HBox.setMargin(text, new Insets(50, 0, 0, 0));
            HBox.setMargin(addressText, new Insets(10, 0, 10, 50));
            HBox.setMargin(addressField, new Insets(10, 50, 10, 0));
            firstRoot.setCenter(generalBox);
            VBox chooseConnection = new VBox();
            setText(chooseConnection, "Please provide an address and a port number");
            firstRoot.setTop(chooseConnection);
            firstRoot.setBottom(boxButton);

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

    public void createGameMap(String configuration) {
        Platform.runLater(() -> {
            try {
                onCreateGameMap(configuration);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void handlePortAddressOptions(TextField portTextfield, TextField addressTextfield){

        String port = portTextfield.getText();
        Integer portNumber = Integer.parseInt(port);
        String address = addressTextfield.getText();
        this.adrenalineGUI.didChooseConnection(connectionType, portNumber, address);
        login();

        //try {
        //     chooseGameMap();
        //}catch (FileNotFoundException e){
        //      System.err.println("File exception:" + e.toString());
        // }

    }

    public void login(){
        firstRoot.getChildren().clear();

        Text usernameText = new Text("username: ");
        usernameText.setFill(Color.WHITE);
        usernameText.setStyle("-fx-font-family: 'Andale Mono';");
        TextField usernameField = new TextField();
        HBox boxUsername = new HBox(usernameText, usernameField);
        boxUsername.setAlignment(Pos.CENTER_LEFT);

        button.setOnAction(e -> {
            handleLoginOptions(usernameField);

        });

        boxUsername.setMargin(usernameText,  new Insets(0, 0, 0, 50));
        boxUsername.setMargin(usernameField,  new Insets(0, 50, 0, 0));
        firstRoot.setCenter(boxUsername);
        VBox textContainerUsername = new VBox();
        setText(textContainerUsername, "Please provide a username");
        firstRoot.setTop(textContainerUsername);
        firstRoot.setBottom(boxButton);
    }

    public void handleLoginOptions(TextField usernameTextfield) {
        String username = usernameTextfield.getText();
        adrenalineGUI.createUser(username);
    }

    public void handleLoginFailure() {
        Text text = new Text("Username already in use");
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-family: 'Andale Mono';");
        VBox box = new VBox(text, boxButton);
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER);
        firstRoot.setBottom(box);
    }

    public void onChooseCharacter(List<String> availableCharacters) {
        Platform.runLater(() -> {
            try {
                chooseCharacter(availableCharacters);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void chooseCharacter(List<String> availableCharacters) throws FileNotFoundException {
        modesChoiceGrid = new GridPane();
        for (int j = 0; j < 6; j++) {
            c = new ColumnConstraints();
            c.setPercentWidth(16.66);
            modesChoiceGrid.getColumnConstraints().add(c);
        }

        RowConstraints r0 = new RowConstraints();
        r0.setPercentHeight(5);
        RowConstraints r1 = new RowConstraints();
        r1.setPercentHeight(35);
        RowConstraints r2 = new RowConstraints();
        r2.setPercentHeight(35);
        RowConstraints r3 = new RowConstraints();
        r3.setPercentHeight(5);
        RowConstraints r4 = new RowConstraints();
        r4.setPercentHeight(20);
        modesChoiceGrid.getRowConstraints().addAll(r0, r1, r2,r3, r4);


        modesChoiceGrid.setBackground(background);

        Button buttonContinue = new Button("Continue");
        buttonContinue.setOnAction(e -> setText(textSelectedContainer, "You have to select something to continue"));
        StackPane buttonContinueContainer = new StackPane();
        buttonContinueContainer.setAlignment(Pos.CENTER);
        buttonContinueContainer.getChildren().add(buttonContinue);
        modesChoiceGrid.add(buttonContinueContainer, 0,4,6,1);


        setText(textContainerSettingChoice,"Select a character, these are the ones that are not taken");
        modesChoiceGrid.add(textContainerSettingChoice, 0,0,6,1);
        modesChoiceGrid.add(textSelectedContainer, 0,3,6,1);

        ArrayList<ImageView> imageViews = new ArrayList<>();
        for(int i=0; i<availableCharacters.size(); i++) {
            imageViews.add(new ImageView());
        }

            unclickedImage(imageViews, availableCharacters, CHARACTERS_DIR_PATH  );

        List<StackPane> stackPanes = new ArrayList<>();
        for(int i=0; i<availableCharacters.size(); i++) {
            stackPanes.add(new StackPane());
            setUpProperties(stackPanes.get(i), imageViews.get(i));
            if(i<3) modesChoiceGrid.add(stackPanes.get(i),i*2, 1,2,1);
            else modesChoiceGrid.add(stackPanes.get(i), ((i-3)*2)+1,2,2,1);

            ImageView imageView = imageViews.get(i);
            String character = availableCharacters.get(i);
            imageViews.get(i).setOnMouseClicked(event -> {
                        unclickedImage(imageViews, availableCharacters, CHARACTERS_DIR_PATH);
                    clickedImage(imageView, CHARACTERS_DIR_PATH + character);

                buttonContinue.setOnAction( e -> {


                    adrenalineGUI.didChooseCharacter(character);});


                        setText(textSelectedContainer, "You selected " + character);


            });

        }


        mainScene.setRoot(modesChoiceGrid);
    }



    public void onChooseCharacterSuccess() {
        Platform.runLater(this::chooseCharacterSuccess);
    }

    public void chooseCharacterSuccess(){
        modesChoiceGrid.getChildren().clear();
        setText(textSelectedContainer, "You chose your character, the game will start soon!");
        modesChoiceGrid.add(textSelectedContainer, 0,1,6,5);
    }

    public void chooseGameMap() {
        Platform.runLater(() -> {
            try {
                onChooseGameMap();
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void onChooseGameMap(){
        clear(modesChoiceGrid);

        for(int i=0; i<2; i++){
            c = new ColumnConstraints();
            c.setPercentWidth(50);
            modesChoiceGrid.getColumnConstraints().add(c);
        }
        RowConstraints r0 = new RowConstraints();
        r0.setPercentHeight(10);
        RowConstraints r1 = new RowConstraints();
        r1.setPercentHeight(40);
        RowConstraints r2 = new RowConstraints();
        r2.setPercentHeight(40);
        RowConstraints r3 = new RowConstraints();
        r3.setPercentHeight(10);
        modesChoiceGrid.getRowConstraints().addAll(r0,r1,r2,r3);

        modesChoiceGrid.setBackground(background);

        ArrayList<ImageView> imageViews = new ArrayList<>();
        for(int i=0; i<4; i++){
            imageViews.add(new ImageView());
        }

        ArrayList<String> confs = new ArrayList<>();
        for(int i=0; i<4; i++){
            confs.add(MapType.values()[i].toString());
        }

        try {
            unclickedImage(imageViews, confs, BOARD_DIR_PATH);
        } catch (Exception e){
            logOnException(e.getMessage(), e);
        }

        StackPane buttonContainer = new StackPane();
        Button gameMapButton = new Button("Continue");
        buttonContainer.getChildren().add(gameMapButton);
        buttonContainer.setAlignment(Pos.CENTER);

        List<StackPane> stackPanes = new ArrayList<>();
        for(int i=0; i<4; i++) {
            stackPanes.add(new StackPane());
            setUpProperties(stackPanes.get(i), imageViews.get(i));
            if(i<2) modesChoiceGrid.add(stackPanes.get(i),i, 1,1,1);
            else modesChoiceGrid.add(stackPanes.get(i), i-2,2,1,1);
            ImageView imageView = imageViews.get(i);
            String configuration = confs.get(i);
            imageViews.get(i).setOnMouseClicked(e -> {
                try {
                    unclickedImage(imageViews, confs, BOARD_DIR_PATH );
                    gameMapButton.setOnAction(e1 -> {adrenalineGUI.didChooseGameMap(configuration);});
                }catch (Exception exc){
                    logOnException(exc.getMessage(), exc);
                }
                clickedImage(imageView, BOARD_DIR_PATH + configuration + "_choice");
            });
        }

        modesChoiceGrid.add(buttonContainer, 0, 3,2,1);
        setText(textContainerSettingChoice, "Choose a map configuration");
        modesChoiceGrid.add(textContainerSettingChoice, 0,0,2,1);
        mainScene.setRoot(modesChoiceGrid);
    }

    public void drawTwoPowerups(List<String> powerups) {
        Platform.runLater(() -> {
            try {
                onDrawTwoPowerups(powerups);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void onDrawTwoPowerups(List<String> powerups){
        clear(modesChoiceGrid);

        for(int i=0; i<3; i++){
            c = new ColumnConstraints();
            if(i==1)c.setPercentWidth(17.5);
            else c.setPercentWidth(41.25);
            modesChoiceGrid.getColumnConstraints().add(c);
        }

        RowConstraints r0 = new RowConstraints();
        r0.setPercentHeight(20);
        RowConstraints r1 = new RowConstraints();
        r1.setPercentHeight(60);
        RowConstraints r2 = new RowConstraints();
        r2.setPercentHeight(20);
        modesChoiceGrid.getRowConstraints().addAll(r0,r1,r2);


        StackPane stackPane = new StackPane();
        ImageView imageView = new ImageView();
        setUpProperties(stackPane, imageView);
        modesChoiceGrid.add(stackPane,1,1 ,1,1);
        clickedImage(imageView, POWERUPS_DIR_PATH+"Back");

        Button powerupButton = new Button("Draw cards");
        StackPane powerupButtonContainer = new StackPane();
        powerupButtonContainer.setAlignment(Pos.CENTER);
        powerupButtonContainer.getChildren().add(powerupButton);
        powerupButton.setOnAction(e -> {try{chooseSpawnPoint(powerups);} catch (Exception ex){ex.printStackTrace();}});
        modesChoiceGrid.add(powerupButtonContainer, 0, 2,3,1);

        setText(textContainerSettingChoice, "Draw two powerups from the deck");
        modesChoiceGrid.add(textContainerSettingChoice, 0,0,3,1);

        mainScene.setRoot(modesChoiceGrid);
        primaryStage.setScene(mainScene);
    }

    private void chooseSpawnPoint(List<String> powerups) throws FileNotFoundException {
        if(modesChoiceGrid!=null) clear(modesChoiceGrid);
        else modesChoiceGrid = new GridPane();

        for(int i=0; i<5; i++){
            c = new ColumnConstraints();
            if(i==2) c.setPercentWidth(30);
            else c.setPercentWidth(17.5);
            modesChoiceGrid.getColumnConstraints().add(c);
        }

        RowConstraints r0 = new RowConstraints();
        r0.setPercentHeight(20);
        RowConstraints r1 = new RowConstraints();
        r1.setPercentHeight(60);
        RowConstraints r2 = new RowConstraints();
        r2.setPercentHeight(20);
        modesChoiceGrid.getRowConstraints().addAll(r0,r1,r2);


        ArrayList<ImageView> imageViews = new ArrayList<>();
        for(int i=0; i<2; i++){
            imageViews.add(new ImageView());
        }
        try {
            unclickedImage(imageViews, powerups, POWERUPS_DIR_PATH);
        } catch (Exception e){
            logOnException(e.getMessage(), e);
        }
        List<StackPane> stackPanes = new ArrayList<>();
        Button powerupButton = new Button("Continue!");
        StackPane powerupButtonContainer = new StackPane();
        powerupButtonContainer.setAlignment(Pos.CENTER);
        powerupButtonContainer.getChildren().add(powerupButton);
        modesChoiceGrid.add(powerupButtonContainer, 0, 2,5,1);

        for(int i=0; i<2; i++){
            stackPanes.add(new StackPane());
            setUpProperties(stackPanes.get(i), imageViews.get(i));
            modesChoiceGrid.add(stackPanes.get(i),(2*i)+1,1 ,1,1);
            ImageView imageView = imageViews.get(i);
            String powerup = powerups.get(i);
            String otherPowerup = powerups.get(1 - i);
            imageViews.get(i).setOnMouseClicked(e -> {
                setText(textContainerSettingChoice, "You selected " + powerup);
                unclickedImage(imageViews, powerups, "powerups/" );
                powerupButton.setOnAction(ev -> {
                    this.adrenalineGUI.didChooseSpawnPoint(powerup, otherPowerup);
                    primaryStage.setScene(secondScene);
                });
                clickedImage(imageView, "powerups/" + powerup + "_click");
            });
        }
        setText(textContainerSettingChoice, "Here are two powerup cards. Choose the one you want to discard: its color will be the color where you will spawn.");
        modesChoiceGrid.add(textContainerSettingChoice, 0,0,5,1);

        mainScene.setRoot(modesChoiceGrid);
    }


    void chooseAction() {
        Platform.runLater(() -> {
            try {
                onChooseAction();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    void onChooseAction() {
        /*while(counter < 15) {
            synchronized (this) {
                try {
                    System.out.println("ciao2");
                    System.out.println(counter);
                    wait();
                } catch (InterruptedException e) {
                    AdrenalineLogger.error(e.toString());
                    AdrenalineLogger.error(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }*/
        mapButton.setText("Choose an action!");
        mapButton.setOnAction(e -> primaryStage.setScene(mainScene));

        VBox actionVisualizer = new VBox();
        actionVisualizer.setBackground(background);
        actionVisualizer.setAlignment(Pos.CENTER);
        actionVisualizer.setSpacing(20);
        ArrayList<ImageView> imageViewss = new ArrayList<>();
        for(int i = 0; i < 3; i++)
            imageViewss.add(new ImageView());

        Button actionButton = new Button("Continue!");
        StackPane buttonCont = new StackPane();
        buttonCont.getChildren().add(actionButton);
        actionVisualizer.setBackground(background);

        ArrayList<String> actions = new ArrayList<>();
        actions.add("grab");
        actions.add("move");
        actions.add("shoot");


        unclickedImage(imageViewss, actions, "actions/");

        for(int i = 0; i < 3; i++) {
            String action = actions.get(i);
            ImageView imageView = imageViewss.get(i);
            actionVisualizer.getChildren().add(imageViewss.get(i));

            imageViewss.get(i).setOnMouseClicked(e -> {
                    unclickedImage(imageViewss, actions, "actions/");
                    clickedImage(imageView, "actions/" + action);
                    if(action != "shoot") {
                        actionButton.setOnAction( ev -> {
                            primaryStage.setScene(secondScene);
                        adrenalineGUI.didChooseAction(action);
                        });
                    } else {
                        actionButton.setOnAction( ev -> {
                            adrenalineGUI.didChooseAction(action);
                        });
                    }
            });
        }

        actionVisualizer.getChildren().add(buttonCont);
        mainScene.setRoot(actionVisualizer);
        //primaryStage.setScene(mainScene);
        //primaryStage.setFullScreen(true);

        /*GridPane actionGrid = new GridPane();
        actionScene = new Scene(actionGrid, 1200, 800);
        actionScene.setRoot(actionGrid);
        primaryStage.setScene(actionScene);


        ColumnConstraints c0 = new ColumnConstraints();
        c0.setPercentWidth(50);
        actionGrid.getColumnConstraints().add(c0);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);
        actionGrid.getColumnConstraints().add(c1);

        RowConstraints r0 = new RowConstraints();
        r0.setPercentHeight(20);
        actionGrid.getRowConstraints().add(r0);

        for(int i=0; i<5; i++){
            r =  new RowConstraints();
            if(i%2==0) r.setPercentHeight(10);
            else r.setPercentHeight(5);
            actionGrid.getRowConstraints().add(r);
        }

        RowConstraints r6 = new RowConstraints();
        r6.setPercentHeight(20);
        RowConstraints r7 = new RowConstraints();
        r7.setPercentHeight(20);
        actionGrid.getRowConstraints().addAll(r6, r7);

        actionGrid.setBackground(background);


        setText(textContainerSettingChoice,"Select an action to perform");
        actionGrid.add(textContainerSettingChoice, 0,0,1,1);
        actionGrid.add(textSelectedContainer, 0,6,1,1);

        Button actionButton = new Button("Continue!");
        StackPane actionButtonContainer = new StackPane();
        actionButtonContainer.setAlignment(Pos.CENTER);
        actionButtonContainer.getChildren().add(actionButton);

        actionButton.setOnAction(e -> setText(textSelectedContainer, "You have to select something to continue"));
        actionGrid.add(actionButtonContainer, 0,7,1,1);

        //ArrayList<ImageView> imageViews = new ArrayList<>();
        for(int i=0; i<3; i++) {
            imageViews.add(new ImageView());
        }

        //ArrayList<String> actions = new ArrayList<>();
        actions.add("grab");
        actions.add("move");
        actions.add("shoot");

        //unclickedImage(imageViews, actions, "actions/"  );

        List<StackPane> stackPanes = new ArrayList<>();
        for(int i=0; i<actions.size(); i++) {
            stackPanes.add(new StackPane());
            setUpProperties(stackPanes.get(i), imageViews.get(i));
            modesChoiceGrid.add(stackPanes.get(i), 0, (2*i)+1);

            ImageView imageView = imageViews.get(i);
            String action = actions.get(i);
            imageViews.get(i).setOnMouseClicked(event -> {
                unclickedImage(imageViews, actions, "actions/");
                clickedImage(imageView, "actions/" + action);
                actionButton.setOnAction( e -> {
                    adrenalineGUI.didChooseAction(action);
                });

                setText(textSelectedContainer, "You selected " + action);
            });


            }*/

    }

    public void pickOptions(Map<String, Map<Integer, Integer>> tiles, Map<String, String> weapons) {
        Platform.runLater(() -> {
            try {
                onPickAmmoTile(tiles, weapons);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void moveOptions(Map<String, Map<Integer, Integer>> movements) {
        Platform.runLater(() -> {
            try {
                onChooseMovement(movements);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }


    void onCreateGameMap(String conf) throws Exception{

        this.conf = conf;

        //Map creation
        StackPane mapContainer = new StackPane();
        Image map = getImageFromPath(IMAGE_DIR_PATH+BOARD_DIR_PATH+conf+PNG_FILE_EXT);
        ImageView ivMap = new ImageView();
        ivMap.setImage(map);
        ivMap.setPreserveRatio(true);
        ivMap.fitHeightProperty().bind(mapContainer.heightProperty());
        ivMap.fitWidthProperty().bind(mapContainer.widthProperty());
        mapContainer.getChildren().add(ivMap);
        mapContainer.setMinHeight(0.0);
        mapContainer.setMinWidth(0.0);
        mapContainer.setAlignment(Pos.CENTER);
        boardGrid.add(mapContainer, 1,2, 1, 4);

        //Map's grid creation
        mapGrid = new GridPane();
        mapGrid.maxWidthProperty().bind(mapContainer.heightProperty().multiply(1.32));
        mapGrid.maxHeightProperty().bind(mapContainer.widthProperty().divide(1.32));
        for(int j=0; j<6; j++){
            c = new ColumnConstraints();
            c.setPercentWidth(16.66);
            mapGrid.getColumnConstraints().add(c);
        }
        RowConstraints ro0 = new RowConstraints();
        ro0.setPercentHeight(23);
        RowConstraints ro1 = new RowConstraints();
        ro1.setPercentHeight(20);
        RowConstraints ro2 = new RowConstraints();
        ro2.setPercentHeight(24);
        RowConstraints ro3 = new RowConstraints();
        ro3.setPercentHeight(22);
        RowConstraints ro4 = new RowConstraints();
        ro4.setPercentHeight(11);
        mapGrid.getRowConstraints().addAll(ro0, ro1, ro2, ro3, ro4);
        mapContainer.getChildren().add(mapGrid);
        mapGrid.setAlignment(Pos.CENTER);


        cellsContainers = new ArrayList<>();
        cellsButtons = new ArrayList<>();
        for(int i=0; i<3; i++){
            cellsButtons.add(new ArrayList<>());
            cellsContainers.add(new ArrayList<>());
            for(int j=0; j<4; j++){
                int row = i;
                int column = j;
                cellsContainers.get(i).add(new StackPane());
                cellsButtons.get(i).add(new Button("SELECT"));
                //if(!(conf=="conf_1"&&i==0&&j==3 || conf=="conf_3"&&i==2&&j==0 || conf=="conf_2"&&(i==0&&j==3 || i==2&&j==0) || i==1&&j==0 || i==0&&j==2 || i==2&&j==3)) {

                    cellsButtons.get(i).get(j).setStyle("-fx-background-color: transparent");
                    cellsButtons.get(i).get(j).setPrefHeight(Integer.MAX_VALUE);



                    mapGrid.add(cellsContainers.get(i).get(j), j + 1, i + 1);
                //}
            }
        }




        //Cards' container creation
        cardsContainer = new GridPane();
        for(int i=0; i<18; i++){
            c = new ColumnConstraints();
            c.setPercentWidth(5.55);
            cardsContainer.getColumnConstraints().add(c);
        }
        RowConstraints r0 = new RowConstraints();
        r0.setPercentHeight(20);
        RowConstraints r1 = new RowConstraints();
        r1.setPercentHeight(80);
        cardsContainer.getRowConstraints().addAll(r0, r1);
        redAmmos = new ArrayList<>();
        yellowAmmos = new ArrayList<>();
        blueAmmos = new ArrayList<>();
        for(int a=0; a<3; a++){
            redAmmos.add(new StackPane());
            yellowAmmos.add(new StackPane());
            blueAmmos.add(new StackPane());
            cardsContainer.add(redAmmos.get(a), a*2,0,2,1);
            cardsContainer.add(yellowAmmos.get(a), a*2+6,0,2,1);
            cardsContainer.add(blueAmmos.get(a), a*2+12,0,2,1);
        }
        weapons = new ArrayList<>();
        powerups = new ArrayList<>();
        for(int b=0; b<3; b++) {
            weapons.add(new StackPane());
            powerups.add(new StackPane());
            cardsContainer.add(weapons.get(b), (b*3),1,3,1);
            cardsContainer.add(powerups.get(b), (b * 3) + 9, 1, 3, 1);

        }
        boardGrid.add(cardsContainer, 1,6, 1, 1);



        //decks handlers creation
        leftDeck = new GridPane();
        RowConstraints r00 = new RowConstraints();
        r00.setPercentHeight(35);
        RowConstraints r01 = new RowConstraints();
        r01.setPercentHeight(14);
        RowConstraints r02 = new RowConstraints();
        r02.setPercentHeight(14);
        RowConstraints r03 = new RowConstraints();
        r03.setPercentHeight(14);
        RowConstraints r04 = new RowConstraints();
        r04.setPercentHeight(23);
        leftDeck.getRowConstraints().addAll(r00, r01, r02, r03, r04);
        ColumnConstraints c00 = new ColumnConstraints();
        c00.setPercentWidth(90);
        ColumnConstraints c01 = new ColumnConstraints();
        c01.setPercentWidth(10);
        leftDeck.getColumnConstraints().addAll(c00, c01);
        mapGrid.add(leftDeck, 0, 0, 1, 5);
        deckLeft = new ArrayList<>();
        leftBackup = new ArrayList<>();
        for(int e=0; e<3; e++){
            deckLeft.add(new StackPane());
            leftDeck.add(deckLeft.get(e), 0, e+1,1,1);
        }
        for(int e=0; e<3; e++){
            leftBackup.add(new StackPane());
        }

        upperDeck = new GridPane();
        ColumnConstraints co00 = new ColumnConstraints();
        co00.setPercentWidth(51);
        ColumnConstraints co01 = new ColumnConstraints();
        co01.setPercentWidth(11);
        ColumnConstraints co02 = new ColumnConstraints();
        co02.setPercentWidth(11);
        ColumnConstraints co03 = new ColumnConstraints();
        co03.setPercentWidth(11);
        ColumnConstraints co04 = new ColumnConstraints();
        co04.setPercentWidth(16);
        upperDeck.getColumnConstraints().addAll(co00,co01,co02,co03,co04);
        RowConstraints ro00 = new RowConstraints();
        ro00.setPercentHeight(80);
        RowConstraints ro01 = new RowConstraints();
        ro01.setPercentHeight(20);
        upperDeck.getRowConstraints().addAll(ro00,ro01);
        mapGrid.add(upperDeck, 0, 0, 6, 1);
        deckAbove = new ArrayList<>();
        aboveBackup = new ArrayList<>();
        for(int d=0; d<3; d++){
            deckAbove.add(new StackPane());
            upperDeck.add(deckAbove.get(d), d+1, 0,1,1);
        }
        for(int d=0; d<3; d++){
            aboveBackup.add(new StackPane());
        }



        rightDeck = new GridPane();
        RowConstraints r000 = new RowConstraints();
        r000.setPercentHeight(58);
        RowConstraints r001 = new RowConstraints();
        r001.setPercentHeight(14);
        RowConstraints r002 = new RowConstraints();
        r002.setPercentHeight(14);
        RowConstraints r003 = new RowConstraints();
        r003.setPercentHeight(14);
        rightDeck.getRowConstraints().addAll(r000, r001, r002, r003);
        ColumnConstraints c000 = new ColumnConstraints();
        c000.setPercentWidth(10);
        ColumnConstraints c001 = new ColumnConstraints();
        c001.setPercentWidth(90);
        rightDeck.getColumnConstraints().addAll(c000, c001);
        mapGrid.add(rightDeck, 5, 0, 1, 5);
        deckRight = new ArrayList<>();
        rightBackup = new ArrayList<>();
        for(int d=0; d<3; d++){
            deckRight.add(new StackPane());
            rightDeck.add(deckRight.get(d), 1, d+1,1,1);
        }
        for(int d=0; d<3; d++){
            rightBackup.add(new StackPane());
        }

        cardsDisplayer = new GridPane();
        RowConstraints ro000 = new RowConstraints();
        ro000.setPercentHeight(20);
        RowConstraints ro001 = new RowConstraints();
        ro001.setPercentHeight(80);
        RowConstraints ro002 = new RowConstraints();
        ro002.setPercentHeight(20);
        cardsDisplayer.getRowConstraints().addAll(ro000, ro001, ro002);
        ColumnConstraints col00 = new ColumnConstraints();
        col00.setPercentWidth(10);
        ColumnConstraints col01 = new ColumnConstraints();
        col01.setPercentWidth(22);
        ColumnConstraints col02 = new ColumnConstraints();
        col02.setPercentWidth(7);
        ColumnConstraints col03 = new ColumnConstraints();
        col03.setPercentWidth(22);
        ColumnConstraints col04 = new ColumnConstraints();
        col04.setPercentWidth(7);
        ColumnConstraints col05 = new ColumnConstraints();
        col05.setPercentWidth(22);
        ColumnConstraints col06 = new ColumnConstraints();
        col06.setPercentWidth(10);
        cardsDisplayer.getColumnConstraints().addAll(col00, col01, col02, col03, col04, col05, col06);

        //Setting background
        BackgroundImage myBI= new BackgroundImage(getImageFromPath(IMAGE_DIR_PATH+"background.png"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, false));
        boardGrid.setBackground(new Background(myBI));

        //Adding text container
        boardGrid.add(textContainer, 0,0,2,1);

        //Adding button container
        boardGrid.add(pickButtonContainer, 0,1,2,1);
        pickButtonContainer.getChildren().add(mapButton);


        //Showing
        primaryStage.setScene(secondScene);
        //primaryStage.setFullScreen(true);


        /*ArrayList<String>ammotiles = new ArrayList();
        ArrayList<String> weapons = new ArrayList<>();
        weapons.add("Furnace");
        weapons.add("Hellion");
        weapons.add("Heatseeker");
        onUpdateDeckRight(weapons);
        onUpdateDeckAbove(weapons);
        onUpdateDeckLeft(weapons);
        onUpdateWeaponsCards(weapons);
        ammotiles.add("powerup, blue, blue");
        ammotiles.add("powerup, blue, blue");
        Map<Integer, Integer>ammoTile = new HashMap<>();
        ammoTile.put(1,1);
        Map<Integer, Integer>ammoTile1 = new HashMap<>();
        ammoTile.put(0,1);
        Map<Integer, Integer>ammoTile2 = new HashMap<>();
        ammoTile.put(2,3);
        Map<Integer, Integer>ammoTile3 = new HashMap<>();
        ammoTile.put(1,2);
        Map<Integer, Integer>ammoTile4 = new HashMap<>();
        ammoTile.put(0,2);
        Map<Integer, Integer>ammoTile5 = new HashMap<>();
        ammoTile.put(2,2);
        onUpdateFirstRow(ammotiles);
        Map<String, Map<Integer, Integer>> tiles = new HashMap<>();
        tiles.put("ciaoNay", ammoTile);
        tiles.put("ciaoNay1", ammoTile1);
        tiles.put("ciaoNay2", ammoTile2);
        tiles.put("ciaoNay3", ammoTile3);
        tiles.put("ciaoNay4", ammoTile4);
        ammotiles.add("powerup, blue, blue");
        onUpdateSecondRow(ammotiles);
        onUpdateThirdRow(ammotiles);*/
        //onChooseMovement(tiles);
        //buyWeapon();
    }

    public void displayCards(int n, int i){
        Platform.runLater(() -> {
            try {
                onDisplayCards(n, i);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    private void onDisplayCards(int n, int i){
        cardsDisplayer.getChildren().clear();
        cardsDisplayer.setBackground(background);
        cardsDisplayer.add(textSelectedContainer, 0,0,7,1);
        if(i==1) cardsDisplayer.add(leftBackup.get(n), 3,1);
        if(i==2) cardsDisplayer.add(aboveBackup.get(n), 3, 1);
        if(i==3) cardsDisplayer.add(rightBackup.get(n), 3, 1);
        currentDeck = i;
        currentCard = n;
        setText(textSelectedContainer, "");

        /*button2.setText("Go back to the map");
        cardsDisplayer.add(buttonContainer,0,2,3,1);*/
        Button button1 = new Button("Go back to the map");
        StackPane button1Container = new StackPane();
        button1Container.setAlignment(Pos.CENTER);
        button1Container.getChildren().add(button1);
        button1.setOnAction(e -> {secondScene.setRoot(boardGrid);});
        cardsDisplayer.add(button1Container,0,2,3,1);
        secondScene.setRoot(cardsDisplayer);

        StackPane button3Container = new StackPane();
        button3Container.setAlignment(Pos.CENTER);
        button3Container.getChildren().add(button3);
        cardsDisplayer.add(button3Container,4,2,3,1);
        secondScene.setRoot(cardsDisplayer);

    }

    private void buyWeapon(Map<String, String> weapons){
        String weaponBought = null;
        String weaponName = null;
        for(Map.Entry<String, String> weapon : weapons.entrySet()){
            if(weaponsInRespawn.get(currentDeck).get(currentCard).equals(weapon.getValue())) {
                weaponBought = weapon.getKey();
                weaponName = weapon.getValue();
                }
        }
        if(weaponBought != null) {
            setText(textSelectedContainer, "You bought the " + weaponName + "!");
            this.adrenalineGUI.didChooseWhatToGrab(weaponBought);
            clearAfterPick();
        }
        else setText(textSelectedContainer, "You can't buy this weapon.");

    }

    private void onPickAmmoTile(Map<String, Map<Integer, Integer>> ammoTiles, Map<String, String> weapons) {
        setText(textContainer, "Select a weapon or an ammmoTile to grab from the map! You can zoom on the weapons by clicking on them.");

        Map<Integer, Integer> couples = new HashMap<>();
        List<Integer> row = new ArrayList<>();
        List<Integer> column = new ArrayList<>();
        for(String entryAmmo : ammoTiles.keySet()) {
            row.addAll(ammoTiles.get(entryAmmo).keySet());
            column.addAll(ammoTiles.get(entryAmmo).values());
        }

        for (int i = 0; i < row.size(); i++) {
            cellsContainers.get(row.get(i)).get(column.get(i)).getChildren().add(cellsButtons.get(row.get(i)).get(column.get(i)));
            cellsButtons.get(row.get(i)).get(column.get(i)).setOnAction(e -> {
                String selected = null;
                int rowPos = 0;
                int colPos = 0;
                for(Map.Entry<String, Map<Integer, Integer>> tile : ammoTiles.entrySet())
                    for(Map.Entry<Integer, Integer> position : tile.getValue().entrySet())
                        for(int j = 0; j < row.size(); j++)
                            if(position.getValue().equals(column.get(j)) && position.getKey().equals(row.get(j))) {
                                selected = tile.getKey();
                                rowPos = row.get(j);
                                colPos = column.get(j);
                            }
                choseCell(selected, rowPos, colPos);
            });
        }
        button3.setOnAction(e -> {
            buyWeapon(weapons);
        });

    }


    private void onChooseMovement(Map<String, Map<Integer, Integer>> movements) {
        setText(textContainer, "Select a place on the map where you would like to go.");
        List<Integer> row = new ArrayList<>();
        List<Integer> column = new ArrayList<>();
        for(String entryAmmo : movements.keySet()) {
            row.addAll(movements.get(entryAmmo).keySet());
            column.addAll(movements.get(entryAmmo).values());
        }

        for (int i = 0; i < row.size(); i++) {
            cellsContainers.get(row.get(i)).get(column.get(i)).getChildren().add(cellsButtons.get(row.get(i)).get(column.get(i)));
            cellsButtons.get(row.get(i)).get(column.get(i)).setOnAction(e -> {
                String selected = null;
                int rowPos = 0;
                int colPos = 0;
                for(Map.Entry<String, Map<Integer, Integer>> tile : movements.entrySet())
                    for(Map.Entry<Integer, Integer> position : tile.getValue().entrySet())
                        for(int j = 0; j < row.size(); j++)
                            if(position.getValue().equals(column.get(j)) && position.getKey().equals(row.get(j))) {
                                selected = tile.getKey();
                                rowPos = row.get(j);
                                colPos = column.get(j);
                            }
                choseWhereToMove(selected, rowPos, colPos);
            });
        }

    }

    private void choseWhereToMove(String chosen, int row, int column){
        textContainer.getChildren().clear();
        setText(textContainer, "You selected row:" + row + " column:" + column);
        Button button = new Button("Move this way!");
        pickButtonContainer.getChildren().add(button);
        pickButtonContainer.setAlignment(Pos.CENTER);
        button.setOnAction(e -> {
            clearAfterPick();
            this.adrenalineGUI.didChooseMovement(chosen);
        });
    }

    private void clearAfterPick() {
        mapButton.setOnAction(null);
        if(pickButtonContainer != null) pickButtonContainer.getChildren().clear();
        pickButtonContainer.getChildren().add(mapButton);
        if(button3 != null) button3.setOnAction(null);
        for(List<Button> buttonList : cellsButtons)
            for(Button bu : buttonList)
                bu.setOnAction(null);
        setText(textContainer, "");

    }


    private void choseCell(String chosen, int row, int column){
        setText(textContainer, "You selected row:" + row + " column:" + column);
        Button button = new Button("Continue");
        pickButtonContainer.getChildren().clear();
        pickButtonContainer.getChildren().add(button);
        pickButtonContainer.setAlignment(Pos.CENTER);
        button.setOnAction(e -> {
            clearAfterPick();
            this.adrenalineGUI.didChooseWhatToGrab(chosen);
        });
    }


    public void updateWeaponsCards(List<String> givenWeapons) {
        Platform.runLater(() -> {
            try {
                onUpdateWeaponsCards(givenWeapons);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void onUpdateWeaponsCards(List<String> givenWeapons) throws Exception{

        counter++;

        ArrayList<ImageView> weaponsIV = new ArrayList<>();
        for(StackPane stackPane: weapons){
            stackPane.getChildren().clear();
        }
        for(int i = 0; i < givenWeapons.size(); i++){
            weaponsIV.add(new ImageView());
            clickedImage(weaponsIV.get(i), WEAPONS_DIR_PATH + givenWeapons.get(i));
            setUpProperties(weapons.get(i), weaponsIV.get(i));
        }

        synchronized (this) {
            this.notifyAll();
        }
    }

    public void updatePowerups(List<String> givenPowerups) {
        Platform.runLater(() -> {
            try {
                onUpdatePowerups(givenPowerups);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void onUpdatePowerups(List<String> givenPowerups) throws Exception{

        counter++;

        ArrayList<ImageView> powerupsIV = new ArrayList<>();
        for(StackPane stackPane: powerups){
            stackPane.getChildren().clear();
        }
        for(int i = 0; i < givenPowerups.size(); i++){
            powerupsIV.add(new ImageView());
            clickedImage(powerupsIV.get(i), POWERUPS_DIR_PATH + givenPowerups.get(i) + "_click");
            setUpProperties(powerups.get(i), powerupsIV.get(i));
        }

        synchronized (this) {
            this.notifyAll();
        }
    }

    public void updateRedAmmos(int num) {
        Platform.runLater(() -> {
            try {
                onUpdateRedAmmos(num);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void onUpdateRedAmmos(int num) throws Exception{

        counter++;

        ArrayList<ImageView> redAmmosIV = new ArrayList<>();
        for(StackPane stackPane:redAmmos){
            stackPane.getChildren().clear();
        }
        for(int i = 0; i < num; i++){
            redAmmosIV.add(new ImageView());
            clickedImage(redAmmosIV.get(i), AMMOTILES_DIR_PATH+"red");
            setUpProperties(redAmmos.get(i), redAmmosIV.get(i));
        }

        synchronized (this) {
            this.notifyAll();
        }

    }

    public void updateBlueAmmos(int num) {
        Platform.runLater(() -> {
            try {
                onUpdateBlueAmmos(num);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void onUpdateBlueAmmos(int num) throws Exception{

        counter++;

        ArrayList <ImageView> blueAmmosIV = new ArrayList<>();
        for(StackPane stackPane:blueAmmos){
            stackPane.getChildren().clear();
        }
        for(int i = 0; i < num; i++){
            blueAmmosIV.add(new ImageView());
            clickedImage(blueAmmosIV.get(i), AMMOTILES_DIR_PATH+"blue");
            setUpProperties(blueAmmos.get(i), blueAmmosIV.get(i));
        }

        synchronized (this) {
            this.notifyAll();
        }
    }


    public void updateYellowAmmos(int num) {
        Platform.runLater(() -> {
            try {
                onUpdateYellowAmmos(num);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void onUpdateYellowAmmos(int num) throws Exception{

        counter++;

        ArrayList<ImageView> yellowAmmosIV = new ArrayList<>();
        for(StackPane stackPane:yellowAmmos){
            stackPane.getChildren().clear();
        }
        for(int i = 0; i < num; i++){
            yellowAmmosIV.add(new ImageView());
            clickedImage(yellowAmmosIV.get(i), AMMOTILES_DIR_PATH+"yellow");
            setUpProperties(yellowAmmos.get(i), yellowAmmosIV.get(i));
        }

        synchronized (this) {
            this.notifyAll();
        }
    }


    public void updateDeckLeft(List<String> givenWeapons) {

        counter++;

        Platform.runLater(() -> {
            try {
                onUpdateDeckLeft(givenWeapons);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });

        synchronized (this) {
            this.notifyAll();
        }
    }

    public void onUpdateDeckLeft(List<String> givenWeapons){

        counter++;

        ArrayList<ImageView> weaponsIV = new ArrayList<>();
        ArrayList<ImageView> weaponsBackUpIV = new ArrayList<>();
        ArrayList<Button> buttons = new ArrayList<>();
        for(StackPane stackPane: deckLeft){
            stackPane.getChildren().clear();
        }
        for(StackPane stackPane: leftBackup){
            stackPane.getChildren().clear();
        }
        weaponsInRespawn.put(1, givenWeapons);
        for(int i = 0; i < givenWeapons.size(); i++){
            int index = i;
            weaponsIV.add(new ImageView());
            weaponsBackUpIV.add(new ImageView());
            buttons.add(new Button());
            clickedImage(weaponsIV.get(i), WEAPONS_DIR_PATH+givenWeapons.get(i));
            clickedImage(weaponsBackUpIV.get(i), WEAPONS_DIR_PATH+givenWeapons.get(i));
            setUpProperties270(deckLeft.get(i), weaponsIV.get(i));
            setUpProperties(leftBackup.get(i), weaponsBackUpIV.get(i));

            deckLeft.get(i).getChildren().add(buttons.get(i));
            buttons.get(i).setStyle("-fx-background-color: transparent");
            buttons.get(i).setPrefHeight(Integer.MAX_VALUE);
            buttons.get(i).setPrefWidth(Integer.MAX_VALUE);
            buttons.get(i).setOnAction(e -> displayCards(index, 1));

        }

        synchronized (this) {
            this.notifyAll();
        }
    }


    public void updateDeckAbove(List<String> givenWeapons) {
        Platform.runLater(() -> {
            try {
                onUpdateDeckAbove(givenWeapons);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }


    private void onUpdateDeckAbove(List<String> givenWeapons){

        counter++;

        ArrayList<ImageView> weaponsIV = new ArrayList<>();
        ArrayList<ImageView> weaponsBackUpIV = new ArrayList<>();
        ArrayList<Button> buttons = new ArrayList<>();
        for(StackPane stackPane: deckAbove){
            stackPane.getChildren().clear();
        }
        for(StackPane stackPane: aboveBackup){
            stackPane.getChildren().clear();
        }
        weaponsInRespawn.put(2, givenWeapons);
        for(int i = 0; i < givenWeapons.size(); i++){
            int index=i;
            weaponsIV.add(new ImageView());
            weaponsBackUpIV.add(new ImageView());
            buttons.add(new Button());
            clickedImage(weaponsIV.get(i), WEAPONS_DIR_PATH+givenWeapons.get(i));
            clickedImage(weaponsBackUpIV.get(i), WEAPONS_DIR_PATH+givenWeapons.get(i));
            setUpProperties(deckAbove.get(i), weaponsIV.get(i));
            setUpProperties(aboveBackup.get(i), weaponsBackUpIV.get(i));

            deckAbove.get(i).getChildren().add(buttons.get(i));
            buttons.get(i).setStyle("-fx-background-color: transparent");
            buttons.get(i).setPrefHeight(Integer.MAX_VALUE);
            buttons.get(i).setPrefWidth(Integer.MAX_VALUE);
            buttons.get(i).setOnAction(e -> displayCards(index, 2));
        }

        synchronized (this) {
            this.notifyAll();
        }
    }


    public void updateDeckRight(List<String> givenWeapons) {
        Platform.runLater(() -> {
            try {
                onUpdateDeckRight(givenWeapons);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    void onUpdateDeckRight(List<String>givenWeapons){

        counter++;

        ArrayList<ImageView> weaponsIV = new ArrayList<>();
        ArrayList<ImageView> weaponsBackUpIV = new ArrayList<>();
        ArrayList<Button> buttons = new ArrayList<>();
        for(StackPane stackPane: deckRight){
            stackPane.getChildren().clear();
        }
        for(StackPane stackPane: rightBackup){
            stackPane.getChildren().clear();
        }
        weaponsInRespawn.put(3, givenWeapons);
        for(int i = 0; i < givenWeapons.size(); i++){
            int index = i;
            weaponsIV.add(new ImageView());
            weaponsBackUpIV.add(new ImageView());
            buttons.add(new Button());
            clickedImage(weaponsIV.get(i), WEAPONS_DIR_PATH+givenWeapons.get(i));
            clickedImage(weaponsBackUpIV.get(i), WEAPONS_DIR_PATH+givenWeapons.get(i));
            setUpProperties90(deckRight.get(i), weaponsIV.get(i));
            setUpProperties(rightBackup.get(i), weaponsBackUpIV.get(i));

            deckRight.get(i).getChildren().add(buttons.get(i));
            buttons.get(i).setStyle("-fx-background-color: transparent");
            buttons.get(i).setPrefHeight(Integer.MAX_VALUE);
            buttons.get(i).setPrefWidth(Integer.MAX_VALUE);
            buttons.get(i).setOnAction(e -> displayCards(index, 3));
        }

        synchronized (this) {
            this.notifyAll();
        }

    }

    public void updateFirstRow(List<String> ammoTiles) {
        Platform.runLater(() -> {
            try {
                onUpdateFirstRow(ammoTiles);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void onUpdateFirstRow(List<String> ammoTiles){

        counter++;

        List<ImageView> ammoTilesIV = new ArrayList<>();
        int i=0;
        for(int j=0; j<4; j++){
            cellsContainers.get(0).get(j).getChildren().clear();
            ammoTilesIV.add(new ImageView());{
                if (!(j==2 || j==3&&(conf=="conf_1" || conf=="conf_2")) && i<ammoTiles.size() ){
                    clickedImage(ammoTilesIV.get(j), AMMOTILES_DIR_PATH + ammoTiles.get(i));
                    setUpPropertiesModified(cellsContainers.get(0).get(j), ammoTilesIV.get(j));
                    i++;
                }
            }
        }

        synchronized (this) {
            this.notifyAll();
        }
    }

    public void updateSecondRow(List<String> ammoTiles) {

        counter++;

        Platform.runLater(() -> {
            try {
                onUpdateSecondRow(ammoTiles);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });

        synchronized (this) {
            this.notifyAll();
        }
    }

    public void onUpdateSecondRow(List<String> ammoTiles){

        counter++;

        List<ImageView> ammoTilesIV = new ArrayList<>();
        int i=0;
        for(int j=0; j<4; j++){
            cellsContainers.get(1).get(j).getChildren().clear();
            ammoTilesIV.add(new ImageView());{
                if (!(j==0) && i<ammoTiles.size()){
                    clickedImage(ammoTilesIV.get(j), AMMOTILES_DIR_PATH + ammoTiles.get(i));
                    setUpPropertiesModified(cellsContainers.get(1).get(j), ammoTilesIV.get(j));
                    i++;
                }
            }
        }

        synchronized (this) {
            this.notifyAll();
        }
    }

    public void updateThirdRow(List<String> ammoTiles) {
        Platform.runLater(() -> {
            try {
                onUpdateThirdRow(ammoTiles);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void onUpdateThirdRow(List<String> ammoTiles){

        counter ++;


        List<ImageView> ammoTilesIV = new ArrayList<>();
        int i=0;
        for(int j=0; j<4; j++){
            cellsContainers.get(2).get(j).getChildren().clear();
            ammoTilesIV.add(new ImageView());{
                if (!((j==3) || (j==0&&(conf=="conf_2" || conf=="conf_3"))) && i<ammoTiles.size()){
                    clickedImage(ammoTilesIV.get(j), "ammotiles/" + ammoTiles.get(i));
                    setUpPropertiesModified(cellsContainers.get(2).get(j), ammoTilesIV.get(j));
                    i++;
                }
            }
        }

        synchronized (this) {
            this.notifyAll();
        }
    }

    public void addPlayerBoards(List<String> playerColors) {
        Platform.runLater(() -> {
            try {
                onAddPlayerBoards(playerColors);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void onAddPlayerBoards(List<String> playerColors) throws Exception{

        playerBoards = new HashMap<>();
        ArrayList<ImageView> imageViews = new ArrayList<>();
        damagesGrid = new HashMap<>();
        bloodTruck = new HashMap<>();
        marksTruck = new HashMap<>();
        killedPointsTruck = new HashMap<>();

        for(int i = 0; i < playerColors.size(); i++){
            playerBoards.put(playerColors.get(i), new StackPane());
            imageViews.add(new ImageView());
            damagesGrid.put(playerColors.get(i), new GridPane());
            bloodTruck.put(playerColors.get(i), new ArrayList<>());
            marksTruck.put(playerColors.get(i), new ArrayList<>());
            killedPointsTruck.put(playerColors.get(i), new ArrayList<>());
            clickedImage(imageViews.get(i), PLAYERBOARDS_DIR_PATH+PLAYERBOARD_FILE_FORMAT+playerColors.get(i));
            setUpProperties(playerBoards.get(playerColors.get(i)), imageViews.get(i));
            if(i==0) boardGrid.add(playerBoards.get(playerColors.get(i)), 0,6);
            else boardGrid.add(playerBoards.get(playerColors.get(i)), 0, i+1);

            playerBoards.get(playerColors.get(i)).getChildren().add(damagesGrid.get(playerColors.get(i)));
            damagesGrid.get(playerColors.get(i)).maxWidthProperty().bind(playerBoards.get(playerColors.get(i)).heightProperty().multiply(4));
            damagesGrid.get(playerColors.get(i)).maxHeightProperty().bind(playerBoards.get(playerColors.get(i)).widthProperty().divide(4));
            RowConstraints row01=new RowConstraints();
            row01.setPercentHeight(34);
            RowConstraints row02= new RowConstraints();
            row02.setPercentHeight(33);
            RowConstraints row03= new RowConstraints();
            row03.setPercentHeight(33);
            damagesGrid.get(playerColors.get(i)).getRowConstraints().addAll(row01,row02,row03);
            for(int j=0;j<14;j++){
                c = new ColumnConstraints();
                if (j==0) c.setPercentWidth(9.04);
                else if (j==13) c.setPercentWidth(24);
                else c.setPercentWidth(5.58);
                damagesGrid.get(playerColors.get(i)).getColumnConstraints().add(c);
            }
            for(int k=0; k<12; k++){
                bloodTruck.get(playerColors.get(i)).add(new StackPane());
                damagesGrid.get(playerColors.get(i)).add(bloodTruck.get(playerColors.get(i)).get(k), k+1, 1);
            }
            for(int m=0; m<5; m++){
                marksTruck.get(playerColors.get(i)).add(new StackPane());
                damagesGrid.get(playerColors.get(i)).add(marksTruck.get(playerColors.get(i)).get(m), m+8, 0);
            }
            for(int n=0; n<6; n++){
                killedPointsTruck.get(playerColors.get(i)).add(new StackPane());
                damagesGrid.get(playerColors.get(i)).add(killedPointsTruck.get(playerColors.get(i)).get(n), n+3, 2);
            }
        }
        this.adrenalineGUI.built();
    }

    public void updateDamages(String color, List<String> damages) {
        Platform.runLater(() -> {
            try {
                onUpdateDamages(color, damages);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void onUpdateDamages(String color, List<String> damages){

        counter++;


        List<ImageView> imageViews = new ArrayList<>();
        for(int i=0; i<12; i++){
            bloodTruck.get(color).get(i).getChildren().clear();
        }
        for(int i=0; i<damages.size(); i++) {
            imageViews.add(new ImageView());
            setUpProperties(bloodTruck.get(color).get(i), imageViews.get(i));
            clickedImage(imageViews.get(i), CHARACTERS_DIR_PATH + damages.get(i)+"_blood");
        }

        synchronized (this) {
            this.notifyAll();
        }
    }

    public void updateMarks(String color, List<String> marks) {
        Platform.runLater(() -> {
            try {
                onUpdateMarks(color, marks);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void onUpdateMarks(String color, List<String> marks){

        counter++;

        List<ImageView> imageViews = new ArrayList<>();
        for(int i=0; i<3; i++){
            marksTruck.get(color).get(i).getChildren().clear();
        }
        for(int i=0; i<marks.size(); i++) {
            imageViews.add(new ImageView());
            setUpProperties(marksTruck.get(color).get(i), imageViews.get(i));
            clickedImage(imageViews.get(i), CHARACTERS_DIR_PATH + marks.get(i)+"_blood");
        }

        synchronized (this) {
            this.notifyAll();
        }
    }


    public void displayMessage(String message) {
        Platform.runLater(() -> {
            try {
                displayMessage(message);
            } catch (Exception ex) {
                logOnException(ex.getMessage(), ex);
            }
        });
    }

    public void onDisplayMessage(String message){
        setText(textContainer, message);
        messageGrid.add(textContainer, 0,0,2,1);
        mainScene.setRoot(messageGrid);

        Button buttonYes = new Button("YES");
        StackPane yesPane = new StackPane();
        yesPane.setAlignment(Pos.CENTER);
        yesPane.getChildren().add(buttonYes);

        Button buttonNo = new Button("NO");
        StackPane noPane = new StackPane();
        noPane.setAlignment(Pos.CENTER);
        noPane.getChildren().add(buttonNo);

        messageGrid.add(yesPane,0,1);
        messageGrid.add(noPane,1,1);
        buttonNo.setOnAction(e -> {adrenalineGUI.grabSomethingResponse(false);});
        buttonYes.setOnAction(e -> {adrenalineGUI.grabSomethingResponse(true);});
    }

    public void buildMessageGrid(){
        messageGrid = new GridPane();
        messageGrid.setBackground(background);

        for(int j=0; j<2; j++){
            c=new ColumnConstraints();
            c.setPercentWidth(50);
            messageGrid.getColumnConstraints().add(c);
        }

        for(int i=0; i<2; i++){
            r=new RowConstraints();
            r.setPercentHeight(50);
            messageGrid.getRowConstraints().add(r);
        }

    }


    public void setUpButtonProperties(StackPane stackPane, Button button){
        stackPane.getChildren().add(button);
        stackPane.setMinHeight(0.0);
        stackPane.setMinWidth(0.0);
        stackPane.setAlignment(Pos.CENTER);
        button.maxHeightProperty().bind(stackPane.heightProperty());
        button.maxWidthProperty().bind(stackPane.widthProperty());
        button.setMinHeight(0.0);
        button.setMinWidth(0.0);
    }

    public void setUpProperties(StackPane stackPane, ImageView imageView){
        imageView.setPreserveRatio(true);
        stackPane.getChildren().add(imageView);
        stackPane.setMinHeight(0.0);
        stackPane.setMinWidth(0.0);
        stackPane.setAlignment(Pos.CENTER);
        imageView.fitHeightProperty().bind(stackPane.heightProperty());
        imageView.fitWidthProperty().bind(stackPane.widthProperty());
    }

    public void setUpProperties90(StackPane stackPane, ImageView imageView){
        imageView.setPreserveRatio(true);
        imageView.setRotate(90);
        stackPane.getChildren().add(imageView);
        stackPane.setMinHeight(0.0);
        stackPane.setMinWidth(0.0);
        stackPane.setAlignment(Pos.CENTER);
        imageView.fitHeightProperty().bind(stackPane.widthProperty());
        imageView.fitWidthProperty().bind(stackPane.heightProperty());
    }

    public void setUpProperties270(StackPane stackPane, ImageView imageView){
        imageView.setPreserveRatio(true);
        imageView.setRotate(270);
        stackPane.getChildren().add(imageView);
        stackPane.setMinHeight(0.0);
        stackPane.setMinWidth(0.0);
        stackPane.setAlignment(Pos.CENTER);
        imageView.fitHeightProperty().bind(stackPane.widthProperty());
        imageView.fitWidthProperty().bind(stackPane.heightProperty());
    }

    public void setUpPropertiesModified(StackPane stackPane, ImageView imageView){
        imageView.setPreserveRatio(true);
        stackPane.getChildren().add(imageView);
        stackPane.setMinHeight(0.0);
        stackPane.setMinWidth(0.0);
        stackPane.setAlignment(Pos.CENTER);
        imageView.fitHeightProperty().bind(stackPane.heightProperty().divide(2));
        imageView.fitWidthProperty().bind(stackPane.widthProperty().divide(2));
    }

    public void clear(GridPane gridPane){
        gridPane.getChildren().clear();
        gridPane.getRowConstraints().clear();
        gridPane.getColumnConstraints().clear();
    }

    public void setText(VBox textBox, String string){
            textBox.getChildren().clear();
            Text text = new Text(string);
            text.setFill(Color.WHITE);
            text.setStyle("-fx-font-family: 'Andale Mono'");

            fontSize.bind(mainScene.widthProperty().add(mainScene.heightProperty()).divide(100));
            textBox.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), ";"));
            text.wrappingWidthProperty().bind(mainScene.widthProperty().subtract(100));
            textBox.getChildren().add(text);
            textBox.setMargin(text, new Insets(50, 50, 50, 50));
    }

    public void clickedImage(ImageView imageView, String image) {
        try {
            Image clickedPic = getImageFromPath(IMAGE_DIR_PATH+image+PNG_FILE_EXT);
            imageView.setImage(clickedPic);
        } catch (Exception e){
            logOnException(e.getMessage(), e);
        }
    }

    public void unclickedImage(ArrayList<ImageView> imageViews, List<String> availableCharacters, String path) {
        try {
            for (int i = 0; i < availableCharacters.size(); i++) {
                imageViews.get(i).setImage(getImageFromPath(IMAGE_DIR_PATH+path+availableCharacters.get(i)+CHARACTER_FILE_UNCLICKED_FORMAT+PNG_FILE_EXT));
            }
        } catch (Exception e){
            logOnException(e.getMessage(), e);
        }
    }

    public void chooseWeapon(ArrayList<String> weapons){

        backToMapButton.setText("Go back to map.");
        backToMapButton.setOnAction(e -> {
            primaryStage.setScene(secondScene);
            this.adrenalineGUI.newAction();
        });

        GridPane weaponsVisualizer = new GridPane();
        weaponsVisualizer.setBackground(background);
        ColumnConstraints c0 = new ColumnConstraints();
        c0.setPercentWidth(10);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(20);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(10);
        ColumnConstraints c3 = new ColumnConstraints();
        c3.setPercentWidth(20);
        ColumnConstraints c4 = new ColumnConstraints();
        c4.setPercentWidth(10);
        ColumnConstraints c5 = new ColumnConstraints();
        c5.setPercentWidth(20);
        ColumnConstraints c6 = new ColumnConstraints();
        c6.setPercentWidth(10);
        weaponsVisualizer.getColumnConstraints().addAll(c0, c1, c2, c3 ,c4, c5 ,c6);

        RowConstraints r0 = new RowConstraints();
        r0.setPercentHeight(20);
        RowConstraints r1 = new RowConstraints();
        r1.setPercentHeight(60);
        RowConstraints r2 = new RowConstraints();
        r2.setPercentHeight(10);
        RowConstraints r3 = new RowConstraints();
        r3.setPercentHeight(10);
        weaponsVisualizer.getRowConstraints().addAll(r0,r1,r2, r3);

        Button weaponButton = new Button("Continue");
        StackPane weaponbc = new StackPane();
        weaponbc.setAlignment(Pos.CENTER);
        weaponbc.getChildren().add(weaponButton);
        weaponsVisualizer.add(weaponbc, 0,2,7,1);

        VBox textCont = new VBox();
        setText(textCont, "Select a weapon to use!");
        weaponsVisualizer.add(textCont, 0,0,7,1);


        ArrayList<StackPane> weaponsSP = new ArrayList<>();
        ArrayList<ImageView> weaponsimages = new ArrayList<>();

        StackPane buttonVisualizerBackToMap = new StackPane();
        buttonVisualizerBackToMap.setAlignment(Pos.CENTER);
        buttonVisualizerBackToMap.getChildren().add(backToMapButton);

        weaponsVisualizer.add(buttonVisualizerBackToMap, 0, 3, 7, 1);

        for(int i=0; i<weapons.size(); i++){
            int index = i;
            weaponsSP.add( new StackPane());
            weaponsimages.add(new ImageView());
            setUpProperties(weaponsSP.get(i), weaponsimages.get(i));
            weaponsVisualizer.add(weaponsSP.get(i), (2*i)+1, 1, 1, 1);
            clickedImage(weaponsimages.get(i), "weapons/" + weapons.get(i));
            String weapon = weapons.get(i);
            weaponsimages.get(i).setOnMouseClicked(e ->{
                setText(textCont, "You selected " + weapons.get(index) + "!");
                weaponButton.setOnAction(ev -> {
                    adrenalineGUI.didChooseWeapon(weapon);
                });
            });
        }
        mainScene.setRoot(weaponsVisualizer);

    }

    public void turnNormal() {
        Platform.runLater(() -> {
            try {
                onTurnNormal();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onTurnNormal() {
        backToMapButton.setText("This weapon can't be used this way! Go back to map");
        backToMapButton.setOnAction(e -> {
            primaryStage.setScene(secondScene);
            this.counter = 15;
            this.adrenalineGUI.newAction();
        });
    }

    public void chooseEffect(ArrayList<String> effects, String weapon) {
        Platform.runLater(() -> {
            try {
                onChooseEffect(effects, weapon);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onChooseEffect(ArrayList<String> effects, String weapon){

        VBox effectVisualizer = new VBox();
        effectVisualizer.setSpacing(20);

        effectVisualizer.setAlignment(Pos.CENTER);
        ArrayList<Button> buttons= new ArrayList<>();
        for(int i=0; i< effects.size(); i++){
            buttons.add(new Button("select "+ effects.get(i)));
            buttons.get(i).setOnAction(e -> {this.adrenalineGUI.didChooseEffects(effects, weapon);});
            effectVisualizer.getChildren().add(buttons.get(i));
        }
        VBox generalBox = new VBox(textContainer, effectVisualizer, backToMapButton);
        generalBox.setBackground(background);
        generalBox.setAlignment(Pos.CENTER);
        generalBox.setSpacing(20);
        mainScene.setRoot(generalBox);
    }

    public void chooseMode(ArrayList<String> modes, String weapon) {
        Platform.runLater(() -> {
            try {
                onChooseMode(modes, weapon);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onChooseMode(ArrayList<String> modes, String weapon){

        VBox modesVisualizer = new VBox();
        modesVisualizer.setSpacing(20);
        modesVisualizer.setAlignment(Pos.CENTER);
        ArrayList<Button> buttons= new ArrayList<>();
        for(int i=0; i<modes.size(); i++){
            buttons.add(new Button("select "+ modes.get(i)));
            String mode = modes.get(i);
            buttons.get(i).setOnAction(e -> {adrenalineGUI.didChooseMode(weapon, mode);});
            modesVisualizer.getChildren().add(buttons.get(i));
        }
        VBox generalBox = new VBox(textContainer, modesVisualizer, backToMapButton);
        generalBox.setBackground(background);
        generalBox.setAlignment(Pos.CENTER);
        generalBox.setSpacing(20);
        mainScene.setRoot(generalBox);
    }

    public void chooseDamage(ArrayList<String> damages, String indexOfEffect, String weapon, String forPotentiableWeapon) {
        Platform.runLater(() -> {
            try {
                onChooseDamage(damages, indexOfEffect, weapon, forPotentiableWeapon);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onChooseDamage(ArrayList<String> damages, String indexOfEffect, String weapon, String forPotentiableWeapon){

        VBox textCont = new VBox();
        setText(textCont, "Select a damage!");
        VBox damagesVisualizer = new VBox();
        damagesVisualizer.setSpacing(20);
        damagesVisualizer.setAlignment(Pos.CENTER);
        ArrayList<Button> buttons= new ArrayList<>();
        for(int i=0; i<damages.size(); i++){
            buttons.add(new Button("select "+ damages.get(i)));
            String damage = damages.get(i);
            Button b = buttons.get(i);
            buttons.get(i).setOnAction(e -> {
                this.adrenalineGUI.didChooseDamage(weapon, damage, indexOfEffect, forPotentiableWeapon);
                b.setOnAction(null);
            });
            damagesVisualizer.getChildren().add(buttons.get(i));
        }
        VBox generalBox = new VBox(textContainer, damagesVisualizer, backToMapButton);
        backToMapButton.setOnAction(e -> primaryStage.setScene(secondScene));
        generalBox.setBackground(background);
        generalBox.setAlignment(Pos.CENTER);
        generalBox.setSpacing(20);
        mainScene.setRoot(generalBox);
    }

}