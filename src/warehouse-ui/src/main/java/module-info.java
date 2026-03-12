module edu.ics372.warehouseui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens edu.ics372.warehouseui to javafx.fxml;
    exports edu.ics372.warehouseui;
}