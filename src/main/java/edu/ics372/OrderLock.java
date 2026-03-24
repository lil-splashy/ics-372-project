package edu.ics372;

import java.io.File;
import java.io.IOException;

/**
 * File-based mutex for orders. Creates a .lock file when an order is started
 * so that other app instances cannot accidentally claim the same order.
 */
public class OrderLock {

    private static final String LOCK_DIR = "src/main/orders/locks/";

    /** Atomically claims the lock. Returns true if this call acquired it, false if already locked. */
    public static boolean tryLock(String orderId) {
        new File(LOCK_DIR).mkdirs();
        try {
            return new File(LOCK_DIR + orderId + ".lock").createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    /** Releases the lock for the given order. Safe to call even if not locked. */
    public static void unlock(String orderId) {
        new File(LOCK_DIR + orderId + ".lock").delete();
    }

    /** Returns true if another session holds the lock for this order. */
    public static boolean isLocked(String orderId) {
        return new File(LOCK_DIR + orderId + ".lock").exists();
    }
}