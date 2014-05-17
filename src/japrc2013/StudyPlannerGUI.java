package japrc2013;

import japrc2013.StudyPlannerInterface.CalendarEventType;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

//EX. No: Y1557324

public final class StudyPlannerGUI extends JFrame implements StudyPlannerGUIInterface {
	private JButton generateButton;
	private JButton exitButton;
	private JButton btnAddTop;
	private JButton btnDeletTop;
	private JButton btnAddEvent;
	private JButton btnSave;
	private JButton btnLoad;
	private JButton btnSetTarget;
	private JList<String> topicList;
	private JList<String> calendarEvents;
	private JList<String> studyPlan;
	private JList<String> calType;
	private JLabel topicLabel;
	private JLabel lblYear;
	private JLabel lblMonth;
	private JLabel lblDay;
	private JLabel lblTime;
	private JLabel planLabel;
	private JLabel lblTopicTitle;
	private JLabel lblTopicsDuration;
	private JLabel lblCalendarEvents;
	private JLabel lblEventsTitle;
	private JLabel lblEventsDuration;
	private JLabel lblEventsStartTime;
	private JLabel lblEventsType;
	private JTextField topicSub;
	private JTextField topicLen;
	private JTextField eventsTitle;
	private JTextField eventsDuration;
	private JTextField eventsYear;
	private JTextField eventsMonth;
	private JTextField eventsDay;
	private JTextField eventsTime;
	private JScrollPane scrollPane1;
	private JScrollPane scrollPane2;
	private JScrollPane scrollPane3;
	private JFileChooser fc;
	private JFileChooser fc2;

	private StudyPlanner planner;

	public StudyPlannerGUI(StudyPlanner simToUse) {
		super("Study Planner");
		setLayout(null);

		this.planner = simToUse;

		generateButton = new JButton("Generate Study Plan");
		generateButton.setBounds(590, 616, 165, 166);
		generateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
//		
//			Calendar start = new GregorianCalendar(2014,0, 3, 9, 00,00);
//				planner.setDailyStartStudyTime(startTime);
			planner.generateStudyPlan();
//			planner.generateStudyPlan(start);
			updateDisplay();
//			
			}
		});
		add(generateButton);

		exitButton = new JButton("Exit Program");
		exitButton.setBounds(757, 730, 159, 52);
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		add(exitButton);

		topicLabel = new JLabel("Topics:");
		topicLabel.setFont(new Font("Lucida Grande", Font.BOLD, 18));
		topicLabel.setBounds(17, 6, 73, 20);
		add(topicLabel);

		String[] data = { "one", "two", "three", "four" };
		topicList = new JList<String>(data);
		topicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane2 = new JScrollPane(topicList);
		scrollPane2.setBounds(17, 34, 326, 121);
		add(scrollPane2);

		planLabel = new JLabel("Study Plan:");
		planLabel.setFont(new Font("Lucida Grande", Font.BOLD, 18));
		planLabel.setBounds(163, 396, 134, 32);
		add(planLabel);

		data = new String[] { "one", "two", "three", "four" };
		studyPlan = new JList<String>(data);
		scrollPane1 = new JScrollPane(studyPlan);
		scrollPane1.setBounds(17, 426, 466, 356);
		add(scrollPane1);
		
		lblTopicTitle = new JLabel("Topic's Title");
		lblTopicTitle.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		lblTopicTitle.setBounds(17, 209, 100, 20);
		add(lblTopicTitle);
		
		lblTopicsDuration = new JLabel("Topic's Duration");
		lblTopicsDuration.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		lblTopicsDuration.setBounds(17, 297, 139, 20);
		add(lblTopicsDuration);

		topicSub = new JTextField();
		topicSub.setBounds(17, 237, 326, 41);
		topicSub.setColumns(10);
		
