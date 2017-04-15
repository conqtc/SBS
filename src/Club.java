
/**
 * 
 * 
 * @author Alex Truong (101265224)
 * @version 0.1
 */

import java.io.*;
import java.util.*;
import CException.*;

public class Club {
	//
	private String name;
	
	//
	private DateAndTime openHour;
	private DateAndTime closeHour;
		
	//
	private ArrayList<Member> memberList;
	
	// 
	private ArrayList<Sport> sportList;
	
	//
	private String fileSports;
	private String fileMembers;
	private String fileBookings;
	
	/**
	 * Club constructor
	 * @param clubName Name of the club
	 * @throws NullPointerException If club name is null
	 * @throws EmptyStringException If club name is empty
	 */
	public Club(String clubName) throws NullPointerException, EmptyStringException  {
		//
		if (clubName == null) {
			throw new NullPointerException("Club name must not be null");
		}
		
		//
		clubName = clubName.trim();
		if (clubName.isEmpty()) {
			throw new EmptyStringException("New Club: name is empty.");
		}
		
		//
		this.name = clubName;
		//
		memberList = new ArrayList<>();
		sportList = new ArrayList<>();
		//
		this.fileSports = Utility.FILE_SPORTS;
		this.fileMembers = Utility.FILE_MEMBERS;
		this.fileBookings = Utility.FILE_BOOKINGS;
	}
	
	/**
	 * Set open hour
	 * @param hour value from 0 to 23
	 * @param minute value from 0 to 59
	 * @throws Exception If something goes wrong
	 */
	public void setOpenHour(int hour, int minute) throws Exception {
		if (hour < 0 || hour > 23)
			throw new InvalidNumberException("Hour and minute values are invalid!");
		
		// extract today's date then add hour:minute
		this.openHour = DateAndTime.fromString(DateAndTime.now().toString("d/M/yyyy") + 
				                               " " + hour + ":" + minute, "d/M/yyyy H:m");
	}

