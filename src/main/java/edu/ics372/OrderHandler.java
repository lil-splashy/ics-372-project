package edu.ics372;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ExecutorService; //
import java.util.concurrent.Executors;      // #
import java.util.concurrent.TimeUnit;

// "E" should be Orders when created
public class OrderHandler {
    //Instance variables to keep orders based on status
    private LinkedList<Order> incomingOrders;
    private LinkedList<Order> startedOrders;
    private LinkedList<Order> completedOrders;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private int ordersCancelled = 0;
    private int ordersImported = 0;
    private int ordersExported = 0;// #

    //variable for file name for saved program orders
    private static final String SAVE_FILE = "saved_orders.json";

    //Using map to associate orders with an id per instruction 3
    private Map<String,Order> ordersById;

    //Using map to store canceled orders
    private Map<String, Order> canceledOrders;

    //Calling parserInterface to have methods that can load and save data using the parser class
    private ParserInterface parser = new Parser();
    private JsonParser jParser = new JsonParser();

    // single warehouse for the program
    private final Warehouse mainWarehouse = new Warehouse("W001", "Main Warehouse");
    private final Warehouse bullseyeWarehouse = new Warehouse("W002", "Bullseye");
    private final Warehouse wallyworldWarehouse = new Warehouse("W003", "WallyWorld");

    private final RandomOrderGenerator orderGenerator;

