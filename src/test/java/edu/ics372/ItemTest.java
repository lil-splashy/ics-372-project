// * Manual Unit Test for the Item class
// * Since this class is mostly getters, setters, and a constructor, the unit test will verify these methods:
// * 1. The constructor correctly initializes all attributes and the getters return the expected values.
// * 2. The setter methods correctly update the attributes and the getters reflect these changes.
// * 3. The toString() method includes all attribute values in its output.

package edu.ics372;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    @Test
    public void testConstructorAndGetters() {
        Item item = new Item("ID123", "M.2 SSD 5TB", 19999.99, 99, "A1");

        assertEquals("ID123", item.getItemID());
        assertEquals("M.2 SSD 5TB", item.getItemName());
        assertEquals(19999.99, item.getItemPrice());
        assertEquals(99, item.getItemQuantity());
        assertEquals("A1", item.getWarehouseLocation());
    }

    @Test
    public void testSetters() {
        Item item = new Item("ID123", "SSD", 100.0, 10, "A1");

        item.setItemID("ID456");
        item.setItemName("3 Musketeers");
        item.setItemPrice(333.33);
        item.setItemQuantity(33);
        item.setWarehouseLocation("C3");

        assertEquals("ID456", item.getItemID());
        assertEquals("3 Musketeers", item.getItemName());
        assertEquals(333.33, item.getItemPrice());
        assertEquals(33, item.getItemQuantity());
        assertEquals("C3", item.getWarehouseLocation());
    }

    @Test
    public void testToString() {
        Item item = new Item("ID123", "M.2 SSD 5TB", 19999.99, 99, "A1");
        String str = item.toString();

        assertTrue(str.contains("ID123"));
        assertTrue(str.contains("M.2 SSD 5TB"));
        assertTrue(str.contains("19999.99"));
        assertTrue(str.contains("99"));
        assertTrue(str.contains("A1"));
    }
}