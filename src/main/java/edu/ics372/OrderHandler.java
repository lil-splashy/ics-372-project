package edu.ics372;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class OrderHandler {

    private final OrderRepository repo = new OrderRepository();
    private final OrderProcessor processor = new OrderProcessor();

    private ParserInterface parser = new Parser();
    private JsonParser jParser = new JsonParser();

    private static final String SAVE_FILE = "saved_orders.json";

    private final Warehouse mainWarehouse =
            new Warehouse("W001", "Main Warehouse", true, true);

    private final Warehouse bullseyeWarehouse =
            new Warehouse("W002", "Bullseye", true, false);

    private final Warehouse wallyworldWarehouse =
            new Warehouse("W003", "WallyWorld", false, true);

    public Warehouse getMainWarehouse() {
        return mainWarehouse;
    }
    public Warehouse getBullseyeWarehouse(){ return bullseyeWarehouse;}
    public Warehouse getWallyworldWarehouse() { return wallyworldWarehouse;}

    private Warehouse resolveWarehouse(Order order) {

        if (bullseyeWarehouse.canFulfill(order)) {
            return bullseyeWarehouse;
        }

        if (wallyworldWarehouse.canFulfill(order)) {
            return wallyworldWarehouse;
        }

        return mainWarehouse;
    }

    public LinkedList<Order> getIncomingOrders() {
        return new LinkedList<>(repo.incoming().getAll());
    }

    public LinkedList<Order> getStartedOrders() {
        return new LinkedList<>(repo.started().getAll());
    }

    public LinkedList<Order> getCompletedOrders() {
        return new LinkedList<>(repo.completed().getAll());
    }

    public Map<String, Order> getCanceledOrders() {
        return repo.getCanceledOrders();
    }


    public void addOrder(Order order) {
        repo.addOrder(order);
    }
    // Loads orders from a file path using the parser to detect format
    public void loadOrders(String filePath) {
        List<Order> orders = parser.parseFile(filePath);
        loadOrders(orders);
    }
    // Loads a list of already-parsed orders
    public void loadOrders(List<Order> orders) {

        if (orders == null || orders.isEmpty()) return;

        for (Order order : orders) {
            order.setWarehouse(mainWarehouse);
            repo.addOrder(order);
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

        if (!repo.started().isEmpty()) {
            System.out.println("Cannot start multiple orders.");
            return;
        }

        Order order = repo.getOrder(id);

        if (order == null) return;
        // Move order from incoming to started
        order.setOrderStatus(OrderStatus.STARTED);

        repo.incoming().remove(order);
        repo.started().add(order);
        // Submit the order to the executor for asynchronous processing
        // Processing can include tasks like updating inventory, notifications, or logging
        // This does NOT automatically complete the order
        processor.process(order);
    }


    public void completeOrder(String id) {

        Order order = repo.getOrder(id);

        if (order == null) return;

        order.setOrderStatus(OrderStatus.COMPLETED);

        repo.started().remove(order);
        repo.completed().add(order);
    }

    //method used to cancel an order and store in hashmap of canceled orders
    //do we want to be able to cancel any orders? even if completed but not shipped?
    public void cancelOrder(String id) {

        Order order = repo.getOrder(id);

        if (order == null) return;

        repo.incoming().remove(order);
        repo.started().remove(order);
        repo.completed().remove(order);

        order.setOrderStatus(OrderStatus.CANCELED);

        repo.addCanceled(order);
        Order.removeExistingOrder(id);
    }

    public Order getOrder(String id) {
        return repo.getOrder(id);
    }


    public void displayIncomingOrders() {
        for (Order o : repo.incoming().getAll()) System.out.println(o);
    }

    public void displayStartedOrders() {
        for (Order o : repo.started().getAll()) System.out.println(o);
    }

    public void displayCompletedOrders() {
        for (Order o : repo.completed().getAll()) System.out.println(o);
    }

    public void displayCanceledOrders() {
        for (Order o : repo.getCanceledOrders().values()) System.out.println(o);
    }
    // Display uncompleted orders
    public void displayUncompletedOrders() {
        // display incoming and started orders linked list
        // Call order method for price
        // getOrderPrice()
        // price total
        double total = 0;
        for (Order o : repo.incoming().getAll()) {
            System.out.println(o);
            total += o.getOrderPrice();
        }
        for (Order o : repo.started().getAll()) {
            System.out.println(o);
            total += o.getOrderPrice();
        }
        System.out.println("Total: " + total);
    }

    // Calculates total price of uncompleted orders
    public double totalPriceUncompletedOrders() {

        double total = 0;

        for (Order o : repo.incoming().getAll())
            total += o.getOrderPrice();

        for (Order o : repo.started().getAll())
            total += o.getOrderPrice();

        return total;
    }


    public void exportCompletedOrders(String extension) {

        if (!extension.equals(".json") && !extension.equals(".xml")) return;

        String filePath = "exports/completed_" + System.currentTimeMillis() + extension;

        parser.exportOrders(repo.completed().getAll(), filePath);

        for (Order o : repo.completed().getAll()) {
            repo.getOrdersById().remove(o.getOrderID());
        }

        repo.completed().clear();
    }

    /**
     * Saves all orders that have not already been exported or removed, usually before exiting the session
     *
     * @param filePath The directory in which the file containing the orders is located
     */
    public void saveData(String filePath) {
        List<Order> all = new ArrayList<>(repo.getOrdersById().values());
        jParser.exportOrders(all, SAVE_FILE);
    }

    /**
     * Restores previously saved program orders from a file and rebuilds
     * the in-memory tracking structures used by OrderHandler.
     *
     * @param filePath path to the saved program-orders file
     */
    public void importProgramOrders(String filePath) {
        //ask the parser to rebuild order objects from the save file
        List<Order> imported = jParser.importProgramOrders(SAVE_FILE);
        //stop if nothing was loaded from the file
        if (imported == null || imported.isEmpty()) return;

        //add each imported order back into the ordersbyidlist
        // also add them to the correct list based on their status
        for (Order order : imported) {
            repo.getOrdersById().put(order.getOrderID(), order);
            addOrderToCorrectList(order);

        }
    }

    /**
     * Places an imported order into the correct tracking structure
     * based on its saved status.
     *
     * @param order imported order to be restored into the proper list/map
     */
    private void addOrderToCorrectList(Order order) {

        order.setWarehouse(mainWarehouse);
        //restore the order to the matching status list
        switch (order.getOrderStatus()) {

            case INCOMING:
                repo.incoming().add(order);
                break;

            case STARTED:
                repo.started().add(order);
                break;

            case COMPLETED:
                repo.completed().add(order);
                break;

            case CANCELED:
                repo.addCanceled(order);
                break;
        }
    }


    /**
     * Gracefully shuts down the executor.
     * Stops accepting new tasks, but allows already submitted tasks to complete.
     */
    public void shutdown() {
        processor.shutdown();
    }

    /**
     * Immediately attempts to stop all running tasks in the executor.
     * Tasks that have not started may never run; running tasks are interrupted.
     */
    public void shutdownNow() {
        processor.shutdownNow();
    }

    /**
     * Waits for the executor to terminate after a shutdown request.
     *
     * @param timeoutSeconds maximum time to wait for termination in seconds
     * @return true if executor terminated successfully within the timeout, false otherwise
     */
    public boolean awaitTermination(long timeoutSeconds) {
        return processor.awaitTermination(timeoutSeconds);
    }
}