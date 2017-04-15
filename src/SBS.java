
/**
 * This class is the main entry of the system.
 * 
 * @author Alex Truong (101265224) 
 * @version 0.1
 */

import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;
import java.io.*;
import java.time.format.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import CException.*;

public class SBS extends JFrame implements ActionListener, WindowListener, ItemListener, ChangeListener, ListSelectionListener {
	
	// system states enumeration
	// http://stackoverflow.com/questions/8157755/how-to-convert-enum-value-to-int
	private enum SystemState {
		SYSTEM_START (0), 
		SHOW_AVAILABLE_COURTS_1 (1), 
		MAKE_BOOKING_2 (2),
		SHOW_MEMBER_BOOKINGS_3 (3),
		SHOW_COURT_BOOKINGS_4 (4),
		DELETE_BOOKING_5 (5),
		SYSTEM_EXIT (6);
		
	    private final int value;
	    /**
	     * 
	     * @param value
	     */
	    private SystemState(int value) {
	        this.value = value;
	    }
	}
	
	private enum CommandState {
		SHOW_AVAILABLE_COURTS (7), 
		MAKE_BOOKING (8), 
		SHOW_COURT_BOOKINGS (9),
		SHOW_MEMBER_BOOKINGS (10),
		ADD_BOOKING (11),
		SHOW_BOOKINGS (12),
		DELETE_BOOKING (13);
		
	    private final int value;
	    /**
	     * 
	     * @param value
	     */
	    private CommandState(int value) {
	        this.value = value;
	    }
	}
	
	private final String[] ActionCommands = {
		"SYSTEM_START", 
		"SHOW_AVAIL_COURTS",
		"MAKE_BOOKING",
		"SHOW_MEMBER_BOOKINGS",
		"SHOW_COURT_BOOKINGS",
		"DELETE_BOOKINGS",
		"SYSTEM_EXIT",
		"Show Available Courts",
		"Make Booking",
		"Show Court Bookings",
		"Show Member Bookings",
		"Add Booking",
		"Show Bookings",
		"Delete Booking"
	};
	
	//
	private enum RunMode {
		CONSOLE,
		GUI
	}
	
	// keep track of what state we're in
	private SystemState systemState;
	
	//
	private RunMode runMode;
		
	// helper object for user input and utility methods
	private Utility utility;
	
	//
	private Club club = null;
	
	//
	private boolean hasDataToWrite;
	
	//
	private JPanel posterPanel;
	private JPanel availableCourtPanel;
	private JPanel makeBookingPanel;
	private JPanel memberBookingPanel;
	private JPanel courtBookingPanel;
	private JPanel deleteBookingPanel;
	
	//
	private JPanel cardsPanel;
	
	// Show Available Courts controls and models
	private DefaultListModel listModel1;
	private CDateTimeSpinner date1;
	private CDateTimeSpinner startHour1;
	private CDateTimeSpinner endHour1;

	// Show Court Booking controls and models
	private DefaultListModel listModel4;

	// Show Member Booking
	private DefaultListModel listModel3;
	
	// 
	private Hashtable<String, BufferedImage> bufferedImageMap;
	private Hashtable<String, Icon> iconMap;

	// Make booking
	private CDateTimeSpinner date2;
	private CDateTimeSpinner startHour2;
	private DefaultListModel listModel2;
	private DefaultComboBoxModel sportModel2;
	private DefaultComboBoxModel memberModel2;
	private JSlider duration2;
	private JLabel durationLabel2;
	private CComboBox sportComboBox2;
	private CComboBox memberComboBox2;
	private CBlockInfo sportInfo2;
	private CBlockInfo memberInfo2;
	private JButton addButton2;
	private CBlockList availableCourtList2;
	private boolean modifiedSince;

	// Delete Booking panel
	private DefaultComboBoxModel memberModel5;
	private CComboBox memberComboBox5;

	private CDateTimeSpinner date5;

	private JButton deleteButton5;

	private DefaultListModel listModel5;

	private CBookingList bookingList5;

	private boolean modifiedSince5;
	
	//
	private static SBS app;
	
	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {
		// finalize system
		systemExit();
	}
	
	/**
	 * 
	 */
	public void valueChanged(ListSelectionEvent lse) {
		JList list = (JList)lse.getSource();
		if (systemState == SystemState.MAKE_BOOKING_2) {
			if (lse.getValueIsAdjusting() && !this.modifiedSince) {
				this.addButton2.setEnabled(true);
			}
		} else if (systemState == SystemState.DELETE_BOOKING_5) {
			if (lse.getValueIsAdjusting() && !this.modifiedSince5) {
				this.deleteButton5.setEnabled(true);
			}
		}
	}
	
	/**
	 * 
	 */
    public void itemStateChanged(ItemEvent event) {
       if (event.getStateChange() == ItemEvent.SELECTED) {
          Object item = event.getItem();
          if (systemState == SystemState.MAKE_BOOKING_2) {
	          if (item instanceof Sport) {
	        	  updateSportInfo();
	        	  filterMemberListBySport();
	        	  updateSliderParameters();
	          } else if (item instanceof Member) {
	        	  updateMemberInfo();
	          }
	          this.modifiedSince = true;
	          this.addButton2.setEnabled(false);
          }
       }
    }       
	
