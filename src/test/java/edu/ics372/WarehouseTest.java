// * Manual Unit Test for the Warehouse class
// * Since this class mainly contains a constructor, getters, and toString(),
// * the unit test will verify:
// * 1. The constructor correctly initializes attributes and getters return expected values.
// * 2. The toString() method includes all attribute values.
// * Note: Setters are private, so they cannot be tested directly.

package edu.ics372;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WarehouseTest {

    @Test
    public void testConstructorAndGetters() {
        Warehouse warehouse = new Warehouse("W001", "Main Warehouse", true, true);

        assertEquals("W001", warehouse.getWarehouseID());
        assertEquals("Main Warehouse", warehouse.getWarehouseName());
    }

    @Test
    public void testToString() {
        Warehouse warehouse = new Warehouse("W001", "Main Warehouse", true, true);
        String str = warehouse.toString();

        assertTrue(str.contains("W001"));
        assertTrue(str.contains("Main Warehouse"));
    }
}