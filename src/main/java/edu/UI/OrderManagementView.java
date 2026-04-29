package edu.UI;

import edu.ics372.*;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderManagementView {
    // REMINDER: ADD LISTENERS
    private final StackPane root;
    private final HBox titleBar;

    //for orderSummary
    private final BorderPane layout;

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
    private final javafx.scene.image.ImageView currentItemImageView = new javafx.scene.image.ImageView();

    private final ListView<Order> orderListView;
    private final VBox buttonBoxWrapper = new VBox();
    // Keep track of all Start Order buttons so they can be enabled/disabled
    private final List<Button> allStartButtons = new ArrayList<>();
    private final HBox centerPane;
    private final Button backBtn = WarehouseButton.action("◀", 120, 40, false);

    public OrderManagementView(OrderHandler handler, String warehouseId,
                               String warehouseName, Stage stage) {
        this.handler       = handler;
        this.warehouseId   = warehouseId;
        this.warehouseName = warehouseName;
        this.stage         = stage;
        this.orders        = loadWarehouseOrders();
        this.orderListView = new ListView<>(orders);
        //orderSummary
        this.layout = new BorderPane();

        root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");



        /* added as an instance variable so
        BorderPane layout = new BorderPane();
         */
        layout.getStyleClass().add("glass-bg");
        layout.setEffect(new DropShadow(24, Color.BLACK));

        titleBar = null;
        centerPane = buildCenter();
        layout.setTop(buildHeaderBar());
        layout.setCenter(centerPane);

        layout.prefWidthProperty().bind(root.widthProperty());
        layout.prefHeightProperty().bind(root.heightProperty());
        root.getChildren().add(layout);

        setupListeners();
        updateCurrentItemDisplay();
        rebuildButtonBox();

        handler.setOnOrderGenerated(() -> Platform.runLater(() -> {
            Notifications.playIncomingOrder();
            refreshOrders();
        }));
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


    /**
     *  Refreshes orders after state change
     */
    private void refreshOrders() {

        // Saving which order is selected to start to maintain selected state.
        String selectedId = orders.isEmpty() ? null
                : orders.get(selectedOrderIndex.get()).getOrderID();

        orders.setAll(loadWarehouseOrders());

        int newIndex = 0;
        if (selectedId != null) {
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getOrderID().equals(selectedId)) {
                    newIndex = i;
                    break;
                }
            }
        }

        selectedOrderIndex.set(newIndex);
        orderListView.getSelectionModel().select(newIndex);
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
    //Order data for cancelled orders, imported orders, and exported orders
    private void showMetricsChart() {
        backBtn.setVisible(true);
        backBtn.setManaged(true);
        layout.setCenter(MetricsChartView.createMetricsChartPane(handler));
    }

    // ─── Header ─────────────────────────────
    private HBox buildHeaderBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(8, 15, 8, 15));
        bar.getStyleClass().add("header-bar");

        SVGPath logo = new SVGPath();
        logo.setContent("M31.25 18.03L11.75 6.78M2.58 12.74L21.5 23.68L40.42 12.74M21.5 45.5V23.66");
        logo.setStroke(Color.WHITE);
        logo.setStrokeWidth(3);
        logo.setFill(Color.TRANSPARENT);
        logo.setScaleX(0.6);
        logo.setScaleY(0.6);

        VBox textBlock = new VBox(2);
        Label name = new Label(warehouseName);
        name.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 24));
        name.setTextFill(Color.WHITE);
        Label idLbl = new Label("ID: " + warehouseId);
        idLbl.setFont(Font.font("IBM Plex Mono", 15));
        idLbl.setTextFill(Color.web("#FFFFFF", 0.6));
        textBlock.getChildren().addAll(name, idLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Group homeSvg = SvgLoader.load("home.svg", Color.WHITE, Color.TRANSPARENT, -285, -560);
        homeSvg.setScaleX(1.1);
        homeSvg.setScaleY(1.1);

        WarehouseButton homeBtn = WarehouseButton.icon(homeSvg);
        homeBtn.setOnAction(e -> Homepage.show(stage, handler));

        //metrics button
        Button metricsBTN = WarehouseButton.action("Metrics", 140, 54, true);
        metricsBTN.setOnAction(e->showMetricsChart());

        //Export button
        Group exportSvg = SvgLoader.load("export.svg", Color.WHITE, Color.TRANSPARENT);
        exportSvg.setScaleX(1.1);
        exportSvg.setScaleY(1.1);

        WarehouseButton exportNavBtn = WarehouseButton.icon(exportSvg);

        exportNavBtn.setOnAction(e->{
            handler.exportCompletedOrders(".json");
//            Alert notification of export.
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Completed");
            alert.setContentText("Exported orders successfully to ");
            refreshOrders();
        });

        // Allow dragging the window by the header bar
        final double[] dragDelta = new double[2];
        bar.setOnMousePressed(e -> { dragDelta[0] = stage.getX() - e.getScreenX(); dragDelta[1] = stage.getY() - e.getScreenY(); });
        bar.setOnMouseDragged(e -> { stage.setX(e.getScreenX() + dragDelta[0]); stage.setY(e.getScreenY() + dragDelta[1]); });

        backBtn.setVisible(false);
        backBtn.setManaged(false);
        backBtn.setOnAction(e -> {
            layout.setCenter(centerPane);
            backBtn.setVisible(false);
            backBtn.setManaged(false);
        });

        bar.getChildren().addAll(logo, textBlock, spacer, backBtn, metricsBTN, exportNavBtn, homeBtn);
        return bar;
    }

    // ─── Center (list + right panel) ────────────
    private HBox buildCenter() {
        HBox center = new HBox(12);
        center.setPadding(new Insets(12, 15, 12, 15));

        orderListView.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-control-inner-background: transparent;" +
                        "-fx-padding: 0 4 0 0; ");
        orderListView.setCellFactory(lv -> new OrderListCell());
        orderListView.getSelectionModel().selectFirst();

        // ── Wrap the list in a ScrollPane ──
        ScrollPane scrollPane = new ScrollPane(orderListView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        HBox.setHgrow(scrollPane, Priority.ALWAYS);   // ← replaces the HGrow on orderListView

        VBox rightPanel = new VBox(10);
        VBox currentItemPane = buildCurrentItemPane();
        VBox.setVgrow(currentItemPane, Priority.ALWAYS);
        rightPanel.getChildren().addAll(currentItemPane, buttonBoxWrapper);
        rightPanel.setMinWidth(500);
        rightPanel.setMaxWidth(700);

        center.getChildren().addAll(scrollPane, rightPanel);  // ← scrollPane instead of orderListView
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



            boolean completed = "completed".equalsIgnoreCase(order.getOrderStatus());
            Color iconTint = isSelected() ? Color.web("#F35621") : Color.WHITE;
            if (isSelected()) bg.setFill(Color.web("#FFFFFF", 0.05));
            else              cell.setOpacity(0.7);

            Node orderIcon = completed
                    ? buildTypeIcon("completed", Color.web("#28C840"))
                    : buildTypeIcon(null, iconTint);

            Label orderLabel = new Label("Order: #" + order.getOrderID());
            orderLabel.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 22));
            orderLabel.setTextFill(Color.WHITE);
            orderLabel.setLayoutX(70);
            orderLabel.setLayoutY(17);

            int itemCount = getItems(order).size();
            Label countLabel = new Label(itemCount + " item" + (itemCount != 1 ? "s" : ""));
            countLabel.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 16));
            countLabel.setTextFill(Color.web("#FFFFFF", 0.6));
            countLabel.layoutXProperty().bind(cell.prefWidthProperty().subtract(185));
            countLabel.setLayoutY(20);

            Label statusLabel = new Label(order.getOrderStatus());
            statusLabel.getStyleClass().add("order-status");
            if (order.getOrderStatus() != null)
                statusLabel.getStyleClass().add(order.getOrderStatus().toLowerCase());
            statusLabel.layoutXProperty().bind(cell.prefWidthProperty().subtract(185));
            statusLabel.setLayoutY(38);

            Group trashSvg = SvgLoader.load("trash.svg", Color.WHITE, Color.TRANSPARENT);
            trashSvg.setScaleX(1.0);
            trashSvg.setScaleY(1.0);

            WarehouseButton deleteBtn = WarehouseButton.transparent(trashSvg);
            deleteBtn.layoutXProperty().bind(cell.prefWidthProperty().subtract(42));
            deleteBtn.setLayoutY(16);
            deleteBtn.setOnAction(e -> {
                // Open dialog modal upon clicking trash can icon
                Optional<ButtonType> result = Notifications.confirmation(
                        "Confirmation",
                        "Are you sure you want to delete this order?",
                        "Please confirm your action.");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    handler.cancelOrder(order.getOrderID());
                    OrderLock.unlock(order.getOrderID());
                    orders.remove(order);
                    selectedOrderIndex.set(
                            Math.max(0, Math.min(selectedOrderIndex.get(), orders.size() - 1)));
                    selectedItemIndex.set(0);
                    updateCurrentItemDisplay();
                    rebuildButtonBox();
                }

            });

            cell.getChildren().addAll(bg, orderIcon, orderLabel, countLabel, statusLabel, deleteBtn);
            cell.setOnMouseEntered(e -> { if (!isSelected()) cell.setOpacity(0.9); });
            cell.setOnMouseExited(e  -> { if (!isSelected()) cell.setOpacity(0.7); });

            setGraphic(cell);
            setStyle("-fx-background-color: transparent; -fx-padding: 4 0;");
        }
    }

    /**
     * Returns an icon node for the given order type (used on completed orders).
     * Pulls from the SVG files in resources/images/. Falls back to the generic
     * package icon when type is null or unrecognized.
     */
    private static Node buildTypeIcon(String orderType, Color tint) {
        String type = orderType == null ? "" : orderType.toLowerCase().trim();
        Group icon = switch (type) {
            case "completed"       -> SvgLoader.load("order-filled.svg", Color.TRANSPARENT, tint);
            case "pickup"          -> SvgLoader.load("pickup.svg",       tint, Color.TRANSPARENT);
            case "shipped"         -> SvgLoader.load("order-filled.svg", Color.TRANSPARENT, tint);
            case "direct delivery" -> SvgLoader.load("home.svg", tint, Color.TRANSPARENT, -285, -560);
            default                -> SvgLoader.load("order-filled.svg", Color.TRANSPARENT, tint);
        };
        icon.setScaleX(0.9);
        icon.setScaleY(0.9);
        icon.setLayoutX(4);
        icon.setLayoutY(16);
        return icon;
    }



    // ─── Current Item Pane ──────────────────────
    private VBox buildCurrentItemPane() {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(14));
        pane.getStyleClass().add("inner-panel");

        // Header row
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Label header = new Label("Current Item:");
        header.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 30));
        header.setTextFill(Color.web("#E5F2E5"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        currentItemIdLabel.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 20));
        currentItemIdLabel.setTextFill(Color.web("#E6E6E6"));
        headerRow.getChildren().addAll(header, spacer, currentItemIdLabel);

        // Body: image + details
        HBox bodyRow = new HBox(16);
        bodyRow.setAlignment(Pos.CENTER_LEFT);
        VBox.setVgrow(bodyRow, Priority.ALWAYS);

        Rectangle imgBackground = new Rectangle(220, 190);
        imgBackground.setFill(Color.web("#333333", 0.7));
        currentItemImageView.setFitWidth(220);
        currentItemImageView.setFitHeight(190);
        currentItemImageView.setPreserveRatio(true);
        currentItemImageView.setSmooth(true);
        StackPane imgContainer = new StackPane(imgBackground, currentItemImageView);
        imgContainer.setPrefSize(220, 190);

        VBox details = new VBox(18);
        details.setAlignment(Pos.CENTER_LEFT);
        currentItemLocationLabel.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 22));
        currentItemLocationLabel.setTextFill(Color.WHITE);
        currentItemQtyLabel.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 22));
        currentItemQtyLabel.setTextFill(Color.WHITE);
        details.getChildren().addAll(currentItemLocationLabel, currentItemQtyLabel);
        bodyRow.getChildren().addAll(imgContainer, details);

        // Footer: name + nav buttons
        HBox footerRow = new HBox(8);
        footerRow.setAlignment(Pos.CENTER_LEFT);
        currentItemNameLabel.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 18));
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
        pane.getStyleClass().add("inner-panel");

        boolean locked = order != null && OrderLock.isLocked(order.getOrderID());
        Button startBtn = WarehouseButton.success(locked ? "Order In Progress" : "Start Order", 600, 60);
        startBtn.setMaxWidth(Double.MAX_VALUE);
        startBtn.setDisable(order == null || locked || !handler.getStartedOrders().isEmpty());

        if (order != null && !locked) {
            startBtn.setOnAction(e -> {  //ADD LISTENER HERE
                if (OrderLock.tryLock(order.getOrderID())) {
                    handler.startOrder(order.getOrderID());
                    refreshOrders();
                    updateStartButtons(); // disable all Start buttons after starting
                } else {
                    Notifications.warning(
                            "Order Locked",
                            "This order is already being handled in another session.");
                    rebuildButtonBox();
                }
            });
        }
        allStartButtons.add(startBtn); // <-- ADD here
        pane.getChildren().add(startBtn);
        return pane;
    }

    /** Shown once the selected order has been started. */
    private VBox buildFullButtonPanel(Order order) {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(14));
        pane.getStyleClass().add("inner-panel");

        Button completeBtn = WarehouseButton.success("Complete Order", 600, 60);
        completeBtn.setMaxWidth(Double.MAX_VALUE);
        completeBtn.setOnAction(e -> {
            handler.completeOrder(order.getOrderID());
            OrderLock.unlock(order.getOrderID());
            refreshOrders();
            updateStartButtons(); // re-enable Start buttons if needed
        });

        pane.getChildren().addAll(completeBtn);
        return pane;
    }

    /** Shown for completed or canceled orders — no actions available. */
    private VBox buildReadOnlyButtonPanel(Order order) {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(14));
        pane.getStyleClass().add("inner-panel");
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
            currentItemImageView.setImage(null);
            return;
        }

        Order order = orders.get(selectedOrderIndex.get());
        List<Item> items = getItems(order);

        if (items.isEmpty()) {
            currentItemIdLabel.setText("");
            currentItemNameLabel.setText("No items in this order");
            currentItemLocationLabel.setText("");
            currentItemQtyLabel.setText("");
            currentItemImageView.setImage(null);
            return;
        }

        Item item = items.get(selectedItemIndex.get());
        currentItemIdLabel.setText("#" + item.getItemID());
        currentItemNameLabel.setText(item.getItemName());
        currentItemLocationLabel.setText("Location: "
                + (item.getWarehouseLocation() != null ? item.getWarehouseLocation() : "N/A"));
        currentItemQtyLabel.setText("Qty: " + item.getItemQuantity());

        java.net.URL imgUrl = getClass().getResource(
                "resources/images/Item-Catalog/" + item.getItemName().trim() + ".png");
        currentItemImageView.setImage(imgUrl != null
                ? new javafx.scene.image.Image(imgUrl.toExternalForm(), true) : null);
    }
    /**
     * Updates the state of all "Start Order" buttons in the GUI.
     * If there is an active order currently being processed,
     *  all start buttons are disabled to prevent
     * starting a new order. Once the active order is completed or canceled,
     * this method re-enables the buttons.
     */
    private void updateStartButtons() {
        // Check if there is any order currently started
        boolean hasActiveOrder = !handler.getStartedOrders().isEmpty();

        // Iterate through all Start Order buttons in the GUI
        for (Button startBtn : allStartButtons) {
            startBtn.setDisable(hasActiveOrder);
        }
    }

    // ─── Getters ────────────────────────────────
    public StackPane getRoot() { return root; }
    public HBox getTitleBar() { return titleBar; }
}