//		Clear input fields when a user click on them
		topicSub.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				topicSub.setText("");
			}
		});
		add(topicSub);

		topicLen = new JTextField();
		topicLen.setBounds(17, 320, 326, 41);
		topicLen.setColumns(10);
		topicLen.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				topicLen.setText("");
			}
		});
		add(topicLen);

		btnAddTop = new JButton("Add Topic");
		btnAddTop.setBounds(355, 237, 128, 124);
		btnAddTop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!topicSub.getText().equals("") && !topicLen.getText().equals("")) {
					planner.addTopic(topicSub.getText(), Integer.parseInt(topicLen.getText()));
					updateDisplay();
				} else {
					throw new StudyPlannerException("No input in fields topic's subject and topic's lenght!");
				}
			}
		});
		add(btnAddTop);

		btnDeletTop = new JButton("Delete Topic");
		btnDeletTop.setBounds(215, 167, 128, 41);
		btnDeletTop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (topicList.getSelectedValue() != null && !topicList.equals("")) {
					String name = topicList.getSelectedValue().split(" \\(")[0];
					planner.deleteTopic(name);
					updateDisplay();
				} else {
					throw new StudyPlannerException("No topic selection");
				}
			}
		});
		add(btnDeletTop);
		
		lblCalendarEvents = new JLabel("Calendar Events:");
		lblCalendarEvents.setFont(new Font("Lucida Grande", Font.BOLD, 18));
		lblCalendarEvents.setBounds(590, 6, 176, 20);
		add(lblCalendarEvents);
		
		data = new String[] { "one", "two", "three", "four" };
		calendarEvents = new JList<String>();
		calendarEvents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane3 = new JScrollPane(calendarEvents);
		scrollPane3.setBounds(590, 34, 326, 121);
		add(scrollPane3);
		
		lblEventsTitle = new JLabel("Event's Title");
		lblEventsTitle.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		lblEventsTitle.setBounds(590, 179, 100, 20);
		add(lblEventsTitle);
		
		eventsTitle= new JTextField();
		eventsTitle.setColumns(10);
		eventsTitle.setBounds(590, 200, 326, 41);
		add(eventsTitle);
		
		lblEventsDuration = new JLabel("Event's Duration");
		lblEventsDuration.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		lblEventsDuration.setBounds(590, 253, 134, 20);
		add(lblEventsDuration);
		
		eventsDuration = new JTextField();
		eventsDuration.setColumns(10);
		eventsDuration.setBounds(590, 276, 326, 41);
		add(eventsDuration);
		
		lblYear = new JLabel("Year");
		lblYear.setBounds(611, 355, 40, 16);
		add(lblYear);
		
		lblMonth = new JLabel("Month");
		lblMonth.setBounds(690, 355, 49, 16);
		add(lblMonth);
		
		lblDay = new JLabel("Day");
		lblDay.setBounds(779, 355, 40, 16);
		add(lblDay);
		
		lblTime = new JLabel("Time");
		lblTime.setBounds(861, 355, 40, 16);
		add(lblTime);
		
		lblEventsStartTime = new JLabel("Event's Start Time");
		lblEventsStartTime.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		lblEventsStartTime.setBounds(590, 329, 149, 20);
		add(lblEventsStartTime);
		
		eventsYear = new JTextField();
		eventsYear.setColumns(10);
		eventsYear.setBounds(590, 372, 81, 41);
		add(eventsYear);
		
		eventsMonth = new JTextField();
		eventsMonth.setColumns(10);
		eventsMonth.setBounds(674, 372, 81, 41);
		add(eventsMonth);
		
		eventsDay = new JTextField();
		eventsDay.setColumns(10);
		eventsDay.setBounds(757, 372, 81, 41);
		add(eventsDay);
		
		eventsTime = new JTextField();
		eventsTime.setColumns(10);
		eventsTime.setBounds(839, 372, 81, 41);
		add(eventsTime);
		
		lblEventsType = new JLabel("Event's Type");
		lblEventsType.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		lblEventsType.setBounds(590, 423, 108, 20);
		add(lblEventsType);
		
		String[] dataTypes = new String[] { "ESSAY", "EXAM", "OTHER"};
		calType = new JList<String>(dataTypes);
		calType.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		calType.setBounds(590, 455, 165, 136);
		add(calType);
		
		btnAddEvent = new JButton("Add Event");
		btnAddEvent.setBounds(788, 465, 128, 117);
		btnAddEvent.addActionListener(new ActionListener() { // Check for the required field and if everything is ok adds an Event to the planner
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!eventsTitle.getText().equals("") && !eventsDuration.getText().equals("") && !eventsYear.getText().equals("") && !eventsMonth.getText().equals("") && !eventsDay.getText().equals("") && !eventsTime.getText().equals("") && calType.getSelectedValue()!=null) {
					if(calType.getSelectedValue().equals("OTHER")){
						String[] time = eventsTime.getText().split(":");
						String hour = time[0];
						String min = time[1];
					planner.addCalendarEvent(eventsTitle.getText(),new GregorianCalendar(Integer.parseInt(eventsYear.getText()), Integer.parseInt(eventsMonth.getText())-1, Integer.parseInt(eventsDay.getText()), Integer.parseInt(hour), Integer.parseInt(min), 00), Integer.parseInt(eventsDuration.getText()));
					} else {
						String[] time = eventsTime.getText().split(":");
						String hour = time[0];
						String min = time[1];
						if(calType.getSelectedValue().equals("ESSAY")) {
						planner.addCalendarEvent(eventsTitle.getText(),new GregorianCalendar(Integer.parseInt(eventsYear.getText()), Integer.parseInt(eventsMonth.getText())-1, Integer.parseInt(eventsDay.getText()), Integer.parseInt(hour), Integer.parseInt(min), 00), Integer.parseInt(eventsDuration.getText()), CalendarEventType.ESSAY);
					} else {
						planner.addCalendarEvent(eventsTitle.getText(),new GregorianCalendar(Integer.parseInt(eventsYear.getText()), Integer.parseInt(eventsMonth.getText())-1, Integer.parseInt(eventsDay.getText()), Integer.parseInt(hour), Integer.parseInt(min), 00), Integer.parseInt(eventsDuration.getText()), CalendarEventType.EXAM);
					}
					}
					updateDisplay();
				} else {
					throw new StudyPlannerException("Invalid action! You should complete all the steps in order to add an Event");
				}
			}
		});
		add(btnAddEvent);
		
		btnSave = new JButton("Save");
		btnSave.setBounds(757, 616, 159, 52);
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fc = new JFileChooser(System.getProperty("user.home") + "/" + "Documents" + "/" + "workspace" + "/" + "java" + "/" + "japrc2013");
				int returnVal = fc.showSaveDialog(StudyPlannerGUI.this);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                try {
						OutputStream os = new FileOutputStream (fc.getSelectedFile()) ;
						planner.saveData(os);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	 
	        }
		});
		add(btnSave);
		
		btnLoad = new JButton("Load");
		btnLoad.setBounds(757, 673, 159, 52);
		btnLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fc2 = new JFileChooser(System.getProperty("user.home") + "/" + "Documents" + "/" + "workspace" + "/" + "java" + "/" + "japrc2013");
				int returnVal = fc2.showSaveDialog(StudyPlannerGUI.this);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
