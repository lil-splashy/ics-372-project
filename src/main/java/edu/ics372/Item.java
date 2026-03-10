package edu.ics372;

public class Item {
    // attributes of the item
    private String itemID;
    private String itemName;
    private double itemPrice;
    private int itemQuantity;

    private String warehouseLocation;

    // the item object constructor
    public Item(String itemID, String itemName, double itemPrice, int itemQuantity, String warehouseLocation) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQuantity = itemQuantity;
        this.warehouseLocation = warehouseLocation;
    }
    //getters and setters
    public String getItemID() {
        return itemID;
    }
    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }
    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }
    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getWarehouseLocation() {return warehouseLocation;}
    public void setWarehouseLocation(String warehouseLocation) {this.warehouseLocation = warehouseLocation;}

    // simply returns an item and its attributes
    @Override
    public String toString() {
        return "\n\n\t Item{ " +
                "\n\t\titemID = " + itemID +
                "\n\t\titemName = " + itemName +
                "\n\t\titemQuantity = " + itemQuantity +
                "\n\t\titemPrice = " + itemPrice +
                "\n\t\titemWarehouseLocation = " + warehouseLocation +
                "\n\t}\n";
    }
}
