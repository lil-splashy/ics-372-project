package edu.ics372;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderHandlerTests {

    private OrderHandler handler;
    private Order order1Existing;
    private Order order2Generated;


    @BeforeEach
    void setup() {
        handler = new OrderHandler();

        order1Existing = new Order("1E", 1L, "new", "shipped", 5, null);
        order2Generated = new Order("2G", 2L, "new", "pickup", 5, null);


        order1Existing.setOrderStatus("incoming");
        order2Generated.setOrderStatus("incoming");


        order1Existing.setOrderPrice(15);
        order2Generated.setOrderPrice(10);


        handler.addOrder(order1Existing);
        handler.addOrder(order2Generated);

    }

    @Test
    void testStartOrder() {
        handler.startOrder(order1Existing.getOrderID());

        assertEquals("started", order1Existing.getOrderStatus());
        assertTrue(handler.getStartedOrders().contains(order1Existing));
        assertFalse(handler.getIncomingOrders().contains(order1Existing));
    }

    @Test
    void testCompleteOrder() {
        handler.startOrder(order1Existing.getOrderID());
        handler.completeOrder(order1Existing.getOrderID());

        assertEquals("completed", order1Existing.getOrderStatus());
        assertTrue(handler.getCompletedOrders().contains(order1Existing));
        assertFalse(handler.getStartedOrders().contains(order1Existing));
    }

    @Test
    void testCancelOrder() {
        handler.cancelOrder(order2Generated.getOrderID());

        assertEquals("canceled", order2Generated.getOrderStatus());
    }

    @Test
    void testInvalidOrder() {
        Order result = handler.getOrder("999");
        assertNull(result);
    }

    @Test
    void testTotalPriceUncompletedOrders() {
        double total = handler.totalPriceUncompletedOrders();

        assertEquals(25, total); // 15 + 10
    }

    @Test
    void testCompleteOrderWithoutStart() {
        handler.completeOrder(String.valueOf(order2Generated.getOrderID()));

        assertNotEquals("completed", order2Generated.getOrderStatus());
        assertFalse(handler.getCompletedOrders().contains(order2Generated));
    }

    @Test
    void testCancelStartedOrder() {
        handler.startOrder(String.valueOf(order1Existing.getOrderID()));
        handler.cancelOrder(String.valueOf(order1Existing.getOrderID()));

        assertEquals("canceled", order1Existing.getOrderStatus());
        assertFalse(handler.getStartedOrders().contains(order1Existing));
    }

    @Test
    void testCancelCompletedOrder() {
        handler.startOrder(String.valueOf(order1Existing.getOrderID()));
        handler.completeOrder(String.valueOf(order1Existing.getOrderID()));
        handler.cancelOrder(String.valueOf(order1Existing.getOrderID()));

        assertEquals("canceled", order1Existing.getOrderStatus());
        assertFalse(handler.getCompletedOrders().contains(order1Existing));
    }

    @Test
    void testOrderMovesCorrectlyBetweenLists() {
        handler.startOrder(String.valueOf(order1Existing.getOrderID()));

        assertFalse(handler.getIncomingOrders().contains(order1Existing));
        assertTrue(handler.getStartedOrders().contains(order1Existing));

        handler.completeOrder(String.valueOf(order1Existing.getOrderID()));

        assertFalse(handler.getStartedOrders().contains(order1Existing));
        assertTrue(handler.getCompletedOrders().contains(order1Existing));
    }
}