    //Constructor creates linked list depending on status and a map for associating orders with their ID
    public OrderHandler() {
        this.incomingOrders = new LinkedList<>();
        this.startedOrders = new LinkedList<>();
        this.completedOrders = new LinkedList<>();
        //used for look up.
        this.ordersById = new HashMap<>();
        //used to record canceled orders
        this.canceledOrders = new HashMap<>();

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

    //getters for order metrics
    public int getOrdersCancelled(){
        return ordersCancelled;
    }

    public int getOrdersImported(){
        return ordersImported;
    }

    public int getOrdersExported(){
        return ordersExported;
    }

    //getters for the linked lists
    public LinkedList<Order> getIncomingOrders(){
        return incomingOrders;
    }
    public LinkedList<Order> getStartedOrders(){
        return startedOrders;
    }
    public LinkedList<Order> getCompletedOrders(){
        return completedOrders;
    }

    public void addOrder(Order order) {
        incomingOrders.add(order);
        ordersById.put(order.getOrderID(), order);
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
            incomingOrders.add(order);
            ordersById.put(order.getOrderID(), order);
            ordersImported++;
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
        if (!startedOrders.isEmpty()) {
            System.out.println("Cannot start a new order until the current started order is completed or canceled.");
            return; // block starting another order
        }
        Order order = ordersById.get(id); // Look up the order by ID

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
        startedOrders.add(order);
        incomingOrders.remove(order);
        // Submit the order to the executor for asynchronous processing
        // Processing can include tasks like updating inventory, notifications, or logging
        // This does NOT automatically complete the order
        executor.execute(() -> processOrder(order));
    }



    //method used to cancel an order and store in hashmap of canceled orders
    //do we want to be able to cancel any orders? even if completed but not shipped?
    public void cancelOrder(String id) {
        if(ordersById.get(id) == null){
            System.out.println("No order associated with the provided id");
            return;
        }
        Order canceledOrder = ordersById.get(id);
        String orderStatus = canceledOrder.getOrderStatus();
        if (orderStatus == null){
            System.out.println("Order status is missing.");
            return;
        }

        switch(orderStatus){
            case "incoming":
                canceledOrders.put(id,canceledOrder);
                incomingOrders.remove(canceledOrder);
                Order.removeExistingOrder(canceledOrder.getOrderID());// remove from order tracking lists
                canceledOrder.setOrderStatus("canceled");
                ordersCancelled++;
                System.out.println("Order has been removed from incoming orders and added to canceled orders.");
                break;
            case "started":
                canceledOrders.put(id,canceledOrder);
                startedOrders.remove(canceledOrder);
                Order.removeExistingOrder(canceledOrder.getOrderID());// remove from order tracking lists
                canceledOrder.setOrderStatus("canceled");
                ordersCancelled++;
                System.out.println("Order has been removed from started orders and added to canceled orders.");
                break;
            case "completed":
                canceledOrders.put(id,canceledOrder);
                completedOrders.remove(canceledOrder);
                Order.removeExistingOrder(canceledOrder.getOrderID()); // remove from order tracking lists
                canceledOrder.setOrderStatus("canceled");
                ordersCancelled++;
                System.out.println("Order has been removed from completed orders and added to canceled orders.");
                break;
            case "canceled":
                Order.removeExistingOrder(canceledOrder.getOrderID()); // remove from order tracking lists
                System.out.println("Order has already been canceled");
                break;
            default:
                System.out.println("order has not been fully processed or loaded.");
        }

    }

    // When prompted by user interface move started order to completed linked list
    public void completeOrder(String id) {
        if (ordersById.get(id) == null) {
            System.out.println("No order associated with this id");
            return;
        }
        if (ordersById.get(id).getOrderStatus().equals("started")){
            Order order = ordersById.get(id);
            order.setOrderStatus("completed");
            completedOrders.add(order);
            startedOrders.remove(order);
            Order.removeExistingOrder(order.getOrderID());
        } else {
            System.out.println("Can't complete an order that hasn't been started yet.");
        }

    }

    public void exportCompletedOrders(String extension){
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
        ordersExported += exportedNow;

        System.out.println("Completed orders ecported to: " + filePath);

        for(Order order: completedOrders){
            ordersById.remove(order.getOrderID());
        }

        completedOrders.clear();
    }


    //going to be used to grab an order by its order id using hashmap;
    public Order getOrder(String id){
        if(ordersById.get(id) == null){
            System.out.println("No order associated with this id");
            return null;
        }
        return ordersById.get(id);
    }


    // Display uncompleted orders
    public void displayUncompletedOrders() {
        // display incoming and started orders linked list
        // Call order method for price
        // getOrderPrice()
        // price total
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
        System.out.println("Incoming Orders: ");
        for (Order order : incomingOrders) {
            System.out.println(order);
        }
    }

    // Displays started orders
    public void displayStartedOrders() {
        System.out.println("Started Orders: ");
        for (Order order : startedOrders) {
            System.out.println(order);
        }
    }

    // Displays completed orders
    public void displayCompletedOrders() {
        //display completedOrders linked list
        System.out.println("Completed Orders: ");
        for (Order order : completedOrders) {
            System.out.println(order);
        }
    }

    public void displayCanceledOrders(){
        System.out.println("Canceled Orders: ");
        for (Order order: canceledOrders.values()){
            System.out.println(order);
        }
    }

    // Calculates total price of uncompleted orders
    public double totalPriceUncompletedOrders(){
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
    public void saveData(String filePath){
        List<Order> allOrders = new ArrayList<>(ordersById.values());
        jParser.exportOrders(allOrders, SAVE_FILE);
        System.out.println("Program data saved to " + SAVE_FILE);
    }

    /**
     * Restores previously saved program orders from a file and rebuilds
     * the in-memory tracking structures used by OrderHandler.
     *
     * @param filePath path to the saved program-orders file
     */
    public void importProgramOrders(String filePath){
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
            ordersById.put(order.getOrderID(), order);
            addOrderToCorrectList(order);
        }

        //confirm the amount of saved orders that were restored
        System.out.println(importedOrders.size() + " program orders imported successfully.");
    }


    /**
     * Places an imported order into the correct tracking structure
     * based on its saved status.
     *
     * @param order imported order to be restored into the proper list/map
     */
    private void addOrderToCorrectList(Order order){
        order.setWarehouse(mainWarehouse);
        //read the saved status of order so it can be put in the correct list
        String status = order.getOrderStatus();

        // making sure there is a status for the imported order
        if (status == null){
            System.out.println("Imported order is missing a status.");
            return;
        }

        //restore the order to the matchig status list
        switch(status){
            case "incoming":
                incomingOrders.add(order);
                break;
            case "started":
                startedOrders.add(order);
                break;
            case "completed":
                completedOrders.add(order);
                break;
            case "canceled":
                canceledOrders.put(order.getOrderID(), order);
                break;
            default:
                System.out.println("Unknown order status: " + status);
        }
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