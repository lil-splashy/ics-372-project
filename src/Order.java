import java.time.LocalDate;
import java.time.LocalTime;

public class Order {

    private int orderID;
    private LocalDate orderDate;
    private LocalTime orderTime;
    private String orderStatus;
    private String orderType;
    private double orderPrice;

    public Order() {
    }

    public Order(int orderID, LocalDate orderDate, LocalTime orderTime,
                 String orderStatus, String orderType, double orderPrice) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
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

    public LocalDate getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalTime getOrderTime() {
        return orderTime;
    }
    public void setOrderTime(LocalTime orderTime) {
        this.orderTime = orderTime;
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
                ", orderTime=" + orderTime +
                ", orderStatus='" + orderStatus + '\'' +
                ", orderType='" + orderType + '\'' +
                ", orderPrice=" + orderPrice +
                '}';
    }

}
