package edu.ics372

class Item(
        private var itemID: String,
        private var itemName: String,
        private var itemPrice: Double,
        private var itemQuantity: Int,
        private var warehouseLocation: String
) {

    fun getItemID(): String = itemID
    fun setItemID(itemID: String) {
        this.itemID = itemID
    }

    fun getItemName(): String = itemName
    fun setItemName(itemName: String) {
        this.itemName = itemName
    }

    fun getItemPrice(): Double = itemPrice
    fun setItemPrice(itemPrice: Double) {
        this.itemPrice = itemPrice
    }

    fun getItemQuantity(): Int = itemQuantity
    fun setItemQuantity(itemQuantity: Int) {
        this.itemQuantity = itemQuantity
    }

    fun getWarehouseLocation(): String = warehouseLocation
    fun setWarehouseLocation(warehouseLocation: String) {
        this.warehouseLocation = warehouseLocation
    }

    override fun toString(): String {
        return """
            
            Item{
                itemID = $itemID
                itemName = $itemName
                itemQuantity = $itemQuantity
                itemPrice = $itemPrice
                itemWarehouseLocation = $warehouseLocation
            }
        """.trimIndent()
    }
}
