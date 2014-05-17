package japrc2013;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

//EX. No: Y1557324

public class StudyPlanner implements StudyPlannerInterface {

	private ArrayList<TopicInterface> topics = new ArrayList<TopicInterface>();
	private ArrayList<StudyBlockInterface> plan = new ArrayList<StudyBlockInterface>();
	private ArrayList<CalendarEventInterface> calEven = new ArrayList<CalendarEventInterface>();
	private ArrayList<Integer> durations = new ArrayList<Integer>();
	private ArrayList<Boolean> added = new ArrayList<Boolean>();
	private ArrayList<Integer> finished = new ArrayList<Integer>();

	private StudyPlannerGUIInterface gui;

	private int blockSize = 60;
	private int minBlockSize = 10;
	private int breakLength = 10;
	private boolean finish;
	private Calendar dailyStart = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), 9, 00, 00);
	private Calendar dailyEnd = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH), 17, 00, 00);
	private int nowHour = 0;
	private int nowMin = 0;
	private int nowMonth = 0;
	private int nowYear = 0;
	private int nowDay = 0;
	private Calendar nowTopicTime = new GregorianCalendar();
	private boolean good = true; 

	@Override
	public void addTopic(String name, int duration) {
		boolean flag_same = false;

		for (int i = 0; i < getTopics().size(); i++) {
			if (getTopics().get(i).getSubject().equalsIgnoreCase(name)) {
				flag_same = true;
				throw new StudyPlannerException(
						"Two topics have the same name!");
				
			}
		}

		if (!flag_same) {
			topics.add(new Topic(name, duration));
		}
	}

	@Override
	public List<TopicInterface> getTopics() {
		return topics;
	}

	@Override
	public List<StudyBlockInterface> getStudyPlan() {
		return plan;
	}

	public void setGUI(StudyPlannerGUIInterface gui) {
		this.gui = gui;
	}

	@Override
	public void generateStudyPlan() {

		if (getTopics().isEmpty()) {
			throw new StudyPlannerException("There are no topics to create a plan!");
		} else {
			
			plan = new ArrayList<StudyBlockInterface>();
			durations = new ArrayList<Integer>();
			added = new ArrayList<Boolean>();
			finished = new ArrayList<Integer>();
			finish = false;
			
			Calendar startTopicTime = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE),00);
			initNowTime(startTopicTime);

			for (int i = 0; i < getTopics().size(); i++) {
				durations.add(topics.get(i).getDuration());
			}
			//Keep track of what events are added to the planner
			for (int i = 0; i < calEven.size(); i++) {
				added.add(false);
			}

			for (int i = 0; i < getTopics().size(); i++) {
				finished.add(0);
			}

			while (!finish) {
				
				for (int i = 0; i < topics.size(); i++) {
					
					if (isInDayWindow(nowTopicTime)) {	//Check if the nowTopicTime is in the day window. The nowTopicTime is the main timer in order to form the plan and indicates the current time	
					} else {
						nextDay();
					}
					System.out.println("Mpainei " + thereIsAnEvent(nowTopicTime));
					if	(thereIsAnEvent(nowTopicTime)) { //Add an event if the time is right
						for (int j = 0; j<calEven.size(); j++) {
							if(nowTopicTime.get(Calendar.DAY_OF_MONTH) == calEven.get(j).getStartTime().get(Calendar.DAY_OF_MONTH)) {
								if (nowTopicTime.equals(calEven.get(j).getStartTime())) {
									plan.add(new StudyBlock(calEven.get(j).getName(), calEven.get(j).getStartTime(),calEven.get(j).getDuration()));
									increaseTime(nowTopicTime, calEven.get(j).getDuration());
//									System.out.println("event ends" + nowTopicTime.getTime());
									added.set(j, true);
									if (breakLength != 0) {
										for (int k = 0; k< calEven.size(); k++) {	
											if(nowTopicTime.equals(calEven.get(k).getStartTime())){
												if(nowTopicTime.get(Calendar.DAY_OF_MONTH) == calEven.get(k).getStartTime().get(Calendar.DAY_OF_MONTH)){
													good = false;
												}
											}
										}
										if (isValidBreakSlot(nowTopicTime) && good) { //Add break if it is valid after the event
											if(!thereIsAnEventBreak(nowTopicTime)) {
												if (timeDiffInMin(getDailyEndStudyTime(), nowTopicTime) >= breakLength && !thereIsAnEventBreak(nowTopicTime)){
													startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
													plan.add(new StudyBlock("break", startTopicTime, breakLength));
													increaseTime(nowTopicTime, breakLength);
												} else if(timeDiffInMin(getDailyEndStudyTime(), nowTopicTime) < breakLength && !thereIsAnEventBreak(nowTopicTime)) {
													startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
													plan.add(new StudyBlock("break", startTopicTime, timeDiffInMin(getDailyEndStudyTime(), nowTopicTime)));
													increaseTime(nowTopicTime, timeDiffInMin(getDailyEndStudyTime(), nowTopicTime));
												}
											} else if(thereIsAnEventBreak(nowTopicTime)) {
												for (int l = 0; l<calEven.size(); l++) {
													if(nowTopicTime.get(Calendar.DAY_OF_MONTH) == calEven.get(l).getStartTime().get(Calendar.DAY_OF_MONTH)) {
														if (timeDiffInMin(calEven.get(l).getStartTime(), nowTopicTime) < breakLength && timeDiffInMin(calEven.get(l).getStartTime(), nowTopicTime)>0){
															startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
															plan.add(new StudyBlock("break", startTopicTime, timeDiffInMin(calEven.get(l).getStartTime(), nowTopicTime)));
															increaseTime(nowTopicTime, timeDiffInMin(calEven.get(l).getStartTime(), nowTopicTime));
														} else if (nowTopicTime.equals(calEven.get(l).getStartTime())) {
																
														}
													}
												}
											}
										}
									}
								} 
							}
						}
					}
						//Manipulation of the topics
					if (durations.get(i) != 0) { // if there is a duration for this topic
						if	(!thereIsAnEvent(nowTopicTime)) {
							if (timeDiffInMin(getDailyEndStudyTime(),nowTopicTime) >= blockSize) { // The remaining time in day is greater or equal to the blockSize
								if (durations.get(i) >= blockSize) { //the remaining duration is greater or equal to the blockSize
									startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin); //When the Block start
//									System.out.println("Topic starts: " + nowTopicTime.getTime());
									plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, blockSize));
									increaseTime(nowTopicTime, blockSize);
									durations.set(i, (durations.get(i) - blockSize));
//									System.out.println("Topic ends: " + nowTopicTime.getTime());
								} else if ((durations.get(i) == durations.get(i) % blockSize) && durations.get(i) >= minBlockSize) { //The remaining duration of the topic is less than BlockSize but it is greater than the minBlockSize
									startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
//									System.out.println("Topic starts: " + nowTopicTime.getTime());
									plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime,(durations.get(i) % blockSize)));
									increaseTime(nowTopicTime, (durations.get(i) % blockSize));
									durations.set(i, (durations.get(i) - (durations.get(i) % blockSize)));
//									System.out.println("Topic ends: " + nowTopicTime.getTime());
								} else if (durations.get(i) < minBlockSize) { // The remaining duration of this topics is less than BlockSize and less than minBlockSize but I study it for minBlockSize
									startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
//									System.out.println("Topic starts: " + nowTopicTime.getTime());
									plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, minBlockSize));
									increaseTime(nowTopicTime, minBlockSize);
									durations.set(i, (durations.get(i) - (durations.get(i) % blockSize)));
