package edu.UI;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// Placeholder model — the app uses edu.ics372.Order for real data.
public class CurrentOrder {
    private final StringProperty id;
    private final StringProperty orderNumber;
    private final ObservableList<OrderItem> items;

    public CurrentOrder(String id, String orderNumber) {
        this.id = new SimpleStringProperty(id);
        this.orderNumber = new SimpleStringProperty(orderNumber);
        this.items = FXCollections.observableArrayList();
    }

    public String getId() { return id.get(); }
    public StringProperty idProperty() { return id; }

    public String getOrderNumber() { return orderNumber.get(); }
    public StringProperty orderNumberProperty() { return orderNumber; }

    public ObservableList<OrderItem> getItems() { return items; }
    public int getItemCount() { return items.size(); }

    @Override
    public String toString() { return "Order: " + orderNumber.get(); }
}