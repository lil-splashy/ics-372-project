import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JsonParser<T> implements parserInterface<T> {


    // Hardcoded file path. Will replace later with user input.
    private String filePath = "../order.json";

    /**
     *
     * @param filePath - Path to order.json
     * @return
     */
    public T parseFile(String filePath) {

        println("Grabbing: " + filePath + ".....");
        try {

            // Read file and create object
            String content = new String(Files.readAllBytes(Paths.get(orderPath)));
            JSONObject jsonObject = new JSONObject(content);

//            Loop for parsing multiple orders
//            for (int i = 0; i < order.length(); i++) {
                JSONObject order = jsonObject.getJSONObject("order");
                private String orderType = order.getString("type");
                private date orderDate = order.getLong("order_date");


                JSONArray items = order.getJSONArray("items");

                // Add create new order
                Order newOrder = Order("O" + i, orderDate, "NEW", orderType, items.length());

                for (int j = 0; j < items.length(); j++) {
                    JSONObject item = items.getJSONObject(i);
                    String name = item.getString("name");
                    int quantity = item.getInt("quantity");
                    double price = item.getDouble("price");
                    Item newItem = Item("I" + 1, item.name, item.quantity, item.price);
                    newOrder.additem(newItem);
                }
            return newOrder;
//            }
        }
        catch (Exception e) {
            e.printStackTrace();
    }


    // export JSON list
    public T exportJSON(T orderList, String filePath) {
            while (orderList.order.next != null) {

            }
        }

        }




    public static void main(String[] args) {


    }
}
