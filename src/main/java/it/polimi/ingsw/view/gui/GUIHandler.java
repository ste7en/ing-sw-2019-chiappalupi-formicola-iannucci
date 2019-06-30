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

    public static void main(String[] args) {
        AdrenalineLogger.setLogName("GUI");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

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
        /*
        try {
            chooseGameMap();
        }catch (FileNotFoundException e){
            System.err.println("File exception:" + e.toString());
        }
        */
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
