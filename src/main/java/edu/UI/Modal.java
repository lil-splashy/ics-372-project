package edu.UI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;



public class Modal extends Application{

    public void start(Stage primaryStage) {
        openDialogButton.setOnAction(e -> showModalDialog(primaryStage));

        VBox layout = new VBox(10);
        layout.getChildren().add(openDialogButton);
        Scene scene = new Scene(layout, 300, 200);

        primaryStage.setTitle("Confirmation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showModalDialog(Stage owner) {
        Dialog<String> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);

        dialog.getDialogPane().setContent(new Label("Are you sure you want to do this?"));
        dialog.getDialogPane().getButtonTypes().add(ButtonType.NO);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.YES);

        dialog.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
