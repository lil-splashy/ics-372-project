package edu.ics372;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
// "E" should be Orders when created
public class OrderHandler {
    //Instance variables to keep orders based on status
    private LinkedList<Order> incomingOrders;
    private LinkedList<Order> startedOrders;
    private LinkedList<Order> completedOrders;

    //Using map to associate orders with an id per instruction 3
    private Map<String,Order> ordersById;

    //Using map to store canceled orders
    private Map<String, Order> canceledOrders;

    //Calling parserInterface to have methods that can load and save data using the parser class
    private ParserInterface parser = new Parser();

    //Constructor creates linked list depending on status and a map for associating orders with their ID
    public OrderHandler() {
        this.incomingOrders = new LinkedList<>();
        this.startedOrders = new LinkedList<>();
        this.completedOrders = new LinkedList<>();
        //used for look up.
        this.ordersById = new HashMap<>();
        //used to record canceled orders
        this.canceledOrders = new HashMap<>();
    }

    //getters for the linked lists
    public LinkedList<Order> getIncomingOrders(){
        return incomingOrders;
    }
    public LinkedList<Order> getStartedOrders(){
        return startedOrders;
    }
    public LinkedList<Order> getCompletedOrders(){
        return completedOrders;
    }


    // Takes the JsonParser object created in UserInterface with the file path
    public void loadOrders(JsonParser parser) {

        Order order = parser.parseFile(parser.getFilePath());

        if (order == null) {
            System.out.println("No order loaded from file");
            return;
        }

        System.out.print("Order successfully loaded :D \n");

        order.setOrderStatus("incoming");
        incomingOrders.add(order);
        ordersById.put(order.getOrderID(), order);
    }

    // when prompted by user interface move specific incoming orders to started orders.
    public void startOrder(String id) {
        if (ordersById.get(id) == null) {
            System.out.println("No order associated with this id");
            return;
        }
        if (ordersById.get(id).getOrderStatus().equals("incoming")){
            Order order = ordersById.get(id);
            order.setOrderStatus("started");
            startedOrders.add(order);
            incomingOrders.remove(order);
        } else {
            System.out.println("Can't start an order that has already been started or completed");
        }

    }

    //method used to cancel an order and store in hashmap of canceled orders
    //do we want to be able to cancel any orders? even if completed but not shipped?
    public void cancelOrder(String id) {
        if(ordersById.get(id) == null){
            System.out.println("No order associated with the provided id");
            return;
        }
        Order canceledOrder = ordersById.get(id);
        String orderStatus = canceledOrder.getOrderStatus();
        if (orderStatus == null){
            System.out.println("Order status is missing.");
            return;
        }

        switch(orderStatus){
            case "incoming":
                canceledOrders.put(id,canceledOrder);
                incomingOrders.remove(canceledOrder);
                canceledOrder.setOrderStatus("canceled");
                System.out.println("Order has been removed from incoming orders and added to canceled orders.");
                break;
            case "started":
                canceledOrders.put(id,canceledOrder);
                startedOrders.remove(canceledOrder);
                canceledOrder.setOrderStatus("canceled");
                System.out.println("Order has been removed from started orders and added to canceled orders.");
                break;
            case "completed":
                canceledOrders.put(id,canceledOrder);
                completedOrders.remove(canceledOrder);
                canceledOrder.setOrderStatus("canceled");
                System.out.println("Order has been removed from completed orders and added to canceled orders.");
                break;
            case "canceled":
                System.out.println("Order has already been canceled");
                break;
            default:
                System.out.println("order has not been fully processed or loaded.");
        }

    }

    // When prompted by user interface move started order to completed linked list
    public void completeOrder(String id) {
        if (ordersById.get(id) == null) {
            System.out.println("No order associated with this id");
            return;
        }
        if (ordersById.get(id).getOrderStatus().equals("started")){
            Order order = ordersById.get(id);
            order.setOrderStatus("completed");
            completedOrders.add(order);
            startedOrders.remove(order);
        } else {
            System.out.println("Can't complete an order that hasn't been started yet.");
        }
    }


    //going to be used to grab an order by its order id using hashmap;
    public Order getOrder(String id){
        if(ordersById.get(id) == null){
            System.out.println("No order associated with this id");
            return null;
        }
        return ordersById.get(id);
    }


    // Display uncompleted orders
    public void displayUncompletedOrders() {
        // display incoming and started orders linked list
        // Call order method for price
        // getOrderPrice()
        // price total
        double totalPriceUncompletedOrders = 0;
        System.out.println("Incoming Orders: ");
        for(Order order : incomingOrders){
            System.out.println(order);
            totalPriceUncompletedOrders += order.getOrderPrice();
        }

        System.out.println("Started Orders: ");
        for(Order order : startedOrders){
            System.out.println(order);
            totalPriceUncompletedOrders += order.getOrderPrice();
        }

        System.out.println("Total price: " + totalPriceUncompletedOrders);
    }

    // Displays completed orders
    public void displayCompletedOrders() {
        //display completedOrders linked list
        System.out.println("Completed Orders: ");
        for (Order order : completedOrders) {
            System.out.println(order);
        }
    }

    public void displayCanceledOrders(){
        System.out.println("Canceled Orders: ");
        for (Order order: canceledOrders.values()){
            System.out.println(order);
        }
    }

    // Calculates total price of uncompleted orders
    public double totalPriceUncompletedOrders(){
        double totalPrice = 0;

        for(Order order : incomingOrders){
            totalPrice += order.getOrderPrice();
        }

        for(Order order : startedOrders){
            totalPrice += order.getOrderPrice();
        }

        return totalPrice;
    }

    public void saveData(String filePath){
        List<Order> allOrders = new ArrayList<>(ordersById.values());
        parser.exportJSON(allOrders,filePath);
    }


    static void main (String [] args) {

    }
}