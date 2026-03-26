package edu.ics372;

/**
 * Manual Unit Test for the XmlParser class
 * This test verifies:
 * 1. Getter and setter for filePath.
 * 2. parseFile() correctly reads XML and creates Order objects.
 * 3. exportOrders() successfully writes an XML file.
 *
 * NOTE:
 * - Requires a valid XML test file to exist for parsing.
 * - File paths may need adjustment based on your project structure.
 */

import org.junit.jupiter.api.Test;

import java.util.*;
import java.nio.file.*;

public class XmlParserTest {

    public static void main(String[] args) {
        System.out.println("=== Starting XmlParser Tests ===\n");

        XmlParserTest tester = new XmlParserTest();

        tester.testGetSetFilePath();
        tester.testParseFile();
        tester.testExportOrders();

        System.out.println("\n=== All XmlParser Tests Completed ===");
    }

    // Test getter and setter for filePath
    @Test
    public void testGetSetFilePath() {
        System.out.println("Test 1: Get & Set FilePath");
        try {
            XmlParser parser = new XmlParser();

            String newPath = "src/test/java/edu/ics372/xmlTest.xml";
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
    @Test
    public void testParseFile() {
        System.out.println("Test 2: parseFile()");
        try {
            XmlParser parser = new XmlParser();

            // Path to a valid XML test file (must exist)
            String testFile = "src/test/java/edu/ics372/xmlTest.xml";

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
    @Test
    public void testExportOrders() {
        System.out.println("Test 3: exportOrders()");
        try {
            XmlParser parser = new XmlParser();

            // Create sample data
            Warehouse wh = new Warehouse("W001", "Main");
            Order order = new Order(1672531200L, "Pending", "Online", 2, wh);
            Item item = new Item("I1", "Laptop", 1200.0, 1, "A1");
            order.addItem(item);

            List<Order> orders = new ArrayList<>();
            orders.add(order);

            String filePath = "src/test/java/edu/ics372/exportedTestFiles/test_export.xml";

            parser.exportOrders(orders, filePath);

            // Check if file exists after export
            boolean success = Files.exists(Paths.get(filePath));

            if (success) {
                System.out.println(" SUCCESS: exportOrders() created XML file\n");
            } else {
                System.out.println(" FAILURE: exportOrders() did not create file\n");
            }

        } catch (Exception e) {
            System.out.println(" FAILURE: Exception occurred: " + e.getMessage() + "\n");
        }
    }
}