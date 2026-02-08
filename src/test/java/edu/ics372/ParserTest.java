// AI GENERATED TESTS


package edu.ics372;

import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Comprehensive test class for JsonParser
 * Tests parsing, exporting, and path management
 * @author Ben
 */
public class ParserTest {

    private static final String TEST_JSON_PATH = "test_order.json";
    private static final String EXPORT_JSON_PATH = "exported_order.json";

    public static void main(String[] args) {
        System.out.println("=== Starting JsonParser Tests ===\n");

        ParserTest tester = new ParserTest();

        // Run all tests
        tester.testConstructor();
        tester.testGetFilePath();
        tester.testSetNewPath();
        tester.createSampleJsonFile();
        tester.testParseFile();
        tester.testExportJSON();
        tester.cleanup();

        System.out.println("\n=== All Tests Completed ===");
    }

    public void testConstructor() {
        System.out.println("Test 1: Constructor");
        try {
            JsonParser parser = new JsonParser();
            System.out.println("  ✓ JsonParser created successfully\n");
        } catch (Exception e) {
            System.out.println("  ✗ Failed: " + e.getMessage() + "\n");
        }
    }

    public void testGetFilePath() {
        System.out.println("Test 2: getFilePath()");
        try {
            JsonParser parser = new JsonParser();
            String path = parser.getFilePath();
            System.out.println("  Current file path: " + path);
            System.out.println("  ✓ getFilePath() works\n");
        } catch (Exception e) {
            System.out.println("  ✗ Failed: " + e.getMessage() + "\n");
        }
    }

    public void testSetNewPath() {
        System.out.println("Test 3: setNewPath()");
        try {
            JsonParser parser = new JsonParser();
            String newPath = TEST_JSON_PATH;
            parser.setNewPath(newPath);
            String retrievedPath = parser.getFilePath();

            if (retrievedPath.equals(newPath)) {
                System.out.println("  ✓ Path changed successfully to: " + retrievedPath + "\n");
            } else {
                System.out.println("  ✗ Path mismatch. Expected: " + newPath + ", Got: " + retrievedPath + "\n");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Failed: " + e.getMessage() + "\n");
        }
    }

    public void createSampleJsonFile() {
        System.out.println("Test 4: Creating sample JSON file");
        try {
            JSONObject root = new JSONObject();
            JSONObject order = new JSONObject();

            order.put("type", "ONLINE");
            order.put("order_date", System.currentTimeMillis());

            JSONArray items = new JSONArray();

            JSONObject item1 = new JSONObject();
            item1.put("name", "Laptop");
            item1.put("price", 999.99);
            item1.put("quantity", 1);
            items.put(item1);

            JSONObject item2 = new JSONObject();
            item2.put("name", "Mouse");
            item2.put("price", 29.99);
            item2.put("quantity", 2);
            items.put(item2);

            JSONObject item3 = new JSONObject();
            item3.put("name", "Keyboard");
            item3.put("price", 79.99);
            item3.put("quantity", 1);
            items.put(item3);

            order.put("items", items);
            root.put("order", order);

            String jsonString = root.toString(4);
            Files.write(Paths.get(TEST_JSON_PATH), jsonString.getBytes());

            System.out.println("  ✓ Sample JSON file created: " + TEST_JSON_PATH + "\n");

        } catch (Exception e) {
            System.out.println("  ✗ Failed to create sample file: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public void testParseFile() {
        System.out.println("Test 5: parseFile()");
        try {
            JsonParser parser = new JsonParser();
            parser.setNewPath(TEST_JSON_PATH);

            Order order = parser.parseFile(parser.getFilePath());

            if (order != null) {
                System.out.println("  ✓ File parsed successfully");
                System.out.println("  Order ID: " + order.getOrderID());
                System.out.println("  Order Type: " + order.getOrderType());
                System.out.println("  Order Status: " + order.getOrderStatus());

                Item[] items = order.getItems();
                if (items != null && items.length > 0) {
                    System.out.println("\n  Items in order:");
                    for (int i = 0; i < items.length; i++) {
                        if (items[i] != null) {
                            System.out.println("    " + (i+1) + ". " + items[i].getItemName() +
                                    " - $" + items[i].getItemPrice() +
                                    " x" + items[i].getItemQuantity());
                        }
                    }
                }
                System.out.println();
            } else {
                System.out.println("  ✗ Failed to parse file - returned null\n");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Exception: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public void testExportJSON() {
        System.out.println("Test 6: exportJSON()");
        try {
            JsonParser parser = new JsonParser();
            parser.setNewPath(TEST_JSON_PATH);

            Order order = parser.parseFile(TEST_JSON_PATH);

            if (order != null) {
                parser.exportJSON(order, EXPORT_JSON_PATH);

                if (Files.exists(Paths.get(EXPORT_JSON_PATH))) {
                    System.out.println("  ✓ Export successful");

                    Order reimportedOrder = parser.parseFile(EXPORT_JSON_PATH);
                    if (reimportedOrder != null) {
                        System.out.println("  ✓ Exported file is valid and can be re-imported\n");
                    } else {
                        System.out.println("  ✗ Exported file exists but cannot be parsed\n");
                    }
                } else {
                    System.out.println("  ✗ Exported file not found\n");
                }
            } else {
                System.out.println("  ✗ Cannot test export - no order to export\n");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Exception: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public void cleanup() {
        System.out.println("Cleanup: Removing test files");
        try {
            Files.deleteIfExists(Paths.get(TEST_JSON_PATH));
            Files.deleteIfExists(Paths.get(EXPORT_JSON_PATH));
            System.out.println("  ✓ Test files cleaned up");
        } catch (Exception e) {
            System.out.println("  ⚠ Warning: Could not delete test files");
        }
    }
}
