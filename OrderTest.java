package edu.ics372;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    void testAddItemIncreasesPrice() {
        Order order = new Order(System.currentTimeMillis(), "incoming", "pickup", 5, null, null);

        Item item = new Item("ABCDEFG", "Ford F150", 40000.00, 10, "B12");
        order.addItem(item);

        assertEquals(40000.00, order.getOrderPrice());
    }

    @Test
    void testOrderStatusChange() {
        Customer customer = new Customer("Takagi", "email.com", "651", "Street");
        Order order = new Order(System.currentTimeMillis(), "incoming", "pickup", 5, customer, null);

        order.setOrderStatus("started");

        assertEquals("started", order.getOrderStatus());

        order.setOrderStatus("completed");
    }

    @Test
    void testUniqueOrderID() {
        Order o1 = new Order(System.currentTimeMillis(), "incoming", "pickup", 5, null, null);
        Order o2 = new Order(System.currentTimeMillis(), "incoming", "pickup", 5, null, null);

        assertNotEquals(o1.getOrderID(), o2.getOrderID());
    }
}