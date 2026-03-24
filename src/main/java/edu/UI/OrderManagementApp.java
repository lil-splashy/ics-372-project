package edu.UI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// Entry point kept for standalone testing. Main app launches via homepage.
public class OrderManagementApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        homepage.main(new String[]{});
    }

    public static void main(String[] args) {
        launch(args);
    }
}