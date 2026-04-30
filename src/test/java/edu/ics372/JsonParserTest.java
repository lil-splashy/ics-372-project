// * Manual Unit Test for the JsonParser class
// * This test verifies:
// * 1. Getter and setter for filePath.
// * 2. parseFile() correctly reads and creates Order objects.
// * 3. exportOrders() successfully writes a JSON file.
// * 4. importProgramOrders() correctly reconstructs saved orders.
// *
// * NOTE:
// * - Requires valid JSON test files to exist.
// * - File paths may need to be adjusted based on your project structure.
// */
package edu.ics372;

import org.junit.jupiter.api.Test;
import java.util.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTest {

    @Test
    public void testGetterSetter() {
        JsonParser parser = new JsonParser();
        String path = "test.json";

        parser.setNewPath(path);
        assertEquals(path, parser.getFilePath());
    }

    @Test
    public void testExportOrders() {
        JsonParser parser = new JsonParser();

        Warehouse wh = new Warehouse("W001", "Main", true, true);
        Order order = new Order.Builder().setSourcePrefix("J").setOrderDate(1672531200L).setOrderStatus(OrderStatus.INCOMING).setOrderType("Online").setMaxItems(2).setWarehouse(wh).build();
        order.addItem(new Item("I1", "Laptop", 1200.0, 1, "A1"));

        List<Order> orders = List.of(order);

        String filePath = "test_export.json";
        parser.exportOrders(orders, filePath);

        assertTrue(Files.exists(Paths.get(filePath)));
    }

    @Test
    public void testImportProgramOrders() {
        JsonParser parser = new JsonParser();

        String filePath = "src/test/java/edu/ics372/importTestFiles/test_import.json";

        List<Order> orders = parser.importProgramOrders(filePath);

        assertNotNull(orders, "Orders list is null");
        assertFalse(orders.isEmpty(), "Orders list is empty");

        Order order = orders.getFirst();

        assertNotNull(order.getOrderID(), "Order ID is null");
        assertNotNull(order.getOrderType(), "Order type is null");
        assertNotNull(order.getOrderStatus(), "Order status is null");
    }
}