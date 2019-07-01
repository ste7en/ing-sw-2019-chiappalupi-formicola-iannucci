package it.polimi.ingsw.view.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.util.ArrayList;

public class StackPaneDemo extends Application {

    private StackPane stackPane;
    private GridPane root;

    @Override
    public void start(Stage primaryStage) throws Exception {


        //Grid creation
        root = new GridPane();
        ColumnConstraints column0 = new ColumnConstraints();
        column0.setPercentWidth(15);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(70);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(15);
        root.getColumnConstraints().addAll(column0, column1, column2); // each get 50% of width
        RowConstraints row0 = new RowConstraints();
        row0.setPercentHeight(15);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(15);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(55);
        RowConstraints row3 = new RowConstraints();
        row3.setPercentHeight(15);
        root.getRowConstraints().addAll(row0, row1, row2, row3);
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
        root.add(mapContainer, 1,2);

        //Adding the grid
        GridPane mapGrid = new GridPane();
        mapGrid.setPrefHeight(446.0);
        mapGrid.setPrefWidth(610.0);
        ColumnConstraints c0 = new ColumnConstraints();
        c0.setPercentWidth(17);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(17);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(17);
        ColumnConstraints c3 = new ColumnConstraints();
        c3.setPercentWidth(16);
        ColumnConstraints c4 = new ColumnConstraints();
        c4.setPercentWidth(16);
        ColumnConstraints c5 = new ColumnConstraints();
        c5.setPercentWidth(17);
        mapGrid.getColumnConstraints().addAll(c0, c1, c2, c3, c4, c5); // each get 50% of width
        RowConstraints r0 = new RowConstraints();
        r0.setPercentHeight(23);
        RowConstraints r1 = new RowConstraints();
        r1.setPercentHeight(20);
        RowConstraints r2 = new RowConstraints();
        r2.setPercentHeight(24);
        RowConstraints r3 = new RowConstraints();
        r3.setPercentHeight(22);
        RowConstraints r4 = new RowConstraints();
        r4.setPercentHeight(11);
        mapGrid.getRowConstraints().addAll(r0, r1, r2, r3, r4);
        mapGrid.setGridLinesVisible(true);
        mapContainer.getChildren().add(mapGrid);
        mapGrid.setAlignment(Pos.CENTER);
        mapGrid.setGridLinesVisible(true);
       // mapGrid.setStyle("-fx-background-color: cornsilk;");

        //Adding buttons
        Button r0c0 = new Button("ciao danni");
        r0c0.setStyle("-fx-background-color: transparent;");
        mapContainer.getChildren().add(r0c0);
        r0c0.setOnAction(e -> {checkHeight(ivMap, map, mapContainer, mapGrid);});


        //Setting background
        root.setStyle("-fx-background-color: black");

        ArrayList<String> prova = new ArrayList<>();
        prova.add("blue");
        prova.add("yellow");
        prova.add("green");
        prova.add("purple");
        prova.add("grey");
        try {
            addPlayerBoards(prova);
        } catch (Exception e){
            e.printStackTrace();
        }

        Scene scene = new Scene(root, 872, 862);
        primaryStage.setTitle("StackPane Layout Demo");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void checkHeight(ImageView ivMap, Image map, StackPane mapContainer, GridPane mapGrid){
        System.out.println(ivMap.getFitWidth());
        System.out.println(ivMap.getFitHeight());
        System.out.println(map.getWidth());
        System.out.println(map.getHeight());
        double aspectRatio = map.getWidth() / map.getHeight();
        double realWidth = Math.min(ivMap.getFitWidth(), ivMap.getFitHeight() * aspectRatio);
        double realHeight = Math.min(ivMap.getFitHeight(), ivMap.getFitWidth() / aspectRatio);
        System.out.println(realHeight);
        System.out.println(realWidth);
        System.out.println(ivMap.getFitWidth());
        System.out.println(ivMap.getFitHeight());
        System.out.println(map.getWidth());
        System.out.println(map.getHeight());
        /*mapGrid.setMaxHeight(realHeight);
        mapGrid.setMaxWidth(realWidth);
        mapContainer.getChildren().add(mapGrid);*/
    }

    public void addPlayerBoards(ArrayList<String> playerColors) throws Exception{

        StackPane playerBoardContainer = new StackPane();
        Image myBoard = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(0) + ".png"));
        ImageView ivMyBoard = new ImageView();
        ivMyBoard.setImage(myBoard);
        ivMyBoard.setPreserveRatio(true);
        playerBoardContainer.getChildren().add(ivMyBoard);
        playerBoardContainer.setMinHeight(0.0);
        playerBoardContainer.setMinWidth(0.0);
        playerBoardContainer.setAlignment(Pos.CENTER);
        ivMyBoard.fitHeightProperty().bind(playerBoardContainer.heightProperty());
        ivMyBoard.fitWidthProperty().bind(playerBoardContainer.widthProperty());
        root.add(playerBoardContainer, 1,3);

