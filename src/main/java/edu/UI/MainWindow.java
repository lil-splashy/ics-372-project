package edu.UI;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.paint.*;

public class MainWindow extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Header
        Label header = new Label("Warehouse Order Manager");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        BorderPane.setAlignment(header, Pos.CENTER);
        BorderPane.setMargin(header, new Insets(16));
        root.setTop(header);

        // Main menu buttons
        VBox menu = new VBox(10);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(10));


        // Left-Pane List
        Rolodex rolodex = new Rolodex();
        root.setLeft(rolodex.getView());

        root.setCenter(menu);

        // Status bar
        Label statusBar = new Label("Ready");
        statusBar.setStyle("-fx-padding: 4px 8px;");
        root.setBottom(statusBar);



        // Window actual
        Scene scene = new Scene(root, 1100, 850);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.UNIFIED);

        primaryStage.setTitle("Warehouse Order Manager");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
    }

    public static void main(String[] args) {
        launch(args);
    }
}