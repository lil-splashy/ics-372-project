package edu.UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import edu.ics372.Order;
import edu.ics372.OrderHandler;

import java.util.ArrayList;
import java.util.List;

public class MainWindow {

    public static void show(Stage stage, OrderHandler handler,
                            String warehouseId, String warehouseName) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: rgba(0,0,0,0.7);");

        // Top bar
        Button backBtn = new Button("← Warehouses");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; " +
                "-fx-font-family: 'Monospaced'; -fx-font-size: 13px; " +
                "-fx-border-color: white; -fx-border-width: 1; -fx-cursor: hand;");
        backBtn.setOnAction(e -> homepage.show(stage, handler));

        Label header = new Label(warehouseName);
        header.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 20px; " +
                "-fx-font-weight: bold; -fx-text-fill: white;");

        HBox topBar = new HBox(20, backBtn, header);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(16));
        root.setTop(topBar);

        // Filter orders for this warehouse
        List<Order> warehouseOrders = new ArrayList<>();
        for (Order o : handler.getIncomingOrders()) {
            if (o.getWarehouse() != null && o.getWarehouse().getWarehouseID().equals(warehouseId)) {
                warehouseOrders.add(o);
            }
        }
        for (Order o : handler.getStartedOrders()) {
            if (o.getWarehouse() != null && o.getWarehouse().getWarehouseID().equals(warehouseId)) {
                warehouseOrders.add(o);
            }
        }
        for (Order o : handler.getCompletedOrders()) {
            if (o.getWarehouse() != null && o.getWarehouse().getWarehouseID().equals(warehouseId)) {
                warehouseOrders.add(o);
            }
        }

        // Rolodex on the left
        Rolodex rolodex = new Rolodex(warehouseOrders, warehouseName);
        root.setLeft(rolodex.getView());

        // Center content area
        VBox center = new VBox();
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(10));
        root.setCenter(center);

        // Status bar
        String countText = warehouseOrders.size() == 1 ? "1 order" : warehouseOrders.size() + " orders";
        Label statusBar = new Label(countText);
        statusBar.setStyle("-fx-padding: 4px 8px; -fx-text-fill: #aaaaaa; -fx-font-family: 'Monospaced';");
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 1100, 850);
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Warehouse Order Manager - " + warehouseName);
        stage.setScene(scene);
    }
}