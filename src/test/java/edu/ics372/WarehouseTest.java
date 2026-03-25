package edu.ics372;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WarehouseTest {

    @Test
    void testWarehouseName() {
        Warehouse warehouse = new Warehouse("B12", "Main Warehouse");

        assertEquals("Main Warehouse", warehouse.getWarehouseName());
    }

    @Test
    void testWarehouseID() {
        Warehouse warehouse = new Warehouse("B12", "Main Warehouse");

        assertEquals("B12", warehouse.getWarehouseID());
    }
}