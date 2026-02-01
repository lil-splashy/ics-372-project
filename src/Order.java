public class Order {


    private int orderID;
    private long orderDate;
    private String orderStatus;
    private String orderType;
    private double orderPrice;

    private Item[] items;
    private int itemCount;

    public Order(int orderID, long orderDate,
                 String orderStatus, String orderType, int maxItems) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.orderType = orderType;
        this.items = new Item[maxItems];
        this.itemCount = 0;
    }

    public void addItem(Item item){
        if (itemCount < items.length){
            items[itemCount] = item;
            itemCount++;
            orderPrice += item.getItemPrice();
        } else {
            System.out.println("Order is full");
        }
    }


    // getters and setters


    public Item[] getItems() {
        return items;
    }

    public int getOrderID() {
        return orderID;
    }
    public void setOrderID(int orderID) {
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