        StackPane board2Container = new StackPane();
        Image board2 = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(1) + ".png"));
        ImageView ivBoard2 = new ImageView();
        ivBoard2.setImage(board2);
        ivBoard2.setPreserveRatio(true);
        board2Container.getChildren().add(ivBoard2);
        board2Container.setMinHeight(0.0);
        board2Container.setMinWidth(0.0);
        board2Container.setAlignment(Pos.CENTER);
        ivBoard2.fitHeightProperty().bind(board2Container.heightProperty());
        ivBoard2.fitWidthProperty().bind(board2Container.widthProperty());
        root.add(board2Container, 1,1);

        StackPane board3Container = new StackPane();
        Image board3 = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(2) + ".png"));
        ImageView ivBoard3 = new ImageView();
        ivBoard3.setImage(board3);
        ivBoard3.setPreserveRatio(true);
        board3Container.getChildren().add(ivBoard3);
        board3Container.setMinHeight(0.0);
        board3Container.setMinWidth(0.0);
        board3Container.setAlignment(Pos.CENTER);
        ivBoard3.fitHeightProperty().bind(board3Container.heightProperty());
        ivBoard3.fitWidthProperty().bind(board3Container.widthProperty());
        root.add(board3Container, 1,0);

        if(playerColors.size()>=4) {
            StackPane board4Container = new StackPane();
            Image board4 = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(3) + ".png"));
            ImageView ivBoard4 = new ImageView();
            ivBoard4.setImage(board4);
            ivBoard4.setPreserveRatio(true);
            ivBoard4.setRotate(270);
            board4Container.getChildren().add(ivBoard4);
            board4Container.setMinHeight(0.0);
            board4Container.setMinWidth(0.0);
            board4Container.setAlignment(Pos.CENTER);
            ivBoard4.fitHeightProperty().bind(board4Container.widthProperty());
            ivBoard4.fitWidthProperty().bind(board4Container.heightProperty());
            root.add(board4Container, 2,1,1,2);
        }

        if(playerColors.size()==5) {
            StackPane board5Container = new StackPane();
            Image board5 = new Image(new FileInputStream("src/main/resources/images/playerboards/playerboard_" + playerColors.get(4) + ".png"));
            ImageView ivBoard5 = new ImageView();
            ivBoard5.setImage(board5);
            ivBoard5.setPreserveRatio(true);
            ivBoard5.setRotate(90);
            board5Container.getChildren().add(ivBoard5);
            board5Container.setMinHeight(0.0);
            board5Container.setMinWidth(0.0);
            board5Container.setAlignment(Pos.CENTER);
            ivBoard5.fitHeightProperty().bind(board5Container.widthProperty());
            ivBoard5.fitWidthProperty().bind(board5Container.heightProperty());
            root.add(board5Container, 0,1,1,2);
        }
        }

        /*
        public void addPlayerBoard(int playerNumber, StackPane playerBoard, ImageView playerBoardImg){
            playerBoardImg.setPreserveRatio(true);
            playerBoard.getChildren().add(playerBoardImg);
            playerBoard.setMinHeight(0.0);
            playerBoard.setMinWidth(0.0);
            playerBoard.setAlignment(Pos.CENTER);

            switch (playerNumber){
                case 1:
                    playerBoardImg.fitHeightProperty().bind(playerBoard.heightProperty());
                    playerBoardImg.fitWidthProperty().bind(playerBoard.widthProperty());
                    root.add(playerBoard, 1,3);
                case 2:
                    playerBoardImg.setRotate(270);
                    playerBoardImg.fitHeightProperty().bind(playerBoard.widthProperty());
                    playerBoardImg.fitWidthProperty().bind(playerBoard.heightProperty());
                    root.add(playerBoard, 2,1, 1 ,2);
                case 3:
                    playerBoardImg.fitHeightProperty().bind(playerBoard.heightProperty());
                    playerBoardImg.fitWidthProperty().bind(playerBoard.widthProperty());
                    root.add(playerBoard, 1,0);
                case 4:
                    playerBoardImg.fitHeightProperty().bind(playerBoard.heightProperty());
                    playerBoardImg.fitWidthProperty().bind(playerBoard.widthProperty());
                    root.add(playerBoard, 1,1);
                case 5:
                    playerBoardImg.setRotate(90);
                    playerBoardImg.fitHeightProperty().bind(playerBoard.widthProperty());
                    playerBoardImg.fitWidthProperty().bind(playerBoard.heightProperty());
                    root.add(playerBoard, 0,1, 1, 2);
            }
        }
        */
}