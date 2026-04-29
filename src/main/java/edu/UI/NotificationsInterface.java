package edu.UI;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public interface NotificationsInterface {

    void playIncomingOrder();

    Optional<ButtonType> confirmation(String title, String header, String content);

    void warning(String title, String content);

    void info(String title, String content);

}
