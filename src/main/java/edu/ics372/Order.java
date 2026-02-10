package edu.ics372;
import java.util.concurrent.ThreadLocalRandom;
public class Order {

    // attributes of an order
    private final String orderID;
    private long orderDate;
    private String orderStatus;
    private String orderType;
    private double orderPrice;
    // the item array of items for the unique order
    private Item[] items;
    private int itemCount;

    // the order constructor
    public Order(long orderDate, String orderStatus, String orderType, int maxItems) {
        this.orderID = generateOrderID(); //creates a unique random id to track
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.orderType = orderType;
        this.items = new Item[maxItems];
        this.itemCount = 0;
    }

    /**
     * adds an Item Object to a item array
     *
     * @param item takes Item Object
     */
    public void addItem(Item item){
        if (itemCount < items.length){
            items[itemCount] = item;
            itemCount++;
            orderPrice += item.getItemPrice();
        } else {
            System.out.println("Order is full");
        }
    }

    /**
     * generates a unique order ID with a random letter and 12 digit number
     *
     * @return a string of both randletter and 12integers
     */
    private static String generateOrderID() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        char letter = (char) ('A' + rnd.nextInt(26));
        long number = rnd.nextLong(100_000_000_000L, 1_000_000_000_000L);

        return letter + Long.toString(number);
    }


    // returns items
    public Item[] getItems() {
        return items;
    }

    // getters and setters
    public String getOrderID() {
        return orderID;
    }
//    public void setOrderID(String orderID {
//        this.orderID = orderID;
//    } FOR FUTURE BUILD

    public long getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(long orderDate) {
        this.orderDate = orderDate;
    }


    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderType() {
        return orderType;
    }
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public double getOrderPrice() {
        return orderPrice;
    }
    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }



    // simply returns a readable order
    @Override
    public String toString() {
        StringBuilder exitLook = new StringBuilder(
                "\nOrder { " +
                        "\n\torderID = " + orderID +
                        "\n\torderStatus = " + orderStatus +
                        "\n\torderType = " + orderType +
                        "\n\torderDate = " + orderDate);
        for (int i = 0; i < items.length; i++){
            if (items[i] != null) {
                exitLook.append(items[i].toString());
            }
        }
        exitLook.append("\n\tTotal Order Price = " + orderPrice +
                "\n}");
        return exitLook.toString();
    }

}
