package edu.UI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import edu.ics372.Parser;
import edu.ics372.Order;
import edu.ics372.OrderHandler;
import edu.ics372.Warehouse;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Homepage extends Application {

    static final String SAVE_FILE = "src/main/orders/warehouse_orders.json";
    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.UNIFIED);
        OrderHandler handler = new OrderHandler();
        if (new File(SAVE_FILE).exists()) {
            handler.importProgramOrders(SAVE_FILE);
        }
        stage.setOnCloseRequest(e -> handler.saveData(SAVE_FILE));
        show(stage, handler);
        stage.show();
    }

    public static void show(Stage stage, OrderHandler handler) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: rgba(26,26,26,0.72);"
                + "-fx-border-color: rgba(255,255,255,0.18);"
                + "-fx-border-width: 1;");

        // ─── Content header ───
        Label title = new Label("Warehouse Order Manager");
        title.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label subtitle = new Label("Select a Warehouse");
        subtitle.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 16px; -fx-text-fill: rgba(255,255,255,0.55);");
        VBox titleBox = new VBox(8, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(30, 0, 20, 0));
        titleBox.setStyle("-fx-background-color: rgba(34,33,33,0.4);"
                + "-fx-border-color: rgba(255,255,255,0.1);"
                + "-fx-border-width: 0 0 1 0;");

        // Allow dragging the window by the title bar
        final double[] dragDelta = new double[2];
        titleBox.setOnMousePressed(e -> { dragDelta[0] = stage.getX() - e.getScreenX(); dragDelta[1] = stage.getY() - e.getScreenY(); });
        titleBox.setOnMouseDragged(e -> { stage.setX(e.getScreenX() + dragDelta[0]); stage.setY(e.getScreenY() + dragDelta[1]); });

        root.setTop(titleBox);

        // Warehouse cards
        FlowPane cardGrid = new FlowPane();
        cardGrid.setHgap(20);
        cardGrid.setVgap(20);
        cardGrid.setPadding(new Insets(20));
        cardGrid.setAlignment(Pos.CENTER);

        Map<String, Warehouse> warehouses = new LinkedHashMap<>();
        Map<String, Integer> orderCounts = new LinkedHashMap<>();

        List<Order> allOrders = new ArrayList<>();
        allOrders.addAll(handler.getIncomingOrders());
        allOrders.addAll(handler.getStartedOrders());
        allOrders.addAll(handler.getCompletedOrders());

        for (Order order : allOrders) {
            Warehouse w = order.getWarehouse();
            if (w != null) {
                warehouses.put(w.getWarehouseID(), w);
                orderCounts.merge(w.getWarehouseID(), 1, Integer::sum);
            }
        }

        if (warehouses.isEmpty()) {
            Label empty = new Label("No warehouses found.\nImport orders to get started.");
            empty.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 16px; " +
                    "-fx-text-fill: #666666; -fx-text-alignment: center;");
            empty.setAlignment(Pos.CENTER);
            cardGrid.getChildren().add(empty);
        } else {
            for (Map.Entry<String, Warehouse> entry : warehouses.entrySet()) {
                Warehouse w = entry.getValue();
                int count = orderCounts.getOrDefault(w.getWarehouseID(), 0);
                cardGrid.getChildren().add(createWarehouseCard(w, count, stage, handler));
            }
        }

        ScrollPane scrollPane = new ScrollPane(cardGrid);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        // Import button
        WarehouseButton importBtn = WarehouseButton.primary("Import Orders");
        importBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Import Orders");
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Order Files", "*.json", "*.xml"),
                    new FileChooser.ExtensionFilter("JSON Files", "*.json"),
                    new FileChooser.ExtensionFilter("XML Files", "*.xml"));
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                Parser p = new Parser();
                List<Order> orders = p.parseFile(file.getAbsolutePath());
                handler.loadOrders(orders);  // Pass the list of orders
                show(stage, handler);
            }
        });

        HBox bottomBar = new HBox(importBtn);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(24));
        bottomBar.setStyle("-fx-background-color: rgba(34,33,33,0.4);"
                + "-fx-border-color: rgba(255,255,255,0.1);"
                + "-fx-border-width: 1 0 0 0;");
        root.setBottom(bottomBar);

        Scene scene = new Scene(root, 1100, 850);
        scene.setFill(Color.TRANSPARENT);

        scene.getStylesheets().add(
                Homepage.class.getResource("resources/styles/orders.css").toExternalForm());

        stage.setTitle("Warehouse Order Manager");
        stage.setScene(scene);
    }

    private static StackPane createWarehouseCard(Warehouse warehouse, int orderCount,
                                                  Stage stage, OrderHandler handler) {
        StackPane card = new StackPane();
        card.setPrefSize(200, 140);
        card.setStyle("-fx-background-color: rgba(42,42,42,0.7); -fx-border-color: rgba(255,255,255,0.6); " +
                "-fx-border-width: 2; -fx-cursor: hand;");

        Label nameLabel = new Label(warehouse.getWarehouseName());
        nameLabel.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 16px; " +
                "-fx-font-weight: bold; -fx-text-fill: white;");
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);

        String countText = orderCount == 1 ? "1 order" : orderCount + " orders";
        Label countLabel = new Label(countText);
        countLabel.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 13px; -fx-text-fill: #aaaaaa;");

        Label idLabel = new Label("ID: " + warehouse.getWarehouseID());
        idLabel.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 11px; -fx-text-fill: #666666;");

        VBox content = new VBox(8, nameLabel, countLabel, idLabel);
        content.setAlignment(Pos.CENTER);
        card.getChildren().add(content);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: rgba(58,58,58,0.75); -fx-border-color: #47CEFF; " +
                "-fx-border-width: 2; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: rgba(42,42,42,0.7); -fx-border-color: rgba(255,255,255,0.6); " +
                "-fx-border-width: 2; -fx-cursor: hand;"));
        card.setOnMouseClicked(e ->
                MainWindow.show(stage, handler, warehouse.getWarehouseID(), warehouse.getWarehouseName()));

        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
}