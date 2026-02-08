// AI GENERATED TEST
package edu.ics372;

import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;

public class ParserTest {

    private static final String TEST_JSON_PATH = "test_order.json";
    private static final String EXPORT_JSON_PATH = "test_order.json";

    public static void main(String[] args) {
        System.out.println("=== Starting JsonParser Tests ===\n");

        ParserTest tester = new ParserTest();

        tester.testConstructor();
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

    public void createSampleJsonFile() {
        System.out.println("Test 2: Creating sample JSON file");
        try {
            JSONObject root = new JSONObject();
            JSONObject order = new JSONObject();

            // IMPORTANT: Add order_date field
            order.put("type", "ONLINE");
            order.put("order_date", 1707427200000L);

            JSONArray items = new JSONArray();

            // Item 1
            JSONObject item1 = new JSONObject();
            item1.put("name", "Gaming Laptop RTX 4060");
            item1.put("price", 1299.99);
            item1.put("quantity", 1);
            items.put(item1);

            // Item 2
            JSONObject item2 = new JSONObject();
            item2.put("name", "Wireless Gaming Mouse");
            item2.put("price", 79.99);
            item2.put("quantity", 1);
            items.put(item2);

            // Item 3
            JSONObject item3 = new JSONObject();
            item3.put("name", "RGB Mechanical Keyboard");
            item3.put("price", 149.99);
            item3.put("quantity", 1);
            items.put(item3);

            // Item 4
            JSONObject item4 = new JSONObject();
            item4.put("name", "27-inch 144Hz Monitor");
            item4.put("price", 349.99);
            item4.put("quantity", 2);
            items.put(item4);

            // Item 5
            JSONObject item5 = new JSONObject();
            item5.put("name", "USB-C Hub");
            item5.put("price", 45.99);
            item5.put("quantity", 1);
            items.put(item5);

            order.put("items", items);
            root.put("order", order);

            String jsonString = root.toString(4);
            Files.write(Paths.get(TEST_JSON_PATH), jsonString.getBytes());

            File testFile = new File(TEST_JSON_PATH);
            System.out.println("  ✓ Sample JSON file created: " + testFile.getAbsolutePath());
            System.out.println("  Items created: " + items.length());
            System.out.println("  File size: " + testFile.length() + " bytes\n");

        } catch (Exception e) {
            System.out.println("  ✗ Failed to create sample file: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    public void testParseFile() {
        System.out.println("Test 3: parseFile()");
        try {
            File testFile = new File(TEST_JSON_PATH);
            if (!testFile.exists()) {
                System.out.println("  ✗ Test file doesn't exist: " + testFile.getAbsolutePath() + "\n");
                return;
            }

            JsonParser parser = new JsonParser();
            Order order = parser.parseFile(TEST_JSON_PATH);

            if (order != null) {
                System.out.println("  ✓ File parsed successfully");
                System.out.println("  Order ID: " + order.getOrderID());
                System.out.println("  Order Type: " + order.getOrderType());
                System.out.println("  Order Status: " + order.getOrderStatus());
                System.out.println("  Order Date: " + order.getOrderDate());

                Item[] items = order.getItems();
                int itemCount = 0;
                if (items != null) {
                    for (Item item : items) {
                        if (item != null) itemCount++;
                    }
                }
                System.out.println("  Number of items: " + itemCount);

                if (items != null && itemCount > 0) {
                    System.out.println("\n  Items in order:");
                    double total = 0.0;
                    int displayNum = 1;
                    for (Item item : items) {
                        if (item != null) {
                            double itemTotal = item.getItemPrice() * item.getItemQuantity();
                            total += itemTotal;
                            System.out.printf("    %d. %-30s $%.2f x%d = $%.2f%n",
                                    displayNum++,
                                    item.getItemName(),
                                    item.getItemPrice(),
                                    item.getItemQuantity(),
                                    itemTotal);
                        }
                    }
                    System.out.printf("  Total Order Value: $%.2f%n", total);
                }
                System.out.println();
            } else {
                System.out.println("  ✗ Failed to parse file - returned null\n");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Exception: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }

    public void testExportJSON() {
        System.out.println("Test 4: exportJSON()");
        try {
            JsonParser parser = new JsonParser();
            Order order = parser.parseFile(TEST_JSON_PATH);

            if (order != null) {
                parser.exportJSON(order, EXPORT_JSON_PATH);

                File exportedFile = new File(EXPORT_JSON_PATH);
                if (exportedFile.exists()) {
                    System.out.println("  ✓ Export successful");
                    System.out.println("  Exported file: " + exportedFile.getAbsolutePath());
                    System.out.println("  File size: " + exportedFile.length() + " bytes");

                    // Try to re-import it
                    Order reimportedOrder = parser.parseFile(EXPORT_JSON_PATH);
                    if (reimportedOrder != null) {
                        System.out.println("  ✓ Exported file can be re-imported successfully\n");
                    } else {
                        System.out.println("  ✗ Exported file cannot be re-imported\n");
                    }
                } else {
                    System.out.println("  ✗ Export failed - file not created\n");
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
            boolean deleted1 = Files.deleteIfExists(Paths.get(TEST_JSON_PATH));
            boolean deleted2 = Files.deleteIfExists(Paths.get(EXPORT_JSON_PATH));
            System.out.println("  ✓ Test files cleaned up (deleted: " +
                    (deleted1 ? "test_order.json " : "") +
                    (deleted2 ? "exported_order.json" : "") + ")");
        } catch (Exception e) {
            System.out.println("  ⚠ Warning: Could not delete test files: " + e.getMessage());
        }
    }
}
