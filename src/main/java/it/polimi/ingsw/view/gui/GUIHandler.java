package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.utility.MapType;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.management.monitor.StringMonitor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;

@SuppressWarnings("Duplicates")
public class GUIHandler extends Application {

    //main attributes
    private AdrenalineGUI adrenalineGUI;
    private Stage primaryStage;
    private Scene mainScene;
    private Background background;
    private String conf;

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

    //playerboard
    private HashMap<String ,StackPane> playerBoards;
    private HashMap<String, GridPane> damagesGrid;
    private HashMap<String, List <StackPane>> bloodTruck;
    private HashMap<String, List <StackPane>> marksTruck;
    private HashMap<String, List<StackPane>> killedPointsTruck;
    private Map<String, List<String>> weaponsInRespawn;

    private ColumnConstraints c;
    private RowConstraints r;

    private Button button;
    private StackPane buttonContainer;
    private Button button2;
    private HBox boxButton;
    private VBox textContainer;
    private VBox textSelectedContainer;
    private BorderPane firstRoot;
    private GridPane modesChoiceGrid;
    private GridPane messageGrid;

    private DoubleProperty fontSize = new SimpleDoubleProperty(10);
    private ConnectionType connectionType;
    private Scene secondScene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.adrenalineGUI = new AdrenalineGUI(this);
        primaryStage.setTitle("Adrenaline");
        textContainer = new VBox();
        textContainer.setAlignment(Pos.CENTER);
        textSelectedContainer = new VBox();
        textSelectedContainer.setAlignment(Pos.CENTER);
        modesChoiceGrid = new GridPane();

        buttonContainer=new StackPane();
        button2 = new Button();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().add(button2);

        BackgroundImage backgroundImageAfter= new BackgroundImage(new Image(new FileInputStream("src/main/resources/images/background.png")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
        background = new Background(backgroundImageAfter);
        buildMessageGrid();
        textContainer.setAlignment(Pos.CENTER);
        button = new Button("Continue");
        button.setOnAction(e -> {try{chooseConnection();}catch (Exception ex){ex.printStackTrace();}});
        boxButton = new HBox(button);
        boxButton.setAlignment(Pos.CENTER);
        boxButton.setMargin(button, new Insets(0, 0, 50, 0));

        firstRoot = new BorderPane();
        BackgroundImage backgroundImage = new BackgroundImage(new Image(new FileInputStream("src/main/resources/images/adrenaline.jpg")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, true));
        firstRoot.setBackground(new Background(backgroundImage));
        firstRoot.setBottom(boxButton);

        weaponsInRespawn = new HashMap<>();


        mainScene = new Scene(firstRoot, 1200,800);
        primaryStage.setScene(mainScene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        AdrenalineLogger.setLogName("GUI");
        launch(args);
    }


    public void chooseConnection() throws FileNotFoundException{

        firstRoot.getChildren().clear();
        firstRoot.setBackground(background);
        /*root.setStyle("-fx-background-color: white");
        BackgroundImage myBI= new BackgroundImage(new Image(new FileInputStream("src/main/resources/images/adrenaline_background.jpg")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);*/
        firstRoot.setBottom(boxButton);

        CheckBox checkBoxRMI = new CheckBox("RMI");
        CheckBox checkBoxTCP = new CheckBox("TCP");
        checkBoxRMI.setStyle("-fx-fill: white;");
        checkBoxRMI.setStyle("-fx-font-family: 'Andale Mono';");

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
        setText(textContainer, "Please, select a connection");
        firstRoot.setTop(textContainer);
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
            firstRoot.setCenter(generalBox);
            setText(textContainer, "Please provide an address and a port number");
            firstRoot.setTop(textContainer);
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
                ex.printStackTrace();
            }
        });
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
        TextField usernameField = new TextField();
        HBox boxUsername = new HBox(usernameText, usernameField);
        boxUsername.setAlignment(Pos.CENTER_LEFT);

        button.setOnAction(e -> handleLoginOptions(usernameField));

