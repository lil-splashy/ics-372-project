package edu.ics372;

import org.junit.jupiter.api.Test;

/**
 * Manual Unit Test for the Item class
 * Since this class is mostly getters, setters, and a constructor, the unit test will verify these methods:
 * 1. The constructor correctly initializes all attributes and the getters return the expected values.
 * 2. The setter methods correctly update the attributes and the getters reflect these changes.
 * 3. The toString() method includes all attribute values in its output.
 * Each test will print a success or failure message based on the results, and any exceptions will be caught and printed to help identify issues.
 */

public class ItemTest {


    public static void main(String[] args) {
        // Print header to indicate tests are starting
        System.out.println("=== Starting Item Tests ===\n");

        // Create an instance of this test class to call instance methods
        ItemTest tester = new ItemTest();

        // Run test for constructor and getters
        tester.testConstructorAndGetters();
        // Run test for setters
        tester.testSetters();
        // Run test for toString() method
        tester.testToString();

        // Print footer to indicate all tests have completed
        System.out.println("\n=== All Item Tests Completed ===");
    }

    // Test method to verify the constructor and getter methods
    @Test
    public void testConstructorAndGetters() {
        System.out.println("Test 1: Constructor and Getters");
        try {
            // Create a new Item object with test data
            Item item = new Item("ID123", "M.2 SSD 5TB", 19999.99, 99, "A1");

            // Initialize a flag to track success of test
            boolean success = true;

            // Check each attribute using getters; if any fail, set success to false
            if (!"ID123".equals(item.getItemID())) success = false;         // Check itemID
            if (!"M.2 SSD 5TB".equals(item.getItemName())) success = false; // Check itemName
            if (item.getItemPrice() != 19999.99) success = false;           // Check itemPrice
            if (item.getItemQuantity() != 99) success = false;              // Check itemQuantity
            if (!"A1".equals(item.getWarehouseLocation())) success = false; // Check warehouseLocation

            // Print result based on success flag
            if (success) {
                System.out.println(" SUCCESS: Constructor and getters work correctly\n");
            } else {
                System.out.println(" FAILURE: Constructor or getters failed\n");
            }
        } catch (Exception e) {
            // Print exception information if an error occurs
            System.out.println(" *** Exception occurred: " + e.getMessage() + " ***\n");
        }
    }

    // Test method to verify the setter methods
    @Test
    public void testSetters() {
        System.out.println("Test 2: Setters");
        try {
            // Create a new Item object with initial test data
            Item item = new Item("ID123", "M.2 SSD 5TB", 19999.99, 99, "A1");

            // Update attributes using setter methods
            item.setItemID("ID456");                    // Update itemID
            item.setItemName("3 Musketeers");           // Update itemName
            item.setItemPrice(333.33);                  // Update itemPrice
            item.setItemQuantity(33);                   // Update itemQuantity
            item.setWarehouseLocation("C3");            // Update warehouseLocation

            // Initialize success flag for this test
            boolean success = true;

            // Verify each updated value using getters
            if (!"ID456".equals(item.getItemID())) success = false;
            if (!"3 Musketeers".equals(item.getItemName())) success = false;
            if (item.getItemPrice() != 333.33) success = false;
            if (item.getItemQuantity() != 33) success = false;
            if (!"C3".equals(item.getWarehouseLocation())) success = false;

            // Print result based on success flag
            if (success) {
                System.out.println(" SUCCESS: Setters work correctly\n");
            } else {
                System.out.println(" FAILURE: Setters failed\n");
            }
        } catch (Exception e) {
            // Print exception information if an error occurs
            System.out.println(" FAILURE: Exception occurred: " + e.getMessage() + "\n");
        }
    }

    // Test method to verify that toString() includes all attribute values
    @Test
    public void testToString() {
        System.out.println("Test 3: toString()");
        try {
            // Create a new Item object with test data
            Item item = new Item("ID123", "M.2 SSD 5TB", 19999.99, 99, "A1");
            // Call toString() method
            String str = item.toString();

            // Check if the resulting string contains all expected values
            boolean containsAll = str.contains("ID123") &&
                    str.contains("M.2 SSD 5TB") &&
                    str.contains("19999.99") &&  // Check price formatting
                    str.contains("99") &&
                    str.contains("A1");

            // Print result based on containsAll flag
            if (containsAll) {
                System.out.println(" SUCCESS: toString() contains all attributes\n");
            } else {
                System.out.println(" FAILURE: toString() missing some attributes\n");
            }
        } catch (Exception e) {
            // Print exception information if an error occurs
            System.out.println(" FAILURE: Exception occurred: " + e.getMessage() + "\n");
        }
    }
}