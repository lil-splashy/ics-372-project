package edu.ics372;

import java.util.*;

/**
 * Acts as the centralized in-memory storage layer for all Order objects,
 * maintaining lookup maps and status-based collections (incoming, started, completed, canceled)
 * so that other classes (like OrderHandler and OrderProcessor) do not directly manage data structures.
 */
public class OrderRepository {

    // ===== MAP STORAGE =====
    private final Map<String, Order> ordersById = new HashMap<>();
    private final Map<String, Order> canceledOrders = new HashMap<>();

    // ===== LIST STORAGE =====
    private final OrderList incoming = new LinkedListOrderList();
    private final OrderList started = new LinkedListOrderList();
    private final OrderList completed = new LinkedListOrderList();

    // ===== CORE STORAGE METHODS =====
    public void addOrder(Order order) {
        ordersById.put(order.getOrderID(), order);
        incoming.add(order);
    }

    public Order getOrder(String id) {
        return ordersById.get(id);
    }

    public Map<String, Order> getOrdersById() {
        return ordersById;
    }

    public Map<String, Order> getCanceledOrders() {
        return canceledOrders;
    }

    // ===== LIST ACCESS  =====
    public OrderList incoming() { return incoming; }
    public OrderList started() { return started; }
    public OrderList completed() { return completed; }

    public void addCanceled(Order order) {
        canceledOrders.put(order.getOrderID(), order);
    }
}