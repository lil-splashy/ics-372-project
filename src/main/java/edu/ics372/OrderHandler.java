package edu.ics372;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the main order-management logic for the application.
 */
public class OrderHandler {

    private final OrderRepository repository;
    private final OrderMetrics orderMetrics;
    private final OrderProcessor processor;

    private static final String SAVE_FILE = "saved_orders.json";

    private final ParserInterface parser = new Parser();
    private final JsonParser jParser = new JsonParser();

    private final Warehouse mainWarehouse      = new Warehouse("W001", "Main Warehouse", true, true);
    private final Warehouse bullseyeWarehouse  = new Warehouse("W002", "Bullseye",       true, false);
    private final Warehouse wallyworldWarehouse= new Warehouse("W003", "WallyWorld",     false, true);

    private final RandomOrderGenerator orderGenerator;

    public OrderHandler() {
        this.repository   = new OrderRepository();
        this.orderMetrics = OrderMetrics.getInstance();
        this.processor    = new OrderProcessor();
        this.orderGenerator = new RandomOrderGenerator(this, mainWarehouse, 10, 60);
        this.orderGenerator.start();
    }

    public void setOnOrderGenerated(Runnable callback) {
        orderGenerator.setOnOrderGenerated(callback);
    }

    // ── Warehouse getters ─────────────────────────────────────────────────────
    public Warehouse getMainWarehouse()       { return mainWarehouse; }
    public Warehouse getBullseyeWarehouse()   { return bullseyeWarehouse; }
    public Warehouse getWallyworldWarehouse() { return wallyworldWarehouse; }

    // ── Order list getters ────────────────────────────────────────────────────
    public List<Order> getIncomingOrders() { return repository.incoming().getAll(); }
    public List<Order> getStartedOrders()  { return repository.started().getAll(); }
    public List<Order> getCompletedOrders(){ return repository.completed().getAll(); }

    // ── Add / Load ────────────────────────────────────────────────────────────
    public void addOrder(Order order) {
        repository.addOrder(order);
        orderMetrics.incrementImported();
        SessionAnalytics.getInstance().addRecord(new Record(order));
    }

    public void loadOrders(String filePath) {
        loadOrders(parser.parseFile(filePath));
    }

    public void loadOrders(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            System.out.println("No orders loaded from file");
            return;
        }
        for (Order order : orders) {
            order.setOrderStatus(OrderStatus.INCOMING);
            order.setWarehouse(mainWarehouse);
            repository.addOrder(order);
            orderMetrics.incrementImported();
            SessionAnalytics.getInstance().addRecord(new Record(order));
        }
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    public void startOrder(String id) {
        if (!repository.started().isEmpty()) {
            System.out.println("Cannot start a new order until the current started order is completed or canceled.");
            return;
        }
        Order order = repository.getOrder(id);
        if (order == null) { System.out.println("No order associated with this id"); return; }
        if (order.getOrderStatus() != OrderStatus.INCOMING) {
            System.out.println("Can't start an order that has already been started or completed");
            return;
        }
        order.setOrderStatus(OrderStatus.STARTED);
        repository.incoming().remove(order);
        repository.started().add(order);
        orderMetrics.incrementStarted();
        Record startRecord = SessionAnalytics.getInstance().getRecord(order.getOrderID());
        if (startRecord != null) startRecord.setStartTime(System.currentTimeMillis());
        processor.process(order);
    }

    public void cancelOrder(String id) {
        Order order = repository.getOrder(id);
        if (order == null) { System.out.println("No order associated with the provided id"); return; }
        OrderStatus status = order.getOrderStatus();
        switch (status) {
            case INCOMING  -> repository.incoming().remove(order);
            case STARTED   -> repository.started().remove(order);
            case COMPLETED -> repository.completed().remove(order);
            case CANCELED  -> { System.out.println("Order has already been canceled"); return; }
            default        -> { System.out.println("Order has not been fully processed or loaded."); return; }
        }
        repository.addCanceled(order);
        Order.removeExistingOrder(order.getOrderID());
        order.setOrderStatus(OrderStatus.CANCELED);
        orderMetrics.incrementCancelled();
        System.out.println("Order cancelled: " + id);
    }

