package edu.ics372;

import java.util.HashMap;
import java.util.Map;

public class SessionAnalytics
{
    private static SessionAnalytics instance;
    private final Map<String, Record> recordMap = new HashMap<>(); //OrderID, and the record of the order with that ID
    private OrderMetrics orderMetrics;

    private SessionAnalytics()
    {}

    // ------------------------------- Relates to the formation of class resources -------------------------------------
    /**
     * This will add the passed record to the recordMap, This will only be called after importing an order, and the
     *  creation of a respective Record. This method assumes that the importing method will not pass a null record
     * @param record The record that will be added to the map, with the id of the order contained within being the key
     */
    public void addRecord(Record record)
    {
        String id = record.getRecordedOrder().getOrderID();
        recordMap.put(id, record);
    }

    public static SessionAnalytics getInstance()
    {
        if(instance == null)
        { instance = new SessionAnalytics(); }
        return instance;
    }

    // -------------------------------------- Analytical methods -------------------------------------------------------

    public Record getRecord(String orderId) {
        return recordMap.get(orderId);
    }

    public int getTotalTrackedCount() {
        return recordMap.size();
    }

    public int getCompletedCount() {
        int count = 0;
        for (Record r : recordMap.values()) {
            if (r.completionTime() > 0) count++;
        }
        return count;
    }

    // Returns the average completion time in seconds across all completed orders, or 0 if none.
    public long getAverageCompletionTimeMs() {
        long total = 0;
        int count = 0;
        for (Record r : recordMap.values()) {
            long ct = r.completionTime();
            if (ct > 0) { total += ct; count++; }
        }
        return count > 0 ? total / count : 0;
    }
}
