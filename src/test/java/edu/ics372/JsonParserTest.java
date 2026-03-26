package edu.ics372;
/**
 * Manual Unit Test for the JsonParser class
 * This test verifies:
 * 1. Getter and setter for filePath.
 * 2. parseFile() correctly reads and creates Order objects.
 * 3. exportOrders() successfully writes a JSON file.
 * 4. importProgramOrders() correctly reconstructs saved orders.
 *
 * NOTE:
 * - Requires valid JSON test files to exist.
 * - File paths may need to be adjusted based on your project structure.
 */

import edu.ics372.JsonParser;

import java.util.*;
import java.nio.file.*;

public class JsonParserTest {

    public static void main(String[] args) {
        System.out.println("=== Starting JsonParser Tests ===\n");

        JsonParserTest tester = new JsonParserTest();

        tester.testGetterSetter();
        tester.testParseFile();
        tester.testExportOrders();
        tester.testImportProgramOrders();

        System.out.println("\n=== All JsonParser Tests Completed ===");
    }

    // Test getter and setter for filePath
    public void testGetterSetter() {
        System.out.println("Test 1: Getter and Setter");
        try {
            JsonParser parser = new JsonParser();

            String newPath = "src/test/java/edu/ics372/jsonTest.json";
            parser.setNewPath(newPath);

            boolean success = parser.getFilePath().equals(newPath);

            if (success) {
                System.out.println(" SUCCESS: Getter and setter work correctly\n");
            } else {
                System.out.println(" FAILURE: Getter or setter failed\n");
            }

        } catch (Exception e) {
            System.out.println(" FAILURE: Exception occurred: " + e.getMessage() + "\n");
        }
    }

    // Test parseFile() method
    public void testParseFile() {
        System.out.println("Test 2: parseFile()");
        try {
            JsonParser parser = new JsonParser();

            // Path to a valid test JSON file (must exist)
            String testFile = "src/test/java/edu/ics372/jsonTest.json";

            List<Order> orders = parser.parseFile(testFile);

            boolean success = (orders != null && !orders.isEmpty());

            if (success) {
                System.out.println(" SUCCESS: parseFile() loaded orders\n");
            } else {
                System.out.println(" FAILURE: parseFile() returned empty or null\n");
            }

        } catch (Exception e) {
            System.out.println(" FAILURE: Exception occurred: " + e.getMessage() + "\n");
        }
    }

    // Test exportOrders() method
    public void testExportOrders() {
        System.out.println("Test 3: exportOrders()");
        try {
            JsonParser parser = new JsonParser();

            // Create sample data
            Warehouse wh = new Warehouse("W001", "Main");
            Order order = new Order(1672531200L, "Pending", "Online", 2, wh);
            Item item = new Item("I1", "Laptop", 1200.0, 1, "A1");
            order.addItem(item);

            List<Order> orders = new ArrayList<>();
            orders.add(order);

            String filePath = "src/test/java/edu/ics372/exportedTestFiles/test_export.json";

            parser.exportOrders(orders, filePath);

            // Check if file exists after export
            boolean success = Files.exists(Paths.get(filePath));

            if (success) {
                System.out.println(" SUCCESS: exportOrders() created file\n");
            } else {
                System.out.println(" FAILURE: exportOrders() did not create file\n");
            }

        } catch (Exception e) {
            System.out.println(" FAILURE: Exception occurred: " + e.getMessage() + "\n");
        }
    }

    // Test importProgramOrders() method
    public void testImportProgramOrders() {
        System.out.println("Test 4: importProgramOrders()");
        try {
            JsonParser parser = new JsonParser();

            // Use testimport JSON file
            String filePath = "src/test/java/edu/ics372/importTestFiles/test_import.json";

            List<Order> orders = parser.importProgramOrders(filePath);

            boolean success = (orders != null && !orders.isEmpty());

            if (success) {
                System.out.println(" SUCCESS: importProgramOrders() loaded orders\n");
            } else {
                System.out.println(" FAILURE: importProgramOrders() returned empty or null\n");
            }

        } catch (Exception e) {
            System.out.println(" FAILURE: Exception occurred: " + e.getMessage() + "\n");
        }
    }
}