package edu.UI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ConfirmationDialog extends Application {
    @Override
    public void start(Stage primaryStage) {
        // action that triggers the confirmation dialog
        primaryStage.setTitle("Confirmation Dialog");
        primaryStage.setScene(new Scene(new StackPane(), 300, 200));
        primaryStage.show();
        showConfirmationDialog();
    }

    private void showConfirmationDialog() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to proceed?");
        alert.setContentText("Choose your option.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("User clicked OK");
            } else {
                System.out.println("User clicked Cancel");
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}