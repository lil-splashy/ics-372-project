package edu.ics372;

public class OrderMetrics {
    private int ordersCancelled = 0;
    private int ordersImported = 0;
    private int ordersExported = 0;
    private int ordersStarted = 0;
    private int ordersCompleted = 0;

    private static OrderMetrics staticInstance;

    /**
     *
     * @return
     */
    public static OrderMetrics getInstance()
    {
        if(staticInstance == null)
        { staticInstance = new OrderMetrics(); }

        return staticInstance;
    }

    //Adds to the cancelled variable after order is cancelled
    public void incrementCancelled() {
        ordersCancelled++;
    }

    //Adds to the imported variable after order is imported
    public void incrementImported() {
        ordersImported++;
    }

    //Adds to the started variable after order is started
    public void incrementStarted(){
        ordersStarted++;
    }

    public void incrementCompleted()
    { ordersCompleted++; }

    //Adds to the exported variable after order is exported
    public void addExported(int amountExported) {
        ordersExported += amountExported;
    }

    //getters for metric numbers
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

    public int getOrdersCompleted()
    { return ordersCompleted; }
}
