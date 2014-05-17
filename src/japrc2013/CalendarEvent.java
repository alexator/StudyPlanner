package japrc2013;

import japrc2013.StudyPlannerInterface.CalendarEventType;

import java.util.Calendar;

// EX. No: Y1557324


public class CalendarEvent implements CalendarEventInterface {

	private String eventName;
    private Calendar startTime;
    private int duration;
    protected CalendarEventType typ;
    
    
    public CalendarEvent (String eventName, Calendar startTime, int duration, CalendarEventType type)
    {
        this.eventName = eventName;
        this.startTime = startTime;
        this.duration = duration;
        this.typ = type;
    }
	
    @Override
	public String getName() {

		return eventName;
	}

	@Override
	public Calendar getStartTime() {
		
		return startTime;
	}

	@Override
	public int getDuration() {
		
		return duration;
	}

	@Override
	public boolean isValidTopicTarget() {
		if(typ != null){
			return true;
		}
		return false;
	}

}
