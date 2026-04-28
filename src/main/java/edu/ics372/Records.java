package edu.ics372;

import java.text.DateFormat;
import java.util.Date;

/**
 * 
 */
public class Records
{
    private final Order recordedOrder; //the order in which this record pertains to
    private final DateFormat df = DateFormat.getDateInstance();
    private Date startTime;
    private Date endTime;

    public Records(Order order)
    {
        recordedOrder = order;
    }

    /**
     * This constructor is to be used for orders that had already been started in a previous session
     * @param order
     * @param start The time in which the order was originally started in milleseconds
     */
    public Records(Order order, long start)
    {
        recordedOrder = order;
        startTime = new Date(start);
    }

    public Order getRecordedOrder()
    { return this.recordedOrder; }

    /**
     * This method will return the ammount of time between the start and end times
     * @return
     */
    public String timeElapsed()
    {
        String result = "This order has not been started.";
        return result;
    }

    public String getStartDate()
    {
        String startDate = df.format(startTime);
        return startDate;
    }

    public String getEndDate()
    {
        String endDate = df.format(endTime);
        return endDate;
    }
}
