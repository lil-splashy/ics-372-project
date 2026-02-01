import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
// "E" should be Orders when created
public class OrderHandler<E> {
    //Instance variables to keep orders based on status
    private LinkedList<E> incomingOrders;
    private LinkedList<E> startedOrders;
    private LinkedList<E> completedOrders;

    //Using map to associate orders with an id per instruction 3
    private Map<String, E> ordersById;


    //Constructor creates linked list depending on status and a map for associating orders with their ID
    public OrderHandler() {
        this.incomingOrders = new LinkedList<>();
        this.startedOrders = new LinkedList<>();
        this.completedOrders = new LinkedList<>();
        //used for look up.
        this.ordersById = new HashMap<>();
    }

    //getters for the linked lists
    public LinkedList<E> getIncomingOrders() {
        return incomingOrders;
    }

    public LinkedList<E> getStartedOrders() {
        return startedOrders;
    }

    public LinkedList<E> getCompletedOrders() {
        return completedOrders;
    }

    /**
     * Methods used to categorize orders by their status type
     * Also a method that categorizes all orders by their ID (hashmap)
     */
    //loads all orders from parser into an incoming orders linkedList
    // need to figure out how to check for nulls and skip incase there is a gap in list.
    public void loadOrders(List<E> parsedOrders) {
        for (E order : parsedOrders) {
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
            E order = ordersById.get(id);
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
            E order = ordersById.get(id);
            order.getOrderStatus("completed");
            completedOrders.add(order);
            startedOrders.remove(order);
        } else {
            System.out.println("Can't complete an order that hasn't been started yet.");
        }
    }


    //going to be used to grab an order by its order id using hashmap;
    public E getOrder(String id) {
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
    public E changeStatus(T order) {

    }

    public E displayOrder(T order) {

    }

    public void displayUncompletedOrders() {
        // display incoming and started orders linkedlist
        // Call order method for price
        // getOrderPrice()
        // price total
        double totalPriceUncompletedOrders = 0;
        System.out.println("Incoming Orders: ");
        for(E Order : incomingOrders){
            System.out.println(Order);
            totalPriceUncompletedOrders += Order.getOrderPrice();
        }

        System.out.println("Started Orders: ");
        for(E Order : startedOrders){
            System.out.println(Order);
            totalPriceUncompletedOrders += Order.getOrderPrice();
        }
    }

    public E displayCompletedOrders() {
        //display completedOrders linkedlist
        System.out.println("Completed Orders: ");
        for (E Order : completedOrders) {
            System.out.println(Order);
        }

//        public E modifyOrder (T order){
//
//        }

//    public E exportOrder(T order) {
//
//    }

    }

}