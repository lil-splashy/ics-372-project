package edu.UI;

import edu.ics372.Item;
import edu.ics372.JsonParser;
import edu.ics372.Order;
import edu.ics372.OrderHandler;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OrderManagementView {

    private final StackPane root;
    private final HBox titleBar;
    private double dragOffsetX, dragOffsetY;

    private final OrderHandler handler;
    private final String warehouseId;
    private final String warehouseName;
    private final Stage stage;

    private final ObservableList<Order> orders;
    private final IntegerProperty selectedOrderIndex = new SimpleIntegerProperty(0);
    private final IntegerProperty selectedItemIndex = new SimpleIntegerProperty(0);

    private final Label currentItemIdLabel = new Label();
    private final Label currentItemNameLabel = new Label();
    private final Label currentItemLocationLabel = new Label();
    private final Label currentItemQtyLabel = new Label();

    private final ListView<Order> orderListView;

    public OrderManagementView(OrderHandler handler, String warehouseId, String warehouseName, Stage stage) {
        this.handler = handler;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.stage = stage;
        this.orders = loadWarehouseOrders();
        this.orderListView = new ListView<>(orders);

        root = new StackPane();
        root.setStyle("-fx-background-color: #1a0a22;");
        root.setPrefSize(1380, 880);

        Pane windowPane = new Pane();
        windowPane.setPrefSize(1380, 880);

        Rectangle windowBg = new Rectangle(1380, 880);
        windowBg.setFill(Color.web("#222121", 0.85));
        windowBg.setStroke(Color.WHITE);
        windowBg.setStrokeWidth(2);
        windowBg.setArcWidth(12);
        windowBg.setArcHeight(12);
        windowBg.setEffect(new DropShadow(20, Color.BLACK));
        windowPane.getChildren().add(windowBg);

        titleBar = buildTitleBar();
        windowPane.getChildren().add(titleBar);

        HBox companyBar = buildCompanyBar();
        companyBar.setLayoutX(15);
        companyBar.setLayoutY(35);
        windowPane.getChildren().add(companyBar);

        Button homeBtn = buildHomeButton();
        homeBtn.setLayoutX(1295);
        homeBtn.setLayoutY(38);
        windowPane.getChildren().add(homeBtn);

        VBox leftPanel = buildOrderListPanel();
        leftPanel.setLayoutX(15);
        leftPanel.setLayoutY(115);
        leftPanel.setPrefSize(680, 750);
        windowPane.getChildren().add(leftPanel);

        Pane currentItemPane = buildCurrentItemPane();
        currentItemPane.setLayoutX(710);
        currentItemPane.setLayoutY(115);
        windowPane.getChildren().add(currentItemPane);

        Pane buttonBox = buildButtonBox();
        buttonBox.setLayoutX(710);
        buttonBox.setLayoutY(555);
        windowPane.getChildren().add(buttonBox);

        root.getChildren().add(windowPane);

        setupListeners();
        updateCurrentItemDisplay();
    }

    // ─── Data ───────────────────────────────────
    private ObservableList<Order> loadWarehouseOrders() {
        ObservableList<Order> list = FXCollections.observableArrayList();
        for (Order o : handler.getIncomingOrders()) addIfWarehouse(o, list);
        for (Order o : handler.getStartedOrders())  addIfWarehouse(o, list);
        for (Order o : handler.getCompletedOrders()) addIfWarehouse(o, list);
        return list;
    }

    private void addIfWarehouse(Order o, ObservableList<Order> list) {
        if (o.getWarehouse() != null && o.getWarehouse().getWarehouseID().equals(warehouseId)) {
            list.add(o);
        }
    }

    private void refreshOrders() {
        orders.setAll(loadWarehouseOrders());
        selectedOrderIndex.set(0);
        selectedItemIndex.set(0);
        updateCurrentItemDisplay();
    }

    private List<Item> getItems(Order order) {
        List<Item> result = new ArrayList<>();
        if (order.getItems() != null) {
            for (Item item : order.getItems()) {
                if (item != null) result.add(item);
            }
        }
        return result;
    }

    // ─── Title Bar ──────────────────────────────
    private HBox buildTitleBar() {
        HBox bar = new HBox();
        bar.setPrefSize(1380, 30);
        bar.setAlignment(Pos.CENTER);
        bar.setStyle("-fx-background-color: linear-gradient(to bottom, #3a3a3a, #2a2a2a);"
                + "-fx-background-radius: 12 12 0 0;"
                + "-fx-padding: 0 10;");

        Label title = new Label("Order Management System");
        title.setFont(Font.font("IBM Plex Mono", 13));
        title.setTextFill(Color.web("#FFFFFF", 0.6));

        bar.getChildren().add(title);
        return bar;
    }

    // ─── Company Bar ────────────────────────────
    private HBox buildCompanyBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPrefHeight(65);
        bar.setPrefWidth(569);
        bar.setStyle("-fx-background-color: rgba(34,33,33,0.6);"
                + "-fx-border-color: white;"
                + "-fx-border-width: 1;"
                + "-fx-padding: 8 15;");

        SVGPath logoSvg = new SVGPath();
        logoSvg.setContent("M31.25 18.03L11.75 6.78M2.58 12.74L21.5 23.68L40.42 12.74M21.5 45.5V23.66");
        logoSvg.setStroke(Color.WHITE);
        logoSvg.setStrokeWidth(3);
        logoSvg.setFill(Color.TRANSPARENT);
        logoSvg.setScaleX(0.6);
        logoSvg.setScaleY(0.6);

        VBox textBlock = new VBox(2);
        Label companyName = new Label(warehouseName);
        companyName.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 20));
        companyName.setTextFill(Color.WHITE);

        Label locationLabel = new Label("ID: " + warehouseId);
        locationLabel.setFont(Font.font("IBM Plex Mono", 13));
        locationLabel.setTextFill(Color.web("#FFFFFF", 0.6));

        textBlock.getChildren().addAll(companyName, locationLabel);
        bar.getChildren().addAll(logoSvg, textBlock);
        return bar;
    }

    // ─── Home Button ────────────────────────────
    private Button buildHomeButton() {
        SVGPath homeSvg = new SVGPath();
        homeSvg.setContent("M11.75 35.08V18.42H21.75V35.08M1.75 13.42L16.75 1.75L31.75 13.42V31.75"
                + "C31.75 32.63 31.4 33.48 30.77 34.11C30.15 34.73 29.3 35.08 28.42 35.08H5.08"
                + "C4.2 35.08 3.35 34.73 2.73 34.11C2.1 33.48 1.75 32.63 1.75 31.75V13.42Z");
        homeSvg.setStroke(Color.WHITE);
        homeSvg.setStrokeWidth(2.5);
        homeSvg.setFill(Color.TRANSPARENT);
        homeSvg.setScaleX(0.7);
        homeSvg.setScaleY(0.7);

        WarehouseButton btn = WarehouseButton.icon(homeSvg);
        btn.setOnAction(e -> homepage.show(stage, handler));
        return btn;
    }

    // ─── Order List ─────────────────────────────
    private VBox buildOrderListPanel() {
        VBox panel = new VBox(0);
        orderListView.setPrefSize(680, 750);
        orderListView.setStyle("-fx-background-color: transparent;"
                + "-fx-control-inner-background: transparent;");
        orderListView.setCellFactory(lv -> new OrderListCell());
        orderListView.getSelectionModel().selectFirst();
        panel.getChildren().add(orderListView);
        return panel;
    }

    private class OrderListCell extends ListCell<Order> {
        @Override
        protected void updateItem(Order order, boolean empty) {
            super.updateItem(order, empty);
            if (empty || order == null) {
                setGraphic(null);
                setStyle("-fx-background-color: transparent;");
                return;
            }

            Pane cell = new Pane();
            cell.setPrefSize(660, 64);

            Rectangle bg = new Rectangle(660, 64);
            bg.setFill(Color.TRANSPARENT);
            bg.setStroke(Color.WHITE);
            bg.setStrokeWidth(2);
            bg.setEffect(new DropShadow(4, 0, 5, Color.web("#000000", 0.4)));

            SVGPath pkgIcon = new SVGPath();
            pkgIcon.setContent("M31.25 18.03L11.75 6.78M2.58 12.74L21.5 23.68L40.42 12.74M21.5 45.5V23.66");
            pkgIcon.setStrokeWidth(3);
            pkgIcon.setFill(Color.TRANSPARENT);
            pkgIcon.setScaleX(0.45);
            pkgIcon.setScaleY(0.45);
            pkgIcon.setLayoutX(-2);
            pkgIcon.setLayoutY(-2);

            if (isSelected()) {
                pkgIcon.setStroke(Color.web("#F35621"));
                bg.setFill(Color.web("#FFFFFF", 0.05));
            } else {
                pkgIcon.setStroke(Color.WHITE);
                cell.setOpacity(0.7);
            }

            Label orderLabel = new Label("Order: #" + order.getOrderID());
            orderLabel.setFont(Font.font("IBM Plex Mono", 20));
            orderLabel.setTextFill(Color.WHITE);
            orderLabel.setLayoutX(70);
            orderLabel.setLayoutY(17);

            int itemCount = (int) getItems(order).size();
            Label countLabel = new Label(itemCount + " item" + (itemCount != 1 ? "s" : ""));
            countLabel.setFont(Font.font("IBM Plex Mono", 14));
            countLabel.setTextFill(Color.web("#FFFFFF", 0.6));
            countLabel.setLayoutX(460);
            countLabel.setLayoutY(20);

            Label statusLabel = new Label(order.getOrderStatus());
            statusLabel.setFont(Font.font("IBM Plex Mono", 12));
            statusLabel.setTextFill(statusColor(order.getOrderStatus()));
            statusLabel.setLayoutX(460);
            statusLabel.setLayoutY(38);

            SVGPath trashSvg = new SVGPath();
            trashSvg.setContent("M1.5 6.6H4.05H24.45M4.05 6.6V24.45C4.05 25.13 4.32 25.78 4.8 26.26"
                    + "C5.28 26.73 5.92 27 6.6 27H19.35C20.03 27 20.68 26.73 21.15 26.26"
                    + "C21.63 25.78 21.9 25.13 21.9 24.45V6.6M7.88 6.6V4.05C7.88 3.37 8.14 2.73"
                    + " 8.62 2.25C9.1 1.77 9.75 1.5 10.43 1.5H15.53C16.2 1.5 16.85 1.77 17.33 2.25"
                    + "C17.81 2.73 18.08 3.37 18.08 4.05V6.6M10.43 12.98V20.63M15.53 12.98V20.63");
            trashSvg.setStroke(Color.WHITE);
            trashSvg.setStrokeWidth(2);
            trashSvg.setFill(Color.TRANSPARENT);
            trashSvg.setScaleX(0.8);
            trashSvg.setScaleY(0.8);

            WarehouseButton deleteBtn = WarehouseButton.transparent(trashSvg);
            deleteBtn.setLayoutX(620);
            deleteBtn.setLayoutY(16);
            deleteBtn.setOnAction(e -> {
                handler.cancelOrder(order.getOrderID());
                orders.remove(order);
                selectedOrderIndex.set(Math.max(0, Math.min(selectedOrderIndex.get(), orders.size() - 1)));
                selectedItemIndex.set(0);
                updateCurrentItemDisplay();
            });

            cell.getChildren().addAll(bg, pkgIcon, orderLabel, countLabel, statusLabel, deleteBtn);
            cell.setOnMouseEntered(e -> { if (!isSelected()) cell.setOpacity(0.9); });
            cell.setOnMouseExited(e -> { if (!isSelected()) cell.setOpacity(0.7); });

            setGraphic(cell);
            setStyle("-fx-background-color: transparent; -fx-padding: 4 0;");
        }
    }

    private Color statusColor(String status) {
        if (status == null) return Color.GRAY;
        switch (status.toLowerCase()) {
            case "incoming": return Color.web("#47CEFF");
            case "started":  return Color.web("#EEAE3F");
            case "completed": return Color.web("#28C840");
            case "canceled": return Color.web("#FF5F57");
            default: return Color.GRAY;
        }
    }

    // ─── Current Item Pane ──────────────────────
    private Pane buildCurrentItemPane() {
        Pane pane = new Pane();
        pane.setPrefSize(658, 420);

        Rectangle bg = new Rectangle(658, 420);
        bg.setFill(Color.web("#222121", 0.4));
        bg.setStroke(Color.WHITE);
        bg.setStrokeWidth(2);
        pane.getChildren().add(bg);

        Label header = new Label("Current Item:");
        header.setFont(Font.font("IBM Plex Mono", 36));
        header.setTextFill(Color.web("#E5F2E5"));
        header.setLayoutX(10);
        header.setLayoutY(5);
        pane.getChildren().add(header);

        currentItemIdLabel.setFont(Font.font("IBM Plex Mono", 24));
        currentItemIdLabel.setTextFill(Color.web("#E6E6E6"));
        currentItemIdLabel.setLayoutX(420);
        currentItemIdLabel.setLayoutY(18);
        pane.getChildren().add(currentItemIdLabel);

        Rectangle imgPlaceholder = new Rectangle(300, 260);
        imgPlaceholder.setFill(Color.web("#333333"));
        imgPlaceholder.setLayoutX(10);
        imgPlaceholder.setLayoutY(70);
        pane.getChildren().add(imgPlaceholder);

        currentItemNameLabel.setFont(Font.font("IBM Plex Mono", 16));
        currentItemNameLabel.setTextFill(Color.web("#E6E6E6"));
        currentItemNameLabel.setLayoutX(10);
        currentItemNameLabel.setLayoutY(345);
        pane.getChildren().add(currentItemNameLabel);

        currentItemLocationLabel.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 24));
        currentItemLocationLabel.setTextFill(Color.WHITE);
        currentItemLocationLabel.setLayoutX(340);
        currentItemLocationLabel.setLayoutY(100);
        pane.getChildren().add(currentItemLocationLabel);

        currentItemQtyLabel.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 24));
        currentItemQtyLabel.setTextFill(Color.WHITE);
        currentItemQtyLabel.setLayoutX(340);
        currentItemQtyLabel.setLayoutY(230);
        pane.getChildren().add(currentItemQtyLabel);

        Button prevBtn = WarehouseButton.nav("\u25C0");
        prevBtn.setLayoutX(500);
        prevBtn.setLayoutY(345);
        prevBtn.setOnAction(e -> {
            if (orders.isEmpty()) return;
            List<Item> items = getItems(orders.get(selectedOrderIndex.get()));
            int idx = selectedItemIndex.get();
            selectedItemIndex.set(idx > 0 ? idx - 1 : items.size() - 1);
            updateCurrentItemDisplay();
        });
        pane.getChildren().add(prevBtn);

        Button nextBtn = WarehouseButton.nav("\u25B6");
        nextBtn.setLayoutX(580);
        nextBtn.setLayoutY(345);
        nextBtn.setOnAction(e -> {
            if (orders.isEmpty()) return;
            List<Item> items = getItems(orders.get(selectedOrderIndex.get()));
            int idx = selectedItemIndex.get();
            selectedItemIndex.set(idx < items.size() - 1 ? idx + 1 : 0);
            updateCurrentItemDisplay();
        });
        pane.getChildren().add(nextBtn);

        return pane;
    }

    // ─── Button Box ─────────────────────────────
    private Pane buildButtonBox() {
        Pane pane = new Pane();
        pane.setPrefSize(658, 310);

        Rectangle bg = new Rectangle(658, 310);
        bg.setFill(Color.web("#222121", 0.4));
        bg.setStroke(Color.WHITE);
        bg.setStrokeWidth(2);
        pane.getChildren().add(bg);

        Button completeBtn = WarehouseButton.action("Complete Order", 615, 60, false);
        completeBtn.setLayoutX(21);
        completeBtn.setLayoutY(20);
        completeBtn.setOnAction(e -> {
            if (!orders.isEmpty()) {
                handler.completeOrder(orders.get(selectedOrderIndex.get()).getOrderID());
                refreshOrders();
            }
        });
        pane.getChildren().add(completeBtn);

        Button modifyBtn = WarehouseButton.action("Modify Order", 290, 54, false);
        modifyBtn.setLayoutX(21);
        modifyBtn.setLayoutY(110);
        pane.getChildren().add(modifyBtn);

        Button importBtn = WarehouseButton.action("Import Orders", 290, 54, false);
        importBtn.setLayoutX(347);
        importBtn.setLayoutY(110);
        importBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Import Orders");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                JsonParser parser = new JsonParser();
                parser.setNewPath(file.getAbsolutePath());
                handler.loadOrders(parser);
                refreshOrders();
            }
        });
        pane.getChildren().add(importBtn);

        Button printBtn = WarehouseButton.action("Print Label", 290, 54, false);
        printBtn.setLayoutX(21);
        printBtn.setLayoutY(190);
        pane.getChildren().add(printBtn);

        Button exportBtn = WarehouseButton.action("Export Orders", 290, 54, true);
        exportBtn.setLayoutX(347);
        exportBtn.setLayoutY(190);
        exportBtn.setOnAction(e -> handler.saveData(homepage.SAVE_FILE));
        pane.getChildren().add(exportBtn);

        return pane;
    }

    // ─── Listeners & State ──────────────────────
    private void setupListeners() {
        orderListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() >= 0) {
                selectedOrderIndex.set(newVal.intValue());
                selectedItemIndex.set(0);
                updateCurrentItemDisplay();
            }
        });
    }

    private void updateCurrentItemDisplay() {
        if (orders.isEmpty()) {
            currentItemIdLabel.setText("");
            currentItemNameLabel.setText("No orders available");
            currentItemLocationLabel.setText("");
            currentItemQtyLabel.setText("");
            return;
        }

        Order order = orders.get(selectedOrderIndex.get());
        List<Item> items = getItems(order);

        if (items.isEmpty()) {
            currentItemIdLabel.setText("");
            currentItemNameLabel.setText("No items in this order");
            currentItemLocationLabel.setText("");
            currentItemQtyLabel.setText("");
            return;
        }

        Item item = items.get(selectedItemIndex.get());
        currentItemIdLabel.setText("#" + item.getItemID());
        currentItemNameLabel.setText(item.getItemName());
        currentItemLocationLabel.setText("Location: " + (item.getWarehouseLocation() != null ? item.getWarehouseLocation() : "N/A"));
        currentItemQtyLabel.setText("Qty: " + item.getItemQuantity());
    }

    // ─── Getters ────────────────────────────────
    public StackPane getRoot() { return root; }
    public HBox getTitleBar() { return titleBar; }
    public double getDragOffsetX() { return dragOffsetX; }
    public double getDragOffsetY() { return dragOffsetY; }
    public void setDragOffsetX(double x) { this.dragOffsetX = x; }
    public void setDragOffsetY(double y) { this.dragOffsetY = y; }
}