package edu.ics372;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Manual Unit Test for the Warehouse class
 * Since this class mainly contains a constructor, getters, and toString(),
 * the unit test will verify:
 * 1. The constructor correctly initializes attributes and getters return expected values.
 * 2. The toString() method includes all attribute values.
 * Note: Setters are private, so they cannot be tested directly.
 */

public class WarehouseTest {

    public static void main(String[] args) {
        // Print header to indicate tests are starting
        System.out.println("=== Starting Warehouse Tests ===\n");

        // Create tester instance
        WarehouseTest tester = new WarehouseTest();

        // Run tests
        tester.testConstructorAndGetters();
        tester.testToString();

        // Print footer to indicate all tests completed
        System.out.println("\n=== All Warehouse Tests Completed ===");
    }

    // Test constructor and getter methods
    public void testConstructorAndGetters() {
        System.out.println("Test 1: Constructor and Getters");
        try {
            // Create Warehouse object with test data
            Warehouse warehouse = new Warehouse("W001", "Main Warehouse");

            // Track test success
            boolean success = true;

            // Verify values using getters
            if (!"W001".equals(warehouse.getWarehouseID())) success = false;
            if (!"Main Warehouse".equals(warehouse.getWarehouseName())) success = false;

            // Print result
            if (success) {
                System.out.println(" SUCCESS: Constructor and getters work correctly\n");
            } else {
                System.out.println(" FAILURE: Constructor or getters failed\n");
            }

        } catch (Exception e) {
            // Catch and display any exception
            System.out.println(" *** Exception occurred: " + e.getMessage() + " ***\n");
        }
    }

    // Test toString() method
    public void testToString() {
        System.out.println("Test 2: toString()");
        try {
            // Create Warehouse object
            Warehouse warehouse = new Warehouse("W001", "Main Warehouse");

            // Call toString()
            String str = warehouse.toString();

            // Check that output contains all expected values
            boolean containsAll = str.contains("W001") &&
                    str.contains("Main Warehouse");

            // Print result
            if (containsAll) {
                System.out.println(" SUCCESS: toString() contains all attributes\n");
            } else {
                System.out.println(" FAILURE: toString() missing some attributes\n");
                System.out.println(" Output: " + str + "\n"); // helpful debug output
            }

        } catch (Exception e) {
            // Catch and display any exception
            System.out.println(" FAILURE: Exception occurred: " + e.getMessage() + "\n");
        }
    }
}