package japrc2013;

//EX. No: Y1557324

public class Topic implements TopicInterface
{
    private String subject;
    private int duration;
    private CalendarEventInterface caletarget;
    
    public Topic(String name, int duration)
    {
        this.subject = name;
        this.duration = duration;
    }

    @Override
    public String getSubject()
    {
        return subject;
    }

    @Override
    public int getDuration()
    {
        return duration;
    }

    @Override
    public void setTargetEvent(CalendarEventInterface target) {
    	caletarget = target;
    }

    @Override
    public CalendarEventInterface getTargetEvent()
    {
        return caletarget;
    }
}
