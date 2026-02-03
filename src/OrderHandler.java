import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
// "E" should be Orders when created
public class OrderHandler<Order> {
    //Instance variables to keep orders based on status
    private LinkedList<Order> incomingOrders;
    private LinkedList<Order> startedOrders;
    private LinkedList<Order> completedOrders;

    //Using map to associate orders with an id per instruction 3
    private Map<String, Order> ordersById;


    //Constructor creates linked list depending on status and a map for associating orders with their ID
    public OrderHandler() {
        this.incomingOrders = new LinkedList<>();
        this.startedOrders = new LinkedList<>();
        this.completedOrders = new LinkedList<>();
        //used for look up.
        this.ordersById = new HashMap<>();
    }

    //getters for the linked lists
    public LinkedList<Order> getIncomingOrders() {
        return incomingOrders;
    }

    public LinkedList<Order> getStartedOrders() {
        return startedOrders;
    }

    public LinkedList<Order> getCompletedOrders() {
        return completedOrders;
    }

    /**
     * Methods used to categorize orders by their status type
     * Also a method that categorizes all orders by their ID (hashmap)
     */
    //loads all orders from parser into an incoming orders linkedList
    // need to figure out how to check for nulls and skip incase there is a gap in list.
    public void loadOrders(List<Order> parsedOrders) {
        for (Order order : parsedOrders) {
            String id = order.getOrderId();
            incomingOrders.add(order);
            ordersById.put(id, order);
            order.getOrderStatus("incoming");
        }
    }

    // when prompted by user interface move specific incoming orders to started orders.
    public void startOrder(String id) {
        if (ordersById.get(id) == null) {
            System.out.println("No order associated with this id");
            return;
        }
        if (ordersById.get(id).getOrderStatus().equals("incoming")) {
            Order order = ordersById.get(id);
            order.setOrderStatus("started");
            startedOrders.add(order);
            incomingOrders.remove(order);
        } else {
            System.out.println("Can't start an order that has already been started or completed");
        }

    }

    // When prompted by user interface move started order to completed linkedlist
    public void completeOrder(String id) {
        if (ordersById.get(id) == null) {
            System.out.println("No order associated with this id");
            return;
        }
        if (ordersById.get(id).getOrderStatus().equals("started")) {
            Order order = ordersById.get(id);
            order.getOrderStatus("completed");
            completedOrders.add(order);
            startedOrders.remove(order);
        } else {
            System.out.println("Can't complete an order that hasn't been started yet.");
        }
    }


    //going to be used to grab an order by its order id using hashmap;
    public Order getOrder(String id) {
        if (ordersById.get(id) == null) {
            System.out.println("No order associated with this id");
            return null;
        }
        return ordersById.get(id);
    }

    //getting the total price of all orders that arent complete yet
    public double totalPrice(HashMap<String, E> orders) {
        //loop through hashmap
        // add total price from inside orders if status != complete
        //return price
    }

    /*
        Start of Chris edits oman
        plz don't get angry Joey }: I'm trying my best

     */
//    public E changeStatus(T order) {
//
//    }
//
    public void displayOrder(Order order) {
        System.out.println(order);
    }

    public void displayUncompletedOrders() {
        // display incoming and started orders linkedlist
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

    public void displayCompletedOrders() {
        //display completedOrders linkedlist
        System.out.println("Completed Orders: ");
        for (Order order : completedOrders) {
            System.out.println(order);
        }

//        public E modifyOrder (Order order){
//
//        }

//    public E exportOrder(Order order) {
//
//    }

    }

}