	/**
	 * Set close hour
	 * @param hour value from 0 to 23
	 * @param minute value from 0 to 59
	 * @throws Exception If something goes wrong
	 */
	public void setCloseHour(int hour, int minute) throws Exception {
		if (hour < 0 || hour > 23)
			throw new InvalidNumberException("Hour and minute values are invalid!");
		
		// extract today's date then add hour:minute
		this.closeHour = DateAndTime.fromString(DateAndTime.now().toString("d/M/yyyy") + 
				                                " " + hour + ":" + minute, "d/M/yyyy H:m");
		
		// close hour is before open hour???
		if (this.closeHour.compareTo(this.openHour) <= 0) {
			throw new InvalidNumberException("Invalid: close hour is before open hour");
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public DateAndTime getOpenHour() {
		return this.openHour;
	}
	
	/**
	 * 
	 * @return
	 */
	public DateAndTime getCloseHour() {
		return this.closeHour;
	}
	
	/**
	 * Check if specific moment is between working hours
	 * @param moment DateAndTime to be checked
	 * @return true if is within working hour, false otherwise
	 */
	public boolean isBetweenWorkingHours(DateAndTime moment) {
		String pattern = "HH:mm";
		String timeString = moment.toString(pattern);
		String openHour = this.openHour.toString(pattern);
		String closeHour = this.closeHour.toString(pattern);
		
    	return ((timeString.compareTo(openHour) >= 0) &&
        		(timeString.compareTo(closeHour) <= 0));
	}	
	/**
	 * Get club name
	 * @return String represents club name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get all sports
	 * @return ArrayList of Sport
	 */
	public ArrayList<Sport> getSportList() {
		return this.sportList;
	}
	
	/**
	 * Get all members who play specific sport
	 * @param sport Specific sport
	 * @return ArrayList of Member, return all members if sport is null
	 */
	public ArrayList<Member> getMemberList(Sport sport) {
		if (sport == null) {
			return this.memberList;
		} else {
			ArrayList<Member> result = new ArrayList<Member>();
			for (Member member: this.memberList) {
				if (member.playSport(sport)) {
					result.add(member);
				}
			}
			
			return result;
		}
	}
		
	/**
	 * Add a new booking
	 * @param booking Booking to be added
	 * @throws NullPointerException If the booking is null
	 * @throws InvalidBookingException If the booking is invalid
	 */
	public void addBooking(Booking booking) throws NullPointerException, InvalidBookingException {
		if (booking == null) {
			throw new NullPointerException("Club.addBooking: booking is null.");
		}
		
    	// check working hour
    	if (!this.isBetweenWorkingHours(booking.getStartTime()) ||
    		!this.isBetweenWorkingHours(booking.getEndTime())) {
    		throw new InvalidBookingException("Booking for member [" + booking.getMember().getName() + ", " + 
    	                                      booking.getMember().getIdentifier() + "] on court [" + booking.getCourt().getNumber() + 
    	                                      "]: booking time [" + booking.getStartTime().toString("HH:mm") + " - " + booking.getEndTime().toString("HH:mm") + "] is out of working hour.");
    	}
		
		// check if overlapped with existing bookings
		for (Member member: this.memberList) {
			for (Booking existingBooking: member.getBookingList()) {
				if (existingBooking.isOverlappedWith(booking)) {
					throw new InvalidBookingException("Club.addBooking: booking [" + booking.toString() + "] is overlapped with other existing booking [" + existingBooking.toString() + "].");
				}
			}
		}
		
		// register this booking with member and court
		booking.registerWithMemberAndCourt();
	}
	
	/**
	 * Add new member
	 * @param member Member to be added
	 * @throws NullPointerException If member is null
	 * @throws DuplicateException If same member id already exists in the club
	 */
	public void addMember(Member member) throws NullPointerException, DuplicateException {
		if (member == null) {
			throw new NullPointerException("Club.addMember: member is null.");
		}
		
		// search existing members for same id
		if (searchMemberByID(member.getIdentifier()) != null) {
			throw new DuplicateException("Club.addMember: member with same number already exists.");
		}
		
		memberList.add(member);
	}
	
	/**
	 * Add new sport
	 * @param sport Sport to be added
	 * @throws NullPointerException If sport is null
	 * @throws EmptyStringException if sport name is empty
	 * @throws DuplicateException if another sport with same id already exists
	 */
	public void addSport(Sport sport) throws NullPointerException, EmptyStringException, DuplicateException {
		if (sport == null) {
			throw new NullPointerException("Club.addSport: sport is null.");
		}
		
		// search existing sports for same name
		// TODO: currently ignore case search
		if (searchSportByName(sport.getSportName(), true) != null) {
			throw new DuplicateException("Club.addSport: sport with same name already exists.");
		}
		
		sportList.add(sport);
	}
	
	/**
	 * Search member by member id
	 * @param identifier Member identification
	 * @return Member object if found, null if not
	 */
	public Member searchMemberByID(int identifier) {
		for (Member member: memberList) {
			if (member.getIdentifier() == identifier) {
				return member;
			}
		}
		
		return null;
	}
	
	/**
	 * Search sport by name
	 * @param name Name of the sport to search for
	 * @param ignoreCase true if search ignoring the case, false otherwise
	 * @throws NullPointerException if name is null
	 * @throws EmptyStringException if name is empty
	 * @return Sport object if found, null if not
	 */
	public Sport searchSportByName(String name, boolean ignoreCase) throws NullPointerException, EmptyStringException {
		if (name == null) {
			throw new NullPointerException("Club.searchSportByName: name is null.");
		}
		
		if (name.isEmpty()) {
			throw new EmptyStringException("Club.searchSportByName: name is empty.");
		}
		
		for (Sport sport: sportList) {
			if (ignoreCase) {	// ignore case
				if (sport.getSportName().equalsIgnoreCase(name)) {
					return sport;
				}
			} else {	// case sensitive
				if (sport.getSportName().equals(name)) {
					return sport;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Search court by number
	 * @param number number of the court to search for
	 * @return Court object if found, null if not
	 */
	public Court searchCourtByNumber(int number) {
		for (Sport sport: sportList) {
			for (Court court: sport.getCourtList()) {
				if (court.getNumber() == number) {
					return court;
				}
			}
		}
		
		return null;
	}

	/**
	 * Get all available courts at the moment
	 * @return ArrayList of Court which is available at the moment
	 */
	public ArrayList<Court> getAvailableCourts() {
		ArrayList<Court> courtList = new ArrayList<>();
		
		for (Sport sport: this.sportList) {
			for (Court court: sport.getCourtList()) {
				if (court.isAvailableNow()) {
					courtList.add(court);
				}
			}
		}
		
		return courtList;
	}
	
	/**
	 * Get all courts
	 * @return ArrayList of Court
	 */
	public ArrayList<Court> getAllCourts() {
		ArrayList<Court> courtList = new ArrayList<>();
		
		for (Sport sport: this.sportList) {
			for (Court court: sport.getCourtList()) {
				courtList.add(court);
			}
		}
		
		return courtList;
	}

	/**
	 * Search for all available courts for specific booking time
	 * @param sport Sport that need to be booked
	 * @param startTime Start time
	 * @param endTime End time
	 * @return Court object appropriate 
	 */
	public Court searchAvailableCourtForBookingTime(Sport sport, DateAndTime startTime, DateAndTime endTime) {
		for (Court court: sport.getCourtList()) {
			if (court.isAvailableForBookingTime(startTime, endTime)) {
				return court;
			}
		}
		
		return null;
	}

	/**
	 * Delete booking
	 * @param booking Booking object to be deleted
	 */
	public void deleteBooking(Booking booking) {
		if (booking == null) {
			throw new NullPointerException("Club.deleteBooking booking is null.");
		}
		
		booking.deregisterFromMemberAndCourt();
	}

	/**
	 * Set sports file name
	 * @param fileName Sports file name
	 */
	public void setSportsFileName(String fileName) {
		this.fileSports = fileName;
	}
	
	/**
	 * Get sports file name
	 * @return String sports file name
	 */
	public String getSportsFileName() {
		return this.fileSports;
	}
	
	/**
	 * Set Members file name
	 * @param fileName Members file name
	 */
	public void setMembersFileName(String fileName) {
		this.fileMembers = fileName;
	}
	
	/**
	 * Get Members file name
	 * @return String Members file name
	 */
	public String getMembersFileName() {
		return this.fileMembers;
	}

	/**
	 * Set Bookings file name
	 * @param fileName Bookings file name
	 */
	public void setBookingsFileName(String fileName) {
		this.fileBookings = fileName;
	}
	
	/**
	 * Get Bookings file name
	 * @return String Bookings file name
	 */
	public String getBookingsFileName() {
		return this.fileBookings;
	}
	
	/**
	 * Read sports from file
	 * @throws Exception when something went wrong, FileNotFound exception if file not found
	 */
	public void readSportsFromFiles() throws Exception {
		ArrayList<String> data;
		int exceptionCount = 0;
		int lineNumber = 0;
		StringBuilder exceptionMessages = new StringBuilder();

		// read sports
		data = Utility.readDataFromFile(this.fileSports);
		for (String line: data) {
			lineNumber++;
			// ignore empty line or comment
			if (Utility.stringIsNullOrEmpty(line) || Utility.stringIsComment(line))
				continue;
			// parse sport data
			try {
				Sport sport = Sport.constructFromString(this, line);
				if (sport != null)
					this.sportList.add(sport);
			} catch (Exception e) {
				exceptionCount++;
				exceptionMessages.append("    " + this.fileSports + "(" + lineNumber + "): ");
				exceptionMessages.append(e.getMessage() + "\n");
			}
		}
		
		// there's exception
		if (exceptionCount > 0) {
			throw new Exception(exceptionMessages.toString());
		}
	}

	/**
	 * Read members from file
	 * @throws Exception when something went wrong, FileNotFound exception if file not found
	 */
	public void readMembersFromFiles() throws Exception {
		ArrayList<String> data;
		int exceptionCount = 0;
		int lineNumber = 0;
		StringBuilder exceptionMessages = new StringBuilder();

		// read members
		data = Utility.readDataFromFile(this.fileMembers);
		lineNumber = 0;
		for (String line: data) {
			lineNumber++;
			// ignore empty line or comment
			if (Utility.stringIsNullOrEmpty(line) || Utility.stringIsComment(line))
				continue;
			// parse member data
			try {
				Member member = Member.constructFromString(this, line);
				if (member != null) 
					this.memberList.add(member);
			} catch (Exception e) {
				exceptionCount++;
				exceptionMessages.append("    " + this.fileMembers + "(" + lineNumber + "): ");
				exceptionMessages.append(e.getMessage() + "\n");
			}
		}
		
		// there's exception
		if (exceptionCount > 0) {
			throw new Exception(exceptionMessages.toString());
		}
	}

	/**
	 * Read bookings from file
	 * @throws Exception when something went wrong, FileNotFound exception if file not found
	 */
	public void readBookingsFromFiles() throws Exception {
		ArrayList<String> data;
		int exceptionCount = 0;
		int lineNumber = 0;
		StringBuilder exceptionMessages = new StringBuilder();
		
		// read bookings
		data = Utility.readDataFromFile(this.fileBookings);
		
		lineNumber = 0;
		for (String line: data) {
			lineNumber++;
			// ignore empty line or comment
			if (Utility.stringIsNullOrEmpty(line) || Utility.stringIsComment(line))
				continue;
			// parse member data
			try {
				Booking booking = Booking.constructFromString(this, line);
				if (booking != null) 
					this.addBooking(booking);
			} catch (Exception e) {
				exceptionCount++;
				exceptionMessages.append("    " + this.fileBookings + "(" + lineNumber + "): ");
				exceptionMessages.append(e.getMessage() + "\n");
			}
		}
		
		// there's exception
		if (exceptionCount > 0) {
			throw new Exception(exceptionMessages.toString());
		}
	}
	
	/**
	 * Write data to the file (only booking data is necessary)
	 * @throws Exception When writing goes wrong
	 */
	public void writeDataToFiles() throws Exception {
		// write sports
		//ArrayList<String> sportsData = new ArrayList<>();
		//for (Sport sport: this.sportList) {
		//	sportsData.add(sport.saveData());
		//}
		//Utility.writeDataToFile(Utility.FILE_SPORTS, sportsData);
		
		// write members
		//ArrayList<String> membersData = new ArrayList<>();
		//for (Member member: this.memberList) {
		//	membersData.add(member.saveData());
		//}
		//Utility.writeDataToFile(Utility.FILE_MEMBERS, membersData);
		
		// write bookings
		ArrayList<String> bookingsData = new ArrayList<>();
		for (Member member: this.memberList) {
			for (Booking booking: member.getBookingList()) {
				bookingsData.add(booking.saveData());
			}
		}
		Utility.writeDataToFile(this.fileBookings, bookingsData);
	}

	/**
	 * Get list of court available at specific period of time
	 * @param startTime Start time to check
	 * @param endTime End time to check
	 * @return ArrayList of court available at that period of time
	 */
	public ArrayList<Court> getAvailableCourts(DateAndTime startTime, DateAndTime endTime) {
		ArrayList<Court> result = new ArrayList<>();
		
		for (Court court: this.getAllCourts()) {
			if (court.isAvailableForBookingTime(startTime, endTime)) {
				result.add(court);
			}
		}
		
		return result;
	}

	/**
	 * Get all courts that has booking
	 * @return ArrayList of court
	 */
	public ArrayList<Court> getBookedCourts() {
		ArrayList<Court> result = new ArrayList<>();
		
		for (Sport sport: this.sportList) {
			for (Court court: sport.getCourtList()) {
				if (court.getBookingList().size() > 0) {
					result.add(court);
				}
			}
		}
		
		return result;
	}

	/**
	 * Get all members that has booking
	 * @return ArrayList of member
	 */
	public ArrayList<Member> getBookedMemberList() {
		ArrayList<Member> result = new ArrayList<>();
		
		for (Member member: this.memberList) {
			if (member.getBookingList().size() > 0) {
				result.add(member);
			}
		}
		
		return result;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<Member> getMemberList() {
		return this.memberList;
	}

	/**
	 * 
	 * @param sport
	 * @param member
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public ArrayList<Court> getAvailableCourts(Sport sport, Member member, DateAndTime startTime, DateAndTime endTime) {
		ArrayList<Court> result = new ArrayList<>();
		
		for (Court court: sport.getCourtList()) {
			if (court.isAvailableForBookingTime(startTime, endTime)) {
				if (member.isAvailableForBookingTime(startTime, endTime)) {
					result.add(court);
				}
			}
		}
		
		return result;

	}

	/**
	 * 
	 * @param member
	 * @param date
	 * @return
	 */
	public ArrayList<Booking> getBookingsByDate(Member member, DateAndTime date) {
		ArrayList<Booking> result = new ArrayList<>();
		
		if (member != null) {
			for (Booking booking: member.getBookingList()) {
				if (booking.getStartTime().toString("d/M/yyyy").equals(date.toString("d/M/yyyy"))) {
					result.add(booking);
				}
			}
		}
		
		return result;
	} 
}
 