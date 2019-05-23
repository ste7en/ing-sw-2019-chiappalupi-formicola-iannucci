package it.polimi.ingsw;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class JavaFXApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Button test");
        CheckBox checkBoxRMI = new CheckBox("RMI");
        CheckBox checkBoxTCP = new CheckBox("TCP");
        Button button = new Button("Continue");
        Label label = new Label("Select a connection");
        button.setOnAction(e -> handleOptions(checkBoxRMI, checkBoxTCP));
        checkBoxRMI.setOnAction(e -> handleOptionsRMI(checkBoxRMI, checkBoxTCP));
        checkBoxTCP.setOnAction(e -> handleOptionsTCP(checkBoxRMI, checkBoxTCP));
        BorderPane root = new BorderPane();

        HBox boxRMI = new HBox(checkBoxRMI);
        boxRMI.setAlignment(Pos.CENTER);

        HBox boxTCP = new HBox(checkBoxTCP);
        boxTCP.setAlignment(Pos.CENTER);

        HBox boxButton = new HBox(button);
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

    public void handleOptions(CheckBox rmi, CheckBox tcp){
        if(rmi.isSelected()){
            System.out.println("Hai selezionato RMI!");
        }
        if(tcp.isSelected()){
            System.out.println("Hai selezionato TCP!");
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
}
