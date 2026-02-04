
import java.util.*;

public class parserTest {


    public static void main (String[] args) {

        JsonParser parser = new JsonParser();
        Order testOrder = parser.parseFile(parser.getFilePath());
        System.out.println("NEW ORDER:");

        System.out.println("ORDER ID:" + testOrder.getOrderID());
        System.out.println("Order DATE:" + testOrder.getOrderDate());
        System.out.println("Order Status: " + testOrder.getOrderStatus());


    }


}