package edu.ics372;

import java.util.List;

// Abstraction for any order collection
public interface OrderList {

    void add(Order order);          // add an order
    void remove(Order order);       // remove an order
    List<Order> getAll();           // return all orders
    boolean isEmpty();              // check if empty
    void clear();                   // clear all orders
}

