package edu.ics372;
public class Warehouse {

    private String warehouseID;
    private String warehouseName;

    public Warehouse(String warehouseID, String warehouseName) {
        this.warehouseID = warehouseID;
        this.warehouseName = warehouseName;

    }

    public String getWarehouseID() {
        return warehouseID;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    private void setWarehouseID(String warehouseID) {
        this.warehouseID = warehouseID;
    }
    private void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }




    @Override
    public String toString() {
        return "Warehouse{" +
                "warehouseID='" + warehouseID + '\'' +
                ", warehouseName='" + warehouseName + '\''  +
                '}';
    }
}