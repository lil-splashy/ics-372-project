package edu.ics372;

import java.util.LinkedList;

/**
 * Comprehensive test class for OrderHandler
 * Tests adding orders, starting, completing, and calculating total prices
 */
public class OrderHandlerTest {

    public static void main(String[] args) {
        System.out.println("=== Starting OrderHandler Tests ===\n");

        OrderHandlerTest tester = new OrderHandlerTest();

        tester.testAddOrders();
        tester.testStartOrder();
        tester.testCompleteOrder();
        tester.testDisplayOrders();
        tester.testTotalPrice();

        System.out.println("\n=== All OrderHandler Tests Completed ===");
    }

    // Instance of OrderHandler for all tests
    private OrderHandler handler;

    // Sample orders
    private Order order1;
    private Order order2;
    private Order order3;

    public OrderHandlerTest() {
        handler = new OrderHandler();

        // Create sample orders
        order1 = new Order(1L, "Alice", "Pizza", 15); // id, customerName, itemName, price
        order2 = new Order(2L, "Bob", "Burger", 10);
        order3 = new Order(3L, "Charlie", "Salad", 12);

        // Initially set status to "incoming"
        order1.setOrderStatus("incoming");
        order2.setOrderStatus("incoming");
        order3.setOrderStatus("incoming");
    }

    public void testAddOrders() {
        System.out.println("Test 1: Adding orders to OrderHandler");

        // Add orders to incomingOrders and map
        addOrderToHandler(order1);
        addOrderToHandler(order2);
        addOrderToHandler(order3);

        System.out.println("  ✓ Orders added successfully\n");
    }

    private void addOrderToHandler(Order order) {
        handler.getIncomingOrders().add(order);
        handler.ordersById.put(order.getOrderID(), order);
    }

    public void testStartOrder() {
        System.out.println("Test 2: Starting an order");

        handler.startOrder(order1.getOrderID());

        assertStatus(order1, "started", "Order 1 should be started");
        assertListContains(handler.getStartedOrders(), order1, "Started orders should contain Order 1");
        assertListNotContains(handler.getIncomingOrders(), order1, "Incoming orders should not contain Order 1");

        System.out.println("  ✓ Starting order works\n");
    }

    public void testCompleteOrder() {
        System.out.println("Test 3: Completing an order");

        handler.completeOrder(order1.getOrderID());

        assertStatus(order1, "completed", "Order 1 should be completed");
        assertListContains(handler.getCompletedOrders(), order1, "Completed orders should contain Order 1");
        assertListNotContains(handler.getStartedOrders(), order1, "Started orders should not contain Order 1");

        System.out.println("  ✓ Completing order works\n");
    }

    public void testDisplayOrders() {
        System.out.println("Test 4: Displaying orders");

        System.out.println("\n--- Uncompleted Orders ---");
        handler.displayUncompletedOrders();

        System.out.println("\n--- Completed Orders ---");
        handler.displayCompletedOrders();

        System.out.println("  ✓ Display methods executed\n");
    }

    public void testTotalPrice() {
        System.out.println("Test 5: Calculating total price of uncompleted orders");

        double total = handler.totalPriceUncompletedOrders(); // order2 + order3 = 10 + 12
        if (total == 22) {
            System.out.println("  ✓ Total price of uncompleted orders is correct: " + total + "\n");
        } else {
            System.out.println("  ✗ Total price incorrect. Expected 22, Got: " + total + "\n");
        }
    }

    // Helper methods for assertions
    private void assertStatus(Order order, String expectedStatus, String message) {
        if (!order.getOrderStatus().equals(expectedStatus)) {
            System.out.println("  ✗ " + message + " - Expected: " + expectedStatus + ", Got: " + order.getOrderStatus());
        }
    }

    private void assertListContains(LinkedList<Order> list, Order order, String message) {
        if (!list.contains(order)) {
            System.out.println("  ✗ " + message);
        }
    }

    private void assertListNotContains(LinkedList<Order> list, Order order, String message) {
        if (list.contains(order)) {
            System.out.println("  ✗ " + message);
        }
    }
}
