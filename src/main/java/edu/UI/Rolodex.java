package edu.UI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

import java.util.ArrayList;
import java.util.List;

public class Rolodex extends Application {

    private static final int ITEM_HEIGHT = 87;
    private static final int TOTAL_ITEMS = 15;
    private static final int START_ORDER = 234324;

    private IntegerProperty selectedIndex = new SimpleIntegerProperty(0);
    private Label headerLabel;
    private VBox listContainer;
    private List<OrderListItem> listItems;
    private ScrollPane scrollPane;

    public StackPane getView() {
        // Main container
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1a1a1a;");
        root.setPrefSize(700, 800);

        // Create header
        Pane header = createHeader();
        StackPane.setAlignment(header, Pos.TOP_LEFT);
        StackPane.setMargin(header, new Insets(15, 0, 0, 15));

        // Create scrollable list
        VBox scrollContent = createScrollableList();

        scrollPane = new ScrollPane(scrollContent);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPrefSize(635, 700);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        StackPane.setAlignment(scrollPane, Pos.TOP_LEFT);
        StackPane.setMargin(scrollPane, new Insets(100, 0, 0, 15));

        // Add scroll listener
        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> handleScroll());

        // Create custom scrollbar indicator
        Pane scrollbarIndicator = createScrollbarIndicator();
        StackPane.setAlignment(scrollbarIndicator, Pos.TOP_LEFT);
        StackPane.setMargin(scrollbarIndicator, new Insets(100, 0, 0, 671));

        root.getChildren().addAll(scrollPane, header, scrollbarIndicator);