//									System.out.println("Topic ends: " + nowTopicTime.getTime());
								}
							} else if (timeDiffInMin(getDailyEndStudyTime(), nowTopicTime) >= minBlockSize) { //The remaining time in day is less than blockSize but greater or equal to minBlockSize
								if (timeDiffInMin(getDailyEndStudyTime(), nowTopicTime) != 0) {
									if(durations.get(i)>= blockSize){
										int block = timeDiffInMin(getDailyEndStudyTime(), nowTopicTime);
										startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
										plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, block));
										increaseTime(nowTopicTime, block);
										durations.set(i, (durations.get(i) - block));
									} else {
										startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
										plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime,(durations.get(i) % blockSize)));
										increaseTime(nowTopicTime, (durations.get(i) % blockSize));
										durations.set(i, (durations.get(i) - (durations.get(i) % blockSize)));
									}
								}
							} else if (timeDiffInMin(getDailyEndStudyTime(), nowTopicTime) < minBlockSize) { //If the remaining time in day is less than minBlockSize skip to the next Day
								
								nextDay();
								
								if (durations.get(i) >= blockSize) { //the remaining duration is greater or equal to the blockSize
									startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
									plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, blockSize));
									increaseTime(nowTopicTime, blockSize);
									durations.set(i, (durations.get(i) - blockSize));
								} else if ((durations.get(i) == durations.get(i) % blockSize) && durations.get(i) >= minBlockSize) { //The remaining duration of the topic is less than BlockSize but it is greater than the minBlockSize
									startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
									plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime,(durations.get(i) % blockSize)));
									increaseTime(nowTopicTime, (durations.get(i) % blockSize));
									durations.set(i, (durations.get(i) - (durations.get(i) % blockSize)));
								} else if (durations.get(i) < minBlockSize) { // The remaining duration of this topics is less than BlockSize and less than minBlockSize but I study it for minBlockSize
									startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
									plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, minBlockSize));
									increaseTime(nowTopicTime, minBlockSize);
									durations.set(i, (durations.get(i) - (durations.get(i) % blockSize)));
								}
							}
						} else if (thereIsAnEvent(nowTopicTime)) {
							for (int j = 0; j<calEven.size(); j++) {
								if (timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime) < blockSize && timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime) > 0 && isInDayWindow(calEven.get(j).getStartTime())) {
									if (durations.get(i) >= blockSize) { //the remaining duration is greater or equal to the blockSize
										startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
//										System.out.println("Topic starts: " + nowTopicTime.getTime());
										plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)));
										durations.set(i, (durations.get(i) - timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)));
										increaseTime(nowTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime));
