public class Order {


    private String orderID;
    private long orderDate;
    private String orderStatus;
    private String orderType;
    private double orderPrice;
    // the item array of items for the unique order
    private Item[] items;
    private int itemCount;

    // the order constructor
    public Order(String orderID, long orderDate,
                 String orderStatus, String orderType, int maxItems) {
        this.orderID = orderID;
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




    // returns items
    public Item[] getItems() {
        return items;
    }

    // getters and setters
    public String getOrderID() {
        return orderID;
    }
    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

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
        return "Order{" +
                "orderID=" + orderID +
                ", orderDate=" + orderDate +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderType='" + orderType + '\'' +
                ", orderPrice=" + orderPrice +
                '}';
    }

}
