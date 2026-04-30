package edu.ics372;

import java.util.LinkedList;
import java.util.List;

import java.util.concurrent.ExecutorService; //
import java.util.concurrent.Executors;      // #
import java.util.concurrent.TimeUnit;

/**
 * Handles the main order-management logic for the application.
 * This class connects the order lists, metrics, parsers, warehouses,
 * order generation, and save/load behavior used by the UI.
 */
public class OrderHandler {
    // Stores incoming, started, completed, and canceled orders.
    private final OrderList orderList;

    // Tracks order counts used by the metrics view.
    private final OrderMetrics orderMetrics;

    // Runs background order-processing tasks one at a time.
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Default file used to save and restore program data.
    private static final String SAVE_FILE = "saved_orders.json";


    // Parser used for importing and exporting JSON or XML order files.
    private ParserInterface parser = new Parser();

    // JSON parser used specifically for saving and restoring program data.
    private JsonParser jParser = new JsonParser();

    // Warehouses currently available in the program.
    private final Warehouse mainWarehouse = new Warehouse("W001", "Main Warehouse");
    private final Warehouse bullseyeWarehouse = new Warehouse("W002", "Bullseye");
    private final Warehouse wallyworldWarehouse = new Warehouse("W003", "WallyWorld");

    // Generates sample orders in the background.
    private final RandomOrderGenerator orderGenerator;

    /**
     * Creates the order handler, initializes the order lists and metrics,
     * and starts the background random order generator.
     */
    public OrderHandler() {
        this.orderList = new OrderList();
        this.orderMetrics = OrderMetrics.getInstance();
        this.orderGenerator = new RandomOrderGenerator(this, mainWarehouse, 10, 60);
        this.orderGenerator.start();
    }
    /** Sets the callback invoked (on the generator thread) after each new order is added. */
    public void setOnOrderGenerated(Runnable callback) {
        orderGenerator.setOnOrderGenerated(callback);
    }
    //getters for warehouses
    public Warehouse getMainWarehouse() {
        return mainWarehouse;
    }

    public Warehouse getBullseyeWarehouse(){
        return bullseyeWarehouse;
    }

    public Warehouse getWallyworldWarehouse() {
        return wallyworldWarehouse;
    }

    //getters for the linked lists
    public LinkedList<Order> getIncomingOrders(){return orderList.getIncomingOrders();}
    public LinkedList<Order> getStartedOrders(){return orderList.getStartedOrders();}
    public LinkedList<Order> getCompletedOrders(){return orderList.getCompletedOrders();
    }

    public void addOrder(Order order) {
        orderList.addIncomingOrder(order);
        orderMetrics.incrementImported();
    }

    // Loads orders from a file path using the parser to detect format
    public void loadOrders(String filePath) {
        List<Order> orders = parser.parseFile(filePath);
        loadOrders(orders);
    }

    // Loads a list of already-parsed orders
    public void loadOrders(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            System.out.println("No orders loaded from file");
            return;
        }