//										System.out.println("Topic ends: " + nowTopicTime.getTime());
									} else if ((durations.get(i) == durations.get(i) % blockSize) && durations.get(i) >= minBlockSize) { //The remaining duration of the topic is less than BlockSize but it is greater than the minBlockSize
										if (timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime) > durations.get(i) % blockSize) {
											startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
//											System.out.println("Topic starts: " + nowTopicTime);
											plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime,(durations.get(i) % blockSize)));
											increaseTime(nowTopicTime, (durations.get(i) % blockSize));
											durations.set(i, (durations.get(i) - (durations.get(i) % blockSize)));
//											System.out.println("Topic ends: " + nowTopicTime.getTime());
										} else {
											startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
											plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)));
											durations.set(i, (durations.get(i) - timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)));
											increaseTime(nowTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime));	
										}
									} else if (durations.get(i) < minBlockSize) { // The remaining duration of this topics is less than BlockSize and less than minBlockSize but I study it for minBlockSize
										if (timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime) > minBlockSize) {
											startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
											plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, minBlockSize));
											increaseTime(nowTopicTime, minBlockSize);
											durations.set(i, (durations.get(i) - (durations.get(i) % blockSize)));
										} else {
											startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
											plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)));
											durations.set(i, (durations.get(i) - timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)));
											increaseTime(nowTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime));	
										}
									}
								} 
							}
						}
