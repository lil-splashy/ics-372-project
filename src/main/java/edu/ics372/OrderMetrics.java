package edu.ics372;

public class OrderMetrics {
    private int ordersCancelled = 0;
    private int ordersImported = 0;
    private int ordersExported = 0;
    private int ordersStarted = 0;

    public void incrementCancelled() {
        ordersCancelled++;
    }

    public void incrementImported() {
        ordersImported++;
    }

    public void incrementStarted(){
        ordersStarted++;
    }

    public void addExported(int amountExported) {
        ordersExported += amountExported;
    }

    public int getOrdersCancelled() {
        return ordersCancelled;
    }

    public int getOrdersImported() {
        return ordersImported;
    }

    public int getOrdersExported() {
        return ordersExported;
    }

    public int getOrdersStarted(){
        return ordersStarted;
    }
}
