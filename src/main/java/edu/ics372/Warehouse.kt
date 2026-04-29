package edu.ics372


/**
 * Represents a fulfillment center that determines whether it can process an order
 * based on the type/source of the order Bullseye or Wallyworld or Both
 */
class Warehouse(
        private val warehouseID: String,
        private val warehouseName: String,
        private val supportsJSON: Boolean,
        private val supportsXML: Boolean
) {
    // Returns the warehouse ID for lookup, filtering, and association with orders
    fun getWarehouseID(): String = warehouseID
    // Returns the warehouse name for display in UI and logs
    fun getWarehouseName(): String = warehouseName

    /**
     * Determines whether this warehouse can fulfill a given order based on its source type
     *     This is the core business rule that enables warehouse-specific processing logic
     *     "capability rule"
     */
    fun canFulfill(order: Order): Boolean {

        val source = order.source // "Bullseye" or "Wallyworld"

        return when (source) {
            "Bullseye" -> supportsJSON
            "Wallyworld" -> supportsXML
            else -> false
        }
    }
    // Returns a formatted string representation of the warehouse object for debugging and display
    override fun toString(): String {
        return "Warehouse(warehouseID='$warehouseID', warehouseName='$warehouseName')"
    }
}