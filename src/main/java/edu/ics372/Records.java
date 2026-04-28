package edu.ics372;

import java.text.DateFormat;
import java.util.Date;

/**
 * 
 */
public class Records
{
    private final Order recordedOrder; //the order in which this record pertains to
    private final DateFormat df = DateFormat.getDateInstance(); //
    private final Date startTime;
    private final Date endTime;

    // ----------------- Constructors ----------------------------------------------------------------------------------
    /**
     * This constructor will be called when orders are called in from a new file
     * @param order The order that is being monitored
     */
    public Records(Order order)
    {
        recordedOrder = order;
        //Set the initial start and end time to the epoch for error checking purposes
        startTime = new Date(0);
        endTime = new Date(0);
    }

    /**
     * This constructor is to be used for orders that had already been started in a previous session
     * @param order The Order which is being monitored in this record
     * @param start The time in which the order was originally started in milliseconds
     */
    public Records(Order order, long start)
    {
        recordedOrder = order;
        startTime = new Date(start);
        endTime = new Date(0);
    }

    // ----------------- Getters and "setters" ---------------------------------------------------------------------------
    public Order getRecordedOrder()
    { return this.recordedOrder; }

    public void setStartTime(long time)
    { this.startTime.setTime(time); }

    public void setEndTime(long time) {
        if (this.endTime.before(startTime)) //This should never happen, but just in case.
        { System.err.println("End time " + endTime.getTime() + " is before start time of " + startTime.getTime()); }
        else
        { this.endTime.setTime(time);}
    }

    // ---------------- These will be used for analytics for the session as a whole ------------------------------------

    // ---------------- These will be used when viewing information on individual orders in the UI ---------------------
    /**
     * This method will return the amount of time between the start and end times of an order
     * @return A string representing the amount of time between the start and completion of the recorded order; It will
     *  output a notice if the order has not been started or completed.
     */
    public String timeElapsed()
    {
        String result = "This order has not been started.";
        if(startTime.getTime() != 0)
        {

        }
        return result;
    }

    public String getStartDate()
    {
        String startDate = df.format(startTime);

        if(startTime.getTime() == 0)
        { startDate = "This order has not been started."; }

        return startDate;
    }

    public String getEndDate()
    {
        String endDate = df.format(endTime);

        if(endTime.getTime() == 0)
        { endDate = "This order has not been completed."; }

        return endDate;
    }

    // --------------------------------- toString() --------------------------------------------------------------------
}
