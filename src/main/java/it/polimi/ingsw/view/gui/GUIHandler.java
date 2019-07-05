package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.cards.AmmoTile;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class GUIHandler extends Application {

    private AdrenalineGUI adrenalineGUI;
    private Stage primaryStage;
    private Scene mainScene;
    private GridPane boardGrid;
    private GridPane cardsContainer;
    private ColumnConstraints c;
    private RowConstraints r;
    private Button button;
    private HBox boxButton;
    private BorderPane firstRoot;
    private GridPane modesChoiceGrid;
    private Background background;
    private VBox textContainer;
    private VBox textSelectedContainer;
    private ArrayList<String> prova2;
    private DoubleProperty fontSize = new SimpleDoubleProperty(10);
    private ConnectionType connectionType;
    private GridPane messageGrid;

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
        BackgroundImage backgroundImageAfter= new BackgroundImage(new Image(new FileInputStream("src/main/resources/images/background.png")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
        background = new Background(backgroundImageAfter);

        buildMessageGrid();

        textContainer.setAlignment(Pos.CENTER);
        button = new Button("Continue");
        ArrayList<String> prova = new ArrayList<>();
        prova2 = new ArrayList<>();
        prova2.add("Newton - red");
        prova2.add("Newton - blue");
        prova.add("blue");
        prova.add("yellow");
        prova.add("purple");
        prova.add("grey");
        prova.add("green");
        button.setOnAction(e -> {try{chooseConnection();} catch (Exception ex){ex.printStackTrace();}});
        boxButton = new HBox(button);
        boxButton.setAlignment(Pos.CENTER);
        boxButton.setMargin(button, new Insets(0, 0, 50, 0));

        firstRoot = new BorderPane();
        BackgroundImage backgroundImage = new BackgroundImage(new Image(new FileInputStream("src/main/resources/images/adrenaline.jpg")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, true));
        firstRoot.setBackground(new Background(backgroundImage));
        firstRoot.setBottom(boxButton);


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

                    handleCharactersOptions(character);});
                    //adrenalineGUI.didChooseCharacter(character);});


                        setText(textSelectedContainer, "You selected " + character);


            });

        }


        mainScene.setRoot(modesChoiceGrid);
    }

    public void handleCharactersOptions(String character){
        System.out.println(character);


    }

    public void onChooseCharacterSuccess() {
        Platform.runLater(() -> {
                chooseCharacterSucces();

        });
    }

    public void chooseCharacterSucces(){
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
            confs.add("conf"+(i+1));
        }

        try {
            unclickedImage(imageViews, confs, "board/");
        } catch (Exception e){
            e.printStackTrace();
        }

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
                }catch (Exception e1){
                    e1.printStackTrace();
                }
                clickedImage(imageView, "board/" + configuration + "_choice");
            });
        }

        modesChoiceGrid.add(boxButton, 0, 3,2,1);
        button.setOnAction(e -> {try{drawTwoPowerups(prova2);} catch (Exception ex){ex.printStackTrace();}});
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
        button.setOnAction(e -> {try{createMap();} catch (Exception ex){ex.printStackTrace();}});
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


    public void createMap() throws Exception{


        //Main modesChoiceGrid creation
        boardGrid = new GridPane();
        ColumnConstraints column0 = new ColumnConstraints();
        column0.setPercentWidth(40);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(60);
        boardGrid.getColumnConstraints().addAll(column0, column1);
        RowConstraints row0 = new RowConstraints();
        row0.setPercentHeight(15);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(15);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(15);
        RowConstraints row3 = new RowConstraints();
        row3.setPercentHeight(15);
        RowConstraints row4 = new RowConstraints();
        row4.setPercentHeight(40);
        boardGrid.getRowConstraints().addAll(row0, row1, row2, row3, row4);
        boardGrid.setGridLinesVisible(true);

        //Map creation
        StackPane mapContainer = new StackPane();
        Image map = new Image(new FileInputStream("src/main/resources/images/board/conf4.png"));
        ImageView ivMap = new ImageView();
        ivMap.setImage(map);
        ivMap.setPreserveRatio(true);
        ivMap.fitHeightProperty().bind(mapContainer.heightProperty());
        ivMap.fitWidthProperty().bind(mapContainer.widthProperty());
        mapContainer.getChildren().add(ivMap);
        mapContainer.setMinHeight(0.0);
        mapContainer.setMinWidth(0.0);
        mapContainer.setAlignment(Pos.CENTER);
        boardGrid.add(mapContainer, 1,0, 1, 4);

        //Map's modesChoiceGrid creation
        GridPane mapGrid = new GridPane();
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
        //mapGrid.setGridLinesVisible(true);
        //mapGrid.setStyle("-fx-background-color: cornsilk;");


        List<List<StackPane>> stackPanes = new ArrayList<>();
        List<List<ImageView>> ammoTiles = new ArrayList<>();
        List<List<Button>> cells = new ArrayList<>();
        for(int i=0; i<3; i++){
            cells.add(new ArrayList<Button>());
            stackPanes.add(new ArrayList<StackPane>());
            ammoTiles.add(new ArrayList<ImageView>());
            for(int j=0; j<4; j++){
                int row = i;
                int column = j;
                stackPanes.get(i).add(new StackPane());

                ammoTiles.get(i).add(new ImageView());
                clickedImage(ammoTiles.get(i).get(j),"ammotiles/Back");
                setUpPropertiesModified(stackPanes.get(i).get(j), ammoTiles.get(i).get(j));

                cells.get(i).add(new Button("Move here"));
                cells.get(i).get(j).setStyle("-fx-background-color: transparent");
                cells.get(i).get(j).setPrefHeight(Integer.MAX_VALUE);
                cells.get(i).get(j).setOnAction(e -> choseCell(row, column));
                stackPanes.get(i).get(j).getChildren().add(cells.get(i).get(j));

                mapGrid.add(stackPanes.get(i).get(j), j+1, i+1);
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


        //Setting background
        BackgroundImage myBI= new BackgroundImage(new Image(new FileInputStream("src/main/resources/images/background.png")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, false));
        boardGrid.setBackground(new Background(myBI));
        //boardGrid.setStyle("-fx-background-color: black");

        //Testing
        ArrayList<String> prova = new ArrayList<>();
        prova.add("blue");
        prova.add("yellow");
        prova.add("green");
        prova.add("purple");
        prova.add("grey");
        try {
            addPlayerBoards(prova);
            prova.clear();
            prova.add("yellow");
            prova.add("yellow");
            prova.add("yellow");
            prova.add("blue");
            prova.add("blue");
            prova.add("blue");
            prova.add("red");
            prova.add("red");
            prova.add("red");
            addWeaponsCards(null, cardsContainer);
            addAmmos(prova, cardsContainer);
            addPowerups(null, cardsContainer);

        } catch (Exception e){
            e.printStackTrace();
        }

        //Showing
        Scene scene = new Scene(boardGrid, 1200, 675);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
    }

    public void choseCell(int row, int column){
        System.out.println("You selected the position /n row:" + row + " column:" + column);
    }

    public void addWeaponsCards(ArrayList<String> weapons, GridPane weaponsContainer) throws Exception{

        List<StackPane> stackPanes = new ArrayList<>();
        List<ImageView> imageViews = new ArrayList<>();

        for(int i = 0; i < 2; i++){
            stackPanes.add(new StackPane());
            imageViews.add(new ImageView());
            clickedImage(imageViews.get(i), "weapons/AD_weapons_IT_026");
            setUpProperties(stackPanes.get(i), imageViews.get(i));
            weaponsContainer.add(stackPanes.get(i), (i*3),1,3,1);
        }

        boardGrid.getChildren().remove(cardsContainer);
        cardsContainer = weaponsContainer;
        boardGrid.add(cardsContainer, 1,4, 1, 1);
    }

    public void addPowerups(ArrayList<String> powerups, GridPane powerupsContainer) throws Exception{

        List<StackPane> stackPanes = new ArrayList<>();
        List<ImageView> imageViews = new ArrayList<>();

        for(int i = 0; i < 3; i++){
            stackPanes.add(new StackPane());
            imageViews.add(new ImageView());
            clickedImage(imageViews.get(i), "weapons/AD_weapons_IT_026");
            setUpProperties(stackPanes.get(i), imageViews.get(i));
            powerupsContainer.add(stackPanes.get(i), (i*3)+9,1,3,1);
        }

        boardGrid.getChildren().remove(cardsContainer);
        cardsContainer = powerupsContainer;
        boardGrid.add(cardsContainer, 1,4, 1, 1);
    }


    public void addAmmos(ArrayList<String> ammoColor, GridPane ammosContainer) throws Exception{

        List<StackPane> stackPanes = new ArrayList<>();
        List<ImageView> imageViews = new ArrayList<>();

        for(int i = 0; i < ammoColor.size(); i++){
            stackPanes.add(new StackPane());
            imageViews.add(new ImageView());
            clickedImage(imageViews.get(i), "ammotiles/"+ammoColor.get(i));
            setUpProperties(stackPanes.get(i), imageViews.get(i));
            ammosContainer.add(stackPanes.get(i), i*2,0,2,1);
        }


        boardGrid.getChildren().removeAll(cardsContainer);
        cardsContainer = ammosContainer;
        boardGrid.add(cardsContainer, 1,4, 1, 1);
    }

    public void addPlayerBoards(ArrayList<String> playerColors) throws Exception{

        List<StackPane> stackPanes = new ArrayList<>();
        List<ImageView> imageViews = new ArrayList<>();

        for(int i = 0; i < playerColors.size(); i++){
            stackPanes.add(new StackPane());
            imageViews.add(new ImageView());
            clickedImage(imageViews.get(i), "playerboards/playerboard_"+playerColors.get(i));
            setUpProperties(stackPanes.get(i), imageViews.get(i));
            if(i==0) boardGrid.add(stackPanes.get(i), 0,4);
            else boardGrid.add(stackPanes.get(i), 0, i-1);
        }
    }

    public void displayMessage(String message){
        setText(textContainer, message);
        messageGrid.add(textContainer, 0,0,2,1);
        mainScene.setRoot(messageGrid);
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

        buttonNo.setOnAction(e -> {System.out.println("Sbagliato!");});
        buttonYes.setOnAction(e -> {System.out.println("Sbagliato!");});
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























































/*
package it.polimi.ingsw.view.gui;
import it.polimi.ingsw.model.utility.MapType;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;
import javafx.application.Platform;
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
import java.util.List;

public class GUIHandler extends Application  {

    private BorderPane root;
    private AdrenalineGUI adrenalineGUI;
    private HBox boxButton;
    private Button button;
    private ConnectionType connectionType;
    private Stage primaryStage;

    public static void main(String[] args) {
        AdrenalineLogger.setLogName("GUI");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;

        this.adrenalineGUI = new AdrenalineGUI(this);

        primaryStage.setTitle("Adrenaline");

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

        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();
    }

    public void chooseConnection() throws FileNotFoundException{

        root.getChildren().clear();
        //root.setStyle("-fx-background-color: white");
        BackgroundImage myBI= new BackgroundImage(new Image(new FileInputStream("src/main/resources/images/adrenaline_background.jpg")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        root.setBackground(new Background(myBI));
        root.setBottom(boxButton);

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
        login();

        //try {
       //     chooseGameMap();
        //}catch (FileNotFoundException e){
      //      System.err.println("File exception:" + e.toString());
       // }

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

    public void chooseCharacter(List<String> availableCharacters) throws FileNotFoundException{
        try {
            root.getChildren().clear();

            Text text = new Text();
            HBox boxText = new HBox(text);
            boxText.setAlignment(Pos.CENTER);

            Text tBlue = new Text("BANSHEE:");
            Image imageBlue;
            if (availableCharacters.contains("blue")) {
                imageBlue = new Image(new FileInputStream("src/main/resources/images/characters/blue_character.png"));
            } else {
                imageBlue = new Image(new FileInputStream("src/main/resources/images/characters/blue_character_taken.png"));
            }
            ImageView ivBlue = new ImageView();
            ivBlue.setImage(imageBlue);
            VBox boxBlue = new VBox(tBlue, ivBlue);
            boxBlue.setAlignment(Pos.CENTER);

            Text tGreen = new Text("SPROG:");
            Image imageGreen;
            if (availableCharacters.contains("green")) {
                imageGreen = new Image(new FileInputStream("src/main/resources/images/characters/green_character.png"));
            } else {
                imageGreen = new Image(new FileInputStream("src/main/resources/images/characters/green_character_taken.png"));
            }
            ImageView ivGreen = new ImageView();
            ivGreen.setImage(imageGreen);
            VBox boxGreen = new VBox(tGreen, ivGreen);
            boxGreen.setAlignment(Pos.CENTER);

            Text tGrey = new Text("DOZER:");
            Image imageGrey;
            if (availableCharacters.contains("grey")) {
                imageGrey = new Image(new FileInputStream("src/main/resources/images/characters/grey_character.png"));
            } else {
                imageGrey = new Image(new FileInputStream("src/main/resources/images/characters/grey_character_taken.png"));
            }
            ImageView ivGrey = new ImageView();
            ivGrey.setImage(imageGrey);
            VBox boxGrey = new VBox(tGrey, ivGrey);
            boxGrey.setAlignment(Pos.CENTER);

            Text tPurple = new Text("VIOLET:");
            Image imagePurple;
            if (availableCharacters.contains("purple")) {
                imagePurple = new Image(new FileInputStream("src/main/resources/images/characters/purple_character.png"));
            } else {
                imagePurple = new Image(new FileInputStream("src/main/resources/images/characters/purple_character_taken.png"));
            }
            ImageView ivPurple = new ImageView();
            ivPurple.setImage(imagePurple);
            VBox boxPurple = new VBox(tPurple, ivPurple);
            boxPurple.setAlignment(Pos.CENTER);

            Text tYellow = new Text("D-STRUCT-OR:");
            Image imageYellow;
            if (availableCharacters.contains("yellow")) {
                imageYellow = new Image(new FileInputStream("src/main/resources/images/characters/yellow_character.png"));
            } else {
                imageYellow = new Image(new FileInputStream("src/main/resources/images/characters/yellow_character_taken.png"));
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void handleCharactersOptions(String selectedCharacter){
        adrenalineGUI.didChooseCharacter(selectedCharacter);
    }

    public void chooseGameMap() throws FileNotFoundException{
        root.getChildren().clear();

        Text tFirst = new Text("First configuration, good for 3 or 4 players:");
        Image firstLeft = new Image(new FileInputStream("src/main/resources/images/board/board1.png"));
        ImageView ivFirstLeft = new ImageView();
        ivFirstLeft.setImage(firstLeft);
        ivFirstLeft.setFitHeight(321);
        ivFirstLeft.setFitWidth(200);
        Image firstRight = new Image(new FileInputStream("src/main/resources/images/board/board4.png"));
        ImageView ivFirstRight = new ImageView();
        ivFirstRight.setImage(firstRight);
        ivFirstRight.setFitHeight(321);
        ivFirstRight.setFitWidth(200);
        HBox boxFirst = new HBox(ivFirstLeft, ivFirstRight);
        VBox vBoxFirst = new VBox(tFirst, boxFirst);

        Text tSecond = new Text("Second configuration, good for any number of players:");
        Image secondLeft = new Image(new FileInputStream("src/main/resources/images/board/board1.png"));
        ImageView ivSecondLeft = new ImageView();
        ivSecondLeft.setImage(secondLeft);
        ivSecondLeft.setFitHeight(321);
        ivSecondLeft.setFitWidth(200);
        Image secondRight = new Image(new FileInputStream("src/main/resources/images/board/board2.png"));
        ImageView ivSecondRight = new ImageView();
        ivSecondRight.setImage(secondRight);
        ivSecondRight.setFitHeight(321);
        ivSecondRight.setFitWidth(200);
        HBox boxSecond = new HBox(ivSecondLeft, ivSecondRight);
        VBox vBoxSecond = new VBox(tSecond, boxSecond);

        Text tThird = new Text("Third configuration, good for any number of players:");
        Image thirdLeft = new Image(new FileInputStream("src/main/resources/images/board/board3.png"));
        ImageView ivThirdLeft = new ImageView();
        ivThirdLeft.setImage(thirdLeft);
        ivThirdLeft.setFitHeight(321);
        ivThirdLeft.setFitWidth(200);
        Image thirdRight = new Image(new FileInputStream("src/main/resources/images/board/board2.png"));
        ImageView ivThirdRight = new ImageView();
        ivThirdRight.setImage(thirdRight);
        ivThirdRight.setFitHeight(321);
        ivThirdRight.setFitWidth(200);
        HBox boxThird = new HBox(ivThirdLeft, ivThirdRight);
        VBox vBoxThird = new VBox(tThird, boxThird);

        Text tFourth = new Text("Fourth configuration, good for 4 or 5 players:");
        Image fourthLeft = new Image(new FileInputStream("src/main/resources/images/board/board3.png"));
        ImageView ivFourthLeft = new ImageView();
        ivFourthLeft.setImage(fourthLeft);
        ivFourthLeft.setFitHeight(321);
        ivFourthLeft.setFitWidth(200);
        Image fourthRight = new Image(new FileInputStream("src/main/resources/images/board/board4.png"));
        ImageView ivFourthRight = new ImageView();
        ivFourthRight.setImage(fourthRight);
        ivFourthRight.setFitHeight(321);
        ivFourthRight.setFitWidth(200);
        HBox boxFourth = new HBox(ivFourthLeft, ivFourthRight);
        VBox vBoxFourth = new VBox(tFourth, boxFourth);

        HBox upperBox = new HBox(vBoxFirst, vBoxSecond);
        HBox lowerBox = new HBox(vBoxThird, vBoxFourth);
        upperBox.setMargin(vBoxFirst, new Insets(10,10,10,10));
        upperBox.setMargin(vBoxSecond, new Insets(10,10,10,10));
        lowerBox.setMargin(vBoxThird, new Insets(10,10,10,10));
        lowerBox.setMargin(vBoxFourth, new Insets(10,10,10,10));

        Text text = new Text();
        VBox generalBox = new VBox(upperBox, lowerBox, text);
        generalBox.setAlignment(Pos.CENTER);
        root.setTop(generalBox);
        root.setBottom(boxButton);

        boxFirst.setOnMouseClicked(e -> {
            text.setText("You selected the first configuration!");
            button.setOnAction(ev -> handleGameMapOptions(MapType.values()[0].toString()));
        });

        boxSecond.setOnMouseClicked(e -> {
            text.setText("You selected the second configuration!");
            button.setOnAction(ev -> handleGameMapOptions(MapType.values()[1].toString()));
        });

        boxThird.setOnMouseClicked(e -> {
            text.setText("You selected the third configuration!");
            button.setOnAction(ev -> handleGameMapOptions(MapType.values()[2].toString()));
        });

        boxFourth.setOnMouseClicked(e -> {
            text.setText("You selected the fourth configuration!");
            button.setOnAction(ev -> handleGameMapOptions(MapType.values()[3].toString()));
        });
    }

    public void handleGameMapOptions(String configuration){
        adrenalineGUI.didChooseGameMap(configuration);
    }

    public void chooseSpawnPoint(List<String> powerups) throws FileNotFoundException{
        root.getChildren().clear();
        Text text = new Text("Draw two power-up cards from this power-ups deck!");
        Image back = new Image(new FileInputStream("src/main/resources/images/powerups/Back.png"));
        ImageView ivBack = new ImageView();
        ivBack.setImage(back);
        HBox cardBox = new HBox(ivBack);
        cardBox.setAlignment(Pos.CENTER);
        Button b = new Button("Draw two cards");
        HBox boxB = new HBox(b);
        boxB.setAlignment(Pos.CENTER);
        boxB.setMargin(b, new Insets(0, 0, 50, 0));
        root.setCenter(cardBox);
        root.setBottom(boxB);
        b.setOnAction(e -> {try {
            chooseSpawnPointHelper(powerups);
        }catch (FileNotFoundException e1){
            System.err.println(e.toString());
        }
        });
    }

    public void chooseSpawnPointHelper(List<String> powerups) throws FileNotFoundException{
        root.getChildren().clear();
        Text text = new Text("Choose a spawn point by selecting one card that you will not keep");
        HBox textBox = new HBox(text);
        textBox.setMargin(text, new Insets(10,10,10,10));
        textBox.setAlignment(Pos.CENTER);

        Image firstCard = new Image(new FileInputStream("src/main/resources/images/powerups/" + powerups.get(0) +".png"));
        ImageView ivFirst = new ImageView();
        ivFirst.setImage(firstCard);

        Image secondCard = new Image(new FileInputStream("src/main/resources/images/powerups/" + powerups.get(1) +".png"));
        ImageView ivSecond = new ImageView();
        ivSecond.setImage(secondCard);

        HBox cards = new HBox(ivFirst, ivSecond);
        cards.setAlignment(Pos.CENTER);
        cards.setMargin(ivFirst, new Insets(10,10,10,10));
        cards.setMargin(ivSecond, new Insets(10,10,10,10));
        root.setTop(textBox);
        root.setCenter(cards);
        Text textBelow = new Text();
        VBox vBox = new VBox(textBelow, boxButton);
        vBox.setMargin(textBelow, new Insets(10,10,10,10));
        root.setBottom(vBox);

        ivFirst.setOnMouseClicked(e -> {
            textBelow.setText("You selected the first card as a spawpoint!");
            button.setOnAction(ev -> handleSpawnPointsOptions(powerups.get(0), powerups.get(1)));
            try {
                Image firstCard_click = new Image(new FileInputStream("src/main/resources/images/powerups/" + powerups.get(0) + "_click.png"));
                ivFirst.setImage(firstCard_click);
                ivSecond.setImage(secondCard);
            } catch (FileNotFoundException e1){
                System.err.println(e.toString());
            }
        });

        ivSecond.setOnMouseClicked(e -> {
            textBelow.setText("You selected the second card as a spawpoint!");
            button.setOnAction(ev -> handleSpawnPointsOptions(powerups.get(1), powerups.get(0)));
            try {
                Image secondCard_click = new Image(new FileInputStream("src/main/resources/images/powerups/" + powerups.get(1) + "_click.png"));
                ivSecond.setImage(secondCard_click);
                ivFirst.setImage(firstCard);
            } catch (FileNotFoundException e1){
                System.err.println(e.toString());
            }
        });
    }

    public void handleSpawnPointsOptions(String spawnPoint, String otherPowerup){
        adrenalineGUI.didChooseSpawnPoint(spawnPoint, otherPowerup);
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

    public void chooseAction() throws FileNotFoundException{

        Text text = new Text("Choose your next move!");
        HBox textBox = new HBox(text);
        textBox.setMargin(text, new Insets(10,10,10,10));
        textBox.setAlignment(Pos.CENTER);


        Image shoot = new Image(new FileInputStream("src/main/resources/images/actions/shoot.png"));
        ImageView ivShoot = new ImageView();
        ivShoot.setImage(shoot);
        Image grab = new Image(new FileInputStream("src/main/resources/images/actions/grab.png"));
        ImageView ivGrab = new ImageView();
        ivGrab.setImage(grab);
        Image move = new Image(new FileInputStream("src/main/resources/images/actions/move.png"));
        ImageView ivMove = new ImageView();
        ivMove.setImage(move);

        VBox actions = new VBox(ivShoot, ivGrab, ivMove);
        actions.setAlignment(Pos.CENTER);
        actions.setMargin(ivShoot, new Insets(10,10,10,10));
        actions.setMargin(ivGrab, new Insets(10,10,10,10));
        actions.setMargin(ivMove, new Insets(10,10,10,10));
        root.setTop(textBox);
        root.setCenter(actions);
        Text textBelow = new Text();
        VBox vBox = new VBox(textBelow, boxButton);
        vBox.setMargin(textBelow, new Insets(10,10,10,10));
        root.setBottom(vBox);

        ivShoot.setOnMouseClicked(e -> {
            textBelow.setText("You chose to shoot!");
            button.setOnAction(ev -> handleActionOptions("shoot"));
            try {
                Image shoot_click = new Image(new FileInputStream("src/main/resources/images/actions/shoot_click.png"));
                ivShoot.setImage(shoot_click);
                ivGrab.setImage(grab);
                ivMove.setImage(move);
            } catch (FileNotFoundException e1){
                System.err.println(e.toString());
            }

        });

        ivGrab.setOnMouseClicked(e -> {
            textBelow.setText("You chose to grab something!");
            button.setOnAction(ev -> handleActionOptions("grab"));
            try{
                Image grab_click = new Image(new FileInputStream("src/main/resources/images/actions/grab_click.png"));
                ivGrab.setImage(grab_click);
                ivShoot.setImage(shoot);
                ivMove.setImage(move);
            } catch (FileNotFoundException e1){
                System.err.println(e.toString());
            }
        });

        ivMove.setOnMouseClicked(e -> {
            textBelow.setText("You chose to move!");
            button.setOnAction(ev -> handleActionOptions("move"));
            try {
                Image move_click = new Image(new FileInputStream("src/main/resources/images/actions/move_click.png"));
                ivMove.setImage(move_click);
                ivShoot.setImage(shoot);
                ivGrab.setImage(grab);
            } catch (FileNotFoundException e1){
                AdrenalineLogger.error(e.toString());
            }
        });
    }

    public void handleActionOptions(String action){
    }

    public void board() throws FileNotFoundException{
        root.getChildren().clear();

        Image row0 = new Image(new FileInputStream("src/main/resources/images/firstboard/first_row.png"));
        ImageView ivRow0 = new ImageView();
        ivRow0.setImage(row0);
        Image above1 = new Image(new FileInputStream("src/main/resources/images/firstboard/1-1.png"));
        ImageView ivAbove1 = new ImageView();
        ivAbove1.setImage(above1);
        Image r0c0 = new Image(new FileInputStream("src/main/resources/images/firstboard/2-1.png"));
        ImageView iv00 = new ImageView();
        iv00.setImage(r0c0);
        Image r1c0 = new Image(new FileInputStream("src/main/resources/images/firstboard/3-1.png"));
        ImageView iv10 = new ImageView();
        iv10.setImage(r1c0);
        Image r2c0 = new Image(new FileInputStream("src/main/resources/images/firstboard/4-1.png"));
        ImageView iv20 = new ImageView();
        iv20.setImage(r2c0);
        Image below1 = new Image(new FileInputStream("src/main/resources/images/firstboard/5-1.png"));
        ImageView ivBelow1 = new ImageView();
        ivBelow1.setImage(below1);

        VBox firstRow = new VBox(ivAbove1, iv00, iv10, iv20, ivBelow1);


        Image above2 = new Image(new FileInputStream("src/main/resources/images/firstboard/1-2.png"));
        ImageView ivAbove2 = new ImageView();
        ivAbove2.setImage(above2);
        Image r0c1 = new Image(new FileInputStream("src/main/resources/images/firstboard/2-2.png"));
        ImageView iv01 = new ImageView();
        iv01.setImage(r0c1);
        Image r1c1 = new Image(new FileInputStream("src/main/resources/images/firstboard/3-2.png"));
        ImageView iv11 = new ImageView();
        iv11.setImage(r1c1);
        Image r2c1 = new Image(new FileInputStream("src/main/resources/images/firstboard/4-2.png"));
        ImageView iv21 = new ImageView();
        iv21.setImage(r2c1);
        Image below2 = new Image(new FileInputStream("src/main/resources/images/firstboard/5-2.png"));
        ImageView ivBelow2 = new ImageView();
        ivBelow2.setImage(below2);

        VBox secondRow = new VBox(ivAbove2, iv01, iv11, iv21, ivBelow2);


        Image middle0 = new Image(new FileInputStream("src/main/resources/images/secondboard/1-0.png"));
        ImageView ivMiddle0 = new ImageView();
        ivMiddle0.setImage(middle0);
        Image middle1 = new Image(new FileInputStream("src/main/resources/images/secondboard/2-0.png"));
        ImageView ivMiddle1 = new ImageView();
        ivMiddle1.setImage(middle1);
        Image middle2 = new Image(new FileInputStream("src/main/resources/images/secondboard/3-0.png"));
        ImageView ivMiddle2 = new ImageView();
        ivMiddle2.setImage(middle2);

        VBox middle = new VBox(ivMiddle0, ivMiddle1, ivMiddle2);


        Image r0c2 = new Image(new FileInputStream("src/main/resources/images/secondboard/1-1.png"));
        ImageView iv02 = new ImageView();
        iv02.setImage(r0c2);
        Image r1c2 = new Image(new FileInputStream("src/main/resources/images/secondboard/2-1.png"));
        ImageView iv12 = new ImageView();
        iv12.setImage(r1c2);
        Image r2c2 = new Image(new FileInputStream("src/main/resources/images/secondboard/3-1.png"));
        ImageView iv22 = new ImageView();
        iv22.setImage(r2c2);

        VBox thirdRow = new VBox( iv02, iv12, iv22);


        Image r0c3 = new Image(new FileInputStream("src/main/resources/images/secondboard/1-2.png"));
        ImageView iv03 = new ImageView();
        iv03.setImage(r0c3);
        Image r1c3 = new Image(new FileInputStream("src/main/resources/images/secondboard/2-2.png"));
        ImageView iv13 = new ImageView();
        iv13.setImage(r1c3);
        Image r2c3 = new Image(new FileInputStream("src/main/resources/images/secondboard/3-2.png"));
        ImageView iv23 = new ImageView();
        iv23.setImage(r2c3);

        VBox fourthRow = new VBox( iv03, iv13, iv23);


        HBox hBox = new HBox(middle, thirdRow, fourthRow);
        Image above34 = new Image(new FileInputStream("src/main/resources/images/secondboard/above.png"));
        ImageView ivAbove34 = new ImageView();
        ivAbove34.setImage(above34);
        Image below34 = new Image(new FileInputStream("src/main/resources/images/secondboard/below.png"));
        ImageView ivBelow34 = new ImageView();
        ivBelow34.setImage(below34);
        VBox third_fourth_row = new VBox(ivAbove34, hBox, ivBelow34);

        Image lastRow = new Image(new FileInputStream("src/main/resources/images/secondboard/last_row.png"));
        ImageView ivLastRow = new ImageView();
        ivLastRow.setImage(lastRow);

        HBox generalBox = new HBox(ivRow0, firstRow, secondRow, third_fourth_row, ivLastRow);
        generalBox.autosize();

        root.getChildren().add(generalBox);
    }
}

*/