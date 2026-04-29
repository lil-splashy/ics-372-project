package edu.ics372

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

/**
 * Generates random orders and adds them to the OrderHandler at random intervals.31
 */
class RandomOrderGenerator(
    private val handler: OrderHandler,
    private val warehouse: Warehouse,
    private val minDelay: Int = 10,
    private val maxDelay: Int = 60
) {

    /** Called on the generator thread after each order is added. Use Platform.runLater() for UI updates. */
    var onOrderGenerated: Runnable? = null

    var isRunning: Boolean = false
        private set

    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "order-generator").also { it.isDaemon = true }
    }

    private val itemPool: List<Item> = loadItemPool()

    fun start() {
        isRunning = true
        scheduleNext()
    }

    fun stop() {
        isRunning = false
        scheduler.shutdownNow()
    }

    // ── internals ────────────────────────────────────────────────────────────

    private fun scheduleNext() {
        if (!isRunning) return
        val delay = ThreadLocalRandom.current().nextInt(minDelay, maxDelay + 1)
        scheduler.schedule({
            if (!isRunning) return@schedule
            generateOrder()
            scheduleNext()
        }, delay.toLong(), TimeUnit.SECONDS)
    }

    private fun generateOrder() {
        val rnd = ThreadLocalRandom.current()

        val orderType = ORDER_TYPES[rnd.nextInt(ORDER_TYPES.size)]
        val itemCount = rnd.nextInt(1, 6) // 1–5 items

        val order = Order.Builder()
            .setSourcePrefix("J")
            .setOrderDate(System.currentTimeMillis())
            .setOrderStatus("incoming")
            .setOrderType(orderType)
            .setMaxItems(itemCount)
            .setWarehouse(warehouse)
            .build()

        itemPool.shuffled().take(itemCount).forEach { catalogItem ->
            val qty = rnd.nextInt(1, 4)
            order.addItem(Item(
                "G${rnd.nextInt(1000, 9999)}",
                catalogItem.itemName,
                catalogItem.itemPrice,
                qty,
                catalogItem.warehouseLocation
            ))
        }
        // Uses order handler to add order.
        handler.addOrder(order)
        onOrderGenerated?.run()
    }

    private fun loadItemPool(): List<Item> {
        val catalogPath = "src/main/orders/Item-Catalog.xml"
        val pool = XmlParser().parseFile(catalogPath)
            .flatMap { order -> order.items?.filterNotNull() ?: emptyList() }
        return pool
    }

    // ── static data ──────────────────────────────────────────────────────────

    companion object {
        private val ORDER_TYPES = arrayOf("Delivery", "Pickup", "Shipped", "Direct Delivery")
    }
}