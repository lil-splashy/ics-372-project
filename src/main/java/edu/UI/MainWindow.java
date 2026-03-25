package edu.UI;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import edu.ics372.OrderHandler;

public class MainWindow {

    public static void show(Stage stage, OrderHandler handler, String warehouseId, String warehouseName) {

        OrderManagementView view = new OrderManagementView(handler, warehouseId, warehouseName, stage);

        Scene scene = new Scene(view.getRoot(), 1380, 880);
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        scene.setFill(isMac ? Color.TRANSPARENT : Color.web("#1a1a1a"));

        String stylesheetPath = MainWindow.class.getResource("resources/styles/orders.css").toExternalForm();
        scene.getStylesheets().add(stylesheetPath);

        // Wire title bar drag
        stage.setTitle("Order Management System - " + warehouseName);
        stage.setScene(scene);
    }
}