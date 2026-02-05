import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JsonParser implements ParserInterface {

    public JsonParser() {

    }
    // Hardcoded file path. Will replace later with user input.
    private String filePath = "../order.json";


    public String getFilePath(){
        return filePath;
    }

    //
    public void setNewPath(String newPath){
        this.filePath = newPath;
    }


    /**
     *
     * @param filePath - Path to order.json
     * @return
     */
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



    /**
     *
     * @param Order to convert
     * @return Json object to be exported
     */
    private static JSONObject orderToJson(Order order) {
        JSONObject orderJson = new JSONObject();

            // Add order properties (adjust field names based on your Order class)
            orderJson.put("orderID", order.getOrderId());
            orderJson.put("orderDate", order.getOrderDate());
            orderJson.put("status", order.getStatus());
            orderJson.put("type", order.getType());

            // Convert items to JSON array
            JSONArray itemsArray = new JSONArray();
            List<Item> items = order.getItems(); // Assuming you have a getItems() method

            if (items != null) {
                for (Item item : items) {
                    JSONObject itemJson = new JSONObject();
                    itemJson.put("itemID", item.getItemId());
                    itemJson.put("name", item.getName());
                    itemJson.put("price", item.getPrice());
                    itemJson.put("quantity", item.getQuantity());
                    itemsArray.put(itemJson);
                }
            }

            orderJson.put("items", itemsArray);
            orderJson.put("item_count", itemsArray.length());

            return orderJson;
        }

    /**
     *
     * @param order - Order to be exported
     * @param filePath - Desired filepath for export
     * @return if 1 - Successfull, 0 - failed
     */
    public void exportJSON(Order order, String filePath) {

        try {
            JSONObject rootObject = new JSONObject();

            // If it's a single order
            if (order.next == null) {
                JSONObject orderJson = orderToJson(order);
                rootObject.put("order", orderJson);
            }

            // Write to file with pretty printing
            String jsonString = rootObject.toString(4); // 4 spaces indentation
            Files.write(Paths.get(filePath), jsonString.getBytes(StandardCharsets.UTF_8));
            system.out.println("Exported JSON to:" + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }


     public static void main (String[] args) {
            System.out.print("New order created!");
        }



}
