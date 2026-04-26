package edu.ics372;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
public class Order {

    private final String orderID;   // unique identifier for each order (immutable once set)
    private long orderDate;         // timestamp or date representation of the order
    private String orderStatus;     // current status (e.g., NEW, SHIPPED, CANCELLED)
    private String orderType;       // type/category of order (e.g., ONLINE, STORE)
    private double orderPrice;      // total accumulated price of all items in the order

    private Item[] items;           // fixed-size array holding items in this order
    private int itemCount;          // tracks how many items are currently in the order

    // shared list storing ALL order IDs to enforce uniqueness across all orders
    private static final ArrayList<String> orderIDs = new ArrayList<>();

    private Warehouse warehouse;    // warehouse associated with fulfilling this order

    // PRIVATE constructor ensures objects can ONLY be created through Builder
    private Order(Builder builder) {
        this.orderID = builder.orderID;             // assign finalized ID after validation
        this.orderDate = builder.orderDate;         // copy provided date
        this.orderStatus = builder.orderStatus;     // copy provided status
        this.orderType = builder.orderType;         // copy provided type
        this.items = new Item[builder.maxItems];    // allocate item array with max size
        this.itemCount = 0;                         // initially no items added
        this.warehouse = builder.warehouse;         // assign warehouse reference
    }


    //================================ BUILDER CLASS ================================
    public static class Builder {

        private String orderID;         // may be null (for generated IDs) or provided (XML input)
        private long orderDate;
        private String orderStatus;
        private String orderType;
        private int maxItems;           // determines size of item array
        private Warehouse warehouse;    // associated warehouse

        // Construction phase Setters for optional/provided ID
        public Builder setOrderID(String orderID) {
            this.orderID = orderID;     // store raw ID (will be normalized later)
            return this;                // allows method chaining
        }
        public Builder setOrderDate(long orderDate)         {this.orderDate = orderDate; return this;}
        public Builder setOrderStatus(String orderStatus)   {this.orderStatus = orderStatus; return this;}
        public Builder setOrderType(String orderType)       {this.orderType = orderType; return this;}
        public Builder setMaxItems(int maxItems)            {this.maxItems = maxItems; return this;}
        public Builder setWarehouse(Warehouse warehouse)    {this.warehouse = warehouse; return this;}

        // central creation logic (this replaces all constructors)
        public Order build() {

            String finalID; // will hold the fully processed and validated ID

            // CASE 1: No ID provided (e.g., JSON input)
            if (orderID == null || orderID.isEmpty()) {
                // generate a random 3-digit number for prefix
                long number = ThreadLocalRandom.current().nextLong(100, 1000);

                // create ID with 'J' prefix (JSON source) + unique suffix
                finalID = "J" + number + "~" + generateOrderID();
            }
            // CASE 2: ID provided (e.g., XML input)
            else {
                // normalize ensures correct prefix and appends unique suffix if needed
                finalID = normalizeID(orderID);
            }

            // check if another order already has the same "core" ID (your substring rule)
            if (isDuplicateCore(finalID)) {
                // stop creation if duplicate detected
                throw new IllegalArgumentException("Duplicate order core ID: " + finalID);
            }

            // register the final ID so future orders can be checked against it
            registerExistingOrder(finalID);

            // store finalized ID in builder before constructing object
            this.orderID = finalID;

            // create and return the actual Order object
            return new Order(this);
        }
    }

    //================================ ID LOGIC ================================

    // ensures ID follows system rules (prefix + suffix if needed)
    private static String normalizeID(String orderID) {

        // if ID does not start with known source identifiers
        if (orderID.charAt(0) != 'J' && orderID.charAt(0) != 'X') {
            // prepend 'X' (XML source) and append generated unique suffix
            return "X" + orderID + "~" + generateOrderID();
        }
        // if already valid, return unchanged
        return orderID;
    }

    // extracts the "core" portion of the ID (ignores prefix and suffix)
    private static String extractCoreID(String id) {
        if (id == null) return "";

        int start = 1; // skip J/X

        int end = id.indexOf("~");
        if (end == -1) end = id.length();

        return id.substring(start, end);
    }

    // checks if another order already has the same core ID
    private static boolean isDuplicateCore(String newID) {
        String newCore = extractCoreID(newID); // extract core of new ID

        // compare against all existing IDs
        for (String existingID : orderIDs) {
            String existingCore = extractCoreID(existingID);

            // if match found → duplicate
            if (existingCore.equals(newCore)) {
                return true;
            }
        }
        return false; // no duplicate found
    }

    // generates a globally unique suffix for IDs
    private static String generateOrderID() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        String id;
        boolean exists;

        do {
            // random uppercase letter
            char letter = (char) ('A' + rnd.nextInt(26));

            // random 6-digit number
            long number = rnd.nextLong(100_000L, 1_000_000L);

            // combine into ID string
            id = letter + Long.toString(number);

            exists = false;

            // ensure uniqueness by checking existing IDs
            for (String currentIDs : orderIDs) {
                if (currentIDs.equals(id)) {
                    exists = true;
                    break;
                }
            }

        } while (exists); // repeat until unique

        return id; // return unique generated suffix
    }

    // adds an ID to the global list (used for duplicate tracking)
    public static void registerExistingOrder(String orderID) {
        orderIDs.add(orderID);
    }


    //================================ BUSINESS LOGIC ================================

    // adds an item to the order
    public void addItem(Item item) {

        // ensure we don't exceed allocated array size
        if (itemCount < items.length) {
            items[itemCount++] = item; // store item and increment count

            // update total price dynamically
            orderPrice += item.getItemPrice();
        } else {
            System.out.println("Order is full"); // prevent overflow
        }
    }

    // ACCESSORS (Getters) & MUTATORS (Setters)
    public Item[] getItems() {return items;}

    public String getOrderID() {return orderID;}

    public long getOrderDate() {return orderDate;}
    public void setOrderDate(long orderDate) {this.orderDate = orderDate;}

    public String getOrderStatus() {return orderStatus;}
    public void setOrderStatus(String orderStatus) {this.orderStatus = orderStatus;}

    public String getOrderType() {return orderType;}
    public void setOrderType(String orderType) {this.orderType = orderType;}

    public Warehouse getWarehouse() {return warehouse;}
    public void setWarehouse(Warehouse warehouse) {this.warehouse = warehouse;}

    public double getOrderPrice() {return orderPrice;}
    public void setOrderPrice(double orderPrice) {this.orderPrice = orderPrice;}

    // determines source system based on ID prefix
    public String getSource() {
        switch (this.orderID.charAt(0)) {
            case 'J': return "Bullseye";   // JSON source
            case 'X': return "Wallyworld"; // XML source
            default: return "Unknown Source"; // fallback
        }
    }

    // formatted string representation of the order
    @Override
    public String toString() {

        StringBuilder exitLook = new StringBuilder(
                "\nOrder { " +
                        "\n\torderID = " + orderID +
                        "\n\torderStatus = " + orderStatus +
                        "\n\torderType = " + orderType +
                        "\n\torderDate = " + orderDate +
                        "\n\twarehouse = " + (warehouse != null ? warehouse.getWarehouseName() : "Unassigned"));

        // append each item if present
        for (Item item : items) {
            if (item != null) {
                exitLook.append(item.toString());
            }
        }

        // append total price
        exitLook.append("\n\tTotal Order Price = " + orderPrice + "\n}");

        return exitLook.toString();
    }
}