	/**
	 *
	 */
	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() instanceof JSlider) {
			JSlider slider = (JSlider)ce.getSource();
			durationLabel2.setText(String.format("%d mins", slider.getValue()));
			this.modifiedSince = true;
			if (this.addButton2 != null) {
				this.addButton2.setEnabled(false);
			}
		}
	}
	
	/**
	 * 
	 */
	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		
		// check for menu command
		if (command.equals("About")) {
			JOptionPane.showMessageDialog(null, "SBS Program\nCreated by Alex Truong", "About", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		for (SystemState state: SystemState.values()) {
			String menuCommand = ActionCommands[state.value];
			if (menuCommand.equals(command)) {
				handleMenuCommand(state);
				return;
			}
		}
		
		CommandState commandState = getCommandState(command);
		switch (this.systemState) {
		case SHOW_AVAILABLE_COURTS_1:
			handleShowAvailableCourtCommands(commandState);
			break;
		case MAKE_BOOKING_2:
			handleMakeBookingCommands(commandState);
			break;
		case SHOW_MEMBER_BOOKINGS_3:
			handleShowMemberBookingsCommands(commandState);
			break;
		case SHOW_COURT_BOOKINGS_4:
			handleShowCourtBookingsCommands(commandState);
			break;
		case DELETE_BOOKING_5:
			handleDeleteBookingCommands(commandState);
			break;
		}
	}
    
	private void handleDeleteBookingCommands(CommandState commandState) {
		if (commandState == CommandState.SHOW_BOOKINGS) {
			this.deleteButton5.setEnabled(false);
			Member member = (Member) this.memberComboBox5.getComboBox().getSelectedItem();
			DateAndTime date = this.date5.getDateAndTime();
			ArrayList<Booking> bookingList = club.getBookingsByDate(member, date);
			Collections.sort(bookingList);
			this.listModel5.removeAllElements();
			for (Booking booking: bookingList) {
				this.listModel5.addElement(booking);
			}
			this.modifiedSince5 = false;
		} else if (commandState == CommandState.DELETE_BOOKING) {
			Booking booking = (Booking) this.bookingList5.getList().getSelectedValue();
			if (booking != null) {
				int ret = JOptionPane.showConfirmDialog(null, "Delete following booking:\n" +
			              booking.toString(), 
						  "Delete booking", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				
				if (ret == JOptionPane.YES_OPTION) {
					this.deleteButton5.setEnabled(false);
					club.deleteBooking(booking);
					this.listModel5.removeElement(booking);
				}
			}
		}
	}
	/**
	 * 
	 * @param commandState
	 */
	private void handleMakeBookingCommands(CommandState commandState) {
		if (commandState == CommandState.SHOW_AVAILABLE_COURTS) {
			this.addButton2.setEnabled(false);
			
			DateAndTime startTime = DateAndTime.fromString(date2.getDateAndTime().toString("d/M/yyyy") + " " + 
                    startHour2.getDateAndTime().toString("H:m"), "d/M/yyyy H:m");

			DateAndTime endTime = startTime.plusMinutes(duration2.getValue());

			if (DateAndTime.isThePast(startTime)) {
				JOptionPane.showMessageDialog(null, "Date and time is in the past", "Invalid time", JOptionPane.ERROR_MESSAGE);
				return;
			} 

			if (!DateAndTime.isWithinNumberOfDays(endTime, 7)) {
				JOptionPane.showMessageDialog(null, "Time exceeds 7 days in advance", "Invalid time", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!club.isBetweenWorkingHours(startTime)) {
				JOptionPane.showMessageDialog(null, "Start hour is not in working hours range", "Invalid hour", JOptionPane.ERROR_MESSAGE);
				return;
			} 
			
			if (!club.isBetweenWorkingHours(endTime)) {
				JOptionPane.showMessageDialog(null, "End hour is not in working hours range", "Invalid hour", JOptionPane.ERROR_MESSAGE);
				return;
			} 
			
			Sport sport = (Sport)this.sportComboBox2.getComboBox().getSelectedItem();
			Member member = (Member)this.memberComboBox2.getComboBox().getSelectedItem();
			if (member != null) {
				ArrayList<Court> courtList = club.getAvailableCourts(sport, member, startTime, endTime); 
				Collections.sort(courtList);
				this.listModel2.removeAllElements();
				for (Court court: courtList) {
					this.listModel2.addElement(court);
				}
				this.modifiedSince = false;
			} else {
				JOptionPane.showMessageDialog(null, "No member selected", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if (commandState == CommandState.ADD_BOOKING) {
			Member member = (Member) this.memberComboBox2.getComboBox().getSelectedItem();
			Court court = (Court) this.availableCourtList2.getList().getSelectedValue();
			Sport sport = (Sport) this.sportComboBox2.getComboBox().getSelectedItem();
			DateAndTime startTime = DateAndTime.fromString(date2.getDateAndTime().toString("d/M/yyyy") + " " + 
                                                           startHour2.getDateAndTime().toString("H:m"), "d/M/yyyy H:m");

			int minutes = this.duration2.getValue();
			
			try {
				Booking booking = new Booking(member, court, startTime, minutes);
				club.addBooking(booking);
				/*
				JOptionPane.showMessageDialog(null, "The following booking has been added succesfully:\n" +
						                            member.getName() + " plays " + sport.getSportName() + 
						                            " on court " + court.getNumber() + "\n" +
						                            booking.getStartTime().toString("d/M/yyyy H:m") + " - " +
						                            booking.getEndTime().toString("H:m"),
						                            "Booking added", JOptionPane.INFORMATION_MESSAGE);
				*/
				this.memberInfo2.repaint();
				this.modifiedSince = true;
				this.addButton2.setEnabled(false);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Booking error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * 
	 * @param commandState
	 */
	private void handleShowMemberBookingsCommands(CommandState commandState) {
		if (commandState == CommandState.SHOW_MEMBER_BOOKINGS) {
			this.listModel3.removeAllElements();
			ArrayList<Member> memberList = club.getBookedMemberList(); 
			Collections.sort(memberList);
			for (Member member: memberList) {
				this.listModel3.addElement(member);
			}
		}
	}

	/**
	 * 
	 * @param commandState
	 */
	private void handleShowCourtBookingsCommands(CommandState commandState) {
		if (commandState == CommandState.SHOW_COURT_BOOKINGS) {
			this.listModel4.removeAllElements();
			ArrayList<Court> courtList = club.getBookedCourts(); 
			Collections.sort(courtList);
			for (Court court: courtList) {
				this.listModel4.addElement(court);
			}
		}
	}
	/**
	 * 
	 * @param commandState
	 */
	private void handleShowAvailableCourtCommands(CommandState commandState) {
		if (commandState == CommandState.SHOW_AVAILABLE_COURTS) {
			DateAndTime startTime = DateAndTime.fromString(date1.getDateAndTime().toString("d/M/yyyy") + " " + 
		                                                   startHour1.getDateAndTime().toString("H:m"), "d/M/yyyy H:m");
			DateAndTime endTime = DateAndTime.fromString(date1.getDateAndTime().toString("d/M/yyyy") + " " + 
		                                                   endHour1.getDateAndTime().toString("H:m"), "d/M/yyyy H:m");
			
			
			if (DateAndTime.isThePast(startTime)) {
				JOptionPane.showMessageDialog(null, "Date and time is in the past", "Invalid time", JOptionPane.ERROR_MESSAGE);
				return;
			} 

			if (!DateAndTime.isWithinNumberOfDays(endTime, 7)) {
				JOptionPane.showMessageDialog(null, "Time exceeds 7 days in advance", "Invalid time", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (startTime.compareTo(endTime) >= 0) {
				JOptionPane.showMessageDialog(null, "Start hour is NOT before end hour.", "Invalid hour", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!club.isBetweenWorkingHours(startTime)) {
				JOptionPane.showMessageDialog(null, "Start hour is not in working hours range", "Invalid hour", JOptionPane.ERROR_MESSAGE);
				return;
			} 
			
			if (!club.isBetweenWorkingHours(endTime)) {
				JOptionPane.showMessageDialog(null, "End hour is not in working hours range", "Invalid hour", JOptionPane.ERROR_MESSAGE);
				return;
			} 

			this.listModel1.removeAllElements();
			ArrayList<Court> courtList = club.getAvailableCourts(startTime, endTime); 
			Collections.sort(courtList);
			for (Court court: courtList) {
				this.listModel1.addElement(court);
			}
		}
	}
	/**
	 * 
	 * @param command
	 * @return
	 */
	private CommandState getCommandState(String command) {
		for (CommandState state: CommandState.values()) {
			String stateCommand = ActionCommands[state.value];
			if (stateCommand.equals(command)) {
				return state;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 */
	private void systemExit() {
		if (JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", 
				                          "Exit confirmation", JOptionPane.YES_NO_OPTION, 
				                          JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
			
			this.finalizeSystem();
			System.exit(0);
		}
	}
	
	/**
	 * 
	 * @param state
	 */
	private void handleMenuCommand(SystemState menu) {
		switch (menu) {
		case SYSTEM_EXIT:
			systemExit();
		default:
			this.systemState = menu;
			setVisiblePanel();
			break;
		}
	}
	/**
	 * main entry of the program
	 * @param args array of arguments passed to the program
	 */
	public static void main(String[] args)
    {
		app = new SBS();
		
		// initialize
		app.initializeSystem(args);

		if (app.runMode == RunMode.CONSOLE) {
			// main loop
			app.mainLoop();
				
			// finalize
			app.finalizeSystem();
		} else {
			// GUI
			app.initializeGUI();
			app.setVisible(true);
		}
    }
	
	public SBS() {
		this.posterPanel = null;
		this.availableCourtPanel = null;
		this.makeBookingPanel = null;
		this.courtBookingPanel = null;
		this.memberBookingPanel = null;
		this.deleteBookingPanel = null;
	}
	
	/**
	 * 
	 */
	private void initializeGUI() {
		// this frame
		this.setTitle(club.getName());
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		this.setSize(1000, 700);

		//
		this.bufferedImageMap = new Hashtable<>();
		this.iconMap = new Hashtable<>();
		
		//
		this.initializeMenu();
		
		//
		this.initializePanels();
		
		//
		this.setVisiblePanel();
	}
	
	/**
	 * http://stackoverflow.com/questions/14821952/changing-panels-using-the-card-layout
	 */
	private void setVisiblePanel() {
		CardLayout cardLayout = (CardLayout) this.cardsPanel.getLayout();
		cardLayout.show(this.cardsPanel, ActionCommands[this.systemState.value]);
	}
	
	/**
	 * 
	 */
	private void initializePanels() {
		this.cardsPanel = new JPanel(new CardLayout());

		// add to the cards panel
		for (SystemState state: SystemState.values()) {
			JPanel panel = getPanel(state);
			if (panel != null) {
				this.cardsPanel.add(panel, ActionCommands[state.value]);
			}
		}
		
		this.add(this.cardsPanel, BorderLayout.CENTER);
	}
	
	/**
	 * 
	 * @param state
	 * @return
	 */
	private JPanel getPanel(SystemState state) {
		
		switch (state) {
		case SYSTEM_START:
			return getPosterPanel();
		case SHOW_AVAILABLE_COURTS_1:
			return getAvailableCourtPanel();
		case MAKE_BOOKING_2:
			return getMakeBookingPanel();
		case SHOW_MEMBER_BOOKINGS_3:
			return getMemberBookingPanel();
		case SHOW_COURT_BOOKINGS_4:
			return getCourtBookingPanel();
		case DELETE_BOOKING_5:
			return getDeleteBookingPanel();
		}
		
		return null;
	}

	/**
	 * 
	 * @return
	 */
	private JPanel getDeleteBookingPanel() {
		if (this.deleteBookingPanel == null) {
			this.deleteBookingPanel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			
			JLabel icon = new JLabel(new CScaleImageIcon("icons/delete_booking.png", 48));
			icon.setBorder(new EmptyBorder(10, 10, 10, 10));
			
			CInfoLabel help = new CInfoLabel("DELETE BOOKING\n"
					+ "1. Select member from the list.\n"
					+ "2. Input the date for the bookings.\n"
					+ "3. Click 'Show Bookings' button to show all bookings for that particular date.\n"
					+ "4. Select the booking from the list and click 'Delete Booking' button.");
			help.setBorder(new EmptyBorder(10, 10, 10, 10));
		    
			Insets defaultInsets = new Insets(0, 5, 0, 0);
			gbc.gridy = 0;
			gbc.gridx = 0;
			gbc.gridwidth = 3;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.insets = defaultInsets;
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			JPanel banner = new JPanel(new BorderLayout());
			banner.add(icon, BorderLayout.WEST);
			banner.add(help, BorderLayout.CENTER);
			this.deleteBookingPanel.add(banner, gbc);
			
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			gbc.fill = GridBagConstraints.LINE_START;
			this.memberModel5 = new DefaultComboBoxModel<>();
			ArrayList<Member> memberList = club.getMemberList();
			Collections.sort(memberList);
			for (Member member: memberList) {
				this.memberModel5.addElement(member);
			}
			this.memberComboBox5 = new CComboBox(this, "Member:", this.memberModel5);
			this.memberComboBox5.getComboBox().addItemListener(this);
			this.deleteBookingPanel.add(this.memberComboBox5, gbc);
	
			gbc.gridx++;
			this.date5 = new CDateTimeSpinner("Date:", CDateTimeSpinner.SpinnerType.DateOnly);
			this.deleteBookingPanel.add(this.date5, gbc);
			
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.weightx = 0.0;
			gbc.fill = GridBagConstraints.LINE_START;
			
			JPanel buttonPanel = new JPanel(new FlowLayout());
			JButton button = new JButton(ActionCommands[CommandState.SHOW_BOOKINGS.value], getScaleImageIcon("search"));
			button.addActionListener(this);
			buttonPanel.add(button);
	
			this.deleteButton5 = new JButton(ActionCommands[CommandState.DELETE_BOOKING.value], getScaleImageIcon("remove"));
			this.deleteButton5.addActionListener(this);
			this.deleteButton5.setEnabled(false);
			buttonPanel.add(this.deleteButton5, gbc);
			
			this.deleteBookingPanel.add(buttonPanel, gbc);
			
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.weighty = 1.0;
			gbc.weightx = 1.0;
			gbc.gridwidth = 3;
			gbc.insets = new Insets(0, 5, 5, 5);
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.PAGE_END;
			this.listModel5 = new DefaultListModel();
			this.bookingList5 = new CBookingList(this, listModel5);
			this.bookingList5.setListBackgound(this.getBackground());
			this.bookingList5.getList().addListSelectionListener(this);
			this.deleteBookingPanel.add(this.bookingList5, gbc);
		}
	
		return this.deleteBookingPanel;
	}
	
	/**
	 * 
	 * @return
	 */
	private JPanel getCourtBookingPanel() {
		if (this.courtBookingPanel == null) {
			this.courtBookingPanel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			
			JLabel icon = new JLabel(new CScaleImageIcon("icons/court_booking.png", 48));
			
			CInfoLabel help = new CInfoLabel("SHOW COURT BOOKINGS\n"
					+ "1. This screen shows the list of courts which have one or more bookings.\n"
					+ "2. Click 'Show Court Bookings' button to show/refresh the list.");
		    
			gbc.gridy = 0;
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(10, 10, 0, 10);
			this.courtBookingPanel.add(icon, gbc);
			
			gbc.gridx = 1;
			gbc.gridwidth = 2;
			this.courtBookingPanel.add(help, gbc);
			
			gbc.gridy = 1;
			gbc.gridx = 0;
			JButton button = new JButton(ActionCommands[CommandState.SHOW_COURT_BOOKINGS.value], getScaleImageIcon("search"));
			button.addActionListener(this);
			this.courtBookingPanel.add(button, gbc);
			
			gbc.gridy = 2;
			gbc.gridx = 0;
			gbc.weighty = 1.0;
			gbc.weightx = 1.0;
			gbc.gridwidth = 3;
			gbc.insets = new Insets(0, 5, 5, 5);
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.PAGE_END;
			this.listModel4 = new DefaultListModel();
			CBlockList list = new CBlockList(this, listModel4);
			list.setListBackgound(this.getBackground());
			this.courtBookingPanel.add(list, gbc);
		}
		
		return this.courtBookingPanel;
	}
	/**
	 * 
	 * @return
	 */
	private JPanel getMemberBookingPanel() {
		if (this.memberBookingPanel == null) {
			this.memberBookingPanel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			
			JLabel icon = new JLabel(new CScaleImageIcon("icons/member_booking.png", 48));
			
			CInfoLabel help = new CInfoLabel("SHOW MEMBER BOOKINGS\n"
					+ "1. This screen shows the list of members which have one or more bookings.\n"
					+ "2. Click 'Show Member Bookings' button to show/refresh the list.");
		    
			gbc.gridy = 0;
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(10, 10, 0, 10);
			this.memberBookingPanel.add(icon, gbc);
			
			gbc.gridx = 1;
			gbc.gridwidth = 2;
			this.memberBookingPanel.add(help, gbc);
			
			gbc.gridy = 1;
			gbc.gridx = 0;
			JButton button = new JButton(ActionCommands[CommandState.SHOW_MEMBER_BOOKINGS.value], getScaleImageIcon("search"));
			button.addActionListener(this);
			this.memberBookingPanel.add(button, gbc);
			
			gbc.gridy = 2;
			gbc.gridx = 0;
			gbc.weighty = 1.0;
			gbc.weightx = 1.0;
			gbc.gridwidth = 3;
			gbc.insets = new Insets(0, 5, 5, 5);
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.PAGE_END;
			this.listModel3 = new DefaultListModel();
			CBlockList list = new CBlockList(this, listModel3);
			list.setListBackgound(this.getBackground());
			this.memberBookingPanel.add(list, gbc);
		}
		
		return this.memberBookingPanel;
	}
	/**
	 * 
	 * @return
	 */
	private JPanel getMakeBookingPanel() {
		if (this.makeBookingPanel == null) {
			this.makeBookingPanel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			
			JLabel icon = new JLabel(new CScaleImageIcon("icons/make_booking.png", 48));
			icon.setBorder(new EmptyBorder(10, 10, 10, 10));
			
			CInfoLabel help = new CInfoLabel("MAKE BOOKING\n"
					+ "1. Select a sport from the list.\n"
					+ "2. Select member who plays the sport.\n"
					+ "3. Input date, start hour and duration for the booking.\n"
					+ "4. Click 'Show Available Courts' button to show all matching courts for the booking.\n"
					+ "5. Select the court and click 'Add Booking' button.");
			help.setBorder(new EmptyBorder(10, 10, 10, 10));
		    
			Insets defaultInsets = new Insets(0, 5, 0, 0);
			gbc.gridy = 0;
			gbc.gridx = 0;
			gbc.gridwidth = 3;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.insets = defaultInsets;
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			JPanel banner = new JPanel(new BorderLayout());
			banner.add(icon, BorderLayout.WEST);
			banner.add(help, BorderLayout.CENTER);
			this.makeBookingPanel.add(banner, gbc);
			
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			gbc.fill = GridBagConstraints.LINE_START;
			this.sportModel2 = new DefaultComboBoxModel<>();
			for (Sport sport: club.getSportList()) {
				this.sportModel2.addElement(sport);
			}
			this.sportComboBox2 = new CComboBox(this, "Sport:", this.sportModel2);
			this.sportComboBox2.getComboBox().addItemListener(this);
			this.makeBookingPanel.add(this.sportComboBox2, gbc);

			gbc.gridx++;
			this.memberModel2 = new DefaultComboBoxModel<>();
			this.memberComboBox2 = new CComboBox(this, "Member:", this.memberModel2);
			this.memberComboBox2.getComboBox().addItemListener(this);
			this.makeBookingPanel.add(this.memberComboBox2, gbc);

			gbc.gridy++;
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(0, 5, 0, 100);
			this.sportInfo2 = new CBlockInfo(this, null);
			this.sportInfo2.setBackground(this.getBackground());
			this.makeBookingPanel.add(this.sportInfo2, gbc);

			gbc.gridx++;
			gbc.gridwidth = 1;
			gbc.insets = defaultInsets;
			this.memberInfo2 = new CBlockInfo(this, null);
			this.memberInfo2.setBackground(this.getBackground());
			this.makeBookingPanel.add(this.memberInfo2, gbc);

			updateSportInfo();
			filterMemberListBySport();
			updateMemberInfo();
			
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			gbc.fill = GridBagConstraints.LINE_START;
			gbc.insets = defaultInsets;
			JPanel timePanel = new JPanel(new FlowLayout());
			
			this.date2 = new CDateTimeSpinner("Date:", CDateTimeSpinner.SpinnerType.DateOnly);
			this.date2.setBorder(new EmptyBorder(0, 0, 0, 20));
			timePanel.add(date2);
			
			this.startHour2 = new CDateTimeSpinner("Start hour:", CDateTimeSpinner.SpinnerType.TimeOnly);
			this.startHour2.setBorder(new EmptyBorder(0, 0, 0, 20));
			timePanel.add(startHour2);
			
			this.makeBookingPanel.add(timePanel, gbc);
			
		    JPanel durationPanel = new JPanel(new BorderLayout());
			this.durationLabel2 = new JLabel("");
			durationPanel.add(this.durationLabel2, BorderLayout.WEST);

			this.duration2 = new JSlider(JSlider.HORIZONTAL);
		    this.duration2.addChangeListener(this);
		    this.duration2.setMinorTickSpacing(10);
		    this.duration2.setMajorTickSpacing(30);
		    this.duration2.setPaintTicks(true);
		    this.duration2.setPaintLabels(true);
		    this.duration2.setLabelTable(this.duration2.createStandardLabels(30));
		    this.duration2.setMinimum(30);
			durationPanel.add(this.duration2, BorderLayout.CENTER);
			
			gbc.gridx++;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			this.makeBookingPanel.add(durationPanel, gbc);
			
			updateSliderParameters();
			
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.weightx = 0.0;
			gbc.fill = GridBagConstraints.LINE_START;
			
			JPanel buttonPanel = new JPanel(new FlowLayout());
			JButton button = new JButton(ActionCommands[CommandState.SHOW_AVAILABLE_COURTS.value], getScaleImageIcon("search"));
			button.addActionListener(this);
			buttonPanel.add(button);

			this.addButton2 = new JButton(ActionCommands[CommandState.ADD_BOOKING.value], getScaleImageIcon("plus"));
			this.addButton2.addActionListener(this);
			this.addButton2.setEnabled(false);
			buttonPanel.add(this.addButton2, gbc);
			
			this.makeBookingPanel.add(buttonPanel, gbc);
			
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.weighty = 1.0;
			gbc.weightx = 1.0;
			gbc.gridwidth = 3;
			gbc.insets = new Insets(0, 5, 5, 5);
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.PAGE_END;
			this.listModel2 = new DefaultListModel();
			this.availableCourtList2 = new CBlockList(this, listModel2);
			this.availableCourtList2.setListBackgound(this.getBackground());
			this.availableCourtList2.getList().addListSelectionListener(this);
			this.makeBookingPanel.add(this.availableCourtList2, gbc);
		}
		
		return this.makeBookingPanel;
	}
	
	/**
	 * 
	 */
	private void updateSportInfo() {
		Sport sport = (Sport)this.sportComboBox2.getComboBox().getSelectedItem();
		this.sportInfo2.setObject(sport);
		this.sportInfo2.repaint();
	}
	
	/**
	 * 
	 */
	private void updateMemberInfo() {
		Member member = (Member)this.memberComboBox2.getComboBox().getSelectedItem();
		if (member != null) {
			this.memberInfo2.setObject(member);
			this.memberInfo2.repaint();
		}
	}
	
	/**
	 * 
	 */
	private void filterMemberListBySport() {
		Sport sport = (Sport)this.sportComboBox2.getComboBox().getSelectedItem();
		ArrayList<Member> memberList = club.getMemberList(sport);
		
		Collections.sort(memberList);
		this.memberModel2.removeAllElements();
		for (Member member: memberList) {
			this.memberModel2.addElement(member);
		}
	}
	
	/**
	 * 
	 */
	private void updateSliderParameters() {
		// TODO: how to invoke in event dispatching thread (ie SwingUtilities.invokeLater)
	    this.duration2.setMaximum(((Sport)this.sportComboBox2.getComboBox().getSelectedItem()).getMaximumBookingMinutes());
	}
	/**
	 * http://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
	 * @return
	 */
	private JPanel getAvailableCourtPanel() {
		if (this.availableCourtPanel == null) {
			this.availableCourtPanel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			
			JLabel icon = new JLabel(new CScaleImageIcon("icons/court.png", 48));
			icon.setBorder(new EmptyBorder(10, 10, 10, 10));
			
			CInfoLabel help = new CInfoLabel("SHOW AVAILABLE COURTS\n"
					+ "1. Select the date you want to show, date must NOT exceed 7 days from now.\n"
					+ "2. Select the start hour, must be in working hour range.\n"
					+ "3. Select the end hour, must be in working hour range.\n"
					+ "4. Click 'Show Available Courts' button to show all available courts for that particular time.");
			help.setBorder(new EmptyBorder(10, 10, 10, 10));
		    
			Insets defaultInsets = new Insets(0, 5, 0, 0);
			gbc.gridy = 0;
			gbc.gridx = 0;
			gbc.gridwidth = 2;
			gbc.weightx = 0.0;
			gbc.weighty = 0.0;
			gbc.insets = defaultInsets;
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			JPanel banner = new JPanel(new BorderLayout());
			banner.add(icon, BorderLayout.WEST);
			banner.add(help, BorderLayout.CENTER);
			this.availableCourtPanel.add(banner, gbc);
			
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.gridwidth = 2;
			gbc.fill = GridBagConstraints.LINE_START;
			
			JPanel timePanel = new JPanel(new FlowLayout());
			this.date1 = new CDateTimeSpinner("Date:", CDateTimeSpinner.SpinnerType.DateOnly);
			this.date1.setBorder(new EmptyBorder(0, 0, 0, 20));
			timePanel.add(date1);

			this.startHour1 = new CDateTimeSpinner("Start hour:", CDateTimeSpinner.SpinnerType.TimeOnly);
			this.startHour1.setBorder(new EmptyBorder(0, 0, 0, 20));
			timePanel.add(startHour1);
			
			this.endHour1 = new CDateTimeSpinner("End hour:", CDateTimeSpinner.SpinnerType.TimeOnly);
			this.endHour1.setBorder(new EmptyBorder(0, 0, 0, 20));
			timePanel.add(endHour1);
			
			this.availableCourtPanel.add(timePanel, gbc);
			
			gbc.gridy++;
			gbc.gridx = 0;
			JButton button = new JButton(ActionCommands[CommandState.SHOW_AVAILABLE_COURTS.value], getScaleImageIcon("search"));
			button.addActionListener(this);
			this.availableCourtPanel.add(button, gbc);
			
			gbc.gridy++;
			gbc.gridx = 0;
			gbc.weighty = 1.0;
			gbc.weightx = 1.0;
			gbc.gridwidth = 2;
			gbc.insets = new Insets(0, 5, 5, 5);
			gbc.fill = GridBagConstraints.BOTH;
			gbc.anchor = GridBagConstraints.PAGE_END;
			this.listModel1 = new DefaultListModel();
			CBlockList list = new CBlockList(this, listModel1);
			list.setListBackgound(this.getBackground());
			this.availableCourtPanel.add(list, gbc);
		}
		
		return this.availableCourtPanel;
	}
	
	/**
	 * 
	 * @return
	 */
	private JPanel getPosterPanel() {
		if (this.posterPanel == null) {
			this.posterPanel = new CImagePanel("icons/poster.png", club.getName(), 
					                           "Open hour: " + club.getOpenHour().toString("HH:mm a"),
					                           "Close hour: " + club.getCloseHour().toString("HH:mm a"));
		}
		
		return this.posterPanel;
	}

	/**
	 * 
	 */
	private void initializeMenu() {
		// main menu
		JMenu mainMenu = new JMenu("Menu");
		mainMenu.setIcon(getScaleImageIcon("down"));
		
		//
		JMenuItem menuItem = new JMenuItem("1. Show available courts");
		menuItem.setActionCommand(ActionCommands[SystemState.SHOW_AVAILABLE_COURTS_1.value]);
		menuItem.setIcon(getScaleImageIcon("court"));
		menuItem.addActionListener(this);
		mainMenu.add(menuItem);
		
		// 
		menuItem = new JMenuItem("2. Make booking");
		menuItem.setActionCommand(ActionCommands[SystemState.MAKE_BOOKING_2.value]);
		menuItem.setIcon(getScaleImageIcon("make_booking"));
		menuItem.addActionListener(this);
		mainMenu.add(menuItem);

		// 
		menuItem = new JMenuItem("3. Show member bookings");
		menuItem.setActionCommand(ActionCommands[SystemState.SHOW_MEMBER_BOOKINGS_3.value]);
		menuItem.setIcon(getScaleImageIcon("member_booking"));
		menuItem.addActionListener(this);
		mainMenu.add(menuItem);

		// 
		menuItem = new JMenuItem("4. Show court bookings");
		menuItem.setActionCommand(ActionCommands[SystemState.SHOW_COURT_BOOKINGS_4.value]);
		menuItem.setIcon(getScaleImageIcon("court_booking"));
		menuItem.addActionListener(this);
		mainMenu.add(menuItem);

		// 
		menuItem = new JMenuItem("5. Delete bookings");
		menuItem.setActionCommand(ActionCommands[SystemState.DELETE_BOOKING_5.value]);
		menuItem.setIcon(getScaleImageIcon("delete_booking"));
		menuItem.addActionListener(this);
		mainMenu.add(menuItem);
		
		//
		mainMenu.add(new JSeparator());
		
		// 
		menuItem = new JMenuItem("6. Exit");
		menuItem.setActionCommand(ActionCommands[SystemState.SYSTEM_EXIT.value]);
		menuItem.setIcon(getScaleImageIcon("exit"));
		menuItem.addActionListener(this);
		mainMenu.add(menuItem);
		
		// help menu
		JMenu helpMenu = new JMenu("Help");
		//helpMenu.setIcon(new CScaleImageIcon("icons/down.png", iconSize));
		
		//
		menuItem = new JMenuItem("About");
		menuItem.setActionCommand("About");
		menuItem.setIcon(getScaleImageIcon("info"));
		menuItem.addActionListener(this);
		helpMenu.add(menuItem);

		// Menu bar
		JMenuBar menuBar = new JMenuBar( );
		menuBar.add(mainMenu);
		menuBar.add(helpMenu);
		
		// set menu
		this.setJMenuBar(menuBar);
	}

	/**
	 * 
	 */
	private void mainLoop() {
		// main program loop, until system state is EXIT
		while (systemState != SystemState.SYSTEM_EXIT) {
			switch (systemState) {
			case SYSTEM_START:
				handleScreenStart();
				break;
				
			case SHOW_AVAILABLE_COURTS_1:
				handleScreenShowAvailableCourts();
				break;
				
			case MAKE_BOOKING_2:
				handleScreenMakeBooking();
				break;
				
			case SHOW_MEMBER_BOOKINGS_3:
				handleScreenShowMemberBookings();
				break;
				
			case SHOW_COURT_BOOKINGS_4:
				handleScreenShowCourtBookings();
				break;
				
			case DELETE_BOOKING_5:
				handleScreenDeleteBooking();
				break;
			
			default:
			}
		}
	}

	/**
	 * Handle screen of delete booking
	 */
	private void handleScreenDeleteBooking() {
		displayGUI();

		Member member = handleSubScreenBookedMemberInput();
		if (member != null) {
			displayMemberBookings(member);
			
			if (member.getBookingList().size() > 0) {
				DateAndTime dateTime = handleSubScreenDateAndTimeInput();
				if (dateTime != null) {
					Booking booking = member.searchBookingByDateAndTime(dateTime);
					if (booking != null) {
						club.deleteBooking(booking);
						utility.loglnMsg("    (i) Booking [" + booking + "] has been deleted.");
					} else {
						utility.loglnMsg("    (x) No booking found with such date and time.");
					}
				}
			}
		}
		
		utility.waitForEnter("(i) Press Enter to go back.");
		systemState = SystemState.SYSTEM_START;
	}

	/**
	 * Handle sub screen to input index
	 * @param size limit of the input index
	 * @return int value of selected index
	 */
	private int handleSubScreenIndexInput(int size) {
		int index = -1;
		
		while (index < 0) {
			index = utility.logNextInt("(?) Enter your selection (number inside the brackets) of the booking listed above (ZERO to cancel): ");
			
			if (index == 0) {
				return -1;
			}
			
			if (index < 0) {
				utility.loglnMsg("    (x) Selection must be positive number.");
			} else if (index > size) {
				utility.loglnMsg("    (x) Selection is out of range");
				index = -1;
			}
		}
		
		return index;
	}

	/**
	 * Handle screen of make the booking
	 */
	private void handleScreenMakeBooking() {
		displayGUI();
		
		// sport input
		Sport sport = handleSubScreenSportInput();
		if (sport != null) {
			DateAndTime startTime = handleSubScreenDateAndTimeInput();
			if (startTime != null) {
				int minutes = handleSubScreenDurationInput(startTime, sport.getMaximumBookingMinutes());
				if (minutes > 0) {
					DateAndTime endTime = startTime.plusMinutes(minutes);
					
					Court court = club.searchAvailableCourtForBookingTime(sport, startTime, endTime);
					if (court == null) {
						utility.loglnMsg("    (x) There's no court available for sport '" + sport.getSportName() + 
								         "' at [" + startTime.toString("HH:mm") + " - " + endTime.toString("HH:mm") + "]");
					} else {	// ask for member number
						utility.loglnMsg("    (i) Court available for this booking time: " + court.getNumber());
						utility.loglnMsg("(i) Members who play " + sport.getSportName() + ":");
						Member member = handleSubScreenMemberInput(sport);
							
						// construct a booking
						if (member != null) {
							try {
								Booking booking = new Booking(member, court, startTime, minutes);
								club.addBooking(booking);
								utility.loglnMsg("    (i) New booking has been made:");
								utility.loglnMsg("        " + booking.toString());
							} catch (InvalidBookingException ibe) {
								utility.loglnMsg("    (x) " + ibe.getMessage());
							}
						}
					}
				}
			}
		}
		
		utility.waitForEnter("(i) Press Enter to go back.");
		systemState = SystemState.SYSTEM_START;
	}
	
	/**
	 * Handle sub screen of duration input
	 * @param startTime Start time used to calculate and validate end time
	 * @param maximumMinutes Limit number of minutes input
	 * @return int value of minutes input
	 */
	private int handleSubScreenDurationInput(DateAndTime startTime, int maximumMinutes) {
		DateAndTime endTime = null;
		
		int minutes = -1;
		while (minutes < 0) {
			minutes = utility.logNextInt("(?) Enter duration in minutes (Max: " + maximumMinutes + ", ZERO to go cancel): ");
			
			if (minutes == 0) {
				return -1;
			}
			
			if (minutes < 0) {
				utility.loglnMsg("    (x) Duration must be positive number.");
			} else if (minutes > maximumMinutes) {
				utility.loglnMsg("    (x) Duration exceeds maximum minutes allowed for sport: " + maximumMinutes + " mins");
				minutes = -1;
			} else {	// okay
				endTime = startTime.plusMinutes(minutes);
				if (!club.isBetweenWorkingHours(endTime)) {
					utility.loglnMsg("    (x) End time will not be in working hours range: " + endTime.toString("HH:mm"));
					minutes = -1;
				}
			}
		}
		
		return minutes;
	}

	/**
	 * 
	 * @return
	 */
	private DateAndTime handleSubScreenDateAndTimeInput() {
		DateAndTime startTime = null;
		while (startTime == null) {
			String dateTimeString = utility.logNextLine("(?) Enter start date time (in format 30/12/2016 14:45, EMPTY to cancel): ");
			
			if (dateTimeString.isEmpty()) {
				return null;
			}
			
			try {
				String pattern = "d/M/yyyy H:m";
				startTime = DateAndTime.fromString(dateTimeString, pattern);
				utility.loglnMsg("    (i) Date and time enterred: " + startTime.toString(pattern));
				// check time validity
				if (DateAndTime.isThePast(startTime)) {
					utility.loglnMsg("    (x) Date and time is in the past.");
					startTime = null;
				} else if (!club.isBetweenWorkingHours(startTime)) {
					utility.loglnMsg("    (x) Time is not in working hours range");
					startTime = null;
				} else if (!DateAndTime.isWithinNumberOfDays(startTime, 7)) {
					utility.loglnMsg("    (x) Time exceeds 7 days in advance.");
					startTime = null;
				}
			} catch (DateTimeParseException e) {
				utility.loglnMsg("    (x) " + e.getMessage()); 
				startTime = null;
			}
		}
		return startTime;
	}

	/**
	 * 
	 * @return
	 */
	private Sport handleSubScreenSportInput() {
		displaySportList();
		
		Sport sport = null;
		while (sport == null) {
			String sportName = utility.logNextLine("(?) Enter sport name (EMPTY to cancel): ");
			
			if (sportName.isEmpty()) {
				return null;
			}
			
			try {
				if ((sport = club.searchSportByName(sportName, true)) == null) {
					utility.loglnMsg("    (x) No sport with name '" + sportName + "'");
					sportName = "";
				}
			} catch (EmptyStringException ese) {}	// won't happen
		}
		
		return sport;
	}

	/**
	 * 
	 */
	private void displaySportList() {
		utility.loglnMsg(String.format("%-15s %-15s %-15s %-15s %-20s %-15s", "Sport Name", "Usage Fee", "Insurance Fee", "Affliation Fee", "Max Booking (mins)", "Court List"));
		utility.loglnMsg(String.format("%-15s %-15s %-15s %-15s %-20s %-15s", "----------", "---------", "-------------", "--------------", "------------------", "----------"));
		for (Sport sport: club.getSportList()) {
			utility.loglnMsg(String.format("%-15s %-15s %-15s %-15s %-20s %-15s", 
					                       sport.getSportName(),
					                       sport.getUsageFee(),
					                       sport.getInsuranceFee(),
					                       sport.getAffiliationFee(),
					                       sport.getMaximumBookingMinutes(),
					                       sport.getCourtListAsString()));
		}
	}

	/**
	 * 
	 */
	private void handleScreenShowCourtBookings() {
		displayGUI();

		Court court = handleSubScreenCourtInput();

		if (court != null) {
			displayCourtBookings(court);
		}
		
		utility.waitForEnter("(i) Press Enter to go back.");
		systemState = SystemState.SYSTEM_START;
	}

	/**
	 * 
	 * @return
	 */
	private Court handleSubScreenCourtInput() {
		displayCourtList();

		// ask user for number of court
		Court court = null;
		while (court == null) {
			int number = utility.logNextInt("(?) Enter court number (ZERO to cancel): ");
			
			if (number == 0) {
				return null;
			}
			
			if (number < 0) {
				utility.loglnMsg("    (x) Court number must be positive number.");
			} else if ((court = club.searchCourtByNumber(number)) == null) {
				utility.loglnMsg("    (x) There's no court with such number.");
			}
		}
		
		return court;
	}

	/**
	 * Display bookings of specific court
	 * @param court Court
	 */
	private void displayCourtBookings(Court court) {
		utility.loglnMsg("(i) Court: " + court.getNumber());
        
        ArrayList<Booking> bookingList = court.getBookingList();
		utility.loglnMsg("(i) " + bookingList.size() + " booking(s)");
		// TODO: should we display the booking in the past?
		for (Booking booking: bookingList) {
			utility.loglnMsg("    " + booking.toString());
		}
	}

	/**
	 * 
	 */
	private void displayCourtList() {
		utility.loglnMsg(String.format("%-15s %-15s", "Court Number", "Booking(s)"));
		//utility.loglnMsg(String.format("%-15s %-15s", "------------", "----------"));
		utility.loglnMsg("----------------------------------------------------------------");
		for (Court court: club.getBookedCourts()) {
			utility.loglnMsg(String.format("%-15s %-15s", 
					                       court.getNumber(),
					                       "Total: " + court.getBookingList().size()));
			for (Booking booking: court.getBookingList()) {
				utility.loglnMsg(String.format("%-15s %-15s", 
	                       "",
	                       booking.toString()));
			}
			//utility.loglnMsg(String.format("%-15s %-15s", "------------", "----------"));
			utility.loglnMsg("----------------------------------------------------------------");
		}
	}

	/**
	 * 
	 */
	private void handleScreenShowAvailableCourts() {
		displayGUI();
		
		DateAndTime startTime = handleSubScreenDateAndTimeInput();
		if (startTime != null) {
			DateAndTime endTime = handleSubScreenTimeInput("end");
			if (endTime != null) {
				try {
					// re-struct end time
					endTime = DateAndTime.fromString(startTime.toString("d/M/yyyy") + " " + endTime.toString("H:m"), "d/M/yyyy H:m");
					if (startTime.compareTo(endTime) < 0) {
						ArrayList<Court> courtList = club.getAvailableCourts(startTime, endTime);
						utility.loglnMsg("(i) " + courtList.size() + " court(s) available from " + 
						                  startTime.toString("H:m") + " to " + endTime.toString("H:m d/M/yyyy") + ":");
						displayAvailableCourts(courtList, startTime);
					} else {
						utility.loglnMsg("    (x) Start time is NOT before end time.");
					}
					// check validity
				} catch (DateTimeParseException dtpe) {
					utility.loglnMsg("    (x) " + dtpe.getMessage());
				}
			}
		}
		
		utility.waitForEnter("(i) Press Enter to go back.");
		systemState = SystemState.SYSTEM_START;
	}

	/**
	 * 
	 * @param courtList
	 * @param startTime
	 */
	private void displayAvailableCourts(ArrayList<Court> courtList, DateAndTime startTime) {
		String pattern = "d/M/yyyy";
		utility.loglnMsg(String.format("%-8s %-20s", "Number", "Booking(s) on [" + startTime.toString(pattern) + "]"));
		utility.loglnMsg(String.format("%-8s %-20s", "------", "-------------------------"));
		for (Court court: courtList) {
			String bookingStrings = "";
			for (Booking booking: court.getBookingList()) {
				if (booking.getStartTime().toString(pattern).equals(startTime.toString(pattern))) {
					bookingStrings += "[" + booking.getStartTime().toString("H:m") + " - " + 
				                      booking.getEndTime().toString("H:m") + ", " + 
							          booking.getSport().getSportName() + "] ";
				}
			}
			
			utility.loglnMsg(String.format("%-8d %-20s", 
										   court.getNumber(),
					                       bookingStrings));
		}
		
	}

	/**
	 * 
	 * @return
	 */
	private DateAndTime handleSubScreenDateInput() {
		DateAndTime startTime = null;
		while (startTime == null) {
			String dateTimeString = utility.logNextLine("(?) Enter date (in format 30/12/2016, EMPTY to cancel): ");
			
			if (dateTimeString.isEmpty()) {
				return null;
			}
			
			try {
				String pattern = "d/M/yyyy H:m";
				startTime = DateAndTime.fromString(dateTimeString + " 0:0", pattern);
				utility.loglnMsg("    (i) Date enterred: " + startTime.toString(pattern));
				// check time validity
				if (DateAndTime.isThePast(startTime)) {
					utility.loglnMsg("    (x) Date is in the past.");
					startTime = null;
				}
			} catch (DateTimeParseException e) {
				utility.loglnMsg("    (x) " + e.getMessage()); 
				startTime = null;
			}
		}
		return startTime;
	}
	
	/**
	 * 
	 * @return
	 */
	private DateAndTime handleSubScreenTimeInput(String prefix) {
		DateAndTime startTime = null;
		while (startTime == null) {
			String dateTimeString = utility.logNextLine("(?) Enter " + prefix + " time (in format 14:45, EMPTY to cancel): ");
			
			if (dateTimeString.isEmpty()) {
				return null;
			}
			
			try {
				String pattern = "d/M/yyyy H:m";
				startTime = DateAndTime.fromString(DateAndTime.now().toString("d/M/yyyy") + " " + dateTimeString, pattern);
				utility.loglnMsg("    (i) Time enterred: " + startTime.toString("H:m"));
				// check time validity
				if (!club.isBetweenWorkingHours(startTime)) {
					utility.loglnMsg("    (x) Time is not in working hours range.");
					startTime = null;
				}
			} catch (DateTimeParseException e) {
				utility.loglnMsg("    (x) " + e.getMessage()); 
				startTime = null;
			}
		}
		return startTime;
	}
	

	/**
	 * 
	 */
	private void handleScreenShowMemberBookings() {
		displayGUI();

		Member member = handleSubScreenBookedMemberInput();

		if (member != null) {
			displayMemberBookings(member);
		}
		
		utility.waitForEnter("(i) Press Enter to go back.");
		systemState = SystemState.SYSTEM_START;
	}
	
	/**
	 * 
	 * @return
	 */
	private Member handleSubScreenBookedMemberInput() {
		displayBookedMemberList();

		// ask user for number of member
		Member member = null;
		while (member == null) {
			int number = utility.logNextInt("(?) Enter member number (ZERO to go back): ");
			
			if (number == 0) {
				return null;
			} else if (number < 0) {
				utility.loglnMsg("    (x) Member number must be positive number.");
			} else if ((member = club.searchMemberByID(number)) == null) {
				utility.loglnMsg("    (x) There's no member with such number.");
			}
		}

		return member;
	}

	/**
	 * 
	 */
	private void displayBookedMemberList() {
		utility.loglnMsg(String.format("%-8s %-15s %-15s", "Number", "Name", "Booking(s)"));
		String separator = "----------------------------------------------------------------------------";
		utility.loglnMsg(separator);
		for (Member member: club.getBookedMemberList()) {
			utility.loglnMsg(String.format("%-8s %-15s %-15s", 
					                       member.getIdentifier(),
					                       member.getName(),
					                       "Total: " + member.getBookingList().size()));
			for (Booking booking: member.getBookingList()) {
				utility.loglnMsg(String.format("%-8s %-15s %-15s", 
	                       "",
	                       "",
	                       booking.toString()));
				
			}
			utility.loglnMsg(separator);
		}
	}

	/**
	 * return null if user cancel
	 * @return
	 */
	private Member handleSubScreenMemberInput(Sport sport) {
		displayMemberList(sport);

		// ask user for number of member
		Member member = null;
		while (member == null) {
			int number = utility.logNextInt("(?) Enter member number (ZERO to go back): ");
			
			if (number == 0) {
				return null;
			} else if (number < 0) {
				utility.loglnMsg("    (x) Member number must be positive number.");
			} else if ((member = club.searchMemberByID(number)) == null) {
				utility.loglnMsg("    (x) There's no member with such number.");
			}
		}

		return member;
	}

	/**
	 * 
	 * @param member
	 */
	private void displayMemberBookings(Member member) {
		utility.loglnMsg("(i) Member: " + member.getName());
		utility.loglnMsg("    Number: " + member.getIdentifier());
		utility.loglnMsg("    Financial: " + (member.isFinancialMember() ? "YES" : "NO"));
        utility.loglnMsg("    Sports: " + member.getSportListAsString());
        
        ArrayList<Booking> bookingList = member.getBookingList();
		utility.loglnMsg("(i) " + bookingList.size() + " booking(s)");
		// TODO: should we display the booking in the past?
		int index = 0;
		for (Booking booking: bookingList) {
			index++;
			utility.loglnMsg("    " + booking.toString());
		}
	}

	/**
	 * 
	 */
	private void displayMemberList(Sport sport) {
		int count;
		utility.loglnMsg(String.format("%-8s %-15s %-12s %-12s", "Number", "Name", "Booking(s)", "Sports"));
		utility.loglnMsg(String.format("--------------------------------------------"));
		for (Member member: club.getMemberList(sport)) {
			utility.loglnMsg(String.format("%-8s %-15s %-12s %-12s", 
					                       member.getIdentifier(),
					                       member.getName(),
					                       (count = member.getBookingList().size()) > 0 ? count : "",
					                       member.getSportListAsString()));
		}
	}
	
	/**
	 * 
	 */
	private void handleScreenStart() {
		while (systemState == SystemState.SYSTEM_START) {
			// display the menu
			displayGUI();
			
			// read number selection from user input
			int number = utility.logNextInt("(?) Enter your selection: ");
			
			switch (number) {
			case 1:
				systemState = SystemState.SHOW_AVAILABLE_COURTS_1;
				break;
				
			case 2:
				systemState = SystemState.MAKE_BOOKING_2;
				break;
				
			case 3:
				systemState = SystemState.SHOW_MEMBER_BOOKINGS_3;
				break;
			
			case 4:
				systemState = SystemState.SHOW_COURT_BOOKINGS_4;
				break;
				
			case 5:
				systemState = SystemState.DELETE_BOOKING_5;
				break;
								
			case 6:
				systemState = SystemState.SYSTEM_EXIT;
				break;
				
			default:
			}
		}
	}
	
	/**
	 * 
	 */
	private void displayGUI() {
		Utility.clearConsoleScreen();
		
		switch (systemState) {
		case SYSTEM_START:
			displayGUIStart();
			break;
			
		case SHOW_AVAILABLE_COURTS_1:
			displayGUIShowAvailableCourts();
			break;
			
		case MAKE_BOOKING_2:
			displayGUIMakeBooking();
			break;
		
		case SHOW_MEMBER_BOOKINGS_3:
			displayGUIShowMemberBookings();
			break;
			
		case SHOW_COURT_BOOKINGS_4:
			displayGUIShowCourtBooking();
			break;
			
		case DELETE_BOOKING_5:
			displayGUIDeleteBooking();
			break;
			
		default:
		}
	}

	/**
	 * 
	 */
	private void displayGUIDeleteBooking() {
		utility.loglnMsg("***** 5. Delete booking *****");
		
	}

	/**
	 * 
	 */
	private void displayGUIShowCourtBooking() {
		utility.loglnMsg("***** 4. Show court bookings *****");
	}

	/**
	 * 
	 */
	private void displayGUIMakeBooking() {
		utility.loglnMsg("***** 2. Make booking for member *****");
	}

	/**
	 * 
	 */
	private void displayGUIShowAvailableCourts() {
		utility.loglnMsg("***** 1. Show available courts *****");
	}

	/**
	 * 
	 */
	private void displayGUIShowMemberBookings() {
		utility.loglnMsg("***** 3. Show member bookings *****");
	}

	/**
	 * 
	 */
	private void displayGUIStart() {
		utility.loglnMsg("***** " + club.getName() + " *****");
		utility.loglnMsg("1. Show available courts");
		utility.loglnMsg("2. Make booking for member");
		utility.loglnMsg("3. Show member bookings");
		utility.loglnMsg("4. Show court bookings");
		utility.loglnMsg("5. Delete booking");
		utility.loglnMsg("6. Exit");
	}

	/**
	 * Initialize step
	 */
	private void initializeSystem(String[] args) {
		runMode = RunMode.GUI;
		if (args != null && args.length > 0) {
			String parameter = args[0].trim();
			if (parameter.equalsIgnoreCase("/c")) {
				runMode = RunMode.CONSOLE;
			}
		}
		
		systemState = SystemState.SYSTEM_START;
		
		utility = new Utility();
		hasDataToWrite = true;
		
		utility.clearConsoleScreen();
		
		// construct Club
		try {
			club = new Club("Swinburne Sport Club");
			club.setOpenHour(8, 0);
			club.setCloseHour(23, 0);
		} catch (Exception e) {
			utility.loglnMsg("(x) Unable to construct Club" + e.getMessage());
			utility.waitForEnter("(i) Press Enter to exit.");
			systemState = SystemState.SYSTEM_EXIT;
			return;
		}
		
		JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
		
		// Read sports
		boolean keepReading = true;
		while (keepReading) {
			try {
				utility.loglnMsg("(i) Reading sports data from file " + club.getSportsFileName() + "...");
				club.readSportsFromFiles();
				keepReading = false;
			} catch (FileNotFoundException fnfe) {
				utility.loglnMsg("    (x) Error file not found: " + fnfe.getMessage());
				if (runMode == RunMode.CONSOLE) {
					String fileName = utility.logNextLine("(?) Enter sports file name (EMPTY to exit): ");
					if (fileName.isEmpty()) {
						hasDataToWrite = false;
						systemState = SystemState.SYSTEM_EXIT;
						return;
					} else {
						club.setSportsFileName(fileName);
					}
				} else {	// GUI
					jfc.setDialogTitle("Select sports file");
					int ret = jfc.showOpenDialog(null);
					if (ret == JFileChooser.APPROVE_OPTION) {
						File file = jfc.getSelectedFile();
						club.setSportsFileName(file.getAbsolutePath());
					} else {
						System.exit(0);
					}
				}
			} catch (Exception e) {
				utility.loglnMsg(e.getMessage());
				keepReading = false;
			}
		}
		
		// Read members
		keepReading = true;
		while (keepReading) {
			try {
				utility.loglnMsg("(i) Reading members data from file " + club.getMembersFileName() + "...");
				club.readMembersFromFiles();
				keepReading = false;
			} catch (FileNotFoundException fnfe) {
				utility.loglnMsg("    (x) Error file not found: " + fnfe.getMessage());
				if (runMode == RunMode.CONSOLE) {
					String fileName = utility.logNextLine("(?) Enter members file name (EMPTY to exit): ");
					if (fileName.isEmpty()) {
						hasDataToWrite = false;
						systemState = SystemState.SYSTEM_EXIT;
						return;
					} else {
						club.setMembersFileName(fileName);
					}
				} else {	// GUI
					jfc.setDialogTitle("Select members file");
					int ret = jfc.showOpenDialog(null);
					if (ret == JFileChooser.APPROVE_OPTION) {
						File file = jfc.getSelectedFile();
						club.setMembersFileName(file.getAbsolutePath());
					} else {
						System.exit(0);
					}
				}
			} catch (Exception e) {
				utility.loglnMsg(e.getMessage());
				keepReading = false;
			}
		}
		
		// Read bookings
		keepReading = true;
		while (keepReading) {
			try {
				utility.loglnMsg("(i) Reading bookings data from file " + club.getBookingsFileName() + "...");
				club.readBookingsFromFiles();
				keepReading = false;
			} catch (FileNotFoundException fnfe) {
				utility.loglnMsg("    (x) Error file not found: " + fnfe.getMessage());
				if (runMode == RunMode.CONSOLE) {
					String fileName = utility.logNextLine("(?) Enter bookings file name (EMPTY to continue without booking data): ");
					if (fileName.isEmpty()) {
						keepReading = false;
					} else {
						club.setBookingsFileName(fileName);
					}
				} else {
					keepReading = false;
				}
			} catch (Exception e) {
				utility.loglnMsg(e.getMessage());
				keepReading = false;
			}
		}
		
		if (runMode == RunMode.CONSOLE) {
			utility.waitForEnter("(i) Press Enter to continue...");
		}
	}
	
	
	/**
	 * Finalize system
	 */
	private void finalizeSystem() {
		// prevent from creating empty bookings file in case sports or/and members files are missing
		if (hasDataToWrite) {
			try {
				utility.loglnMsg("(i) Writing data to files...");
				club.writeDataToFiles();
			} catch (Exception e) {
				utility.loglnMsg("(x) Error writing to files: " + e.getMessage());
			}
		}
		utility.destructor();
		System.exit(0);
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public BufferedImage getBufferedImage(String file) {
		if (!this.bufferedImageMap.containsKey(file)) {
			try {
				BufferedImage image = ImageIO.read(new File("icons/" + file + ".png")); 
				bufferedImageMap.put(file, image);
			} catch (IOException ioe) {
				return null;
			}
		}
		
		return this.bufferedImageMap.get(file);
	}
	
	public Icon getScaleImageIcon(String file) {
		if (!this.iconMap.containsKey(file)) {
			CScaleImageIcon icon = new CScaleImageIcon("icons/" + file + ".png", 14); 
			this.iconMap.put(file, icon);
		}
		
		return this.iconMap.get(file);
	}
}
