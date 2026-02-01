import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonParser implements parserInterface {


    // Hardcoded file path. Will replace later with user input.
    private String orderPath = "../order.json";

    /**
     *
     * @param filePath - Path to order.json
     * @return
     */
    public <T> parseFile(String filePath) {

        try {
            // Read file and create object
            String content = new String(Files.readAllBytes(Paths.get(orderPath)));
            JSONObject jsonObject = new JSONObject(content);


            JSONObject order = jsonObject.getJSONObject("order");
            private String orderType = order.getString("type");
            private long orderDate = order.getLong("order_date");


            JSONArray items = order.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                String name = item.getString("name");
                int quantity = item.getInt("quantity");
                double price = item.getDouble("price");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
    }

    public static void main(String[] args) {

            // Hardcoded file path. Will replace later with user input.
            private String orderPath = "../order.json";

            parseFile(orderPath);


    }
}
