import java.util.Date;


public class Order {

    private int orderID;
    private Date orderDate;
    private String orderStatus;
    private String orderType;
    private double orderPrice;

    public Order() {
    }

    public Order(int orderID, Date orderDate,
                 String orderStatus, String orderType, double orderPrice) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.orderType = orderType;
        this.orderPrice = orderPrice;
    }

    // getters and setters
    public int getOrderID() {
        return orderID;
    }
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public Date getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(Date orderDate) {
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
