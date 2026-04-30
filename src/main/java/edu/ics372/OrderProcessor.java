package edu.ics372;

import java.util.concurrent.*;

/**
 * Defines a service class responsible for asynchronous order processing using a thread executor
 */
public class OrderProcessor {
    // Creates a single-thread executor to process orders sequentially in a background thread
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * A Public method that submits an order to be processed asynchronously
     * Submits a runnable task to the executor for background execution
     * Retrieves the unique identifier of the order being processed
     * Attempts to acquire a lock for this order ID to prevent duplicate concurrent processing
     *      Logs that the order is already being processed elsewhere
     *      Exits early if the lock cannot be acquired
     *   Begins protected section where order processing logic occurs
     *      Print Simulates processing activity for the order
     *    Finally Ensures lock release happens regardless of success or failure in processing block
     *      Releases the lock associated with the order ID to allow future processing
     * @param order
     */
    public void process(Order order) {

        executor.execute(() -> {

            String id = order.getOrderID();

            if (!OrderLock.tryLock(id)) {
                System.out.println("Order already locked elsewhere: " + id);
                return;
            }

            try {
                System.out.println("Processing order: " + id);
            } finally {
                OrderLock.unlock(id);
            }
        });
    }

    /**
     * Initiates an orderly shutdown of the executor service
     * Prevents new tasks from being submitted while allowing existing tasks to finish
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Attempts to forcefully shut down the executor immediately
     * NOTE: Intended to be shutdownNow(), but currently calls shutdown() (same as above behavior)
     */
    public void shutdownNow() {
        executor.shutdown();
    }

    /**
     * Waits for the executor to finish all tasks within a given timeout period
     * Begins attempt to wait for executor termination
     *      Blocks until termination or timeout, returning success status
     *   Handles interruption while waiting for termination
     *   Restores interrupt status for proper thread behavior
     *      Return False Indicates termination was not completed successfully
     * @param timeoutSeconds
     * @return
     */
    public boolean awaitTermination(long timeoutSeconds) {
        try {
            return executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}