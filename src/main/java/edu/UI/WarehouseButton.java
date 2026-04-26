package edu.UI;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Base button class for the warehouse UI.
 * Use the static factory methods to create styled button variants.
 * All variants get a subtle scale + style change on hover automatically.
 */
public class WarehouseButton extends Button {

    private final String baseStyle;
    private final String hoverStyle;

    public WarehouseButton(String text, String baseStyle, String hoverStyle) {
        super(text);
        this.baseStyle = baseStyle;
        this.hoverStyle = hoverStyle;
        wire();
    }

    public WarehouseButton(Node graphic, String baseStyle, String hoverStyle) {
        super();
        setGraphic(graphic);
        this.baseStyle = baseStyle;
        this.hoverStyle = hoverStyle;
        wire();
    }

    private void wire() {
        setStyle(baseStyle);
        setOnMouseEntered(e -> {
            setStyle(hoverStyle);
            setScaleX(1.04);
            setScaleY(1.04);
        });
        setOnMouseExited(e -> {
            setStyle(baseStyle);
            setScaleX(1.0);
            setScaleY(1.0);
        });
    }

    // ─── Factories ───────────────────────────────

    /** Large action button — semi-transparent, or orange gradient when accent=true. */
    public static WarehouseButton action(String text, double width, double height, boolean accent) {
        String base = accent
                ? "-fx-background-color: linear-gradient(to right, #D44B1C, #C98A1A); -fx-background-radius: 8; -fx-cursor: hand;"
                : "-fx-background-color: rgba(60,60,60,0.9); -fx-background-radius: 8; -fx-cursor: hand;";
        String hover = accent
                ? "-fx-background-color: linear-gradient(to right, #BF4219, #B57A18); -fx-background-radius: 8; -fx-cursor: hand;"
                : "-fx-background-color: rgba(80,80,80,0.95); -fx-background-radius: 8; -fx-cursor: hand;";
        WarehouseButton btn = new WarehouseButton(text, base, hover);
        btn.setPrefSize(width, height);
        btn.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 26));
        btn.setTextFill(Color.WHITE);
        btn.setEffect(new DropShadow(4.8, 4, 5, Color.web("#000000", 0.25)));
        return btn;
    }

    /** Arrow navigation button (◀ / ▶). */
    public static WarehouseButton nav(String arrow) {
        String base  = "-fx-background-color: rgba(60,60,60,0.9); -fx-background-radius: 8; -fx-cursor: hand;";
        String hover = "-fx-background-color: rgba(80,80,80,0.95); -fx-background-radius: 8; -fx-cursor: hand;";
        WarehouseButton btn = new WarehouseButton(arrow, base, hover);
        btn.setPrefSize(58, 60);
        btn.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 22));
        btn.setTextFill(Color.WHITE);
        btn.setEffect(new DropShadow(4.8, 4, 5, Color.web("#000000", 0.25)));
        return btn;
    }

    /** Square button with an SVG graphic (home, settings, etc.). */
    public static WarehouseButton icon(Node graphic) {
        String base  = "-fx-background-color: rgba(60,60,60,0.9); -fx-background-radius: 8; -fx-cursor: hand;";
        String hover = "-fx-background-color: rgba(80,80,80,0.95); -fx-background-radius: 8; -fx-cursor: hand;";
        WarehouseButton btn = new WarehouseButton(graphic, base, hover);
        btn.setPrefSize(58, 54);
        return btn;
    }

    /** Large green action button (complete, confirm, etc.). */
    public static WarehouseButton success(String text, double width, double height) {
        String base  = "-fx-background-color: linear-gradient(to right, #1A6B35, #2EAA5E); -fx-background-radius: 8; -fx-cursor: hand;";
        String hover = "-fx-background-color: linear-gradient(to right, #165C2D, #279150); -fx-background-radius: 8; -fx-cursor: hand;";
        WarehouseButton btn = new WarehouseButton(text, base, hover);
        btn.setPrefSize(width, height);
        btn.setFont(Font.font("IBM Plex Mono", FontWeight.BOLD, 26));
        btn.setTextFill(Color.WHITE);
        btn.setEffect(new DropShadow(4.8, 4, 5, Color.web("#000000", 0.25)));
        return btn;
    }

    /** Solid orange primary button (import, confirm, etc.). */
    public static WarehouseButton primary(String text) {
        String base  = "-fx-background-color: #C94600; -fx-text-fill: white; -fx-font-family: 'Monospaced'; -fx-font-size: 16px; -fx-padding: 10 24; -fx-cursor: hand;";
        String hover = "-fx-background-color: #B33D00; -fx-text-fill: white; -fx-font-family: 'Monospaced'; -fx-font-size: 16px; -fx-padding: 10 24; -fx-cursor: hand;";
        return new WarehouseButton(text, base, hover);
    }

    /** Transparent icon-only button (delete, etc.). */
    public static WarehouseButton transparent(Node graphic) {
        WarehouseButton btn = new WarehouseButton(graphic,
                "-fx-background-color: transparent; -fx-cursor: hand;",
                "-fx-background-color: rgba(255,255,255,0.1); -fx-cursor: hand;");
        btn.setPrefSize(30, 30);
        return btn;
    }
}