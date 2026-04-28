package edu.ics372;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public class OrderList {

    private LinkedList<Order> incomingOrders;
    private LinkedList<Order> startedOrders;
    private LinkedList<Order> completedOrders;

    private Map<String, Order> ordersById;
    private Map<String, Order> canceledOrders;

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
    public Order getOrderById(String id) {
        return ordersById.get(id);
    }
    public List<Order> getAllOrders() {
        return new ArrayList<>(ordersById.values());
    }
    public Collection<Order> getCanceledOrdersCollection() {
        return canceledOrders.values();
    }

    public void addIncomingOrder(Order order) {
        incomingOrders.add(order);
        ordersById.put(order.getOrderID(), order);
    }
    public void moveIncomingToStarted(Order order) {
        incomingOrders.remove(order);
        startedOrders.add(order);
    }

    public void moveStartedToCompleted(Order order) {
        startedOrders.remove(order);
        completedOrders.add(order);
    }

    public void moveToCanceled(String id, Order order) {
        incomingOrders.remove(order);
        startedOrders.remove(order);
        completedOrders.remove(order);

        canceledOrders.put(id, order);
    }

    public void removeCompletedOrdersAfterExport() {
        for (Order order : completedOrders) {
            ordersById.remove(order.getOrderID());
        }

        completedOrders.clear();
    }

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
