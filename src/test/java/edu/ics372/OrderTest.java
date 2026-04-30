package edu.ics372;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    private Order buildOrder(long date, String status, String type, int maxItems, Warehouse wh) {
        return new Order.Builder()
                .setSourcePrefix("J")
                .setOrderDate(date)
                .setOrderStatus(status)
                .setOrderType(type)
                .setMaxItems(maxItems)
                .setWarehouse(wh)
                .build();
    }

    @Test
    public void testConstructorAndGetters() {
        Warehouse wh = new Warehouse("W1", "Test1 Warehouse");
        Order order = buildOrder(1672531200L, "Pending", "Online", 5, wh);

        assertEquals("Pending", order.getOrderStatus());
        assertEquals("Online", order.getOrderType());
        assertEquals(1672531200L, order.getOrderDate());
        assertEquals(wh, order.getWarehouse());
        assertEquals(0, order.getOrderPrice());
        assertTrue(order.getOrderID().startsWith("J"));
    }

    @Test
    public void testConstructorWithExistingID() {
        Warehouse wh = new Warehouse("W1", "Test1 Warehouse");
        Order order = buildOrder(1672531200L, "Pending", "Online", 5, wh);

//        assertEquals("X123456789012", order.getOrderID());
    }

    @Test
    public void testSetters() {
        Warehouse wh1 = new Warehouse("W1", "Test1 Warehouse");
        Warehouse wh2 = new Warehouse("W2", "Test2 Warehouse");
        Order order = buildOrder(1672531200L, "Pending", "Online", 5, wh1);

        order.setOrderStatus("Shipped");
        order.setOrderType("In-Store");
        order.setOrderDate(1672617600L);
        order.setWarehouse(wh2);
        order.setOrderPrice(999.99);

        assertEquals("Shipped", order.getOrderStatus());
        assertEquals("In-Store", order.getOrderType());
        assertEquals(1672617600L, order.getOrderDate());
        assertEquals(wh2, order.getWarehouse());
        assertEquals(999.99, order.getOrderPrice());
    }

    @Test
    public void testAddItem() {
        Warehouse wh = new Warehouse("W1", "Test1 Warehouse");
        Order order = buildOrder(1672531200L, "Pending", "Online", 3, wh);

        Item item1 = new Item("I1", "Laptop", 1200.00, 1, "A1");
        Item item2 = new Item("I2", "Mouse", 25.50, 1, "B2");

        order.addItem(item1);
        order.addItem(item2);

        assertEquals(item1, order.getItems()[0]);
        assertEquals(item2, order.getItems()[1]);
        assertEquals(1225.50, order.getOrderPrice());
    }

    @Test
    public void testToStringGeneratedID() {
        Warehouse wh = new Warehouse("W1", "Test1 Warehouse");
        Order order = buildOrder(1672531200L, "Pending", "Online", 2, wh);
        order.addItem(new Item("I1", "Laptop", 1200.00, 1, "A1"));

        String str = order.toString();

        assertTrue(str.contains(order.getOrderID()));
        assertTrue(str.contains("Pending"));
        assertTrue(str.contains("Online"));
    }
}