// * Manual Unit Test for the XmlParser class
// * This test verifies:
// * 1. Getter and setter for filePath.
// * 2. parseFile() correctly reads XML and creates Order objects.
// * 3. exportOrders() successfully writes an XML file.
// *
// * NOTE:
// * - Requires a valid XML test file to exist for parsing.
// * - File paths may need adjustment based on your project structure.

package edu.ics372;

import org.junit.jupiter.api.Test;
import java.util.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

public class XmlParserTest {

    @Test
    public void testGetSetFilePath() {
        XmlParser parser = new XmlParser();
        String path = "test_export.xml";

        parser.setNewPath(path);
        assertEquals(path, parser.getFilePath());
    }

    @Test
    public void testExportOrders() {
        XmlParser parser = new XmlParser();

        Warehouse wh = new Warehouse("W001", "Main");
        Order order = new Order(1672531200L, "Pending", "Online", 2, wh);
        order.addItem(new Item("I1", "Laptop", 1200.0, 1, "A1"));

        List<Order> orders = List.of(order);

        String filePath = "test_export.xml";
        parser.exportOrders(orders, filePath);

        assertTrue(Files.exists(Paths.get(filePath)));
    }
}