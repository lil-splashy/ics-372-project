package edu.ics372

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

/**
 * Generates random orders and adds them to the OrderHandler at random intervals.
 *
 * Usage:
 *   val gen = RandomOrderGenerator(handler, warehouse, minDelay = 10, maxDelay = 60)
 *   gen.onOrderGenerated = { Platform.runLater { myView.refreshOrders() } }
 *   gen.start()
 *   ...
 *   gen.stop()
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

        val order = Order(System.currentTimeMillis(), "incoming", orderType, itemCount, warehouse)

        ITEM_POOL.shuffled().take(itemCount).forEach { (name, price, location) ->
            val itemID = "G${rnd.nextInt(1000, 9999)}"
            val qty    = rnd.nextInt(1, 4)
            order.addItem(Item(itemID, name, price, qty, location))
        }

        handler.addOrder(order)
        println("[RandomOrderGenerator] Added order ${order.orderID}")
        onOrderGenerated?.run()
    }

    // ── static data ──────────────────────────────────────────────────────────

    companion object {
        private val ORDER_TYPES = arrayOf("Delivery", "Pickup", "Shipped", "Direct Delivery")

        private data class ItemTemplate(val name: String, val price: Double, val location: String)

        private val ITEM_POOL = listOf(
            ItemTemplate("Perfect Gold Bar",       250.00, "A1"),
            ItemTemplate("Perfect Necklace",       180.00, "A2"),
            ItemTemplate("Perfect Ring",           150.00, "A3"),
            ItemTemplate("2-5ths Full Bucket",       5.00, "B1"),
            ItemTemplate("31070",                   10.00, "B2"),
            ItemTemplate("Abyssal Lantern",        320.00, "B3"),
            ItemTemplate("Adamant Full Helm",       95.00, "C1"),
            ItemTemplate("Adventurer's Boots",     130.00, "C2"),
            ItemTemplate("Agility Potion",          12.50, "C3"),
            ItemTemplate("Agility Tome",            75.00, "D1"),
            ItemTemplate("Ahab's Beer",              3.50, "D2"),
            ItemTemplate("Alchemical Chart Icon",   45.00, "D3"),
            ItemTemplate("Ancestral Hat",          500.00, "E1"),
            ItemTemplate("Ancestral Robe Bottom",  480.00, "E2"),
            ItemTemplate("Ancestral Robe Top",     490.00, "E3"),
            ItemTemplate("Ancestral Robes Set",   1400.00, "E4"),
            ItemTemplate("Ancient Brew",            22.00, "F1"),
            ItemTemplate("Anti-poison Supermix",    18.00, "F2"),
            ItemTemplate("Anti-venom",              30.00, "F3"),
            ItemTemplate("Anti-venom+",             55.00, "F4"),
            ItemTemplate("Arcane Grimoire",        275.00, "G1"),
            ItemTemplate("Armadyl Brew",            28.00, "G2"),
            ItemTemplate("Lightning Bolt Scroll",   85.00, "G3"),
        )
    }
}