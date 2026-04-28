package edu.UI

import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.util.Optional

/**
 * Central class for all application alerts and notification sounds.
 */
object Notifications {

    // ── Sounds ───────────────────────────────────────────────────────────────

    fun playIncomingOrder() = playSound("resources/audio/incoming-order.mp3")

    private fun playSound(resourcePath: String) {
        try {
            val url = Notifications::class.java.getResource(resourcePath) ?: return
            MediaPlayer(Media(url.toExternalForm())).play()
        } catch (e: Exception) {
            println("Could not play sound '$resourcePath': ${e.message}")
        }
    }

    // ── Alerts ───────────────────────────────────────────────────────────────

    fun confirmation(title: String, header: String, content: String): Optional<ButtonType> {
        val alert = Alert(AlertType.CONFIRMATION)
        alert.title = title
        alert.headerText = header
        alert.contentText = content
        applyStyle(alert)
        return alert.showAndWait()
    }

    fun warning(title: String, content: String) {
        val alert = Alert(AlertType.WARNING)
        alert.title = title
        alert.headerText = null
        alert.contentText = content
        applyStyle(alert)
        alert.showAndWait()
    }

    fun info(title: String, content: String) {
        val alert = Alert(AlertType.INFORMATION)
        alert.title = title
        alert.headerText = null
        alert.contentText = content
        applyStyle(alert)
        alert.showAndWait()
    }

    private fun applyStyle(alert: Alert) {
        val css = Notifications::class.java
            .getResource("resources/styles/alert.css")?.toExternalForm() ?: return
        alert.dialogPane.stylesheets.add(css)
    }
}