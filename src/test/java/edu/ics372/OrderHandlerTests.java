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

        order1Existing = new Order.Builder().setSourcePrefix("J").setOrderDate(1L).setOrderStatus(OrderStatus.NEW).setOrderType("shipped").setMaxItems(5).setWarehouse(null).build();
        order2Generated = new Order.Builder().setSourcePrefix("J").setOrderDate(2L).setOrderStatus(OrderStatus.NEW).setOrderType("pickup").setMaxItems(5).setWarehouse(null).build();


        order1Existing.setOrderStatus(OrderStatus.INCOMING);
        order2Generated.setOrderStatus(OrderStatus.INCOMING);


        order1Existing.setOrderPrice(15);
        order2Generated.setOrderPrice(10);


        handler.addOrder(order1Existing);
        handler.addOrder(order2Generated);

    }

    @Test
    void testStartOrder() {
        handler.startOrder(order1Existing.getOrderID());

        assertEquals(OrderStatus.STARTED, order1Existing.getOrderStatus());
        assertTrue(handler.getStartedOrders().contains(order1Existing));
        assertFalse(handler.getIncomingOrders().contains(order1Existing));
    }

    @Test
    void testCompleteOrder() {
        handler.startOrder(order1Existing.getOrderID());
        handler.completeOrder(order1Existing.getOrderID());

        assertEquals(OrderStatus.COMPLETED, order1Existing.getOrderStatus());
        assertTrue(handler.getCompletedOrders().contains(order1Existing));
        assertFalse(handler.getStartedOrders().contains(order1Existing));
    }

    @Test
    void testCancelOrder() {
        handler.cancelOrder(order2Generated.getOrderID());

        assertEquals(OrderStatus.CANCELED, order2Generated.getOrderStatus());
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

        assertNotEquals(OrderStatus.COMPLETED, order2Generated.getOrderStatus());
        assertFalse(handler.getCompletedOrders().contains(order2Generated));
    }

    @Test
    void testCancelStartedOrder() {
        handler.startOrder(String.valueOf(order1Existing.getOrderID()));
        handler.cancelOrder(String.valueOf(order1Existing.getOrderID()));

        assertEquals(OrderStatus.CANCELED, order1Existing.getOrderStatus());
        assertFalse(handler.getStartedOrders().contains(order1Existing));
    }

    @Test
    void testCancelCompletedOrder() {
        handler.startOrder(String.valueOf(order1Existing.getOrderID()));
        handler.completeOrder(String.valueOf(order1Existing.getOrderID()));
        handler.cancelOrder(String.valueOf(order1Existing.getOrderID()));

        assertEquals(OrderStatus.CANCELED, order1Existing.getOrderStatus());
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