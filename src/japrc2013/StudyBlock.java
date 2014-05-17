package japrc2013;

import java.util.Calendar;

//EX. No: Y1557324
public class StudyBlock implements StudyBlockInterface
{
    private String subject;
    private Calendar startTime;
    private int duration;
    
    
    public StudyBlock(String subject, Calendar startTime, int duration)
    {
        this.subject = subject;
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public String getTopic() {
        return subject;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    
    @Override
    public Calendar getStartTime() {
        return startTime;
    }
}
