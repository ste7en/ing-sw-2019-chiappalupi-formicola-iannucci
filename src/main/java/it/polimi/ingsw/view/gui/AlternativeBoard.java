package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.utility.AdrenalineLogger;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
public class AlternativeBoard extends Application {

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
    private ArrayList<String> prova2;
    private DoubleProperty fontSize = new SimpleDoubleProperty(10);

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        //this.adrenalineGUI = new AdrenalineGUI(this);
        primaryStage.setTitle("Adrenaline");

        textContainer = new VBox();
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
        button.setOnAction(e -> {try{chooseCharacter(prova);} catch (Exception ex){ex.printStackTrace();}});
        boxButton = new HBox(button);
        boxButton.setAlignment(Pos.CENTER);
        boxButton.setMargin(button, new Insets(0, 0, 50, 0));

        firstRoot = new BorderPane();
        BackgroundImage backgroundImage = new BackgroundImage(new Image(new FileInputStream("src/main/resources/images/adrenaline.jpg")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
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

    public void clickedImage(ImageView imageView, String image) {
        try {
            Image clickedPic = new Image(new FileInputStream("src/main/resources/images/" + image + ".png"));
            imageView.setImage(clickedPic);
        } catch (FileNotFoundException e){
            System.err.println(e.toString());
        }
    }

    public void unclickedImage(ArrayList<ImageView> imageViews, List<String> availableCharacters, String path) throws Exception{
        for (int i=0; i<availableCharacters.size(); i++){
            imageViews.get(i).setImage(new Image(new FileInputStream("src/main/resources/images/" + path + availableCharacters.get(i) + "_unclicked.png")));
        }
    }

    public void chooseCharacter(List<String> availableCharacters) throws FileNotFoundException {
        modesChoiceGrid = new GridPane();
        for (int j = 0; j < 6; j++) {
            c = new ColumnConstraints();
            c.setPercentWidth(16.66);
            modesChoiceGrid.getColumnConstraints().add(c);
        }

        RowConstraints r0 = new RowConstraints();
        r0.setPercentHeight(10);
        RowConstraints r1 = new RowConstraints();
        r1.setPercentHeight(35);
        RowConstraints r2 = new RowConstraints();
        r2.setPercentHeight(35);
        RowConstraints r3 = new RowConstraints();
        r3.setPercentHeight(10);
        RowConstraints r4 = new RowConstraints();
        r4.setPercentHeight(10);
        modesChoiceGrid.getRowConstraints().addAll(r0, r1, r2,r3,r4);

        BackgroundImage backgroundImage= new BackgroundImage(new Image(new FileInputStream("src/main/resources/images/background.png")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
        background = new Background(backgroundImage);
        modesChoiceGrid.setBackground(background);

        ArrayList<ImageView> imageViews = new ArrayList<>();
        for(int i=0; i<availableCharacters.size(); i++) {
            imageViews.add(new ImageView());
        }

        try {
            unclickedImage(imageViews, availableCharacters, "characters/"  );
        } catch (Exception e){
            e.printStackTrace();
        }

        List<StackPane> stackPanes = new ArrayList<>();
        for(int i=0; i<availableCharacters.size(); i++) {
            stackPanes.add(new StackPane());
            setUpProperties(stackPanes.get(i), imageViews.get(i));
            if(i<3) modesChoiceGrid.add(stackPanes.get(i),i*2, 1,2,1);
            else modesChoiceGrid.add(stackPanes.get(i), ((i-3)*2)+1,2,2,1);

            ImageView imageView = imageViews.get(i);
            String character = availableCharacters.get(i);
            imageViews.get(i).setOnMouseClicked(e -> {
                try {
                    unclickedImage(imageViews, availableCharacters, "characters/" );
                }catch (Exception e1){
                    e1.printStackTrace();
                }
                clickedImage(imageView, "characters/" + character);
            });
        }

        //text.setStyle("-fx-font: 40px Tahoma; -fx-fill: linear-gradient(from 0% 0% to 100% 200%, repeat, yellow 0%, white 50%);");
        modesChoiceGrid.add(boxButton, 0, 4,6,1);
        button.setOnAction(e -> {try{chooseGameMap();} catch (Exception ex){ex.printStackTrace();}});
        setText("Select a character");
        modesChoiceGrid.add(textContainer, 0,0,6,1);

        mainScene.setRoot(modesChoiceGrid);
    }

    public void chooseGameMap(){
        if(modesChoiceGrid!=null) clear(modesChoiceGrid);
        else modesChoiceGrid = new GridPane();

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
        setText("Choose a map configuration");
        modesChoiceGrid.add(textContainer, 0,0,2,1);
        mainScene.setRoot(modesChoiceGrid);
    }

    public void drawTwoPowerups(List<String> powerups){
        if(modesChoiceGrid!=null) clear(modesChoiceGrid);
        else modesChoiceGrid = new GridPane();
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
        setText("Draw two powerups from the deck");
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
        setText("Here are two powerup cards. Choose the one you want to discard: its color will be the color where you will spawn.");
        modesChoiceGrid.add(textContainer, 0,0,5,1);

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
        Image map = new Image(new FileInputStream("src/main/resources/images/board/conf3.png"));
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

        //Adding buttons to the map's modesChoiceGrid
        Button r0c0 = new Button("Move here");
        r0c0.setStyle("-fx-background-color: rgba(210,204,161,0.2);");
        mapGrid.add(r0c0, 1,1);
        r0c0.setOnAction(e -> choseCell(0,0));
        Button r0c1 = new Button("Move here");
        r0c1.setStyle("-fx-background-color: transparent;");
        mapGrid.add(r0c1, 2,1);
        r0c1.setOnAction(e -> choseCell(0,1));
        Button r0c2 = new Button("Move here");
        r0c2.setStyle("-fx-background-color: transparent;");
        mapGrid.add(r0c2, 3,1);
        r0c2.setOnAction(e -> choseCell(0,2));
        Button r0c3 = new Button("Move here");
        r0c3.setStyle("-fx-background-color: transparent;");
        mapGrid.add(r0c3, 4,1);
        r0c3.setOnAction(e -> choseCell(0,3));
        Button r1c0 = new Button("Move here");
        r1c0.setStyle("-fx-background-color: transparent;");
        mapGrid.add(r1c0, 1,2);
        r1c0.setOnAction(e -> choseCell(1,0));
        Button r1c1 = new Button("Move here");
        r1c1.setStyle("-fx-background-color: transparent;");
        mapGrid.add(r1c1, 2,2);
        r1c1.setOnAction(e -> choseCell(1,1));
        Button r1c2 = new Button("Move here");
        r1c2.setStyle("-fx-background-color: transparent;");
        mapGrid.add(r1c2, 3,2);
        r1c2.setOnAction(e -> choseCell(1,2));
        Button r1c3 = new Button("Move here");
        r1c3.setStyle("-fx-background-color: transparent;");
        mapGrid.add(r1c3, 4,2);
        r1c3.setOnAction(e -> choseCell(1,3));
        Button r2c0 = new Button("Move here");
        r2c0.setStyle("-fx-background-color: transparent;");
        mapGrid.add(r2c0, 1,3);
        r2c0.setOnAction(e -> choseCell(2,0));
        Button r2c1 = new Button("Move here");
        r2c1.setStyle("-fx-background-color: transparent;");
        mapGrid.add(r2c1, 2,3);
        r2c1.setOnAction(e -> choseCell(2,1));
        Button r2c2 = new Button("Move here");
        r2c2.setStyle("-fx-background-color: transparent;");
        mapGrid.add(r2c2, 3,3);
        r2c2.setOnAction(e -> choseCell(2,2));
        Button r2c3 = new Button("Move here");
        r2c3.setStyle("-fx-background-color: transparent;");
        mapGrid.add(r2c3, 4,3);
        r2c3.setOnAction(e -> choseCell(2,3));


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


        StackPane weapon1Container = new StackPane();
        Image weapon1 = new Image(new FileInputStream("src/main/resources/images/weapons/AD_weapons_IT_026.png"));
        ImageView ivWeapon1 = new ImageView();
        ivWeapon1.setImage(weapon1);
        setUpProperties(weapon1Container, ivWeapon1);

        StackPane weapon2Container = new StackPane();
        Image weapon2 = new Image(new FileInputStream("src/main/resources/images/weapons/AD_weapons_IT_022.png"));
        ImageView ivWeapon2 = new ImageView();
        ivWeapon2.setImage(weapon2);
        setUpProperties(weapon2Container, ivWeapon2);

        StackPane weapon3Container = new StackPane();
        Image weapon3 = new Image(new FileInputStream("src/main/resources/images/weapons/AD_weapons_IT_029.png"));
        ImageView ivWeapon3 = new ImageView();
        ivWeapon3.setImage(weapon3);
        setUpProperties(weapon3Container, ivWeapon3);


        weaponsContainer.add(weapon1Container, 0,1,3,1);
        weaponsContainer.add(weapon2Container, 3,1,3,1);
        weaponsContainer.add(weapon3Container, 6,1,3,1);

        StackPane playerBoardContainer = new StackPane();
        Image myBoard = new Image(new FileInputStream("src/main/resources/images/powerups/Newton - blue_unclicked.png"));
        ImageView ivMyBoard = new ImageView();
        ivMyBoard.setImage(myBoard);
        setUpProperties(playerBoardContainer, ivMyBoard);
        weaponsContainer.add(playerBoardContainer, 9,1,3,1);

        StackPane playerBoardContainer2 = new StackPane();
        Image myBoard2 = new Image(new FileInputStream("src/main/resources/images/powerups/TagbackGrenade - blue_unclicked.png"));
        ImageView ivMyBoard2 = new ImageView();
        ivMyBoard2.setImage(myBoard2);
        setUpProperties(playerBoardContainer2, ivMyBoard2);
        weaponsContainer.add(playerBoardContainer2, 12,1,3,1);

        StackPane playerBoardContainer3 = new StackPane();
        Image myBoard3 = new Image(new FileInputStream("src/main/resources/images/powerups/Teleporter - red_unclicked.png"));
        ImageView ivMyBoard3 = new ImageView();
        ivMyBoard3.setImage(myBoard3);
        setUpProperties(playerBoardContainer3, ivMyBoard3);
        weaponsContainer.add(playerBoardContainer3, 15,1,3,1);
        //weaponsContainer.setStyle("-fx-background-color: cornsilk;");
        weaponsContainer.setGridLinesVisible(true);
        boardGrid.getChildren().remove(cardsContainer);
        cardsContainer = weaponsContainer;
        boardGrid.add(cardsContainer, 1,4, 1, 1);
    }

    public void chooseAction()throws FileNotFoundException{

    }

    public void addAmmos(ArrayList<String> ammoColor, GridPane ammosContainer) throws Exception{
        StackPane ammoContainer1 = new StackPane();
        Image ammo1 = new Image(new FileInputStream("src/main/resources/images/ammotiles/" + ammoColor.get(0) + ".png"));
        ImageView ivAmmo1 = new ImageView();
        ivAmmo1.setImage(ammo1);
        setUpProperties(ammoContainer1, ivAmmo1);
        ammosContainer.add(ammoContainer1, 0,0,2,1);

        StackPane ammoContainer2 = new StackPane();
        Image ammo2 = new Image(new FileInputStream("src/main/resources/images/ammotiles/" + ammoColor.get(1) + ".png"));
        ImageView ivAmmo2 = new ImageView();
        ivAmmo2.setImage(ammo2);
        setUpProperties(ammoContainer2, ivAmmo2);
        ammosContainer.add(ammoContainer2, 2,0,2,1);

        StackPane ammoContainer3 = new StackPane();
        Image ammo3 = new Image(new FileInputStream("src/main/resources/images/ammotiles/" + ammoColor.get(2) + ".png"));
        ImageView ivAmmo3 = new ImageView();
        ivAmmo3.setImage(ammo3);
        setUpProperties(ammoContainer3, ivAmmo3);
        ammosContainer.add(ammoContainer3, 4,0,2,1);

        StackPane ammoContainer4 = new StackPane();
        Image ammo4 = new Image(new FileInputStream("src/main/resources/images/ammotiles/" + ammoColor.get(3) + ".png"));
        ImageView ivAmmo4 = new ImageView();
        ivAmmo4.setImage(ammo4);
        setUpProperties(ammoContainer4, ivAmmo4);
        ammosContainer.add(ammoContainer4, 6,0,2,1);

        StackPane ammoContainer5 = new StackPane();
        Image ammo5 = new Image(new FileInputStream("src/main/resources/images/ammotiles/" + ammoColor.get(4) + ".png"));
        ImageView ivAmmo5 = new ImageView();
        ivAmmo5.setImage(ammo5);
        setUpProperties(ammoContainer5, ivAmmo5);
        ammosContainer.add(ammoContainer5, 8,0,2,1);

        StackPane ammoContainer6 = new StackPane();
        Image ammo6 = new Image(new FileInputStream("src/main/resources/images/ammotiles/" + ammoColor.get(5) + ".png"));
        ImageView ivAmmo6 = new ImageView();
        ivAmmo6.setImage(ammo6);
        setUpProperties(ammoContainer6, ivAmmo6);
        ammosContainer.add(ammoContainer6, 10,0,2,1);

        StackPane ammoContainer7 = new StackPane();
        Image ammo7 = new Image(new FileInputStream("src/main/resources/images/ammotiles/" + ammoColor.get(6) + ".png"));
        ImageView ivAmmo7 = new ImageView();
        ivAmmo7.setImage(ammo7);
        setUpProperties(ammoContainer7, ivAmmo7);
        ammosContainer.add(ammoContainer7, 12,0,2,1);

        StackPane ammoContainer8 = new StackPane();
        Image ammo8 = new Image(new FileInputStream("src/main/resources/images/ammotiles/" + ammoColor.get(7) + ".png"));
        ImageView ivAmmo8 = new ImageView();
        ivAmmo8.setImage(ammo8);
        setUpProperties(ammoContainer8, ivAmmo8);
        ammosContainer.add(ammoContainer8, 14,0,2,1);

        StackPane ammoContainer9 = new StackPane();
        Image ammo9 = new Image(new FileInputStream("src/main/resources/images/ammotiles/" + ammoColor.get(8) + ".png"));
        ImageView ivAmmo9 = new ImageView();
        ivAmmo9.setImage(ammo9);
        setUpProperties(ammoContainer9, ivAmmo9);
        ammosContainer.add(ammoContainer9, 16,0,2,1);
        boardGrid.getChildren().removeAll(cardsContainer);
        cardsContainer = ammosContainer;
        boardGrid.add(cardsContainer, 1,4, 1, 1);
    }

    public void addPlayerBoards(ArrayList<String> playerColors) throws Exception{

        StackPane playerBoardContainer = new StackPane();
        Image myBoard = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(0) + ".png"));
        ImageView ivMyBoard = new ImageView();
        ivMyBoard.setImage(myBoard);
        setUpProperties(playerBoardContainer, ivMyBoard);
        boardGrid.add(playerBoardContainer, 0,4);

        StackPane board2Container = new StackPane();
        Image board2 = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(1) + ".png"));
        ImageView ivBoard2 = new ImageView();
        ivBoard2.setImage(board2);
        setUpProperties(board2Container, ivBoard2);
        boardGrid.add(board2Container, 0,0);

        StackPane board3Container = new StackPane();
        Image board3 = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(2) + ".png"));
        ImageView ivBoard3 = new ImageView();
        ivBoard3.setImage(board3);
        setUpProperties(board3Container, ivBoard3);
        boardGrid.add(board3Container, 0,1);

        if(playerColors.size()>=4) {
            StackPane board4Container = new StackPane();
            Image board4 = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(3) + ".png"));
            ImageView ivBoard4 = new ImageView();
            ivBoard4.setImage(board4);
            setUpProperties(board4Container, ivBoard4);
            boardGrid.add(board4Container, 0,2);
        }

        if(playerColors.size()==5) {
            StackPane board5Container = new StackPane();
            Image board5 = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(4) + ".png"));
            ImageView ivBoard5 = new ImageView();
            ivBoard5.setImage(board5);
            setUpProperties(board5Container, ivBoard5);
            boardGrid.add(board5Container, 0,3);
        }
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

        public void clear(GridPane gridPane){
        gridPane.getChildren().clear();
        gridPane.getRowConstraints().clear();
        gridPane.getColumnConstraints().clear();
        }

        public void setText(String string){
        textContainer.getChildren().clear();
        Text text = new Text(string);
        //text.setFont(Font.font("Futura", FontWeight.LIGHT, 20));
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-family: 'Andale Mono'");

        fontSize.bind(mainScene.widthProperty().add(mainScene.heightProperty()).divide(100));
        textContainer.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), ";"));
        text.wrappingWidthProperty().bind(mainScene.widthProperty().subtract(100));
        textContainer.getChildren().add(text);
        textContainer.setMargin(text, new Insets(50, 50, 50, 50));

    }

}