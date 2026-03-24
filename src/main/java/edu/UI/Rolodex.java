package edu.UI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.ics372.Order;

public class Rolodex {

    private static final int ITEM_HEIGHT = 87;
    private static final String RESOURCES_DIR = "src/main/java/edu/UI/resources/";

    private final List<Order> orders;
    private final String warehouseName;
    private final int totalItems;

    private IntegerProperty selectedIndex = new SimpleIntegerProperty(0);
    private Label headerLabel;
    private VBox listContainer;
    private List<OrderListItem> listItems;
    private ScrollPane scrollPane;

    public Rolodex(List<Order> orders, String warehouseName) {
        this.orders = new ArrayList<>(orders);
        this.warehouseName = warehouseName;
        this.totalItems = orders.isEmpty() ? 1 : orders.size();
    }

    // Loads all <path d="..."> values from an SVG file
    private static List<String> loadPathData(String fileName) {
        List<String> paths = new ArrayList<>();
        try (InputStream is = new FileInputStream(RESOURCES_DIR + fileName)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            NodeList pathNodes = doc.getElementsByTagName("path");
            for (int i = 0; i < pathNodes.getLength(); i++) {
                Element el = (Element) pathNodes.item(i);
                String d = el.getAttribute("d");
                if (!d.isEmpty()) paths.add(d);
            }
        } catch (Exception e) {
            System.err.println("Failed to load SVG: " + fileName + " — " + e.getMessage());
        }
        return paths;
    }

    // Builds a Group of SVGPaths from an SVG file with the given fill and stroke
    private static Group loadSvgGroup(String fileName, Paint fill, Paint stroke, double strokeWidth) {
        Group group = new Group();
        for (String d : loadPathData(fileName)) {
            SVGPath path = new SVGPath();
            path.setContent(d);
            if (fill != null) path.setFill(fill);
            if (stroke != null) { path.setStroke(stroke); path.setStrokeWidth(strokeWidth); }
            group.getChildren().add(path);
        }
        return group;
    }

    public StackPane getView() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1a1a1a;");
        root.setPrefSize(700, 800);

        Pane header = createHeader();
        StackPane.setAlignment(header, Pos.TOP_LEFT);
        StackPane.setMargin(header, new Insets(15, 0, 0, 15));

        VBox scrollContent = createScrollableList();

        scrollPane = new ScrollPane(scrollContent);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPrefSize(635, 700);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        StackPane.setAlignment(scrollPane, Pos.TOP_LEFT);
        StackPane.setMargin(scrollPane, new Insets(100, 0, 0, 15));

        scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> handleScroll());

        Pane scrollbarIndicator = createScrollbarIndicator();
        StackPane.setAlignment(scrollbarIndicator, Pos.TOP_LEFT);
        StackPane.setMargin(scrollbarIndicator, new Insets(100, 0, 0, 671));

        root.getChildren().addAll(scrollPane, header, scrollbarIndicator);

        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(this::updateItemStyles);
            }
        });

        return root;
    }

    private Pane createHeader() {
        StackPane header = new StackPane();
        header.setPrefSize(600, 60);
        header.setStyle("-fx-border-color: white; -fx-border-width: 2;");

        String initialText = orders.isEmpty()
                ? "No Orders"
                : "Order: #" + orders.get(0).getOrderID();
        headerLabel = new Label(initialText);
        headerLabel.setStyle("-fx-font-family: 'Monospaced'; -fx-font-weight: bold; " +
                "-fx-font-size: 24px; -fx-text-fill: white;");
        headerLabel.setAlignment(Pos.CENTER);

        // Load package icon from resources
        Group packageIcon = loadSvgGroup("order-filled.svg", createOrangeGradient(), createOrangeGradient(), 1.5);
        packageIcon.setScaleX(0.55);
        packageIcon.setScaleY(0.55);

        StackPane iconContainer = new StackPane(packageIcon);
        iconContainer.setPrefSize(52, 52);
        StackPane.setAlignment(iconContainer, Pos.CENTER_LEFT);
        StackPane.setMargin(iconContainer, new Insets(0, 0, 0, 10));

        header.getChildren().addAll(headerLabel, iconContainer);

        selectedIndex.addListener((obs, oldVal, newVal) -> {
            int idx = newVal.intValue();
            if (!orders.isEmpty() && idx < orders.size()) {
                headerLabel.setText("Order: #" + orders.get(idx).getOrderID());
            }
        });

        return header;
    }

    private VBox createScrollableList() {
        listContainer = new VBox();
        listContainer.setStyle("-fx-background-color: transparent;");
        listContainer.setPadding(new Insets(20, 0, 400, 0));

        listItems = new ArrayList<>();

        if (orders.isEmpty()) {
            OrderListItem placeholder = new OrderListItem("No orders for this warehouse", 0);
            listItems.add(placeholder);
            listContainer.getChildren().add(placeholder);
        } else {
            for (int i = 0; i < orders.size(); i++) {
                final int index = i;
                String label = "Order: #" + orders.get(i).getOrderID();
                OrderListItem item = new OrderListItem(label, index);
                item.setOnMouseClicked(e -> scrollToItem(index));
                listItems.add(item);
                listContainer.getChildren().add(item);
            }
        }

        return listContainer;
    }

    private Pane createScrollbarIndicator() {
        VBox scrollbarBg = new VBox();
        scrollbarBg.setPrefSize(4, 700);
        scrollbarBg.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);");

        Region thumb = new Region();
        thumb.setPrefSize(4, 700.0 / totalItems);
        thumb.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-background-radius: 3;");

        scrollbarBg.getChildren().add(thumb);

        selectedIndex.addListener((obs, oldVal, newVal) -> {
            int idx = newVal.intValue();
            double position = totalItems > 1
                    ? (idx / (double) (totalItems - 1)) * 700 * ((totalItems - 1) / (double) totalItems)
                    : 0;
            thumb.setTranslateY(position);
        });

        return scrollbarBg;
    }

    private void handleScroll() {
        double scrollPos = scrollPane.getVvalue();
        double maxScroll = scrollPane.getContent().getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight();
        double currentPos = scrollPos * maxScroll;

        int index = (int) Math.round(currentPos / ITEM_HEIGHT);
        index = Math.max(0, Math.min(totalItems - 1, index));

        selectedIndex.set(index);
        updateItemStyles();
    }

    private void scrollToItem(int index) {
        double targetScroll = (index * ITEM_HEIGHT) /
                (scrollPane.getContent().getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight());

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

            double scale, opacity, rotateX;

            if (isSelected) {
                scale = 1.15; opacity = 1.0; rotateX = 0;
            } else if (distance == 1) {
                scale = 0.95; opacity = 0.85; rotateX = -8;
            } else if (distance == 2) {
                scale = 0.85; opacity = 0.65; rotateX = -12;
            } else if (distance == 3) {
                scale = 0.75; opacity = 0.45; rotateX = -16;
            } else {
                scale = 0.7; opacity = 0.3; rotateX = -20;
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

    class OrderListItem extends StackPane {
        private SVGPath archiveIcon;
        private Label orderLabel;
        private Pane border;
        private Rotate rotateTransform;

        public OrderListItem(String orderText, int index) {
            this.setPrefSize(635, 64);
            this.setMaxSize(635, 64);
            this.setStyle("-fx-cursor: hand;");
            VBox.setMargin(this, new Insets(0, 0, 23, 0));

            border = new Pane();
            border.setPrefSize(600, 64);
            border.setStyle("-fx-border-color: white; -fx-border-width: 2;");

            DropShadow dropShadow = new DropShadow();
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.4));
            dropShadow.setOffsetY(5);
            dropShadow.setRadius(4);
            border.setEffect(dropShadow);

            // Archive icon from resources
            archiveIcon = new SVGPath();
            List<String> archivePaths = loadPathData("filling-order.svg");
            if (!archivePaths.isEmpty()) archiveIcon.setContent(archivePaths.get(0));
            archiveIcon.setFill(Color.WHITE);
            archiveIcon.setScaleX(0.6);
            archiveIcon.setScaleY(0.6);

            StackPane iconContainer = new StackPane(archiveIcon);
            iconContainer.setPrefSize(50, 48);
            StackPane.setAlignment(iconContainer, Pos.CENTER_LEFT);
            StackPane.setMargin(iconContainer, new Insets(0, 0, 0, 10));

            // Trash icon from resources
            SVGPath trashIcon = new SVGPath();
            List<String> trashPaths = loadPathData("trash.svg");
            if (!trashPaths.isEmpty()) trashIcon.setContent(trashPaths.get(0));
            trashIcon.setStroke(Color.WHITE);
            trashIcon.setStrokeWidth(2);
            trashIcon.setFill(Color.TRANSPARENT);
            trashIcon.setScaleX(0.8);
            trashIcon.setScaleY(0.8);

            StackPane trashContainer = new StackPane(trashIcon);
            trashContainer.setPrefSize(30, 30);
            StackPane.setAlignment(trashContainer, Pos.CENTER_RIGHT);
            StackPane.setMargin(trashContainer, new Insets(0, 19, 0, 0));

            orderLabel = new Label(orderText);
            orderLabel.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 18px; -fx-text-fill: white;");
            StackPane.setMargin(orderLabel, new Insets(0, 0, 0, 180));

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
}