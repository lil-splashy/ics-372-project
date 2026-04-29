package edu.ics372;

// Represents a fulfillment center that owns orders and determines whether it can process them,
// including tracking incoming, active, and completed orders assigned to this warehouse.
public class Warehouse {

    private String warehouseID;
    private String warehouseName;

    private final OrderList incoming = new LinkedListOrderList();
    private final OrderList started = new LinkedListOrderList();
    private final OrderList completed = new LinkedListOrderList();

    public Warehouse(String warehouseID, String warehouseName) {
        this.warehouseID = warehouseID;
        this.warehouseName = warehouseName;
    }

    // ===== CORE IDENTITY =====
    public String getWarehouseID() { return warehouseID; }
    public String getWarehouseName() { return warehouseName; }

    // ===== ORDER OWNERSHIP =====
    public OrderList incoming() { return incoming; }
    public OrderList started() { return started; }
    public OrderList completed() { return completed; }

    // ===== PHASE 2 REQUIREMENT HOOK =====
    // Determines whether this warehouse can fulfill the order
    public boolean canFulfill(Order order) {
        // placeholder rule (you define real logic)
        // ex: based on item type, shipping type, region, etc.
        return true;
    }

    // ===== ORDER ROUTING =====
    public void acceptOrder(Order order) {
        incoming.add(order);
        order.setWarehouse(this);
    }

    public void startOrder(Order order) {
        incoming.remove(order);
        started.add(order);
    }

    public void completeOrder(Order order) {
        started.remove(order);
        completed.add(order);
    }

    public void cancelOrder(Order order) {
        incoming.remove(order);
        started.remove(order);
        completed.remove(order);
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "warehouseID ='" + warehouseID + '\'' +
                ", warehouseName ='" + warehouseName + '\'' +
                '}';
    }
}