package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.utility.AdrenalineLogger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.util.ArrayList;

@SuppressWarnings("Duplicates")
public class AlternativeBoard extends Application {

    private StackPane stackPane;
    private GridPane root;
    private GridPane cardsContainer;
    private ColumnConstraints c;
    private Stage primaryStage;
    private Button button;
    private AdrenalineGUI adrenalineGUI;
    private HBox boxButton;
    private BorderPane firstRoot;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        //this.adrenalineGUI = new AdrenalineGUI(this);
        primaryStage.setTitle("Adrenaline");

        button = new Button("Continue");
        button.setOnAction(e -> adrenalineGUI.willChooseConnection());
        boxButton = new HBox(button);
        boxButton.setAlignment(Pos.CENTER);
        boxButton.setMargin(button, new Insets(0, 0, 50, 0));

        firstRoot = new BorderPane();
        BackgroundImage myBI= new BackgroundImage(new Image(new FileInputStream("src/main/resources/images/adrenaline.jpg")),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
        firstRoot.setBackground(new Background(myBI));
        firstRoot.setBottom(boxButton);

        primaryStage.setScene(new Scene(firstRoot, 1200, 800));
        primaryStage.show();

    }

    public static void main(String[] args) {
        AdrenalineLogger.setLogName("GUI");
        launch(args);
    }

    public void createMap() throws Exception{


        //Main grid creation
        root = new GridPane();
        ColumnConstraints column0 = new ColumnConstraints();
        column0.setPercentWidth(40);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(60);
        root.getColumnConstraints().addAll(column0, column1); // each get 50% of width
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
        root.getRowConstraints().addAll(row0, row1, row2, row3, row4);
        root.setGridLinesVisible(true);

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
        root.add(mapContainer, 1,0, 1, 4);

        //Map's grid creation
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

        //Adding buttons to the map's grid
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
        root.setBackground(new Background(myBI));
        //root.setStyle("-fx-background-color: black");

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
        Scene scene = new Scene(root, 1200, 675);
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
        Image myBoard = new Image(new FileInputStream("src/main/resources/images/powerups/Newton - blue.png"));
        ImageView ivMyBoard = new ImageView();
        ivMyBoard.setImage(myBoard);
        setUpProperties(playerBoardContainer, ivMyBoard);
        weaponsContainer.add(playerBoardContainer, 9,1,3,1);

        StackPane playerBoardContainer2 = new StackPane();
        Image myBoard2 = new Image(new FileInputStream("src/main/resources/images/powerups/TagbackGrenade - blue.png"));
        ImageView ivMyBoard2 = new ImageView();
        ivMyBoard2.setImage(myBoard2);
        setUpProperties(playerBoardContainer2, ivMyBoard2);
        weaponsContainer.add(playerBoardContainer2, 12,1,3,1);

        StackPane playerBoardContainer3 = new StackPane();
        Image myBoard3 = new Image(new FileInputStream("src/main/resources/images/powerups/Teleporter - red.png"));
        ImageView ivMyBoard3 = new ImageView();
        ivMyBoard3.setImage(myBoard3);
        setUpProperties(playerBoardContainer3, ivMyBoard3);
        weaponsContainer.add(playerBoardContainer3, 15,1,3,1);
        //weaponsContainer.setStyle("-fx-background-color: cornsilk;");
        weaponsContainer.setGridLinesVisible(true);
        root.getChildren().remove(cardsContainer);
        cardsContainer = weaponsContainer;
        root.add(cardsContainer, 1,4, 1, 1);
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
        root.getChildren().removeAll(cardsContainer);
        cardsContainer = ammosContainer;
        root.add(cardsContainer, 1,4, 1, 1);
    }

    public void addPlayerBoards(ArrayList<String> playerColors) throws Exception{

        StackPane playerBoardContainer = new StackPane();
        Image myBoard = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(0) + ".png"));
        ImageView ivMyBoard = new ImageView();
        ivMyBoard.setImage(myBoard);
        setUpProperties(playerBoardContainer, ivMyBoard);
        root.add(playerBoardContainer, 0,4);

        StackPane board2Container = new StackPane();
        Image board2 = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(1) + ".png"));
        ImageView ivBoard2 = new ImageView();
        ivBoard2.setImage(board2);
        setUpProperties(board2Container, ivBoard2);
        root.add(board2Container, 0,0);

        StackPane board3Container = new StackPane();
        Image board3 = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(2) + ".png"));
        ImageView ivBoard3 = new ImageView();
        ivBoard3.setImage(board3);
        setUpProperties(board3Container, ivBoard3);
        root.add(board3Container, 0,1);

        if(playerColors.size()>=4) {
            StackPane board4Container = new StackPane();
            Image board4 = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(3) + ".png"));
            ImageView ivBoard4 = new ImageView();
            ivBoard4.setImage(board4);
            setUpProperties(board4Container, ivBoard4);
            root.add(board4Container, 0,2);
        }

        if(playerColors.size()==5) {
            StackPane board5Container = new StackPane();
            Image board5 = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(4) + ".png"));
            ImageView ivBoard5 = new ImageView();
            ivBoard5.setImage(board5);
            setUpProperties(board5Container, ivBoard5);
            root.add(board5Container, 0,3);
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
}