//						System.out.println("Before break : " + thereIsAnEventBreak(nowTopicTime));
						if (breakLength != 0) { //If there is length for break period
							if (isValidBreakSlot(nowTopicTime)) {
								if(!thereIsAnEventBreak(nowTopicTime)) {
									if (timeDiffInMin(getDailyEndStudyTime(), nowTopicTime) >= breakLength && !thereIsAnEventBreak(nowTopicTime)){
										startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
//										System.out.println("Break starts: " + nowTopicTime.getTime());
										plan.add(new StudyBlock("break", startTopicTime, breakLength));
										increaseTime(nowTopicTime, breakLength);
//										System.out.println("Break ends: " + nowTopicTime.getTime());
									} else if(timeDiffInMin(getDailyEndStudyTime(), nowTopicTime) < breakLength && !thereIsAnEventBreak(nowTopicTime)) {
										startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
										plan.add(new StudyBlock("break", startTopicTime, timeDiffInMin(getDailyEndStudyTime(), nowTopicTime)));
										increaseTime(nowTopicTime, timeDiffInMin(getDailyEndStudyTime(), nowTopicTime));
									}
								} else if(thereIsAnEventBreak(nowTopicTime)) {
									for (int j = 0; j<calEven.size(); j++) {
										if(nowTopicTime.get(Calendar.DAY_OF_MONTH) == calEven.get(j).getStartTime().get(Calendar.DAY_OF_MONTH)) {
											if (timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime) < breakLength && timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)>0){
												startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
												plan.add(new StudyBlock("break", startTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)));
												increaseTime(nowTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime));
											} else if (nowTopicTime.equals(calEven.get(j).getStartTime())) {
//												System.out.println("Break starts: " + nowTopicTime.getTime());
//												System.out.println("Break ends: " + nowTopicTime.getTime());
											}
										}
									}
								}
							}
						}
					}
				}
				
				if (durations.equals(finished)) {
					finish = true;
				}
			}
			//Addition of the events that are outside the day window
			for (int i = 0; i<calEven.size(); i++) {
				if (added.get(i).equals(false)){
					plan.add(new StudyBlock(calEven.get(i).getName(), calEven.get(i).getStartTime(),calEven.get(i).getDuration()));
				}
			}
			
			sortPlan(); //Sort the plann
			
			if (gui != null) {
				gui.notifyModelHasChanged();
			}
		}
	}
	
	
	public void sortPlan() {

		Collections.sort(plan, new Comparator<StudyBlockInterface>() {
			@Override
			public int compare(StudyBlockInterface o1, StudyBlockInterface o2) {
				return Long.compare(o1.getStartTime().getTimeInMillis(), o2.getStartTime().getTimeInMillis());
			}

		});
	}

	private boolean isInDayWindow(Calendar now) { //Check is the current time is in day window 

		Calendar endD = getDailyEndStudyTime();
		boolean cond = false;

		if (now.get(Calendar.HOUR_OF_DAY) >= getDailyStartStudyTime().get(Calendar.HOUR_OF_DAY) && (now.get(Calendar.HOUR_OF_DAY) < getDailyEndStudyTime().get(Calendar.HOUR_OF_DAY))) {
			if (timeDiffInMin(endD, now) >= minBlockSize) {
				return true;
			}
		} else {
			return false;
		}
		return cond;
	}
	
	private void increaseTime(Calendar now, int amount) { //Increase time based on an amount of min.
		now.add(Calendar.MINUTE,amount);
		nowHour = now.get(Calendar.HOUR_OF_DAY);
		nowMin = now.get(Calendar.MINUTE);
	}
	
	private boolean isValidBreakSlot(Calendar now) { //Check the validity of the current time for a break
		if (!durations.equals(finished) && now.get(Calendar.HOUR_OF_DAY) < getDailyEndStudyTime().get(Calendar.HOUR_OF_DAY) && timeDiffInMin(getDailyStartStudyTime(), now) != 0) {
			if (now.get(Calendar.HOUR_OF_DAY) - getDailyEndStudyTime().get(Calendar.HOUR_OF_DAY) != 0) {
				return true;
			}
		} 
		return false;
	}
	
	private boolean thereIsAnEvent(Calendar now) { //Detects the Events when the current study block is a topic
		boolean cond = false;
		for (int i = 0; i<calEven.size(); i++) {
			if(now.get(Calendar.DAY_OF_MONTH) == calEven.get(i).getStartTime().get(Calendar.DAY_OF_MONTH)) {
				if (now.equals(calEven.get(i).getStartTime())) {
//					System.out.println("1");
					return true;
				} else if (timeDiffInMin(calEven.get(i).getStartTime(), now)<blockSize && timeDiffInMin(calEven.get(i).getStartTime(), now) > 0){
//					System.out.println("2");
					return true;
				} 
			}
		}
//		System.out.println("What is the cond: " +cond);
		return cond;
	}
	
	private boolean thereIsAnEventBreak(Calendar now) { //Detects the Events when the current study block is a break
		boolean cond = false;
		for (int i = 0; i<calEven.size(); i++) {
			if(now.get(Calendar.DAY_OF_MONTH) == calEven.get(i).getStartTime().get(Calendar.DAY_OF_MONTH)) {
				if (now.equals(calEven.get(i).getStartTime())) {
					return true;
				} else if (timeDiffInMin(calEven.get(i).getStartTime(), now)<breakLength && timeDiffInMin(calEven.get(i).getStartTime(), now) > 0){
					return true;
				} 
			} 
		}
		return cond;
	}


	
	

	private void nextDay() {
		nowHour = getDailyStartStudyTime().get(Calendar.HOUR_OF_DAY);
		nowMin = getDailyStartStudyTime().get(Calendar.MINUTE);
		nowTopicTime.set(Calendar.HOUR_OF_DAY, getDailyStartStudyTime().get(Calendar.HOUR_OF_DAY));
		nowTopicTime.set(Calendar.MINUTE, getDailyStartStudyTime().get(Calendar.MINUTE));
		nowTopicTime.set(Calendar.SECOND, 00);
		nowTopicTime.add(Calendar.DATE, 1);
		nowDay = nowTopicTime.get(Calendar.DAY_OF_MONTH);
		nowMonth = nowTopicTime.get(Calendar.MONTH);
		nowYear = nowTopicTime.get(Calendar.YEAR);
		dailyStart.add(Calendar.DAY_OF_MONTH, 1);
		dailyEnd.add(Calendar.DAY_OF_MONTH, 1);
	}

	private void initNowTime(Calendar start) {
		nowTopicTime.clear();
		nowTopicTime.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY));
		nowTopicTime.set(Calendar.MINUTE, start.get(Calendar.MINUTE));
		nowTopicTime.set(Calendar.SECOND, start.get(Calendar.SECOND));
		nowTopicTime.set(Calendar.MONTH, start.get(Calendar.MONTH));
		nowTopicTime.set(Calendar.DAY_OF_MONTH, start.get(Calendar.DAY_OF_MONTH));
		nowTopicTime.set(Calendar.YEAR, start.get(Calendar.YEAR));
		nowHour = nowTopicTime.get(Calendar.HOUR_OF_DAY);
		nowMin = nowTopicTime.get(Calendar.MINUTE);
		nowMonth = nowTopicTime.get(Calendar.MONTH);
		nowDay = nowTopicTime.get(Calendar.DAY_OF_MONTH);
		nowYear = nowTopicTime.get(Calendar.YEAR);
	}

	private int timeDiffInMin(Calendar timPar1, Calendar timPar2) { // Computes the time diff between two times and the result is in min.

		SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm");
		String time1;
		String time2;
		
		time1 = timeFormat.format(timPar1.getTime());
		time2 = timeFormat.format(timPar2.getTime());
		float timeparse1 = Float.parseFloat(time1);
		float timeparse2 = Float.parseFloat(time2);
		int timeparseint1 = (int) timeparse1;
		int timeparseint2 = (int) timeparse2;
		int actual1 = (int) (((timeparseint1 % 60) * 60) + ((Math.round(timeparse1 * 100)) % 100));
		int actual2 = (int) (((timeparseint2 % 60) * 60) + ((Math.round(timeparse2 * 100)) % 100));
		float diff = (float) ((actual1 - actual2));
		int diffint = (int) (diff);
		
		return diffint;

	}

	@Override
	public void deleteTopic(String topic) {
		for (int i = 0; i < getTopics().size(); i++) {
			if (getTopics().get(i).getSubject().equals(topic)) {
				topics.remove(i);
			}
		}
	}

	@Override
	public void generateStudyPlan(Calendar startStudy) {
	
		if (getTopics().isEmpty()) {
			throw new StudyPlannerException("There are no topics to create a plan!");
		} else {
			
			plan = new ArrayList<StudyBlockInterface>();
			durations = new ArrayList<Integer>();
			added = new ArrayList<Boolean>();
			finished = new ArrayList<Integer>();
			finish = false;
			
			Calendar startTopicTime = startStudy;
			initNowTime(startTopicTime);

			for (int i = 0; i < getTopics().size(); i++) {
				durations.add(topics.get(i).getDuration());
			}
			
			for (int i = 0; i < calEven.size(); i++) {
				added.add(false);
			}

			for (int i = 0; i < getTopics().size(); i++) {
				finished.add(0);
			}

			while (!finish) {
				
				for (int i = 0; i < topics.size(); i++) {
					
					if (isInDayWindow(nowTopicTime)) {		
					} else {
						nextDay();
					}

					if	(thereIsAnEvent(nowTopicTime)) {
						for (int j = 0; j<calEven.size(); j++) {
							if(nowTopicTime.get(Calendar.DAY_OF_MONTH) == calEven.get(j).getStartTime().get(Calendar.DAY_OF_MONTH)) {
								if (nowTopicTime.equals(calEven.get(j).getStartTime())) {
									plan.add(new StudyBlock(calEven.get(j).getName(), calEven.get(j).getStartTime(),calEven.get(j).getDuration()));
									increaseTime(nowTopicTime, calEven.get(j).getDuration());
									added.set(j, true);
									if (breakLength != 0) {
										for (int k = 0; k< calEven.size(); k++) {	
											if(nowTopicTime.equals(calEven.get(k).getStartTime())){
												if(nowTopicTime.get(Calendar.DAY_OF_MONTH) == calEven.get(k).getStartTime().get(Calendar.DAY_OF_MONTH)){
													good = false;
												}
											}
										}
										if (isValidBreakSlot(nowTopicTime) && good) {
											if(!thereIsAnEventBreak(nowTopicTime)) {
												if (timeDiffInMin(getDailyEndStudyTime(), nowTopicTime) >= breakLength && !thereIsAnEventBreak(nowTopicTime)){
													startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
													plan.add(new StudyBlock("break", startTopicTime, breakLength));
													increaseTime(nowTopicTime, breakLength);
												} else if(timeDiffInMin(getDailyEndStudyTime(), nowTopicTime) < breakLength && !thereIsAnEventBreak(nowTopicTime)) {
													startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
													plan.add(new StudyBlock("break", startTopicTime, timeDiffInMin(getDailyEndStudyTime(), nowTopicTime)));
													increaseTime(nowTopicTime, timeDiffInMin(getDailyEndStudyTime(), nowTopicTime));
												}
											} else if(thereIsAnEventBreak(nowTopicTime)) {
												for (int l = 0; l<calEven.size(); l++) {
													if(nowTopicTime.get(Calendar.DAY_OF_MONTH) == calEven.get(l).getStartTime().get(Calendar.DAY_OF_MONTH)) {
														if (timeDiffInMin(calEven.get(l).getStartTime(), nowTopicTime) < breakLength && timeDiffInMin(calEven.get(l).getStartTime(), nowTopicTime)>0){
															startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
															plan.add(new StudyBlock("break", startTopicTime, timeDiffInMin(calEven.get(l).getStartTime(), nowTopicTime)));
															increaseTime(nowTopicTime, timeDiffInMin(calEven.get(l).getStartTime(), nowTopicTime));
														} else if (nowTopicTime.equals(calEven.get(l).getStartTime())) {
																
														}
													}
												}
											}
										}
									}
								} 
							}
						}
					}
						
					if (durations.get(i) != 0) { // if there is a duration for this topic
						if	(!thereIsAnEvent(nowTopicTime)) {
							if (timeDiffInMin(getDailyEndStudyTime(),nowTopicTime) >= blockSize) { // The remaining time in day is greater or equal to the blockSize
								if (durations.get(i) >= blockSize) { //the remaining duration is greater or equal to the blockSize
									startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
									plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, blockSize));
									increaseTime(nowTopicTime, blockSize);
									durations.set(i, (durations.get(i) - blockSize));
								} else if ((durations.get(i) == durations.get(i) % blockSize) && durations.get(i) >= minBlockSize) { //The remaining duration of the topic is less than BlockSize but it is greater than the minBlockSize
									startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
									plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime,(durations.get(i) % blockSize)));
									increaseTime(nowTopicTime, (durations.get(i) % blockSize));
									durations.set(i, (durations.get(i) - (durations.get(i) % blockSize)));
								} else if (durations.get(i) < minBlockSize) { // The remaining duration of this topics is less than BlockSize and less than minBlockSize but I study it for minBlockSize
									startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
									plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, minBlockSize));
									increaseTime(nowTopicTime, minBlockSize);
									durations.set(i, (durations.get(i) - (durations.get(i) % blockSize)));
								}
							} else if (timeDiffInMin(getDailyEndStudyTime(), nowTopicTime) >= minBlockSize) { //The remaining time in day is less than blockSize but greater or equal to minBlockSize
								if (timeDiffInMin(getDailyEndStudyTime(), nowTopicTime) != 0) {
									if(durations.get(i)>= blockSize){
										int block = timeDiffInMin(getDailyEndStudyTime(), nowTopicTime);
										startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
										plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, block));
										increaseTime(nowTopicTime, block);
										durations.set(i, (durations.get(i) - block));
									} else {
										startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
										plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime,(durations.get(i) % blockSize)));
										increaseTime(nowTopicTime, (durations.get(i) % blockSize));
										durations.set(i, (durations.get(i) - (durations.get(i) % blockSize)));
									}
								}
							} else if (timeDiffInMin(getDailyEndStudyTime(), nowTopicTime) < minBlockSize) { //If the remaining time in day is less than minBlockSize skip to the next Day
								
								nextDay();
								
								if (durations.get(i) >= blockSize) { //the remaining duration is greater or equal to the blockSize
									startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
									plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, blockSize));
									increaseTime(nowTopicTime, blockSize);
									durations.set(i, (durations.get(i) - blockSize));
								} else if ((durations.get(i) == durations.get(i) % blockSize) && durations.get(i) >= minBlockSize) { //The remaining duration of the topic is less than BlockSize but it is greater than the minBlockSize
									startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
									plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime,(durations.get(i) % blockSize)));
									increaseTime(nowTopicTime, (durations.get(i) % blockSize));
									durations.set(i, (durations.get(i) - (durations.get(i) % blockSize)));
								} else if (durations.get(i) < minBlockSize) { // The remaining duration of this topics is less than BlockSize and less than minBlockSize but I study it for minBlockSize
									startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
									plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, minBlockSize));
									increaseTime(nowTopicTime, minBlockSize);
									durations.set(i, (durations.get(i) - (durations.get(i) % blockSize)));
								}
							}
						} else if (thereIsAnEvent(nowTopicTime)) {
							for (int j = 0; j<calEven.size(); j++) {
								if (timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime) < blockSize && timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime) > 0 && isInDayWindow(calEven.get(j).getStartTime())) {
									if (durations.get(i) >= blockSize) { //the remaining duration is greater or equal to the blockSize
										startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
										plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)));
										durations.set(i, (durations.get(i) - timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)));
										increaseTime(nowTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime));
									} else if ((durations.get(i) == durations.get(i) % blockSize) && durations.get(i) >= minBlockSize) { //The remaining duration of the topic is less than BlockSize but it is greater than the minBlockSize
										if (timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime) > durations.get(i) % blockSize) {
											startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
											plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime,(durations.get(i) % blockSize)));
											increaseTime(nowTopicTime, (durations.get(i) % blockSize));
											durations.set(i, (durations.get(i) - (durations.get(i) % blockSize)));
										} else {
											startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
											plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)));
											durations.set(i, (durations.get(i) - timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)));
											increaseTime(nowTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime));	
										}
									} else if (durations.get(i) < minBlockSize) { // The remaining duration of this topics is less than BlockSize and less than minBlockSize but I study it for minBlockSize
										if (timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime) > minBlockSize) {
											startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
											plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, minBlockSize));
											increaseTime(nowTopicTime, minBlockSize);
											durations.set(i, (durations.get(i) - (durations.get(i) % blockSize)));
										} else {
											startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
											plan.add(new StudyBlock(topics.get(i).getSubject(), startTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)));
											durations.set(i, (durations.get(i) - timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)));
											increaseTime(nowTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime));	
										}
									}
								} 
							}
						}

						if (breakLength != 0) { //If there is length for break period
							if (isValidBreakSlot(nowTopicTime)) {
								if(!thereIsAnEventBreak(nowTopicTime)) {
									if (timeDiffInMin(getDailyEndStudyTime(), nowTopicTime) >= breakLength && !thereIsAnEventBreak(nowTopicTime)){
										startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
										plan.add(new StudyBlock("break", startTopicTime, breakLength));
										increaseTime(nowTopicTime, breakLength);
									} else if(timeDiffInMin(getDailyEndStudyTime(), nowTopicTime) < breakLength && !thereIsAnEventBreak(nowTopicTime)) {
										startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
										plan.add(new StudyBlock("break", startTopicTime, timeDiffInMin(getDailyEndStudyTime(), nowTopicTime)));
										increaseTime(nowTopicTime, timeDiffInMin(getDailyEndStudyTime(), nowTopicTime));
									}
								} else if(thereIsAnEventBreak(nowTopicTime)) {
									for (int j = 0; j<calEven.size(); j++) {
										if(nowTopicTime.get(Calendar.DAY_OF_MONTH) == calEven.get(j).getStartTime().get(Calendar.DAY_OF_MONTH)) {
											if (timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime) < breakLength && timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)>0){
												startTopicTime = new GregorianCalendar(nowYear, nowMonth, nowDay, nowHour, nowMin);
												plan.add(new StudyBlock("break", startTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime)));
												increaseTime(nowTopicTime, timeDiffInMin(calEven.get(j).getStartTime(), nowTopicTime));
											} else if (nowTopicTime.equals(calEven.get(j).getStartTime())) {
												
											}
										}
									}
								}
							}
						}
					}
				}
				
				if (durations.equals(finished)) {
					finish = true;
				}
			}
			
			for (int i = 0; i<calEven.size(); i++) {
				if (added.get(i).equals(false)){
					plan.add(new StudyBlock(calEven.get(i).getName(), calEven.get(i).getStartTime(),calEven.get(i).getDuration()));
				}
			}
			
			sortPlan();
			
			if (gui != null) {
				gui.notifyModelHasChanged();
			}
		}
	}

	@Override
	public void setBlockSize(int size) {
		if (size > minBlockSize) {
			blockSize = size;
		} else {
			throw new StudyPlannerException(
					"The minimum study block should be 10 minutes!");
		}
	}

	@Override
	public void setBreakLength(int i) {
		breakLength = i;
	}

	@Override
	public void setDailyStartStudyTime(Calendar startTime) {
		if (getDailyEndStudyTime().equals(null)) {
			dailyStart = startTime;
		} else {
			if (startTime.compareTo(getDailyEndStudyTime()) < 0) {
				if (getDailyEndStudyTime().get(Calendar.HOUR_OF_DAY)
						- getDailyStartStudyTime().get(Calendar.HOUR_OF_DAY) < blockSize) {
					throw new StudyPlannerException(
							"Daily start time minus Daily end time must be larger than your Current Study Block ");
				} else {
					dailyStart = startTime;
				}
			} else if (startTime.compareTo(getDailyEndStudyTime()) > 0) {
				throw new StudyPlannerException(
						"Your starting time is after the the end Time");
			}
		}
	}

	@Override
	public void setDailyEndStudyTime(Calendar endTime) {
		if (getDailyStartStudyTime().equals(null)) {
			if (getDailyEndStudyTime().get(Calendar.HOUR_OF_DAY)
					- getDailyStartStudyTime().get(Calendar.HOUR_OF_DAY) < blockSize) {
				throw new StudyPlannerException("Daily start time minus Daily end time must be larger than your Current Study Block ");
			} else {
				dailyEnd = endTime;
			}
		} else {
			if (endTime.compareTo(getDailyStartStudyTime()) > 0) {
				dailyEnd = endTime;
			} else if (endTime.compareTo(getDailyEndStudyTime()) < 0) {
				throw new StudyPlannerException("Your ending Time is before the the start Time");
			}
		}
	}

	@Override
	public Calendar getDailyStartStudyTime() {
		return dailyStart;
	}

	@Override
	public Calendar getDailyEndStudyTime() {
		return dailyEnd;
	}

	@Override
	public void addCalendarEvent(String eventName, Calendar startTime, int duration) {
		
		Calendar now1 = new GregorianCalendar();
		Calendar now2 = new GregorianCalendar();
		boolean ok = false;
		long starts1;
		long ends1;
		long starts2;
		long ends2;
		
		if (calEven.isEmpty()) {
			calEven.add(new CalendarEvent(eventName, startTime, duration, null));
		} else {
			
			for(int i = 0; i<calEven.size(); i++) {
				if (startTime.get(Calendar.MONTH) == calEven.get(i).getStartTime().get(Calendar.MONTH) && startTime.get(Calendar.DAY_OF_MONTH) == calEven.get(i).getStartTime().get(Calendar.DAY_OF_MONTH)) {
					now1.clear();
					now1.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
					now1.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
					now1.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
					now1.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH));
					now1.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
					now1.set(Calendar.SECOND, startTime.get(Calendar.SECOND));
					starts1 = now1.getTimeInMillis();
					now1.add(Calendar.MINUTE, duration);
					ends1 = now1.getTimeInMillis();
					
					now2.clear();
					now2.set(Calendar.HOUR_OF_DAY, calEven.get(i).getStartTime().get(Calendar.HOUR_OF_DAY));
					now2.set(Calendar.MINUTE, calEven.get(i).getStartTime().get(Calendar.MINUTE));
					now2.set(Calendar.MONTH, calEven.get(i).getStartTime().get(Calendar.MONTH));
					now2.set(Calendar.DAY_OF_MONTH, calEven.get(i).getStartTime().get(Calendar.DAY_OF_MONTH));
					now2.set(Calendar.YEAR, calEven.get(i).getStartTime().get(Calendar.YEAR));
					now2.set(Calendar.SECOND, startTime.get(Calendar.SECOND));
					
					starts2 = now2.getTimeInMillis();
					now2.add(Calendar.MINUTE, calEven.get(i).getDuration());
					ends2 =  now2.getTimeInMillis();			
					
					if (((starts2< starts1)&&(ends2<=starts1))||((starts2>=ends1)&&(ends2>ends1))) {
						ok = true;
					}
					
					now1.clear();
					now2.clear();
					starts1 = 0;
					ends1 = 0;
					starts2 = 0;
					ends2 = 0;
				} else {
					ok = true;
				}
			}
			
			if(ok) {
				calEven.add(new CalendarEvent(eventName, startTime, duration, null));
			} else {
				throw new StudyPlannerException("Calendar Events Overlap");
			}
			
			now1.clear();
			now2.clear();
			starts1 = 0;
			ends1 = 0;
			starts2 = 0;
			ends2 = 0;
		}
	}

	@Override
	public void addCalendarEvent(String eventName, Calendar startTime, int duration, CalendarEventType type) {
		
		Calendar now1 = new GregorianCalendar();
		Calendar now2 = new GregorianCalendar();
		boolean ok = false;
		long starts1;
		long ends1;
		long starts2;
		long ends2;
		
		if (calEven.isEmpty()) {
			calEven.add(new CalendarEvent(eventName, startTime, duration, type));
		} else {
			
			for(int i = 0; i<calEven.size(); i++) {
				if (startTime.get(Calendar.MONTH) == calEven.get(i).getStartTime().get(Calendar.MONTH) && startTime.get(Calendar.DAY_OF_MONTH) == calEven.get(i).getStartTime().get(Calendar.DAY_OF_MONTH)) {
					now1.clear();
					now1.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
					now1.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
					now1.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
					now1.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH));
					now1.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
					now1.set(Calendar.SECOND, startTime.get(Calendar.SECOND));
					starts1 = now1.getTimeInMillis();
					now1.add(Calendar.MINUTE, duration);
					ends1 = now1.getTimeInMillis();
					
					now2.clear();
					now2.set(Calendar.HOUR_OF_DAY, calEven.get(i).getStartTime().get(Calendar.HOUR_OF_DAY));
					now2.set(Calendar.MINUTE, calEven.get(i).getStartTime().get(Calendar.MINUTE));
					now2.set(Calendar.MONTH, calEven.get(i).getStartTime().get(Calendar.MONTH));
					now2.set(Calendar.DAY_OF_MONTH, calEven.get(i).getStartTime().get(Calendar.DAY_OF_MONTH));
					now2.set(Calendar.YEAR, calEven.get(i).getStartTime().get(Calendar.YEAR));
					now2.set(Calendar.SECOND, startTime.get(Calendar.SECOND));
					
					starts2 = now2.getTimeInMillis();
					now2.add(Calendar.MINUTE, calEven.get(i).getDuration());
					ends2 =  now2.getTimeInMillis();			
					
					if (((starts2< starts1)&&(ends2<=starts1))||((starts2>=ends1)&&(ends2>ends1))) {
						ok = true;
					}
					
					now1.clear();
					now2.clear();
					starts1 = 0;
					ends1 = 0;
					starts2 = 0;
					ends2 = 0;
				} else {
					ok = true;
				}
			}
			
			if(ok) {
				calEven.add(new CalendarEvent(eventName, startTime, duration, type));
			} else {
				throw new StudyPlannerException("Calendar Events Overlap");
			}
			
			now1.clear();
			now2.clear();
			starts1 = 0;
			ends1 = 0;
			starts2 = 0;
			ends2 = 0;
		}
	}

	@Override
	public List<CalendarEventInterface> getCalendarEvents() {
		return calEven;
	}

	@Override
	public void saveData(OutputStream saveStream) {
		PrintWriter out = new PrintWriter(saveStream);
		SimpleDateFormat eventFormat = new SimpleDateFormat("dd@MM@yyyy'@'HH@mm");
		String eventDate;
		for(int i = 0; i < getStudyPlan().size(); i++) {
			eventDate = eventFormat.format(getStudyPlan().get(i).getStartTime().getTime());
			out.println(eventDate+ "@" + getStudyPlan().get(i).getTopic() + "@" + getStudyPlan().get(i).getDuration());
		}
		out.close();
	}

	@Override
	public void loadData(InputStream loadStream) {
		Calendar st;
		try {
			if(loadStream.available()==0){
				throw new StudyPlannerException("File is empty");
			} else {
				 BufferedReader reader = new BufferedReader(new InputStreamReader(loadStream));
			        StringBuilder out = new StringBuilder();
//			        String out;
			        String line;
			        while ((line = reader.readLine()) != null) {
			            out.append(line + "\n");
			            String blockDay = line.split("@")[0];
			            String blockMonth = line.split("@")[1];
			            String blockYear = line.split("@")[2];
			            String blockHour = line.split("@")[3];
			            String blockMinutes = line.split("@")[4];
			            String blockTitle = line.split("@")[5];
			            String blockDuration = line.split("@")[6];
			            st = new GregorianCalendar(Integer.parseInt(blockYear), Integer.parseInt(blockMonth), Integer.parseInt(blockDay), Integer.parseInt(blockHour), Integer.parseInt(blockMinutes),00);
			            plan.add(new StudyBlock(blockTitle, st, Integer.parseInt(blockDuration)));
			            
			            System.out.println(blockDay + " "  + blockMonth + " "+ blockYear + " "  + blockHour + " "    + blockMinutes + " "  + blockTitle );
			        }
			        System.out.println(out.toString());   //Prints the string content read from input stream
			        reader.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (gui != null) {
			gui.notifyModelHasChanged();
		}
		//StringWriter in = new StringWriter()
	}

}
