package edu.UI;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.Optional;

public class Notifications implements NotificationsInterface {

    public static final Notifications INSTANCE = new Notifications();

    private Notifications() {}

    // ── Sounds ───────────────────────────────────────────────────────────────

    @Override
    public void playIncomingOrder() {
        try {
            var url = Notifications.class.getResource("resources/audio/incoming-order.mp3");
            if (url == null) return;
            new MediaPlayer(new Media(url.toExternalForm())).play();
        } catch (Exception e) {
            System.out.println("Could not play sound: " + e.getMessage());
        }
    }

    // ── Alerts ───────────────────────────────────────────────────────────────

    @Override
    public Optional<ButtonType> confirmation(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        applyStyle(alert);
        return alert.showAndWait();
    }

    @Override
    public void warning(String title, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        applyStyle(alert);
        alert.showAndWait();
    }

    @Override
    public void info(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        applyStyle(alert);
        alert.showAndWait();
    }

    private static void applyStyle(Alert alert) {
        var css = Notifications.class.getResource("resources/styles/alert.css");
        if (css == null) return;
        alert.getDialogPane().getStylesheets().add(css.toExternalForm());
    }
}