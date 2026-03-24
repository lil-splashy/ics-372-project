package edu.ics372;

import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JsonParser implements ParserInterface {


    private  String filePath = "order.json";

    public String getFilePath() {
        return filePath;
    }

    public void setNewPath(String newPath) {
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
            Order newOrder = new Order(orderDate, "NEW", orderType, items.length(), null, null);

            for (int j = 0; j < items.length(); j++) {
                JSONObject item = items.getJSONObject(j);
                String name = item.getString("name");
                double price = item.getDouble("price");
                int quantity = item.getInt("quantity");

                Item newItem = new Item("I" + j, name, price, quantity, null);
                newOrder.addItem(newItem);
            }
            return newOrder;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    private JSONObject orderToJson(Order order) {
        JSONObject orderJson = new JSONObject();

        orderJson.put("orderID", order.getOrderID());
        orderJson.put("order_date", order.getOrderDate());
        orderJson.put("status", order.getOrderStatus());
        orderJson.put("type", order.getOrderType());

        // Convert items to JSON array
        JSONArray itemsArray = new JSONArray();
        Item[] items = order.getItems();

        if (items != null) {
            for (Item item : items) {
                JSONObject itemJson = new JSONObject();
                itemJson.put("itemID", item.getItemID());
                itemJson.put("name", item.getItemName());
                itemJson.put("price", item.getItemPrice());
                itemJson.put("quantity", item.getItemQuantity());
                itemsArray.put(itemJson);
            }
        }

        orderJson.put("items", itemsArray);
        orderJson.put("item_count", itemsArray.length());

        return orderJson;
    }


    public void exportJSON(List<Order> orders, String filePath) {

        try {
            JSONObject rootObject = new JSONObject();

            JSONArray ordersArray = new JSONArray();
            for (Order order : orders) {
                ordersArray.put(orderToJson(order));
            }
            rootObject.put("orders", ordersArray);

            String jsonString = rootObject.toString(4);
            Files.write(Paths.get(filePath), jsonString.getBytes(StandardCharsets.UTF_8));
            System.out.println("Exported JSON to:" + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads previously saved program orders from a JSON file.
     * This is different from parsing a brand-new incoming order file because
     * these orders already contain program state such as their saved order ID
     * and current status.
     *
     * @param filePath path to the JSON file containing saved program orders
     * @return a list of Order objects rebuilt from the save file
     */
    public List<Order> importProgramOrders(String filePath){
        //stores a list of orders that were imported from JSON file
        List<Order> importedOrders = new ArrayList<>();

        try{
            // read the entire JSON file to a string, then convert it into a root JSON object.
            String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            JSONObject rootObject = new JSONObject(content);
            //Get the array of saved orders from the root object
            JSONArray ordersArray = rootObject.getJSONArray("orders");

            //loop through each saved order in the JSON file
            for (int i = 0; i < ordersArray.length(); i++){
                JSONObject orderJson = ordersArray.getJSONObject(i);

                //read the saved order fields needed to rebuild the order object
                String orderID = orderJson.getString("orderID");
                long orderDate = orderJson.getLong("order_date");
                String status = orderJson.getString("status");
                String type = orderJson.getString("type");
                JSONArray itemsArray = orderJson.getJSONArray("items");

                //rebuilds the saved order using the import constructor so the original order ID is kept.
                //might need to assign warehouses somewhere and add here when orders are assigned to them.
                Order importedOrder = new Order(orderID, orderDate, status, type, itemsArray.length(), null, null);

                //loop through each saved item and rebuild it before adding it back to the order
                for (int j = 0; j < itemsArray.length(); j++){
                    JSONObject itemJson = itemsArray.getJSONObject(j);

                    String itemID = itemJson.getString("itemID");
                    String name = itemJson.getString("name");
                    double price = itemJson.getDouble("price");
                    int quantity = itemJson.getInt("quantity");

                    //same warehouse situation here for the item
                    Item importedItem = new Item(itemID, name, price, quantity, null);
                    importedOrder.addItem(importedItem);
                }

                //adds the completed order to the list of imported orders
                importedOrders.add(importedOrder);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        //returns all orders loaded from the program save file
        return importedOrders;
    }
    public static void main (String[] args) {
        System.out.print("New order created!");
    }

}