        // Initialize styles once added to a scene
        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(this::updateItemStyles);
            }
        });

        return root;
    }

    @Override
    public void start(Stage primaryStage) {
        StackPane root = getView();
        Scene scene = new Scene(root, 700, 850);
        primaryStage.setTitle("Rolodex List");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Pane createHeader() {
        StackPane header = new StackPane();
        header.setPrefSize(600, 60);

        // Border
        header.setStyle("-fx-border-color: white; -fx-border-width: 2;");

        // Header label
        headerLabel = new Label("Order: #" + START_ORDER);
        headerLabel.setStyle("-fx-font-family: 'Monospaced'; -fx-font-weight: bold; " +
                "-fx-font-size: 24px; -fx-text-fill: white;");
        headerLabel.setAlignment(Pos.CENTER);

        // Package icon (simplified)
        SVGPath packageIcon = new SVGPath();
        packageIcon.setContent("M31.25 18.0289L11.75 6.78389M2.585 12.7422L21.5 23.6839L40.415 12.7422M21.5 45.5022V23.6622M41 32.3289V14.9956C40.9992 14.2357 40.7986 13.4893 40.4183 12.8314C40.038 12.1735 39.4914 11.6272 38.8333 11.2472L23.6667 2.58056C23.0079 2.20023 22.2607 2 21.5 2C20.7393 2 19.9921 2.20023 19.3333 2.58056L4.16667 11.2472C3.50857 11.6272 2.96196 12.1735 2.58167 12.8314C2.20138 13.4893 2.00078 14.2357 2 14.9956V32.3289C2.00078 33.0888 2.20138 33.8351 2.58167 34.493C2.96196 35.1509 3.50857 35.6973 4.16667 36.0772L19.3333 44.7439C19.9921 45.1242 20.7393 45.3244 21.5 45.3244C22.2607 45.3244 23.0079 45.1242 23.6667 44.7439L38.8333 36.0772C39.4914 35.6973 40.038 35.1509 40.4183 34.493C40.7986 33.8351 40.9992 33.0888 41 32.3289Z");
        packageIcon.setFill(createOrangeGradient());
        packageIcon.setStroke(createOrangeGradient());
        packageIcon.setStrokeWidth(2);
        packageIcon.setScaleX(0.8);
        packageIcon.setScaleY(0.8);

        StackPane iconContainer = new StackPane(packageIcon);
        iconContainer.setPrefSize(52, 52);
        StackPane.setAlignment(iconContainer, Pos.CENTER_LEFT);
        StackPane.setMargin(iconContainer, new Insets(0, 0, 0, 10));

        header.getChildren().addAll(headerLabel, iconContainer);

        // Update header when selection changes
        selectedIndex.addListener((obs, oldVal, newVal) -> {
            headerLabel.setText("Order: #" + (START_ORDER + newVal.intValue()));
        });

        return header;
    }

    private VBox createScrollableList() {
        listContainer = new VBox();
        listContainer.setStyle("-fx-background-color: transparent;");
        listContainer.setPadding(new Insets(20, 0, 400, 0));

        listItems = new ArrayList<>();

        for (int i = 0; i < TOTAL_ITEMS; i++) {
            final int index = i;
            OrderListItem item = new OrderListItem("Order: #" + (START_ORDER + i), index);
            item.setOnMouseClicked(e -> scrollToItem(index));
            listItems.add(item);
            listContainer.getChildren().add(item);
        }

        return listContainer;
    }

    private Pane createScrollbarIndicator() {
        VBox scrollbarBg = new VBox();
        scrollbarBg.setPrefSize(4, 700);
        scrollbarBg.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");

        Region thumb = new Region();
        thumb.setPrefSize(4, 700.0 / TOTAL_ITEMS);
        thumb.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-background-radius: 3;");

        scrollbarBg.getChildren().add(thumb);

        // Update thumb position when selection changes
        selectedIndex.addListener((obs, oldVal, newVal) -> {
            int idx = newVal.intValue();
            double position = (idx / (double)(TOTAL_ITEMS - 1)) * 700 * ((TOTAL_ITEMS - 1) / (double)TOTAL_ITEMS);
            thumb.setTranslateY(position);
        });

        return scrollbarBg;
    }

    private void handleScroll() {
        double scrollPos = scrollPane.getVvalue();
        double maxScroll = scrollPane.getContent().getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight();
        double currentPos = scrollPos * maxScroll;

        int index = (int) Math.round(currentPos / ITEM_HEIGHT);
        index = Math.max(0, Math.min(TOTAL_ITEMS - 1, index));

        selectedIndex.set(index);
        updateItemStyles();
    }

    private void scrollToItem(int index) {
        double targetScroll = (index * ITEM_HEIGHT) /
                (scrollPane.getContent().getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight());

        // Animate scroll
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.millis(300),
                        new javafx.animation.KeyValue(scrollPane.vvalueProperty(), targetScroll))
        );
        timeline.play();
    }

    private void updateItemStyles() {
        int selected = selectedIndex.get();

        for (int i = 0; i < listItems.size(); i++) {
            OrderListItem item = listItems.get(i);
            int distance = Math.abs(i - selected);
            boolean isSelected = i == selected;

            double scale = 1.0;
            double opacity = 1.0;
            double rotateX = 0;

            if (isSelected) {
                scale = 1.15;
                opacity = 1.0;
                rotateX = 0;
            } else if (distance == 1) {
                scale = 0.95;
                opacity = 0.85;
                rotateX = -8;
            } else if (distance == 2) {
                scale = 0.85;
                opacity = 0.65;
                rotateX = -12;
            } else if (distance == 3) {
                scale = 0.75;
                opacity = 0.45;
                rotateX = -16;
            } else {
                scale = 0.7;
                opacity = 0.3;
                rotateX = -20;
            }

            item.updateStyle(isSelected, scale, opacity, rotateX);
        }
    }

    private LinearGradient createOrangeGradient() {
        return new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#F35621")),
                new Stop(1, Color.web("#EEAE3F"))
        );
    }

    private LinearGradient createBlueGradient() {
        return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.WHITE),
                new Stop(0.47, Color.web("#47CEFF")),
                new Stop(1, Color.web("#0059D5"))
        );
    }

    // Inner class for list items
    class OrderListItem extends StackPane {
        private SVGPath archiveIcon;
        private Label orderLabel;
        private Pane border;
        private int index;
        private Rotate rotateTransform;

        public OrderListItem(String orderNumber, int index) {
            this.index = index;
            this.setPrefSize(635, 64);
            this.setMaxSize(635, 64);
            this.setStyle("-fx-cursor: hand;");
            VBox.setMargin(this, new Insets(0, 0, 23, 0));

            // Border pane
            border = new Pane();
            border.setPrefSize(600, 64);
            border.setStyle("-fx-border-color: white; -fx-border-width: 2;");

            // Drop shadow
            DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.4));
            dropShadow.setOffsetY(5);
            dropShadow.setRadius(4);
            border.setEffect(dropShadow);

            // Archive icon
            archiveIcon = new SVGPath();
            archiveIcon.setContent("M18.6483 30.1449L26.9365 22.1063L24.0356 19.2928L20.7204 22.5082V14.0676H16.5763V22.5082L13.261 19.2928L10.3602 22.1063L18.6483 30.1449ZM4.14408 10.0483V32.1546H33.1526V10.0483H4.14408ZM4.14408 36.1739C3.00446 36.1739 2.02024 35.7887 1.19142 35.0184C0.397141 34.2145 0 33.2599 0 32.1546V7.08406C0 6.61514 0.0690684 6.16296 0.207205 5.72754C0.379874 5.29211 0.621612 4.89018 0.932418 4.52174L3.52247 1.45701C3.90234 0.988084 4.36855 0.636393 4.92109 0.401932C5.50817 0.133977 6.11251 0 6.73413 0H30.5626C31.1842 0 31.7713 0.133977 32.3238 0.401932C32.9109 0.636393 33.3944 0.988084 33.7742 1.45701L36.3643 4.52174C36.6751 4.89018 36.8995 5.29211 37.0377 5.72754C37.2104 6.16296 37.2967 6.61514 37.2967 7.08406V32.1546C37.2967 33.2599 36.8823 34.2145 36.0535 35.0184C35.2592 35.7887 34.2922 36.1739 33.1526 36.1739H4.14408ZM4.97289 6.02899H32.3238L30.5626 4.01933H6.73413L4.97289 6.02899Z");
            archiveIcon.setFill(Color.WHITE);
            archiveIcon.setScaleX(0.9);
            archiveIcon.setScaleY(0.9);

            StackPane iconContainer = new StackPane(archiveIcon);
            iconContainer.setPrefSize(50, 48);
            StackPane.setAlignment(iconContainer, Pos.CENTER_LEFT);
            StackPane.setMargin(iconContainer, new Insets(0, 0, 0, 10));

            // Trash icon
            SVGPath trashIcon = new SVGPath();
            trashIcon.setContent("M1.5 6.44686H4.0502M4.0502 6.44686H24.4518M4.0502 6.44686L4.0502 23.7609C4.0502 24.4169 4.31888 25.046 4.79714 25.5098C5.27539 25.9737 5.92405 26.2343 6.6004 26.2343H19.3514C20.0278 26.2343 20.6764 25.9737 21.1547 25.5098C21.6329 25.046 21.9016 24.4169 21.9016 23.7609V6.44686M7.8755 6.44686V3.97343C7.8755 3.31744 8.14418 2.68831 8.62244 2.22445C9.10069 1.76059 9.74935 1.5 10.4257 1.5H15.5261C16.2025 1.5 16.8511 1.76059 17.3294 2.22445C17.8076 2.68831 18.0763 3.31744 18.0763 3.97343V6.44686M10.4257 12.6304V20.0507M15.5261 12.6304V20.0507");
            trashIcon.setStroke(Color.WHITE);
            trashIcon.setStrokeWidth(2);
            trashIcon.setFill(Color.TRANSPARENT);
            trashIcon.setScaleX(0.8);
            trashIcon.setScaleY(0.8);

            StackPane trashContainer = new StackPane(trashIcon);
            trashContainer.setPrefSize(30, 30);
            StackPane.setAlignment(trashContainer, Pos.CENTER_RIGHT);
            StackPane.setMargin(trashContainer, new Insets(0, 19, 0, 0));

            // Order label
            orderLabel = new Label(orderNumber);
            orderLabel.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 18px; -fx-text-fill: white;");
            StackPane.setMargin(orderLabel, new Insets(0, 0, 0, 180));

            // Add rotation transform
            rotateTransform = new Rotate(0, Rotate.X_AXIS);
            this.getTransforms().add(rotateTransform);

            this.getChildren().addAll(border, iconContainer, trashContainer, orderLabel);
        }

        public void updateStyle(boolean isSelected, double scale, double opacity, double rotateX) {
            this.setScaleX(scale);
            this.setScaleY(scale);
            this.setOpacity(opacity);
            rotateTransform.setAngle(rotateX);

            if (isSelected) {
                border.setStyle("-fx-border-color: #47CEFF; -fx-border-width: 2;");
                archiveIcon.setFill(createBlueGradient());

                DropShadow glowShadow = new DropShadow();
                glowShadow.setColor(Color.rgb(71, 206, 255, 0.6));
                glowShadow.setOffsetY(8);
                glowShadow.setRadius(12);
                border.setEffect(glowShadow);
            } else {
                border.setStyle("-fx-border-color: white; -fx-border-width: 2;");
                archiveIcon.setFill(Color.WHITE);

                DropShadow dropShadow = new DropShadow();
                dropShadow.setColor(Color.rgb(0, 0, 0, 0.4));
                dropShadow.setOffsetY(5);
                dropShadow.setRadius(4);
                border.setEffect(dropShadow);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
