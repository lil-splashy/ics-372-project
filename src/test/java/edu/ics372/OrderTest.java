package edu.ics372;

import org.junit.jupiter.api.Test;

/**
 * Manual Unit Test for the Order class
 * Includes toString() tests for both constructors.
 */

public class OrderTest {

    public static void main(String[] args) {
        System.out.println("=== Starting Order Tests ===\n");

        OrderTest tester = new OrderTest();

        tester.testConstructorAndGetters();       // tests first constructor
        tester.testConstructorWithExistingID();   // tests second constructor
        tester.testSetters();
        tester.testAddItem();
        tester.testToStringGeneratedID();         // toString for generated ID constructor
        tester.testToStringExistingID();          // toString for pre-existing ID constructor

        System.out.println("\n=== All Order Tests Completed ===");
    }

    // Test the constructor that generates a new orderID
    @Test
    public void testConstructorAndGetters() {
        System.out.println("Test 1: Constructor and Getters (new orderID)");
        try {
            Warehouse wh = new Warehouse("W1", "Test1 Warehouse");
            Order order = new Order(1672531200L, "Pending", "Online", 5, wh);

            boolean success = order.getOrderStatus().equals("Pending") &&
                              order.getOrderType().equals("Online") &&
                              order.getOrderDate() == 1672531200L &&
                              order.getWarehouse().equals(wh) &&
                              order.getOrderPrice() == 0 &&
                              order.getOrderID().startsWith("J"); // auto-generated

            System.out.println(success ? " SUCCESS\n" : " FAILURE\n");

        } catch (Exception e) {
            System.out.println(" *** Exception occurred: " + e.getMessage() + " ***\n");
        }
    }

    // Test the constructor that accepts a pre-existing orderID
    @Test
    public void testConstructorWithExistingID() {
        System.out.println("Test 2: Constructor with existing orderID");
        try {
            Warehouse wh = new Warehouse("W1", "Test1 Warehouse");
            String existingID = "123456789012";
            Order order = new Order(existingID, 1672531200L, "Pending", "Online", 5, wh);

            boolean success = order.getOrderID().equals("X" + existingID) &&
                              order.getOrderStatus().equals("Pending") &&
                              order.getOrderType().equals("Online") &&
                              order.getOrderDate() == 1672531200L &&
                              order.getWarehouse().equals(wh) &&
                              order.getOrderPrice() == 0;

            System.out.println(success ? " SUCCESS\n" : " FAILURE\n");

        } catch (Exception e) {
            System.out.println(" *** Exception occurred: " + e.getMessage() + " ***\n");
        }
    }
    @Test
    public void testSetters() {
        System.out.println("Test 3: Setters");
        try {
            Warehouse wh1 = new Warehouse("W1", "Test1 Warehouse");
            Warehouse wh2 = new Warehouse("W2", "Test2 Warehouse");
            Order order = new Order(1672531200L, "Pending", "Online", 5, wh1);

            order.setOrderStatus("Shipped");
            order.setOrderType("In-Store");
            order.setOrderDate(1672617600L);
            order.setWarehouse(wh2);
            order.setOrderPrice(999.99);

            boolean success = order.getOrderStatus().equals("Shipped") &&
                              order.getOrderType().equals("In-Store") &&
                              order.getOrderDate() == 1672617600L &&
                              order.getWarehouse().equals(wh2) &&
                              order.getOrderPrice() == 999.99;

            System.out.println(success ? " SUCCESS\n" : " FAILURE\n");

        } catch (Exception e) {
            System.out.println(" FAILURE: Exception occurred: " + e.getMessage() + "\n");
        }
    }
    @Test
    public void testAddItem() {
        System.out.println("Test 4: addItem()");
        try {
            Warehouse wh = new Warehouse("W1", "Test1 Warehouse");
            Order order = new Order(1672531200L, "Pending", "Online", 3, wh);

            Item item1 = new Item("I1", "Laptop", 1200.00, 1, "A1");
            Item item2 = new Item("I2", "Mouse", 25.50, 1, "B2");

            order.addItem(item1);
            order.addItem(item2);
            // Must compare and match all data to succeed
            boolean success = order.getItems()[0] == item1 &&
                              order.getItems()[1] == item2 &&
                              order.getOrderPrice() == 1225.50;

            System.out.println(success ? " SUCCESS\n" : " FAILURE\n");

        } catch (Exception e) {
            System.out.println(" FAILURE: Exception occurred: " + e.getMessage() + "\n");
        }
    }

    // Test toString for constructor that generates orderID
    @Test
    public void testToStringGeneratedID() {
        System.out.println("Test 5: toString() (generated orderID)");
        try {
            Warehouse wh = new Warehouse("W1", "Test1 Warehouse");
            Order order = new Order(1672531200L, "Pending", "Online", 2, wh);
            Item item = new Item("I1", "Laptop", 1200.00, 1, "A1");
            order.addItem(item);

            String str = order.toString();
            // Must compare and match all data to succeed
            boolean containsAll = str.contains(order.getOrderID()) &&
                                  str.contains("Pending") &&
                                  str.contains("Online") &&
                                  str.contains("1672531200") &&
                                  str.contains("Test1 Warehouse") &&
                                  str.contains("Laptop") &&
                                  str.contains("1200.0");

            System.out.println(containsAll ? " SUCCESS\n" : " FAILURE\n");

        } catch (Exception e) {
            System.out.println(" FAILURE: Exception occurred: " + e.getMessage() + "\n");
        }
    }

    // Test toString for constructor with existing orderID
    @Test
    public void testToStringExistingID() {
        System.out.println("Test 6: toString() (existing orderID)");
        try {
            Warehouse wh = new Warehouse("W1", "Test1 Warehouse");
            String existingID = "123456789012";
            Order order = new Order(existingID, 1672531200L, "Pending", "Online", 2, wh);
            Item item = new Item("I1", "Laptop", 1200.00, 1, "A1");
            order.addItem(item);

            String str = order.toString();
            // Must compare and match all data to succeed
            boolean containsAll = str.contains(order.getOrderID()) && // should include "X123456789012"
                                  str.contains("Pending") &&
                                  str.contains("Online") &&
                                  str.contains("1672531200") &&
                                  str.contains("Test1 Warehouse") &&
                                  str.contains("Laptop") &&
                                  str.contains("1200.0");

            System.out.println(containsAll ? " SUCCESS\n" : " FAILURE\n");

        } catch (Exception e) {
            System.out.println(" FAILURE: Exception occurred: " + e.getMessage() + "\n");
        }
    }
}