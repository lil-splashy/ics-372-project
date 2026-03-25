package edu.UI;

import edu.ics372.Item;
import edu.ics372.Parser;
import edu.ics372.Order;
import edu.ics372.OrderHandler;
import edu.ics372.OrderLock;
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

    private static final String GLASS_BG   = "-fx-background-color: rgba(0,0,0,0.72);";
    private static final String PANEL_BG   = "-fx-background-color: rgba(34,33,33,0.55);"
                                           + "-fx-border-color: rgba(255,255,255,0.2);"
                                           + "-fx-border-width: 1;";

    private final StackPane root;
    private final HBox titleBar;

    private final OrderHandler handler;
    private final String warehouseId;
    private final String warehouseName;
    private final Stage stage;

    private final ObservableList<Order> orders;
    private final IntegerProperty selectedOrderIndex = new SimpleIntegerProperty(0);
    private final IntegerProperty selectedItemIndex  = new SimpleIntegerProperty(0);

    private final Label currentItemIdLabel       = new Label();
    private final Label currentItemNameLabel     = new Label();
    private final Label currentItemLocationLabel = new Label();
    private final Label currentItemQtyLabel      = new Label();

    private final ListView<Order> orderListView;
    private final VBox buttonBoxWrapper = new VBox();

    public OrderManagementView(OrderHandler handler, String warehouseId,
                               String warehouseName, Stage stage) {
        this.handler       = handler;
        this.warehouseId   = warehouseId;
        this.warehouseName = warehouseName;
        this.stage         = stage;
        this.orders        = loadWarehouseOrders();
        this.orderListView = new ListView<>(orders);

        root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");

        BorderPane layout = new BorderPane();
        layout.setStyle(GLASS_BG
                + "-fx-border-color: rgba(255,255,255,0.22);"
                + "-fx-border-width: 1;"
                + "-fx-background-radius: 12;"
                + "-fx-border-radius: 12;");
        layout.setEffect(new DropShadow(24, Color.BLACK));

        titleBar = null;
        layout.setTop(buildHeaderBar());
        layout.setCenter(buildCenter());

        layout.prefWidthProperty().bind(root.widthProperty());
        layout.prefHeightProperty().bind(root.heightProperty());
        root.getChildren().add(layout);

        setupListeners();
        updateCurrentItemDisplay();
        rebuildButtonBox();
    }

    // ─── Data ───────────────────────────────────
    private ObservableList<Order> loadWarehouseOrders() {
        ObservableList<Order> list = FXCollections.observableArrayList();
        for (Order o : handler.getIncomingOrders())  addIfWarehouse(o, list);
        for (Order o : handler.getStartedOrders())   addIfWarehouse(o, list);
        for (Order o : handler.getCompletedOrders()) addIfWarehouse(o, list);
        return list;
    }

    private void addIfWarehouse(Order o, ObservableList<Order> list) {
        if (o.getWarehouse() != null
                && o.getWarehouse().getWarehouseID().equals(warehouseId)) {
            list.add(o);
        }
    }

    private void refreshOrders() {
        orders.setAll(loadWarehouseOrders());
        selectedOrderIndex.set(0);
        selectedItemIndex.set(0);
        updateCurrentItemDisplay();
        rebuildButtonBox();
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

    // ─── Header ─────────────────────────────
    private HBox buildHeaderBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(8, 15, 8, 15));
        bar.setStyle("-fx-background-color: rgba(34,33,33,0.5);"
                + "-fx-border-color: rgba(255,255,255,0.12);"
                + "-fx-border-width: 0 0 1 0;");

        SVGPath logo = new SVGPath();
        logo.setContent("M31.25 18.03L11.75 6.78M2.58 12.74L21.5 23.68L40.42 12.74M21.5 45.5V23.66");
        logo.setStroke(Color.WHITE);
        logo.setStrokeWidth(3);
        logo.setFill(Color.TRANSPARENT);
        logo.setScaleX(0.6);
        logo.setScaleY(0.6);

        VBox textBlock = new VBox(2);
        Label name = new Label(warehouseName);
        name.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 20));
        name.setTextFill(Color.WHITE);
        Label idLbl = new Label("ID: " + warehouseId);
        idLbl.setFont(Font.font("IBM Plex Mono", 13));
        idLbl.setTextFill(Color.web("#FFFFFF", 0.6));
        textBlock.getChildren().addAll(name, idLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        SVGPath homeSvg = new SVGPath();
        homeSvg.setContent("M11.75 35.08V18.42H21.75V35.08M1.75 13.42L16.75 1.75L31.75 13.42V31.75"
                + "C31.75 32.63 31.4 33.48 30.77 34.11C30.15 34.73 29.3 35.08 28.42 35.08H5.08"
                + "C4.2 35.08 3.35 34.73 2.73 34.11C2.1 33.48 1.75 32.63 1.75 31.75V13.42Z");
        homeSvg.setStroke(Color.WHITE);
        homeSvg.setStrokeWidth(2.5);
        homeSvg.setFill(Color.TRANSPARENT);
        homeSvg.setScaleX(0.7);
        homeSvg.setScaleY(0.7);

        WarehouseButton homeBtn = WarehouseButton.icon(homeSvg);
        homeBtn.setOnAction(e -> Homepage.show(stage, handler));

        // Allow dragging the window by the header bar
        final double[] dragDelta = new double[2];
        bar.setOnMousePressed(e -> { dragDelta[0] = stage.getX() - e.getScreenX(); dragDelta[1] = stage.getY() - e.getScreenY(); });
        bar.setOnMouseDragged(e -> { stage.setX(e.getScreenX() + dragDelta[0]); stage.setY(e.getScreenY() + dragDelta[1]); });

        bar.getChildren().addAll(logo, textBlock, spacer, homeBtn);
        return bar;
    }

    // ─── Center (list + right panel) ────────────
    private HBox buildCenter() {
        HBox center = new HBox(12);
        center.setPadding(new Insets(12, 15, 12, 15));

        orderListView.setStyle("-fx-background-color: transparent;"
                + "-fx-control-inner-background: transparent;");
        orderListView.setCellFactory(lv -> new OrderListCell());
        orderListView.getSelectionModel().selectFirst();
        HBox.setHgrow(orderListView, Priority.ALWAYS);

        VBox rightPanel = new VBox(10);
        VBox currentItemPane = buildCurrentItemPane();
        VBox.setVgrow(currentItemPane, Priority.ALWAYS);
        rightPanel.getChildren().addAll(currentItemPane, buttonBoxWrapper);
        rightPanel.setMinWidth(500);
        rightPanel.setMaxWidth(700);

        center.getChildren().addAll(orderListView, rightPanel);
        return center;
    }

    // ─── Order List Cell ────────────────────────
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
            cell.prefWidthProperty().bind(orderListView.widthProperty().subtract(20));
            cell.setPrefHeight(64);

            Rectangle bg = new Rectangle();
            bg.widthProperty().bind(cell.prefWidthProperty());
            bg.setHeight(64);
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

            int itemCount = getItems(order).size();
            Label countLabel = new Label(itemCount + " item" + (itemCount != 1 ? "s" : ""));
            countLabel.setFont(Font.font("IBM Plex Mono", 14));
            countLabel.setTextFill(Color.web("#FFFFFF", 0.6));
            countLabel.layoutXProperty().bind(cell.prefWidthProperty().subtract(185));
            countLabel.setLayoutY(20);

            Label statusLabel = new Label(order.getOrderStatus());
            statusLabel.setFont(Font.font("IBM Plex Mono", 12));
            statusLabel.setTextFill(statusColor(order.getOrderStatus()));
            statusLabel.layoutXProperty().bind(cell.prefWidthProperty().subtract(185));
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
            deleteBtn.layoutXProperty().bind(cell.prefWidthProperty().subtract(42));
            deleteBtn.setLayoutY(16);
            deleteBtn.setOnAction(e -> {
                handler.cancelOrder(order.getOrderID());
                OrderLock.unlock(order.getOrderID());
                orders.remove(order);
                selectedOrderIndex.set(
                        Math.max(0, Math.min(selectedOrderIndex.get(), orders.size() - 1)));
                selectedItemIndex.set(0);
                updateCurrentItemDisplay();
                rebuildButtonBox();
            });

            cell.getChildren().addAll(bg, pkgIcon, orderLabel, countLabel, statusLabel, deleteBtn);
            cell.setOnMouseEntered(e -> { if (!isSelected()) cell.setOpacity(0.9); });
            cell.setOnMouseExited(e  -> { if (!isSelected()) cell.setOpacity(0.7); });

            setGraphic(cell);
            setStyle("-fx-background-color: transparent; -fx-padding: 4 0;");
        }
    }

    private Color statusColor(String status) {
        if (status == null) return Color.GRAY;
        return switch (status.toLowerCase()) {
            case "incoming"  -> Color.web("#47CEFF");
            case "started"   -> Color.web("#EEAE3F");
            case "completed" -> Color.web("#28C840");
            case "canceled"  -> Color.web("#FF5F57");
            default          -> Color.GRAY;
        };
    }

    // ─── Current Item Pane ──────────────────────
    private VBox buildCurrentItemPane() {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(14));
        pane.setStyle(PANEL_BG);

        // Header row
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Label header = new Label("Current Item:");
        header.setFont(Font.font("IBM Plex Mono", 26));
        header.setTextFill(Color.web("#E5F2E5"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        currentItemIdLabel.setFont(Font.font("IBM Plex Mono", 18));
        currentItemIdLabel.setTextFill(Color.web("#E6E6E6"));
        headerRow.getChildren().addAll(header, spacer, currentItemIdLabel);

        // Body: image + details
        HBox bodyRow = new HBox(16);
        bodyRow.setAlignment(Pos.CENTER_LEFT);
        VBox.setVgrow(bodyRow, Priority.ALWAYS);

        Rectangle imgPlaceholder = new Rectangle(220, 190);
        imgPlaceholder.setFill(Color.web("#333333", 0.7));

        VBox details = new VBox(18);
        details.setAlignment(Pos.CENTER_LEFT);
        currentItemLocationLabel.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 18));
        currentItemLocationLabel.setTextFill(Color.WHITE);
        currentItemQtyLabel.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 18));
        currentItemQtyLabel.setTextFill(Color.WHITE);
        details.getChildren().addAll(currentItemLocationLabel, currentItemQtyLabel);
        bodyRow.getChildren().addAll(imgPlaceholder, details);

        // Footer: name + nav buttons
        HBox footerRow = new HBox(8);
        footerRow.setAlignment(Pos.CENTER_LEFT);
        currentItemNameLabel.setFont(Font.font("IBM Plex Mono", 14));
        currentItemNameLabel.setTextFill(Color.web("#E6E6E6"));
        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        Button prevBtn = WarehouseButton.nav("\u25C0");
        prevBtn.setOnAction(e -> {
            if (orders.isEmpty()) return;
            List<Item> items = getItems(orders.get(selectedOrderIndex.get()));
            int idx = selectedItemIndex.get();
            selectedItemIndex.set(idx > 0 ? idx - 1 : items.size() - 1);
            updateCurrentItemDisplay();
        });

        Button nextBtn = WarehouseButton.nav("\u25B6");
        nextBtn.setOnAction(e -> {
            if (orders.isEmpty()) return;
            List<Item> items = getItems(orders.get(selectedOrderIndex.get()));
            int idx = selectedItemIndex.get();
            selectedItemIndex.set(idx < items.size() - 1 ? idx + 1 : 0);
            updateCurrentItemDisplay();
        });

        footerRow.getChildren().addAll(currentItemNameLabel, footerSpacer, prevBtn, nextBtn);
        pane.getChildren().addAll(headerRow, bodyRow, footerRow);
        return pane;
    }

    // ─── Button Box ─────────────────────────────
    private void rebuildButtonBox() {
        buttonBoxWrapper.getChildren().clear();
        if (orders.isEmpty()) {
            buttonBoxWrapper.getChildren().add(buildStartButtonPanel(null));
            return;
        }
        Order order = orders.get(selectedOrderIndex.get());
        switch (order.getOrderStatus()) {
            case "started"            -> buttonBoxWrapper.getChildren().add(buildFullButtonPanel(order));
            case "completed",
                 "canceled"           -> buttonBoxWrapper.getChildren().add(buildReadOnlyButtonPanel(order));
            default                   -> buttonBoxWrapper.getChildren().add(buildStartButtonPanel(order));
        }
    }

    /** Only shown when the selected order is incoming. */
    private VBox buildStartButtonPanel(Order order) {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(14));
        pane.setStyle(PANEL_BG);

        boolean locked = order != null && OrderLock.isLocked(order.getOrderID());
        Button startBtn = WarehouseButton.success(locked ? "Order In Progress" : "Start Order", 600, 60);
        startBtn.setMaxWidth(Double.MAX_VALUE);
        startBtn.setDisable(order == null || locked);

        if (order != null && !locked) {
            startBtn.setOnAction(e -> {
                if (OrderLock.tryLock(order.getOrderID())) {
                    handler.startOrder(order.getOrderID());
                    refreshOrders();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Order Locked");
                    alert.setHeaderText(null);
                    alert.setContentText("This order is already being handled in another session.");
                    alert.showAndWait();
                    rebuildButtonBox();
                }
            });
        }

        pane.getChildren().add(startBtn);
        return pane;
    }

    /** Shown once the selected order has been started. */
    private VBox buildFullButtonPanel(Order order) {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(14));
        pane.setStyle(PANEL_BG);

        Button completeBtn = WarehouseButton.success("Complete Order", 600, 60);
        completeBtn.setMaxWidth(Double.MAX_VALUE);
        completeBtn.setOnAction(e -> {
            handler.completeOrder(order.getOrderID());
            OrderLock.unlock(order.getOrderID());
            refreshOrders();
        });

        HBox row2 = new HBox(10);
        Button modifyBtn = WarehouseButton.action("Modify Order", 0, 54, false);
        modifyBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(modifyBtn, Priority.ALWAYS);

        Button importBtn = WarehouseButton.action("Import Orders", 0, 54, false);
        importBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(importBtn, Priority.ALWAYS);
        importBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Import Orders");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON or XML Files", "*.json", "*.xml"));
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                try {
                    Parser parser = new Parser();
                    parser.setNewPath(file.getAbsolutePath());
                    List<Order> orders = parser.parseFile(file.getAbsolutePath());
                    handler.loadOrders(orders);
                    refreshOrders();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        row2.getChildren().addAll(modifyBtn, importBtn);

        HBox row3 = new HBox(10);
        Button printBtn = WarehouseButton.action("Print Label", 0, 54, false);
        printBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(printBtn, Priority.ALWAYS);

        Button exportBtn = WarehouseButton.action("Export Orders", 0, 54, true);
        exportBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(exportBtn, Priority.ALWAYS);
        exportBtn.setOnAction(e -> handler.saveData(Homepage.SAVE_FILE));
        row3.getChildren().addAll(printBtn, exportBtn);

        pane.getChildren().addAll(completeBtn, row2, row3);
        return pane;
    }

    /** Shown for completed or canceled orders — no actions available. */
    private VBox buildReadOnlyButtonPanel(Order order) {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(14));
        pane.setStyle(PANEL_BG);

        HBox row = new HBox(10);
        Button printBtn = WarehouseButton.action("Print Label", 0, 54, false);
        printBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(printBtn, Priority.ALWAYS);

        Button exportBtn = WarehouseButton.action("Export Orders", 0, 54, true);
        exportBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(exportBtn, Priority.ALWAYS);
        exportBtn.setOnAction(e -> handler.saveData(Homepage.SAVE_FILE));
        row.getChildren().addAll(printBtn, exportBtn);

        pane.getChildren().add(row);
        return pane;
    }

    // ─── Listeners & State ──────────────────────
    private void setupListeners() {
        orderListView.getSelectionModel().selectedIndexProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal.intValue() >= 0) {
                        selectedOrderIndex.set(newVal.intValue());
                        selectedItemIndex.set(0);
                        updateCurrentItemDisplay();
                        rebuildButtonBox();
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
        currentItemLocationLabel.setText("Location: "
                + (item.getWarehouseLocation() != null ? item.getWarehouseLocation() : "N/A"));
        currentItemQtyLabel.setText("Qty: " + item.getItemQuantity());
    }

    // ─── Getters ────────────────────────────────
    public StackPane getRoot() { return root; }
    public HBox getTitleBar() { return titleBar; }
}