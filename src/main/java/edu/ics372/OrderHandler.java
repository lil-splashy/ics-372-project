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
            new Warehouse("W001", "Main Warehouse");

    public Warehouse getMainWarehouse() {
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

    public void loadOrders(String filePath) {
        List<Order> orders = parser.parseFile(filePath);
        loadOrders(orders);
    }

    public void loadOrders(List<Order> orders) {

        if (orders == null || orders.isEmpty()) return;

        for (Order order : orders) {
            order.setWarehouse(mainWarehouse);
            repo.addOrder(order);
        }
    }

    public void startOrder(String id) {

        if (!repo.started().isEmpty()) {
            System.out.println("Cannot start multiple orders.");
            return;
        }

        Order order = repo.getOrder(id);

        if (order == null) return;

        order.setOrderStatus(OrderStatus.STARTED);

        repo.incoming().remove(order);
        repo.started().add(order);

        processor.process(order);
    }


    public void completeOrder(String id) {

        Order order = repo.getOrder(id);

        if (order == null) return;

        order.setOrderStatus(OrderStatus.COMPLETED);

        repo.started().remove(order);
        repo.completed().add(order);
    }


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

    public void displayUncompletedOrders() {

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

    public void saveData(String filePath) {

        List<Order> all = new ArrayList<>(repo.getOrdersById().values());

        jParser.exportOrders(all, SAVE_FILE);
    }

    public void importProgramOrders(String filePath) {

        List<Order> imported = jParser.importProgramOrders(SAVE_FILE);

        if (imported == null || imported.isEmpty()) return;

        for (Order order : imported) {

            repo.getOrdersById().put(order.getOrderID(), order);

            addOrderToCorrectList(order);
        }
    }

    private void addOrderToCorrectList(Order order) {

        order.setWarehouse(mainWarehouse);

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


    public void shutdown() {
        processor.shutdown();
    }

    public void shutdownNow() {
        processor.shutdownNow();
    }

    public boolean awaitTermination(long timeoutSeconds) {
        return processor.awaitTermination(timeoutSeconds);
    }
}