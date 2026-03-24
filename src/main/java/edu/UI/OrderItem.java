package edu.UI;

import javafx.beans.property.*;

// Placeholder model — the app uses edu.ics372.Item for real data.
public class OrderItem {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty location;
    private final IntegerProperty scanned;
    private final IntegerProperty total;
    private final StringProperty imagePath;

    public OrderItem(String id, String name, String location, int scanned, int total, String imagePath) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.location = new SimpleStringProperty(location);
        this.scanned = new SimpleIntegerProperty(scanned);
        this.total = new SimpleIntegerProperty(total);
        this.imagePath = new SimpleStringProperty(imagePath);
    }

    public String getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getLocation() { return location.get(); }
    public int getScanned() { return scanned.get(); }
    public void setScanned(int value) { scanned.set(value); }
    public int getTotal() { return total.get(); }
    public String getImagePath() { return imagePath.get(); }
}