//	            	if(fc2.getSelectedFile().length() != 0){
	            	try {
						InputStream is = new FileInputStream (fc2.getSelectedFile()) ;
						planner.loadData(is);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//	            } else {
//	            	throw new StudyPlannerException("File is empty");
//	            }
	            	updateDisplay();	
	            }
	 
	        }
		});
		add(btnLoad);
		
		btnSetTarget = new JButton("<--- Set Target --->");
		btnSetTarget.setBounds(378, 69, 175, 52);
		btnSetTarget.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (topicList.getSelectedValue()!=null && calendarEvents.getSelectedValue()!=null) {
					String topicName = topicList.getSelectedValue().split(" \\(")[0];
					String calEvName = calendarEvents.getSelectedValue().split(" \\ ")[2];
					String calEvTime = calendarEvents.getSelectedValue().split(" \\ ")[1];
					for (int i = 0; i < planner.getTopics().size(); i++) {
						for (int j =0; j < planner.getCalendarEvents().size(); j++) {
							if (planner.getTopics().get(i).getSubject().equals(topicName)){
								SimpleDateFormat eventFormat2 = new SimpleDateFormat("HH:mm");
								String eventDate2;
								eventDate2 = eventFormat2.format(planner.getCalendarEvents().get(j).getStartTime().getTime());
								if (eventDate2.equals(calEvTime) && planner.getCalendarEvents().get(j).getName().equals(calEvName)) {
									if (planner.getCalendarEvents().get(j).isValidTopicTarget()) {
										planner.getTopics().get(i).setTargetEvent(planner.getCalendarEvents().get(j));
										System.out.println(planner.getTopics().get(i).getTargetEvent().toString());
										System.out.println(planner.getCalendarEvents().get(j).toString());
									} else {
										throw new StudyPlannerException("This Event is Invalid for a target");
									}
								}
							}
						}
					}
					
				} else {
					throw new StudyPlannerException("Please select Topic AND Event");
				}
			}
		});
		add(btnSetTarget);
	
	
	}

	@Override
	public void notifyModelHasChanged() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateDisplay();
			}
		});
	}

	private void updateDisplay() {
		if (planner == null) {
			// nothing to update from, so do nothing
		} else {
			
			List<String> topicData = new ArrayList<String>();
			for (TopicInterface t : planner.getTopics()) {
				topicData.add(t.getSubject() + " (" + t.getDuration() + ")");
			}
			topicList.setListData(topicData.toArray(new String[1]));
			
			SimpleDateFormat eventFormat = new SimpleDateFormat("dd/MM/yyyy'  'HH:mm");
			String eventDate;
			List<String> calenData = new ArrayList<String>();
			for (CalendarEventInterface t : planner.getCalendarEvents()) {
				eventDate = eventFormat.format(t.getStartTime().getTime());
				calenData.add(eventDate + "  " + t.getName() + "  (" + t.getDuration() + ")");
			}
			calendarEvents.setListData(calenData.toArray(new String[1]));
			

			List<String> eventData = new ArrayList<String>();
			SimpleDateFormat calendarFormat = new SimpleDateFormat("dd/MM/yyyy'  'HH:mm");
			String time;
			for (StudyBlockInterface ev : planner.getStudyPlan()) {
				time = calendarFormat.format(ev.getStartTime().getTime());

				if ( ev.getTopic().equals("break")) {
					eventData.add(time + " " + "(" + ev.getTopic() +")");
				} else {
					eventData.add(time + ") " + ev.getTopic() + " (" + ev.getDuration() + ")");
				}
			}
			
			studyPlan.setListData(eventData.toArray(new String[1]));
		}

	}

	public static void main(String[] args) {
		StudyPlanner planner = new StudyPlanner();
		planner.addTopic("Java file handling", 480);
		planner.addTopic("European agricultural policy 1950-1974", 720);
		

		StudyPlannerGUI gui = new StudyPlannerGUI(planner);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setSize(940, 840);

		planner.setGUI(gui);

		gui.setVisible(true);
		gui.updateDisplay();
	}

}
