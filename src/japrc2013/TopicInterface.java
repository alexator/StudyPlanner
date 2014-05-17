package japrc2013;

import java.util.Calendar;

public interface TopicInterface
{

    /**
     * Returns a string describing the topic of study
     */
    String getSubject();
    

    /** 
     * Returns the required study duration for this topic, in minutes
     * 
     */
    int getDuration();
    
    /** 
     * Assigns a given event to be the target for which a particular topic is being studied (e.g. the exam).
     * If another event was previously set to be the target for this topic, the specified one replaces it.
     * 
     */
    void setTargetEvent(CalendarEventInterface target);


    /** 
     * Returns the calendar event that this topic targets
     * 
     */
    CalendarEventInterface getTargetEvent();

}
