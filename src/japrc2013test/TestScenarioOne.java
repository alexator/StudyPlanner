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

public class TestScenarioOne {

	private StudyPlannerInterface planner;
	
	
	@Before
    public void setUp() throws Exception {
		
		planner = new StudyPlanner();
    }
	
	@Test
    public final void testAddSameTopic() {
		
		try {
			planner.addTopic("Java file handling", 480);
			planner.addTopic("Java file handling", 480);
		} catch (StudyPlannerException e) {
			System.err.println(e);
		}
		
    }

	@Test
    public final void testGeneartePlanWithZeroBreak() {
		Calendar start = new GregorianCalendar(2014,0, 3, 9, 00,00);
		planner.addTopic("Java file handling", 480);
        planner.setBreakLength(0);
		planner.generateStudyPlan(start);
		assertNotNull(planner.getStudyPlan());
		assertEquals(8, planner.getStudyPlan().size()); 	// 480/60 = 8 so 8 blocks of 60 minutes
    }
	@Test
    public final void testGeneartePlanWithBreak() {
		Calendar start = new GregorianCalendar(2014,0, 3, 9, 00,00);
		planner.addTopic("Java file handling", 480);
        planner.setBreakLength(10);
		planner.generateStudyPlan(start);
		assertNotNull(planner.getStudyPlan());
		assertEquals(14, planner.getStudyPlan().size()); 	// 480/60 = 8 so 8 block of 60 minutes + 6 blocks of break of 10 minutes
    }
	@Test
    public final void testGeneartePlanWithBreakAndDuration() {
		Calendar start = new GregorianCalendar(2014,0, 3, 9, 00,00);
		planner.addTopic("Java file handling", 100);
		planner.setBreakLength(0);
		planner.generateStudyPlan(start);
		assertNotNull(planner.getStudyPlan());
		
		assertEquals(60, planner.getStudyPlan().get(0).getDuration());
		assertEquals(40, planner.getStudyPlan().get(1).getDuration()); //Test the duration of a topic of 100 hours => 100 = 60 +40
    }
}
