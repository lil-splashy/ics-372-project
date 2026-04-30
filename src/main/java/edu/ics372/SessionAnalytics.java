package edu.ics372;

import java.util.HashMap;
import java.util.Map;

public class SessionAnalytics
{
    private static SessionAnalytics instance;
    private final Map<String, Record> recordMap = new HashMap<>();
    private final OrderMetrics orderMetrics;

    private SessionAnalytics()
    { orderMetrics = OrderMetrics.getInstance(); }

    public static SessionAnalytics getInstance()
    {
        if (instance == null)
        { instance = new SessionAnalytics(); }
        return instance;
    }

    // ------------------------------- Record management -------------------------------------------------------

    public void addRecord(Record record)
    {
        String id = record.getRecordedOrder().getOrderID();
        recordMap.put(id, record);
    }

    public Record getRecord(String orderId)
    { return recordMap.get(orderId); }

    public int getTotalTrackedCount()
    { return recordMap.size(); }

    // ------------------------------- OrderMetrics delegates -------------------------------------------------------

    public int getOrdersImported()   { return orderMetrics.getOrdersImported(); }
    public int getOrdersStarted()    { return orderMetrics.getOrdersStarted(); }
    public int getOrdersCancelled()  { return orderMetrics.getOrdersCancelled(); }
    public int getOrdersExported()   { return orderMetrics.getOrdersExported(); }
    public int getOrdersCompleted()  { return orderMetrics.getOrdersCompleted(); }

    // ------------------------------- Analytical methods -------------------------------------------------------

    /** Returns the average completion time in seconds across completed orders, or 0 if none. */
    public long getAverageCompletionTimeMs()
    {
        long total = 0;
        int count = 0;
        for (Record r : recordMap.values())
        {
            long ct = r.completionTime();
            if (ct > 0) { total += ct; count++; }
        }
        return count > 0 ? total / count : 0;
    }
}