package edu.ics372;

import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JsonParser implements ParserInterface {


    private  String filePath = "src/main/orders/order.json";

    public String getFilePath() {
        return filePath;
    }

    public void setNewPath(String newPath) {
        this.filePath = newPath;
    }


    public List<Order> parseFile(String filePath) {
        List<Order> orders = new ArrayList<>();

        try {
            // Read file and create objects
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);

            // Check if the JSON contains an array of orders or a single order
            if (jsonObject.has("orders")) {
                // Handle multiple orders
                JSONArray ordersArray = jsonObject.getJSONArray("orders");

                for (int i = 0; i < ordersArray.length(); i++) {
                    JSONObject orderJson = ordersArray.getJSONObject(i);
                    Order order = parseSingleOrder(orderJson, i);
                    if (order != null) {
                        orders.add(order);
                    }
                }
            } else if (jsonObject.has("order")) {
                // Handle single order (backward compatibility)
                JSONObject orderJson = jsonObject.getJSONObject("order");
                Order order = parseSingleOrder(orderJson, 0);
                if (order != null) {
                    orders.add(order);
                }
            } else {
                // Assume the root object itself is an order
                Order order = parseSingleOrder(jsonObject, 0);
                if (order != null) {
                    orders.add(order);
                }
            }

            return orders;
        } catch (Exception e) {
            System.out.println("Error parsing file: " + e.getMessage());
            return orders; // Return empty list instead of null
        }
    }

    private Order parseSingleOrder(JSONObject orderJson, int orderIndex) {
        try {
            String orderType = orderJson.getString("type");
            long orderDate = orderJson.getLong("order_date");

            JSONArray items = orderJson.getJSONArray("items");

            // Create new order via the order builder
            Order.Builder builder = new Order.Builder()
                    .setSourcePrefix("J")
                    .setOrderDate(orderDate)
                    .setOrderStatus("NEW")
                    .setOrderType(orderType)
                    .setMaxItems(items.length())
                    .setWarehouse(null);

            // JSON may include an ID, optionally support it
            if (orderJson.has("orderID")) {
                builder.setOrderID(orderJson.getString("orderID"));
            }

            Order newOrder = builder.build();

            for (int j = 0; j < items.length(); j++) {
                JSONObject item = items.getJSONObject(j);
                String name = item.getString("name");
                double price = item.getDouble("price");
                int quantity = item.getInt("quantity");

                // Use orderIndex to ensure unique item IDs across orders
                Item newItem = new Item("I" + orderIndex + "_" + j, name, price, quantity, null);
                newOrder.addItem(newItem);
            }

            return newOrder;
        } catch (Exception e) {
            System.out.println("Error parsing order " + orderIndex + ": " + e.getMessage());
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
                if (item == null){
                    continue;
                }
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

        if (order.getWarehouse() != null) {
            orderJson.put("warehouseID", order.getWarehouse().getWarehouseID());
            orderJson.put("warehouseName", order.getWarehouse().getWarehouseName());
        }

        return orderJson;
    }


    public void exportOrders(List<Order> orders, String filePath) {

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
                // use orderBuilder
                Order importedOrder = new Order.Builder()
                        .setSourcePrefix(String.valueOf(orderID.charAt(0))) // keep J/X from stored ID
                        .setOrderID(orderID) // preserve existing ID
                        .setOrderDate(orderDate)
                        .setOrderStatus(status)
                        .setOrderType(type)
                        .setMaxItems(itemsArray.length())
                        .setWarehouse(null)
                        .setImportMode(true)
                        .build();

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
