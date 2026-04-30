package edu.ics372;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive test class for OrderHandler
 * Tests adding orders, starting, completing, and calculating total prices
 */
public class OrderHandlerTest {

    public static void main(String[] args) {
        System.out.println("=== Starting OrderHandler Tests ===\n");

        OrderHandlerTest tester = new OrderHandlerTest();

        tester.testAddOrders();

        tester.resetHandlerAndOrders();
        tester.testStartOrder();

        tester.resetHandlerAndOrders();
        tester.testCompleteOrder();

        tester.resetHandlerAndOrders();
        tester.testDisplayOrders();

        tester.resetHandlerAndOrders();
        tester.testTotalPrice();

        tester.resetHandlerAndOrders();
        tester.testLoadOrders();

        tester.resetHandlerAndOrders();
        tester.testCancelIncomingOrder();

        tester.resetHandlerAndOrders();
        tester.testCancelStartOrder();

        tester.resetHandlerAndOrders();
        tester.testGetOrder();

        System.out.println("\n=== All OrderHandler Tests Completed ===");
    }

    // Instance of OrderHandler for all tests
    private OrderHandler handler;

    // Sample orders
    private Order order1;
    private Order order2;
    private Order order3;

    public OrderHandlerTest() {
       resetHandlerAndOrders();
    }

    private void resetHandlerAndOrders(){
        handler = new OrderHandler();

        // Create sample orders
        order1 = new Order.Builder().setSourcePrefix("J").setOrderDate(System.currentTimeMillis()).setOrderStatus("incoming").setOrderType("Online").setMaxItems(3).setWarehouse(null).build();
        order2 = new Order.Builder().setSourcePrefix("J").setOrderDate(System.currentTimeMillis()).setOrderStatus("incoming").setOrderType("Pickup").setMaxItems(3).setWarehouse(null).build();
        order3 = new Order.Builder().setSourcePrefix("J").setOrderDate(System.currentTimeMillis()).setOrderStatus("incoming").setOrderType("Delivery").setMaxItems(3).setWarehouse(null).build();

        //adding items so orderPrice is tested correctly
        order1.addItem(new Item("I1", "Keyboard", 50.00,1,null));
        order1.addItem(new Item("I2", "Mouse", 25.00,1,null));
        //Total should be $75

        order2.addItem(new Item("I3", "Monitor", 200.00, 1, null));
        //total 200

        order3.addItem(new Item("I4", "Desk", 150.00, 1, null));
        order3.addItem(new Item("I5", "Chair", 100.00, 2, null));



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

        assertListContains(handler.getIncomingOrders(), order1, "Incoming orders should contain Order 1");
        assertListContains(handler.getIncomingOrders(), order2, "Incoming orders should contain Order 2");
        assertListContains(handler.getIncomingOrders(), order3, "Incoming orders should contain Order 3");

        System.out.println("  ✓ Orders added successfully\n");
    }

    private void addOrderToHandler(Order order) {
        handler.addOrder(order);
    }

    public void testStartOrder() {
        System.out.println("Test 2: Starting an order");

        addOrderToHandler(order1);

        handler.startOrder(order1.getOrderID());

        assertStatus(order1, "started", "Order 1 should be started");
        assertListContains(handler.getStartedOrders(), order1, "Started orders should contain Order 1");
        assertListNotContains(handler.getIncomingOrders(), order1, "Incoming orders should not contain Order 1");

        System.out.println("  ✓ Starting order works\n");
    }

    public void testCompleteOrder() {
        System.out.println("Test 3: Completing an order");

        addOrderToHandler(order1);
        handler.startOrder(order1.getOrderID());
        handler.completeOrder(order1.getOrderID());

        assertStatus(order1, "completed", "Order 1 should be completed");
        assertListContains(handler.getCompletedOrders(), order1, "Completed orders should contain Order 1");
        assertListNotContains(handler.getStartedOrders(), order1, "Started orders should not contain Order 1");

        System.out.println("  ✓ Completing order works\n");
    }

    public void testDisplayOrders() {
        System.out.println("Test 4: Displaying orders");

        addOrderToHandler(order1);
        addOrderToHandler(order2);
        System.out.println("\n--- Uncompleted Orders ---");
        handler.displayUncompletedOrders();

        System.out.println("\n--- Completed Orders ---");
        handler.displayCompletedOrders();

        System.out.println("  ✓ Display methods executed\n");
    }

    public void testTotalPrice() {
        System.out.println("Test 5: Calculating total price of uncompleted orders");

        addOrderToHandler(order1);
        addOrderToHandler(order2);
        addOrderToHandler(order3);

        double total = handler.totalPriceUncompletedOrders(); // should == 525.00
        if (total == 525.00) {
            System.out.println("  ✓ Total price of uncompleted orders is correct: " + total + "\n");
        } else {
            System.out.println("  ✗ Total price incorrect. Expected 22, Got: " + total + "\n");
        }
    }

    public void testLoadOrders(){
        System.out.println("Test 6: Loading orders from a List<order>");
        List<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        orders.add(order3);

        handler.loadOrders(orders);

        assertListContains(handler.getIncomingOrders(), order1, "Incoming orders should contain loaded Order 1");
        assertListContains(handler.getIncomingOrders(), order2, "Incoming orders should contain loaded Order 2");
        assertListContains(handler.getIncomingOrders(), order3, "Incoming orders should contain loaded Order 3");

        System.out.println("loadOrders(List<Order> works");
    }

    public void testCancelIncomingOrder(){
        System.out.println("Test 7: Canceling an incoming order");

        addOrderToHandler(order1);

        handler.cancelOrder(order1.getOrderID());

        assertStatus(order1, "canceled", "Order 1 should be canceled");
        assertListNotContains(handler.getIncomingOrders(), order1, "Incoming orders should not contain canceled Order 1");

        System.out.println("cancelOrder() works for incoming order\n");
    }

    public void testCancelStartOrder(){
        System.out.println("Test 8: Canceling a started order");

        addOrderToHandler(order1);
        handler.startOrder(order1.getOrderID());
        handler.cancelOrder(order1.getOrderID());

        assertStatus(order1, "canceled", "Started order should become canceled");
        assertListNotContains(handler.getStartedOrders(), order1, "Started orders should not contain canceled order");

        System.out.println("Cancel order works in started orders");

    }

    public void testGetOrder(){
        System.out.println("Test 9: Getting an order by ID");

        addOrderToHandler(order2);

        Order foundOrder = handler.getOrder(order2.getOrderID());

        if(foundOrder == order2){
            System.out.println("Get order returned correct");
        }
        else{
            System.out.println("Error");
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