        boxUsername.setMargin(usernameText,  new Insets(0, 0, 0, 50));
        boxUsername.setMargin(usernameField,  new Insets(0, 50, 0, 0));
        firstRoot.setCenter(boxUsername);
        setText(textContainer, "Please provide a username");
        firstRoot.setTop(textContainer);
        firstRoot.setBottom(boxButton);
    }

    public void handleLoginOptions(TextField usernameTextfield) {
        String username = usernameTextfield.getText();
        adrenalineGUI.createUser(username);
    }

    public void handleLoginFailure() {
        Text text = new Text("Username already in use");
        VBox box = new VBox(text, boxButton);
        box.setAlignment(Pos.CENTER);
        firstRoot.setBottom(box);
    }

    public void onChooseCharacter(List<String> availableCharacters) {
        Platform.runLater(() -> {
            try {
                chooseCharacter(availableCharacters);
            } catch (Exception ex) {
                ex.printStackTrace();
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

        modesChoiceGrid.setGridLinesVisible(true);

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
        modesChoiceGrid.setGridLinesVisible(true);


        modesChoiceGrid.setBackground(background);

        Button buttonContinue = new Button("Continue");
        button.setOnAction(e -> setText(textSelectedContainer, "You have to select something to continue"));
        button.setPrefHeight(Integer.MAX_VALUE);
        modesChoiceGrid.add(button, 0,4,6,1);


        setText(textContainer,"Select a character, these are the ones that are not taken");
        modesChoiceGrid.add(textContainer, 0,0,6,1);
        modesChoiceGrid.add(textSelectedContainer, 0,3,6,1);

        ArrayList<ImageView> imageViews = new ArrayList<>();
        for(int i=0; i<availableCharacters.size(); i++) {
            imageViews.add(new ImageView());
        }

            unclickedImage(imageViews, availableCharacters, "characters/"  );

        List<StackPane> stackPanes = new ArrayList<>();
        for(int i=0; i<availableCharacters.size(); i++) {
            stackPanes.add(new StackPane());
            setUpProperties(stackPanes.get(i), imageViews.get(i));
            if(i<3) modesChoiceGrid.add(stackPanes.get(i),i*2, 1,2,1);
            else modesChoiceGrid.add(stackPanes.get(i), ((i-3)*2)+1,2,2,1);

            ImageView imageView = imageViews.get(i);
            String character = availableCharacters.get(i);
            imageViews.get(i).setOnMouseClicked(event -> {
                        unclickedImage(imageViews, availableCharacters, "characters/");
                    clickedImage(imageView, "characters/" + character);

                button.setOnAction( e -> {


                    adrenalineGUI.didChooseCharacter(character);});


                        setText(textSelectedContainer, "You selected " + character);


            });

        }


        mainScene.setRoot(modesChoiceGrid);
    }



    public void onChooseCharacterSuccess() {
        Platform.runLater(() -> {
                chooseCharacterSuccess();

        });
    }

    public void chooseCharacterSuccess(){
        System.out.println(primaryStage.getHeight());
        modesChoiceGrid.getChildren().clear();
        setText(textSelectedContainer, "You chose your character, the game will start soon!");
        modesChoiceGrid.add(textSelectedContainer, 0,1,6,5);
    }

    public void chooseGameMap() {
        Platform.runLater(() -> {
            try {
                onChooseGameMap();
            } catch (Exception ex) {
                ex.printStackTrace();
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
            unclickedImage(imageViews, confs, "board/");
        } catch (Exception e){
            e.printStackTrace();
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
                    unclickedImage(imageViews, confs, "board/" );
                    gameMapButton.setOnAction(e1 -> {adrenalineGUI.didChooseGameMap(configuration);});
                }catch (Exception e1){
                    e1.printStackTrace();
                }
                clickedImage(imageView, "board/" + configuration + "_choice");
            });
        }

        modesChoiceGrid.add(buttonContainer, 0, 3,2,1);
        setText(textContainer, "Choose a map configuration");
        modesChoiceGrid.add(textContainer, 0,0,2,1);
        mainScene.setRoot(modesChoiceGrid);
    }

    public void drawTwoPowerups(List<String> powerups) {
        Platform.runLater(() -> {
            try {
                onDrawTwoPowerups(powerups);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onDrawTwoPowerups(List<String> powerups){
        clear(modesChoiceGrid);
        modesChoiceGrid.setGridLinesVisible(true);

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
        clickedImage(imageView, "powerups/Back");


        modesChoiceGrid.add(boxButton, 0, 2,3,1);
        button.setText("Draw cards");
        button.setOnAction(e -> {try{chooseSpawnPoint(powerups);} catch (Exception ex){ex.printStackTrace();}});
        setText(textContainer, "Draw two powerups from the deck");
        modesChoiceGrid.add(textContainer, 0,0,3,1);

        mainScene.setRoot(modesChoiceGrid);
    }

    public void chooseSpawnPoint(List<String> powerups) throws FileNotFoundException {
        if(modesChoiceGrid!=null) clear(modesChoiceGrid);
        else modesChoiceGrid = new GridPane();
        modesChoiceGrid.setGridLinesVisible(true);

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
            unclickedImage(imageViews, powerups, "powerups/");
        } catch (Exception e){
            e.printStackTrace();
        }
        List<StackPane> stackPanes = new ArrayList<>();
        for(int i=0; i<2; i++){
            stackPanes.add(new StackPane());
            setUpProperties(stackPanes.get(i), imageViews.get(i));
            modesChoiceGrid.add(stackPanes.get(i),(2*i)+1,1 ,1,1);
            ImageView imageView = imageViews.get(i);
            String powerup = powerups.get(i);
            imageViews.get(i).setOnMouseClicked(e -> {
                try {
                    unclickedImage(imageViews, powerups, "powerups/" );
                }catch (Exception e1){
                    e1.printStackTrace();
                }
                clickedImage(imageView, "powerups/" + powerup + "_click");
            });
        }
        modesChoiceGrid.add(boxButton, 0, 2,5,1);
        button.setText("Continue");
        button.setOnAction(e -> {try{} catch (Exception ex){ex.printStackTrace();}});
        setText(textContainer, "Here are two powerup cards. Choose the one you want to discard: its color will be the color where you will spawn.");
        modesChoiceGrid.add(textContainer, 0,0,5,1);

        mainScene.setRoot(modesChoiceGrid);
    }


    public void chooseAction()throws FileNotFoundException{
        clear(modesChoiceGrid);

        ColumnConstraints c0 = new ColumnConstraints();
        c0.setPercentWidth(50);
        modesChoiceGrid.getColumnConstraints().add(c0);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);
        modesChoiceGrid.getColumnConstraints().add(c1);

        RowConstraints r0 = new RowConstraints();
        r0.setPercentHeight(20);
        modesChoiceGrid.getRowConstraints().add(r0);

        for(int i=0; i<5; i++){
            r =  new RowConstraints();
            if(i%2==0) r.setPercentHeight(10);
            else r.setPercentHeight(5);
            modesChoiceGrid.getRowConstraints().add(r);
        }

        RowConstraints r6 = new RowConstraints();
        r6.setPercentHeight(20);
        RowConstraints r7 = new RowConstraints();
        r7.setPercentHeight(20);
        modesChoiceGrid.getRowConstraints().addAll(r6, r7);

        modesChoiceGrid.setBackground(background);


        setText(textContainer,"Select an action to perform");
        modesChoiceGrid.add(textContainer, 0,0,1,1);
        modesChoiceGrid.add(textSelectedContainer, 0,6,1,1);

        Button buttonContinue = new Button("Continue");
        StackPane buttonContainer = new StackPane();
        setUpButtonProperties(buttonContainer, buttonContinue);
        button.setOnAction(e -> setText(textSelectedContainer, "You have to select something to continue"));
        modesChoiceGrid.add(button, 0,7,1,1);

        ArrayList<ImageView> imageViews = new ArrayList<>();
        for(int i=0; i<3; i++) {
            imageViews.add(new ImageView());
        }

        ArrayList<String> actions = new ArrayList<>();
        actions.add("grab");
        actions.add("move");
        actions.add("shoot");

        unclickedImage(imageViews, actions, "actions/"  );

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
                button.setOnMouseClicked( e -> {
                    adrenalineGUI.didChooseAction(action);
                });

                setText(textSelectedContainer, "You selected " + action);
            });


            }

        mainScene.setRoot(modesChoiceGrid);

    }


    void onCreateGameMap(String conf) throws Exception{

        this.conf = conf;
        //Main modesChoiceGrid creation
        boardGrid = new GridPane();
        ColumnConstraints column0 = new ColumnConstraints();
        column0.setPercentWidth(40);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(60);
        boardGrid.getColumnConstraints().addAll(column0, column1);
        RowConstraints rowtext = new RowConstraints();
        rowtext.setPercentHeight(9);
        RowConstraints row0 = new RowConstraints();
        row0.setPercentHeight(14);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(14);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(14);
        RowConstraints row3 = new RowConstraints();
        row3.setPercentHeight(14);
        RowConstraints row4 = new RowConstraints();
        row4.setPercentHeight(35);
        boardGrid.getRowConstraints().addAll(rowtext, row0, row1, row2, row3, row4);
        boardGrid.setGridLinesVisible(true);

        //Map creation
        StackPane mapContainer = new StackPane();
        Image map = new Image(new FileInputStream("src/main/resources/images/board/" + conf + ".png"));
        ImageView ivMap = new ImageView();
        ivMap.setImage(map);
        ivMap.setPreserveRatio(true);
        ivMap.fitHeightProperty().bind(mapContainer.heightProperty());
        ivMap.fitWidthProperty().bind(mapContainer.widthProperty());
        mapContainer.getChildren().add(ivMap);
        mapContainer.setMinHeight(0.0);
        mapContainer.setMinWidth(0.0);
        mapContainer.setAlignment(Pos.CENTER);
        boardGrid.add(mapContainer, 1,1, 1, 4);

        //Map's modesChoiceGrid creation
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
        mapGrid.setGridLinesVisible(true);
        mapContainer.getChildren().add(mapGrid);
        mapGrid.setAlignment(Pos.CENTER);
        mapGrid.setGridLinesVisible(true);


        cellsContainers = new ArrayList<>();
        cellsButtons = new ArrayList<>();
        for(int i=0; i<3; i++){
            cellsButtons.add(new ArrayList<Button>());
            cellsContainers.add(new ArrayList<StackPane>());
            for(int j=0; j<4; j++){
                int row = i;
                int column = j;
                cellsContainers.get(i).add(new StackPane());
                cellsButtons.get(i).add(new Button("Move here"));
                if(!(conf=="conf_1"&&i==0&&j==3 || conf=="conf_3"&&i==2&&j==0 || conf=="conf_2"&&(i==0&&j==3 || i==2&&j==0) || i==1&&j==0 || i==0&&j==2 || i==2&&j==3)) {

                    cellsButtons.get(i).get(j).setStyle("-fx-background-color: transparent");
                    cellsButtons.get(i).get(j).setPrefHeight(Integer.MAX_VALUE);
                    cellsButtons.get(i).get(j).setOnAction(e -> choseCell(row, column));
                    cellsContainers.get(i).get(j).getChildren().add(cellsButtons.get(i).get(j));

                    mapGrid.add(cellsContainers.get(i).get(j), j + 1, i + 1);
                }
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
        boardGrid.add(cardsContainer, 1,5, 1, 1);



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
        leftDeck.setGridLinesVisible(true);
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
        upperDeck.setGridLinesVisible(true);
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
        rightDeck.setGridLinesVisible(true);
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
        BackgroundImage myBI= new BackgroundImage(new Image(new FileInputStream("src/main/resources/images/background.png")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, false));
        boardGrid.setBackground(new Background(myBI));

        //Showing
        secondScene = new Scene(boardGrid, 1200, 675);
        primaryStage.setScene(secondScene);
        primaryStage.setFullScreen(true);


        ArrayList<String> weapons = new ArrayList<>();
        weapons.add("Furnace");
        weapons.add("Hellion");
        weapons.add("Heatseeker");
        onUpdateDeckRight(weapons);
        onUpdateDeckAbove(weapons);
        onUpdateDeckLeft(weapons);
        onUpdateWeaponsCards(weapons);
    }

    public void displayCards(int n, int i){
        Platform.runLater(() -> {
            try {
                onDisplayCards(n, i);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onDisplayCards(int n, int i){
        cardsDisplayer.getChildren().clear();
        cardsDisplayer.setBackground(background);
                if(i==1) cardsDisplayer.add(leftBackup.get(n), 3,1);
                if(i==2) cardsDisplayer.add(aboveBackup.get(n), 3, 1);
                if(i==3) cardsDisplayer.add(rightBackup.get(n), 3, 1);
        button2.setOnAction(e -> {secondScene.setRoot(boardGrid);});
        button2.setText("Go back to the map");
        cardsDisplayer.add(buttonContainer,0,2,7,1);
        secondScene.setRoot(cardsDisplayer);
    }


    public void choseCell(int row, int column){
        System.out.println("You selected the position /n row:" + row + " column:" + column);
    }


    public void updateWeaponsCards(List<String> givenWeapons) {
        Platform.runLater(() -> {
            try {
                onUpdateWeaponsCards(givenWeapons);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onUpdateWeaponsCards(List<String> givenWeapons) throws Exception{

        ArrayList<ImageView> weaponsIV = new ArrayList<>();
        for(StackPane stackPane: weapons){
            stackPane.getChildren().clear();
        }
        for(int i = 0; i < givenWeapons.size(); i++){
            weaponsIV.add(new ImageView());
            clickedImage(weaponsIV.get(i), "weapons/" + givenWeapons.get(i));
            setUpProperties(weapons.get(i), weaponsIV.get(i));
        }
    }

    public void updatePowerups(List<String> givenPowerups) {
        Platform.runLater(() -> {
            try {
                onUpdatePowerups(givenPowerups);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onUpdatePowerups(List<String> givenPowerups) throws Exception{

        ArrayList<ImageView> powerupsIV = new ArrayList<>();
        for(StackPane stackPane: powerups){
            stackPane.getChildren().clear();
        }
        for(int i = 0; i < givenPowerups.size(); i++){
            powerupsIV.add(new ImageView());
            clickedImage(powerupsIV.get(i), "powerups/" + givenPowerups.get(i) + "_click");
            setUpProperties(powerups.get(i), powerupsIV.get(i));
        }
    }

    public void updateRedAmmos(int num) {
        Platform.runLater(() -> {
            try {
                onUpdateRedAmmos(num);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onUpdateRedAmmos(int num) throws Exception{

        ArrayList<ImageView> redAmmosIV = new ArrayList<>();
        for(StackPane stackPane:redAmmos){
            stackPane.getChildren().clear();
        }
        for(int i = 0; i < num; i++){
            redAmmosIV.add(new ImageView());
            clickedImage(redAmmosIV.get(i), "ammotiles/red");
            setUpProperties(redAmmos.get(i), redAmmosIV.get(i));
        }

    }

    public void updateBlueAmmos(int num) {
        Platform.runLater(() -> {
            try {
                onUpdateBlueAmmos(num);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onUpdateBlueAmmos(int num) throws Exception{

        ArrayList <ImageView> blueAmmosIV = new ArrayList<>();
        for(StackPane stackPane:blueAmmos){
            stackPane.getChildren().clear();
        }
        for(int i = 0; i < num; i++){
            blueAmmosIV.add(new ImageView());
            clickedImage(blueAmmosIV.get(i), "ammotiles/blue");
            setUpProperties(blueAmmos.get(i), blueAmmosIV.get(i));
        }
    }


    public void updateYellowAmmos(int num) {
        Platform.runLater(() -> {
            try {
                onUpdateYellowAmmos(num);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onUpdateYellowAmmos(int num) throws Exception{
        ArrayList<ImageView> yellowAmmosIV = new ArrayList<>();
        for(StackPane stackPane:yellowAmmos){
            stackPane.getChildren().clear();
        }
        for(int i = 0; i < num; i++){
            yellowAmmosIV.add(new ImageView());
            clickedImage(yellowAmmosIV.get(i), "ammotiles/yellow");
            setUpProperties(yellowAmmos.get(i), yellowAmmosIV.get(i));
        }
    }


    public void updateDeckLeft(List<String> givenWeapons) {
        Platform.runLater(() -> {
            try {
                onUpdateDeckLeft(givenWeapons);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onUpdateDeckLeft(List<String> givenWeapons){
        ArrayList<ImageView> weaponsIV = new ArrayList<>();
        ArrayList<ImageView> weaponsBackUpIV = new ArrayList<>();
        ArrayList<Button> buttons = new ArrayList<>();
        for(StackPane stackPane: deckLeft){
            stackPane.getChildren().clear();
        }
        for(StackPane stackPane: leftBackup){
            stackPane.getChildren().clear();
        }
        weaponsInRespawn.put("1, 0", givenWeapons);
        for(int i = 0; i < givenWeapons.size(); i++){
            int index = i;
            weaponsIV.add(new ImageView());
            weaponsBackUpIV.add(new ImageView());
            buttons.add(new Button());
            clickedImage(weaponsIV.get(i), "weapons/"+givenWeapons.get(i));
            clickedImage(weaponsBackUpIV.get(i), "weapons/"+givenWeapons.get(i));
            setUpProperties270(deckLeft.get(i), weaponsIV.get(i));
            setUpProperties(leftBackup.get(i), weaponsBackUpIV.get(i));

            deckLeft.get(i).getChildren().add(buttons.get(i));
            buttons.get(i).setStyle("-fx-background-color: transparent");
            buttons.get(i).setPrefHeight(Integer.MAX_VALUE);
            buttons.get(i).setPrefWidth(Integer.MAX_VALUE);
            buttons.get(i).setOnAction(e -> displayCards(index, 1));

        }
    }


    public void updateDeckAbove(List<String> givenWeapons) {
        Platform.runLater(() -> {
            try {
                onUpdateDeckAbove(givenWeapons);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }


    private void onUpdateDeckAbove(List<String> givenWeapons){
        ArrayList<ImageView> weaponsIV = new ArrayList<>();
        ArrayList<ImageView> weaponsBackUpIV = new ArrayList<>();
        ArrayList<Button> buttons = new ArrayList<>();
        for(StackPane stackPane: deckAbove){
            stackPane.getChildren().clear();
        }
        for(StackPane stackPane: aboveBackup){
            stackPane.getChildren().clear();
        }
        weaponsInRespawn.put("0, 2", givenWeapons);
        for(int i = 0; i < givenWeapons.size(); i++){
            int index=i;
            weaponsIV.add(new ImageView());
            weaponsBackUpIV.add(new ImageView());
            buttons.add(new Button());
            clickedImage(weaponsIV.get(i), "weapons/"+givenWeapons.get(i));
            clickedImage(weaponsBackUpIV.get(i), "weapons/"+givenWeapons.get(i));
            setUpProperties(deckAbove.get(i), weaponsIV.get(i));
            setUpProperties(aboveBackup.get(i), weaponsBackUpIV.get(i));

            deckAbove.get(i).getChildren().add(buttons.get(i));
            buttons.get(i).setStyle("-fx-background-color: transparent");
            buttons.get(i).setPrefHeight(Integer.MAX_VALUE);
            buttons.get(i).setPrefWidth(Integer.MAX_VALUE);
            buttons.get(i).setOnAction(e -> displayCards(index, 2));
        }
    }


    public void updateDeckRight(List<String> givenWeapons) {
        Platform.runLater(() -> {
            try {
                onUpdateDeckRight(givenWeapons);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    void onUpdateDeckRight(List<String>givenWeapons){

        ArrayList<ImageView> weaponsIV = new ArrayList<>();
        ArrayList<ImageView> weaponsBackUpIV = new ArrayList<>();
        ArrayList<Button> buttons = new ArrayList<>();
        for(StackPane stackPane: deckRight){
            stackPane.getChildren().clear();
        }
        for(StackPane stackPane: rightBackup){
            stackPane.getChildren().clear();
        }
        weaponsInRespawn.put("2, 3", givenWeapons);
        for(int i = 0; i < givenWeapons.size(); i++){
            int index = i;
            weaponsIV.add(new ImageView());
            weaponsBackUpIV.add(new ImageView());
            buttons.add(new Button());
            clickedImage(weaponsIV.get(i), "weapons/"+givenWeapons.get(i));
            clickedImage(weaponsBackUpIV.get(i), "weapons/"+givenWeapons.get(i));
            setUpProperties90(deckRight.get(i), weaponsIV.get(i));
            setUpProperties(rightBackup.get(i), weaponsBackUpIV.get(i));

            deckRight.get(i).getChildren().add(buttons.get(i));
            buttons.get(i).setStyle("-fx-background-color: transparent");
            buttons.get(i).setPrefHeight(Integer.MAX_VALUE);
            buttons.get(i).setPrefWidth(Integer.MAX_VALUE);
            buttons.get(i).setOnAction(e -> displayCards(index, 3));
        }

    }

    public void updateFirstRow(List<String> ammoTiles) {
        Platform.runLater(() -> {
            try {
                onUpdateFirstRow(ammoTiles);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onUpdateFirstRow(List<String> ammoTiles){
        List<ImageView> ammoTilesIV = new ArrayList<>();
        int i=0;
        for(int j=0; j<4; j++){
            cellsContainers.get(0).get(j).getChildren().clear();
            ammoTilesIV.add(new ImageView());{
                if (!(j==2 || j==3&&(conf=="conf_1" || conf=="conf_2")) && i<ammoTiles.size() ){
                    clickedImage(ammoTilesIV.get(j), "ammotiles/" + ammoTiles.get(i));
                    setUpPropertiesModified(cellsContainers.get(0).get(j), ammoTilesIV.get(j));
                    i++;
                }
            }
        }
    }

    public void updateSecondRow(List<String> ammoTiles) {
        Platform.runLater(() -> {
            try {
                onUpdateSecondRow(ammoTiles);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onUpdateSecondRow(List<String> ammoTiles){
        List<ImageView> ammoTilesIV = new ArrayList<>();
        int i=0;
        for(int j=0; j<4; j++){
            cellsContainers.get(1).get(j).getChildren().clear();
            ammoTilesIV.add(new ImageView());{
                if (!(j==0) && i<ammoTiles.size()){
                    clickedImage(ammoTilesIV.get(j), "ammotiles/" + ammoTiles.get(i));
                    setUpPropertiesModified(cellsContainers.get(1).get(j), ammoTilesIV.get(j));
                    i++;
                }
            }
        }
    }

    public void updateThirdRow(List<String> ammoTiles) {
        Platform.runLater(() -> {
            try {
                onUpdateThirdRow(ammoTiles);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onUpdateThirdRow(List<String> ammoTiles){
        List<ImageView> ammoTilesIV = new ArrayList<>();
        int i=0;
        for(int j=0; j<4; j++){
            cellsContainers.get(2).get(j).getChildren().clear();
            ammoTilesIV.add(new ImageView());{
                if (!(j==3 || j==0&&(conf=="conf_2" || conf=="conf_3")) && i<ammoTiles.size()){
                    clickedImage(ammoTilesIV.get(j), "ammotiles/" + ammoTiles.get(i));
                    setUpPropertiesModified(cellsContainers.get(2).get(j), ammoTilesIV.get(j));
                    i++;
                }
            }
        }
    }

    public void addPlayerBoards(List<String> playerColors) {
        Platform.runLater(() -> {
            try {
                onAddPlayerBoards(playerColors);
            } catch (Exception ex) {
                ex.printStackTrace();
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
            clickedImage(imageViews.get(i), "playerboards/playerboard_"+playerColors.get(i));
            setUpProperties(playerBoards.get(playerColors.get(i)), imageViews.get(i));
            if(i==0) boardGrid.add(playerBoards.get(playerColors.get(i)), 0,5);
            else boardGrid.add(playerBoards.get(playerColors.get(i)), 0, i);

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
            damagesGrid.get(playerColors.get(i)).setGridLinesVisible(true);
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
    }

    public void updateDamages(String color, List<String> damages) {
        Platform.runLater(() -> {
            try {
                onUpdateDamages(color, damages);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onUpdateDamages(String color, List<String> damages){
        List<ImageView> imageViews = new ArrayList<>();
        for(int i=0; i<12; i++){
            bloodTruck.get(color).get(i).getChildren().clear();
        }
        for(int i=0; i<damages.size(); i++) {
            imageViews.add(new ImageView());
            setUpProperties(bloodTruck.get(color).get(i), imageViews.get(i));
            clickedImage(imageViews.get(i), "characters/" + damages.get(i)+"_blood");
        }
    }

    public void updateMarks(String color, List<String> marks) {
        Platform.runLater(() -> {
            try {
                onUpdateMarks(color, marks);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void onUpdateMarks(String color, List<String> marks){
        List<ImageView> imageViews = new ArrayList<>();
        for(int i=0; i<5; i++){
            marksTruck.get(color).get(i).getChildren().clear();
        }
        for(int i=0; i<marks.size(); i++) {
            imageViews.add(new ImageView());
            setUpProperties(marksTruck.get(color).get(i), imageViews.get(i));
            clickedImage(imageViews.get(i), "characters/" + marks.get(i)+"_blood");
        }
    }


    public void displayMessage(String message) {
        Platform.runLater(() -> {
            try {
                displayMessage(message);
            } catch (Exception ex) {
                ex.printStackTrace();
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
        buttonNo.setOnAction(e -> {adrenalineGUI.grabSomethingResponse(true);});
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
            Image clickedPic = new Image(new FileInputStream("src/main/resources/images/" + image + ".png"));
            imageView.setImage(clickedPic);
        } catch (FileNotFoundException e){
            System.err.println(e.toString());
        }
    }

    public void unclickedImage(ArrayList<ImageView> imageViews, List<String> availableCharacters, String path) {
        try {
            for (int i = 0; i < availableCharacters.size(); i++) {
                imageViews.get(i).setImage(new Image(new FileInputStream("src/main/resources/images/" + path + availableCharacters.get(i) + "_unclicked.png")));
            }
        } catch (FileNotFoundException e){
            System.out.println(e.toString());
        }
    }

}





























