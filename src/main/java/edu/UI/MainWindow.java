package edu.UI;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

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
        menu.setPadding(new Insets(20));


        root.setCenter(menu);

        // Status bar
        Label statusBar = new Label("Ready");
        statusBar.setStyle("-fx-padding: 4px 8px;");
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 400, 380);
        primaryStage.setTitle("Warehouse Order Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}