        for (Order order : orders) {
            System.out.print("Order successfully loaded :D \n");
            order.setOrderStatus("incoming");
            order.setWarehouse(mainWarehouse);

            orderList.addIncomingOrder(order);

            orderMetrics.incrementImported();
        }
    }

    /**
     * Processes an order in the background.
     * Ensures only one instance of the order is processed at a time using OrderLock.
     * Note: If the program exits before this task finishes, the order remains in its
     * current status.
     *
     * @param order the Order object to process
     */
    private void processOrder(Order order) {
        String orderId = order.getOrderID();

        // Attempt to acquire a file-based lock to prevent duplicate processing across threads or systems
        if (!OrderLock.tryLock(orderId)) {
            System.out.println("Order already locked elsewhere: " + orderId);
            return;
        }
        try {
            System.out.println("Processing order: " + orderId);
        } finally {
            OrderLock.unlock(orderId); // Always release the lock
        }
    }

    /**
     * Starts an order by moving it from the incoming list to the started list.
     * Submits the order for background processing using the executor.
     * Note: The order status remains "started" until the user manually completes or cancels it.
     * The background processing task does not automatically change the status.
     *
     * @param id the ID of the order to start
     */
    public void startOrder(String id) {
        // Check if there is already an active started order
        if (!orderList.getStartedOrders().isEmpty()) {
            System.out.println("Cannot start a new order until the current started order is completed or canceled.");
            return; // block starting another order
        }
        Order order = orderList.getOrderById(id); // Look up the order by ID

        if (order == null) {
            System.out.println("No order associated with this id");
            return; // Exit if order not found
        }
        // Only allow starting orders that are still incoming
        if (!order.getOrderStatus().equals("incoming")) {
            System.out.println("Can't start an order that has already been started or completed");
            return;
        }
        // Move order from incoming to started
        order.setOrderStatus("started");
        orderList.moveIncomingToStarted(order);
        orderMetrics.incrementStarted();
        // Submit the order to the executor for asynchronous processing
        // Processing can include tasks like updating inventory, notifications, or logging
        // This does NOT automatically complete the order
        executor.execute(() -> processOrder(order));
    }



    /**
     * Cancels an order by moving it from its current list to the canceled orders collection.
     * Incoming, started, and completed orders can be canceled.
     *
     * @param id ID of the order being canceled
     */
    public void cancelOrder(String id) {
        Order canceledOrder = orderList.getOrderById(id);
        if(canceledOrder == null){
            System.out.println("No order associated with the provided id");
            return;
        }
        String orderStatus = canceledOrder.getOrderStatus();
        if (orderStatus == null){
            System.out.println("Order status is missing.");
            return;
        }

        switch(orderStatus){
            case "incoming":
            case "started":
            case "completed":
                orderList.moveToCanceled(id, canceledOrder);
                Order.removeExistingOrder(canceledOrder.getOrderID()); // remove from order tracking lists
                canceledOrder.setOrderStatus("canceled");
                orderMetrics.incrementCancelled();
                System.out.println("Order has been removed from " + orderStatus + " orders and added to canceled orders.");
                break;

            case "canceled":
                Order.removeExistingOrder(canceledOrder.getOrderID()); // remove from order tracking lists
                System.out.println("Order has already been canceled");
                break;

            default:
                System.out.println("order has not been fully processed or loaded.");
        }

    }

    /**
     * Completes a started order by moving it from the started list to the completed list.
     * Only orders with a status of "started" can be completed.
     *
     * @param id ID of the order being completed
     */
    public void completeOrder(String id) {
        Order order = orderList.getOrderById(id);
        if (order == null) {
            System.out.println("No order associated with this id");
            return;
        }
        if (order.getOrderStatus().equals("started")){
            order.setOrderStatus("completed");
            orderList.moveStartedToCompleted(order);
            Order.removeExistingOrder(order.getOrderID());
            orderMetrics.incrementCompleted();
        } else {
            System.out.println("Can't complete an order that hasn't been started yet.");
        }

    }

    /**
     * Exports all completed orders to either a JSON or XML file.
     * The exported file is saved in the exports folder with a timestamped file name.
     * After a successful export, completed orders are removed from the completed list.
     *
     * @param extension file extension for the export format, either ".json" or ".xml"
     */
    public void exportCompletedOrders(String extension){
        LinkedList<Order> completedOrders = orderList.getCompletedOrders();
        if(completedOrders == null || completedOrders.isEmpty()){
            System.out.println("No completed orders to export.");
            return;
        }
        if(!extension.equals(".json") && !extension.equals(".xml")){
            System.out.print("Use .json or .xml as the entension.");
            return;
        }

        String timeStamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        //add an export folder if one doesnt exist
        java.io.File exportDir = new java.io.File("exports");
        if (!exportDir.exists()){
            exportDir.mkdirs();
        }

        String filePath = "exports/completed_orders_" + timeStamp + extension;

        //grabs the amount of completed orders that will be exported
        int exportedNow = completedOrders.size();
        parser.exportOrders(completedOrders, filePath);
        //adds to the counter for metrics
        orderMetrics.addExported(exportedNow);

        System.out.println("Completed orders exported to: " + filePath);

        orderList.removeCompletedOrdersAfterExport();
    }


    /**
     * Finds and returns an order by its order ID.
     *
     * @param id ID of the order being searched for
     * @return the matching Order object, or null if no order is found
     */
    public Order getOrder(String id){
        Order order = orderList.getOrderById(id);
        if(order == null){
            System.out.println("No order associated with this id");
            return null;
        }
        return order;
    }


    /**
     * Displays all incoming and started orders in the console.
     * Also calculates and displays the total price of those uncompleted orders.
     */
    public void displayUncompletedOrders() {
        // display incoming and started orders linked list
        // Call order method for price
        // getOrderPrice()
        // price total
        LinkedList<Order> incomingOrders = orderList.getIncomingOrders();
        LinkedList<Order> startedOrders = orderList.getStartedOrders();

        double totalPriceUncompletedOrders = 0;
        System.out.println("Incoming Orders: ");
        for(Order order : incomingOrders){
            System.out.println(order);
            totalPriceUncompletedOrders += order.getOrderPrice();
        }

        System.out.println("Started Orders: ");
        for(Order order : startedOrders){
            System.out.println(order);
            totalPriceUncompletedOrders += order.getOrderPrice();
        }

        System.out.println("Total price: " + totalPriceUncompletedOrders);
    }

    // Displays incoming orders
    public void displayIncomingOrders() {
        LinkedList<Order> incomingOrders = orderList.getIncomingOrders();
        System.out.println("Incoming Orders: ");
        for (Order order : incomingOrders) {
            System.out.println(order);
        }
    }

    // Displays started orders
    public void displayStartedOrders() {
        LinkedList<Order> startedOrders = orderList.getStartedOrders();
        System.out.println("Started Orders: ");
        for (Order order : startedOrders) {
            System.out.println(order);
        }
    }

    // Displays completed orders
    public void displayCompletedOrders() {
        //display completedOrders linked list
        LinkedList<Order> completedOrders = orderList.getCompletedOrders();
        System.out.println("Completed Orders: ");
        for (Order order : completedOrders) {
            System.out.println(order);
        }
    }

    public void displayCanceledOrders(){
        System.out.println("Canceled Orders: ");
        for (Order order: orderList.getCanceledOrdersCollection()){
            System.out.println(order);
        }
    }

    // Calculates total price of uncompleted orders
    public double totalPriceUncompletedOrders(){
        LinkedList<Order> incomingOrders = orderList.getIncomingOrders();
        LinkedList<Order> startedOrders = orderList.getStartedOrders();

        double totalPrice = 0;

        for(Order order : incomingOrders){
            totalPrice += order.getOrderPrice();
        }

        for(Order order : startedOrders){
            totalPrice += order.getOrderPrice();
        }

        return totalPrice;
    }

    /**
     * Saves all orders that have not already been exported or removed, usually before exiting the session
     *
     * @param filePath The directory in which the file containing the orders is located
     */
    public void saveData(){
        List<Order> allOrders = orderList.getAllOrders();
        jParser.exportOrders(allOrders, SAVE_FILE);
        System.out.println("Program data saved to " + SAVE_FILE);
    }

    /**
     * Restores previously saved program orders from a file and rebuilds
     * the in-memory tracking structures used by OrderHandler.
     *
     * @param filePath path to the saved program-orders file
     */
    public void loadSavedData(){
        //ask the parser to rebuild order objects from the save file
        List<Order> importedOrders = jParser.importProgramOrders(SAVE_FILE);
        //stop if nothing was loaded from the file
        if(importedOrders == null || importedOrders.isEmpty()){
            System.out.println("No program orders were imported.");
            return;
        }

        //add each imported order back into the ordersbyidlist
        // also add them to the correct list based on their status
        for(Order order: importedOrders){
            order.setWarehouse(mainWarehouse);
            orderList.addOrderToCorrectList(order);
        }

        //confirm the amount of saved orders that were restored
        System.out.println(importedOrders.size() + " program orders imported successfully.");
    }

    /**
     * Gracefully shuts down the executor.
     * Stops accepting new tasks, but allows already submitted tasks to complete.
     */
    public void shutdown() { // #
        orderGenerator.stop();
        executor.shutdown();
    }
    /**
     * Immediately attempts to stop all running tasks in the executor.
     * Tasks that have not started may never run; running tasks are interrupted.
     */
    public void shutdownNow() { // #
        executor.shutdown();
    }

    /**
     * Waits for the executor to terminate after a shutdown request.
     *
     * @param timeoutSeconds maximum time to wait for termination in seconds
     * @return true if executor terminated successfully within the timeout, false otherwise
     */
    public boolean awaitTermination(long timeoutSeconds) {
        boolean terminated = false;
        try {
            // Wait for the executor to finish all running tasks or until timeout
            terminated = executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // If current thread is interrupted while waiting, restore interrupt status
            Thread.currentThread().interrupt(); // restore interrupt status
        }
        return terminated; // return whether executor terminated in time
    }
}