import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JsonParser implements ParserInterface {


    // Hardcoded file path. Will replace later with user input.
    private String filePath = "../order.json";

    /**
     *
     * @param filePath - Path to order.json
     * @return
     */

    public String getFilePath(){
        return filePath;
    }

    //
    public void setNewPath(String newPath){
        this.filePath = newPath;
    }





    public Order parseFile(String filePath) {
        try {

            // Read file and create object
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);


            JSONObject order = jsonObject.getJSONObject("order");
            String orderType = order.getString("type");
            long orderDate = order.getLong("order_date");


            JSONArray items = order.getJSONArray("items");


            // Add create new order
            Order newOrder = new Order("O0", orderDate, "NEW", orderType, items.length());

            for (int j = 0; j < items.length(); j++) {
                JSONObject item = items.getJSONObject(j);
                String name = item.getString("name");
                double price = item.getDouble("price");
                int quantity = item.getInt("quantity");

                Item newItem = new Item("I" + j, name, price, quantity);
                newOrder.addItem(newItem);
            }
            return newOrder;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // export JSON list
//    public String exportJSON(E orderList, String filePath) {
//            while (orderList.order.next != null) {
//
//            }
//        }
//
//        }
//
//
//
     public static void main (String[] args) {
            System.out.print("New ORDER");
        }


}
