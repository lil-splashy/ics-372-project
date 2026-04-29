package edu.ics372;

import java.util.LinkedList;
import java.util.List;

// Concrete implementation using LinkedList
public class LinkedListOrderList implements OrderList {

    private final List<Order> orders = new LinkedList<>();

    @Override
    public void add(Order order) {
        orders.add(order); // add order to list
    }

    @Override
    public void remove(Order order) {
        orders.remove(order); // remove order
    }

    @Override
    public List<Order> getAll() {
        return orders; // return underlying list
    }

    @Override
    public boolean isEmpty() {
        return orders.isEmpty(); // check emptiness
    }

    @Override
    public void clear() {
        orders.clear(); // clear all orders
    }
}