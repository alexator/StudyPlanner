package japrc2013test;
//EX. No: Y1557324

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

import japrc2013.StudyPlanner;
import japrc2013.StudyPlannerException;
import japrc2013.StudyPlannerInterface;

import org.junit.Before;
import org.junit.Test;

public class TestScenarioTwo {

	private StudyPlannerInterface planner;
	
	
	@Before
  public void setUp() throws Exception {
		
		planner = new StudyPlanner();
  }
	

	@Test
  public final void testTruncateTopicsDuration() {
		Calendar start = new GregorianCalendar(2014,0, 3, 9, 00,00);
		planner.addTopic("Java file handling", 480);
		planner.addTopic("Java 2", 100);
		planner.setBreakLength(10);
		planner.generateStudyPlan(start);
		assertNotNull(planner.getStudyPlan());
		assertEquals(10, planner.getStudyPlan().get(14).getDuration());	// End of day so truncate the duration of the specific study topic 
  }
	
	@Test
	  public final void testEventOne() {
			Calendar start1 = new GregorianCalendar(2014,0, 3, 9, 00,00);
			Calendar start2 = new GregorianCalendar(2014,0, 3, 12, 25,00);
			planner.addTopic("Java file handling", 480);
			planner.addTopic("Java 2", 100);
			planner.addCalendarEvent("Java Test", start2, 60);
			planner.setBreakLength(10);
			planner.generateStudyPlan(start1);
			assertNotNull(planner.getStudyPlan());
			assertEquals("break", planner.getStudyPlan().get(5).getTopic());
			assertEquals(5, planner.getStudyPlan().get(5).getDuration());	// Truncate the duration of break before an event
	  }
	
	@Test
	  public final void testEventTwo() {
			Calendar start1 = new GregorianCalendar(2014,0, 3, 9, 00,00);
			Calendar start2 = new GregorianCalendar(2014,0, 3, 18, 00,00);
			planner.addTopic("Java file handling", 480);
			planner.addCalendarEvent("Java Test", start2, 120);
			planner.setBreakLength(10);
			planner.generateStudyPlan(start1);
			assertNotNull(planner.getStudyPlan());
			assertEquals("Java Test", planner.getStudyPlan().get(13).getTopic());
			assertEquals(120, planner.getStudyPlan().get(13).getDuration());	// Event outside window of day
	  }
	
	@Test
    public final void testOverlap() {
		Calendar start1 = new GregorianCalendar(2014,0, 3, 10, 25,00);
		Calendar start2 = new GregorianCalendar(2014,0, 3, 12, 25,00);
		planner.addTopic("Java file handling", 480);
		try {
			planner.addCalendarEvent("Java Test", start2, 60);
			planner.addCalendarEvent("Java Test", start2, 60);
			planner.addCalendarEvent("Java Test", start1, 130); //Overlap of events
		} catch (StudyPlannerException e) {
			System.err.println(e);
		}
		
    }
}