    public void completeOrder(String id) {
        Order order = repository.getOrder(id);
        if (order == null) { System.out.println("No order associated with this id"); return; }
        if (order.getOrderStatus() != OrderStatus.STARTED) {
            System.out.println("Can't complete an order that hasn't been started yet.");
            return;
        }
        order.setOrderStatus(OrderStatus.COMPLETED);
        repository.started().remove(order);
        repository.completed().add(order);
        Order.removeExistingOrder(order.getOrderID());
        orderMetrics.incrementCompleted();
        Record endRecord = SessionAnalytics.getInstance().getRecord(order.getOrderID());
        if (endRecord != null) endRecord.setEndTime(System.currentTimeMillis());
    }

    // ── Export / Save / Load ──────────────────────────────────────────────────
    public void exportCompletedOrders(String extension) {
        List<Order> completed = repository.completed().getAll();
        if (completed.isEmpty()) { System.out.println("No completed orders to export."); return; }
        if (!extension.equals(".json") && !extension.equals(".xml")) {
            System.out.println("Use .json or .xml as the extension.");
            return;
        }
        String timeStamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        java.io.File exportDir = new java.io.File("exports");
        if (!exportDir.exists()) exportDir.mkdirs();
        String filePath = "exports/completed_orders_" + timeStamp + extension;
        parser.exportOrders(completed, filePath);
        orderMetrics.addExported(completed.size());
        System.out.println("Completed orders exported to: " + filePath);
        repository.completed().clear();
    }

    public void saveData() {
        List<Order> all = new ArrayList<>();
        all.addAll(repository.incoming().getAll());
        all.addAll(repository.started().getAll());
        all.addAll(repository.completed().getAll());
        jParser.exportOrders(all, SAVE_FILE);
        System.out.println("Program data saved to " + SAVE_FILE);
    }

    public void loadSavedData() {
        List<Order> imported = jParser.importProgramOrders(SAVE_FILE);
        if (imported == null || imported.isEmpty()) {
            System.out.println("No program orders were imported.");
            return;
        }
        for (Order order : imported) {
            order.setWarehouse(mainWarehouse);
            repository.getOrdersById().put(order.getOrderID(), order);
            OrderStatus status = order.getOrderStatus();
            if (status == OrderStatus.STARTED)        repository.started().add(order);
            else if (status == OrderStatus.COMPLETED) repository.completed().add(order);
            else                                      repository.incoming().add(order);
        }
        System.out.println(imported.size() + " program orders imported successfully.");
    }

    public Order getOrder(String id) { return repository.getOrder(id); }

    // ── Shutdown ──────────────────────────────────────────────────────────────
    public void shutdown()    { orderGenerator.stop(); processor.shutdown(); }
    public void shutdownNow() { processor.shutdownNow(); }
    public boolean awaitTermination(long timeoutSeconds) { return processor.awaitTermination(timeoutSeconds); }

    // ── Console display helpers ───────────────────────────────────────────────
    public void displayUncompletedOrders() {
        double total = 0;
        System.out.println("Incoming Orders:");
        for (Order o : repository.incoming().getAll()) { System.out.println(o); total += o.getOrderPrice(); }
        System.out.println("Started Orders:");
        for (Order o : repository.started().getAll())  { System.out.println(o); total += o.getOrderPrice(); }
        System.out.println("Total price: " + total);
    }

    public void displayIncomingOrders()  { System.out.println("Incoming:"); repository.incoming().getAll().forEach(System.out::println); }
    public void displayStartedOrders()   { System.out.println("Started:");  repository.started().getAll().forEach(System.out::println); }
    public void displayCompletedOrders() { System.out.println("Completed:");repository.completed().getAll().forEach(System.out::println); }
    public void displayCanceledOrders()  { System.out.println("Canceled:"); repository.getCanceledOrders().values().forEach(System.out::println); }

    public double totalPriceUncompletedOrders() {
        double total = 0;
        for (Order o : repository.incoming().getAll()) total += o.getOrderPrice();
        for (Order o : repository.started().getAll())  total += o.getOrderPrice();
        return total;
    }
}