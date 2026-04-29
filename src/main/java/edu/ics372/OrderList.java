package edu.ics372;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

/**
 * Stores and organizes orders based on their current status.
 * This class keeps separate lists for incoming, started, and completed orders,
 * while also keeping maps for fast order lookup and canceled order tracking.
 */
public class OrderList {
    // Orders that have been imported but have not been started yet.
    private LinkedList<Order> incomingOrders;

    // Orders that are currently being processed.
    private LinkedList<Order> startedOrders;

    // Orders that have been completed and are ready to export.
    private LinkedList<Order> completedOrders;

    // Stores all active orders by their order ID for quick lookup.
    private Map<String, Order> ordersById;

    // Stores canceled orders by their order ID.
    private Map<String, Order> canceledOrders;

    /**
     * Creates empty collections for each order status.
     */
    public OrderList() {
        incomingOrders = new LinkedList<>();
        startedOrders = new LinkedList<>();
        completedOrders = new LinkedList<>();

        ordersById = new HashMap<>();
        canceledOrders = new HashMap<>();
    }

    public LinkedList<Order> getIncomingOrders() {
        return incomingOrders;
    }

    public LinkedList<Order> getStartedOrders() {
        return startedOrders;
    }

    public LinkedList<Order> getCompletedOrders() {
        return completedOrders;
    }

    public Map<String, Order> getOrdersById() {
        return ordersById;
    }

    public Map<String, Order> getCanceledOrders() {
        return canceledOrders;
    }

    /**
     * Finds an active order using its order ID.
     *
     * @param id order ID being searched for
     * @return matching Order object, or null if no order is found
     */
    public Order getOrderById(String id) {
        return ordersById.get(id);
    }

    /**
     * Returns all active orders in one list.
     * This is useful when saving the current program state.
     *
     * @return list of all active orders
     */
    public List<Order> getAllOrders() {
        return new ArrayList<>(ordersById.values());
    }

    /**
     * Returns all canceled orders as a collection.
     *
     * @return collection of canceled orders
     */
    public Collection<Order> getCanceledOrdersCollection() {
        return canceledOrders.values();
    }

    /**
     * Adds a new order to the incoming order list and stores it by ID.
     *
     * @param order order being added
     */
    public void addIncomingOrder(Order order) {
        incomingOrders.add(order);
        ordersById.put(order.getOrderID(), order);
    }

   //Moves an order from the incoming list to the started list
    public void moveIncomingToStarted(Order order) {
        incomingOrders.remove(order);
        startedOrders.add(order);
    }

    //Moves an order from the started list to completed list
    public void moveStartedToCompleted(Order order) {
        startedOrders.remove(order);
        completedOrders.add(order);
    }

    /**
     * Moves an order from its current active list into the canceled orders map.
     * The order is removed from incoming, started, and completed lists to make
     * sure it only exists in the canceled collection.
     *
     * @param id order ID of the canceled order
     * @param order order being canceled
     */
    public void moveToCanceled(String id, Order order) {
        incomingOrders.remove(order);
        startedOrders.remove(order);
        completedOrders.remove(order);

        canceledOrders.put(id, order);
    }

    /**
     * Removes completed orders from active tracking after they are exported.
     * This prevents already-exported completed orders from being saved or exported again.
     */
    public void removeCompletedOrdersAfterExport() {
        for (Order order : completedOrders) {
            ordersById.remove(order.getOrderID());
        }

        completedOrders.clear();
    }

    /**
     * Adds an imported or restored order to the correct collection based on its status.
     * This is mainly used when loading saved program data back into memory.
     *
     * @param order order being restored
     */
    public void addOrderToCorrectList(Order order) {
        ordersById.put(order.getOrderID(), order);

        String status = order.getOrderStatus();

        if (status == null) {
            System.out.println("Imported order is missing a status.");
            return;
        }

        switch (status) {
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
}
