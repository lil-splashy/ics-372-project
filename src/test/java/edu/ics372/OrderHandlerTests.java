package edu.ics372;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderHandlerTests {

    private OrderHandler handler;
    private Order order1;
    private Order order2;
    private Order order3;

    @BeforeEach
    void setup() {
        handler = new OrderHandler();

        order1 = new Order(1L, "Alice", "Pizza", 15, null, null);
        order2 = new Order(2L, "Bob", "Burger", 10, null, null);
        order3 = new Order(3L, "Charlie", "Salad", 12, null, null);

        order1.setOrderStatus("incoming");
        order2.setOrderStatus("incoming");
        order3.setOrderStatus("incoming");

        order1.setOrderPrice(15);
        order2.setOrderPrice(10);
        order3.setOrderPrice(12);

        handler.addOrder(order1);
        handler.addOrder(order2);
        handler.addOrder(order3);
    }

    @Test
    void testStartOrder() {
        handler.startOrder(order1.getOrderID());

        assertEquals("started", order1.getOrderStatus());
        assertTrue(handler.getStartedOrders().contains(order1));
        assertFalse(handler.getIncomingOrders().contains(order1));
    }

    @Test
    void testCompleteOrder() {
        handler.startOrder(order1.getOrderID());
        handler.completeOrder(order1.getOrderID());

        assertEquals("completed", order1.getOrderStatus());
        assertTrue(handler.getCompletedOrders().contains(order1));
        assertFalse(handler.getStartedOrders().contains(order1));
    }

    @Test
    void testCancelOrder() {
        handler.cancelOrder(order2.getOrderID());

        assertEquals("canceled", order2.getOrderStatus());
    }

    @Test
    void testInvalidOrder() {
        Order result = handler.getOrder("999");
        assertNull(result);
    }

    @Test
    void testTotalPriceUncompletedOrders() {
        double total = handler.totalPriceUncompletedOrders();

        assertEquals(37, total); // 15 + 10 + 12
    }

    @Test
    void testCompleteOrderWithoutStart() {
        handler.completeOrder(String.valueOf(order2.getOrderID()));

        assertNotEquals("completed", order2.getOrderStatus());
        assertFalse(handler.getCompletedOrders().contains(order2));
    }

    @Test
    void testCancelStartedOrder() {
        handler.startOrder(String.valueOf(order1.getOrderID()));
        handler.cancelOrder(String.valueOf(order1.getOrderID()));

        assertEquals("canceled", order1.getOrderStatus());
        assertFalse(handler.getStartedOrders().contains(order1));
    }

    @Test
    void testCancelCompletedOrder() {
        handler.startOrder(String.valueOf(order1.getOrderID()));
        handler.completeOrder(String.valueOf(order1.getOrderID()));
        handler.cancelOrder(String.valueOf(order1.getOrderID()));

        assertEquals("canceled", order1.getOrderStatus());
        assertFalse(handler.getCompletedOrders().contains(order1));
    }

    @Test
    void testOrderMovesCorrectlyBetweenLists() {
        handler.startOrder(String.valueOf(order1.getOrderID()));

        assertFalse(handler.getIncomingOrders().contains(order1));
        assertTrue(handler.getStartedOrders().contains(order1));

        handler.completeOrder(String.valueOf(order1.getOrderID()));

        assertFalse(handler.getStartedOrders().contains(order1));
        assertTrue(handler.getCompletedOrders().contains(order1));
    }
}