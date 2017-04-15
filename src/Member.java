
/**
 * 
 * 
 * @author Alex Truong (101265224)
 * @version 0.1
 */

import java.util.*;
import CException.*;

public class Member implements Comparable<Member> {
	//
	private String name;
	
	//
	private int identifier;
	
	//
	private boolean isFinancialMember;
	
	//
	private ArrayList<Sport> sportList;
	
	//
	private ArrayList<Booking> bookingList;
	
	/**
	 * 
	 */
	public int compareTo(Member other) {
		return this.name.compareTo(other.getName());
	}
			
	/**
	 * Member constructor
	 * @param name name of the Member
	 * @param identifier Member id
	 * @param isFinancialMember true if member is financial, false if not
	 * @throws NullPointerException When name is null
	 * @throws EmptyStringException When name is empty
	 * @throws InvalidNumberException if identifier value is invalid
	 */
	public Member(String name, int identifier, boolean isFinancialMember) throws NullPointerException, EmptyStringException, InvalidNumberException {
		if (name == null) {
			throw new NullPointerException("New Member: name is null.");
		}
		
		if (identifier <= 0) {
			throw new InvalidNumberException("New Member: number is NOT positive.");
		}
		
		name = name.trim();
		
		if (name.isEmpty()) {
			throw new EmptyStringException("New Member: name is empty.");
		}
		
		this.name = name;
		this.identifier = identifier;
		this.isFinancialMember = isFinancialMember;
		this.sportList = new ArrayList<>();
		this.bookingList = new ArrayList<>();
	}
	
	/**
	 * Get member name
	 * @return member name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get member identifier number
	 * @return member id
	 */
	public int getIdentifier() {
		return this.identifier;
	}
		
	/**
	 * Check if this member is financial
	 * @return true if this member is financial, false if not
	 */
	public boolean isFinancialMember() {
		return this.isFinancialMember;
	}
	
	/**
	 * Set member financial status
	 * @param financialMemberShip true or false
	 */
	public void setFinancialMembership(boolean financialMemberShip) {
		this.isFinancialMember = financialMemberShip;
	}
	
	/**
	 * Get all sports this member can play
	 * @return ArrayList of Sport
	 */
	public ArrayList<Sport> getSportList() {
		return this.sportList;
	}
	
	/**
	 * Add new sport
	 * @param sport Sport to be added
	 * @throws NullPointerException If Sport is null
	 * @throws EmptyStringException If Sport name is empty
	 * @throws DuplicateException If another sport with same name already exists
	 */
	public void addSport(Sport sport) throws NullPointerException, EmptyStringException, DuplicateException {
		if (sport == null) {
			throw new NullPointerException("Member.addSport: sport is null.");
		}
		
		if (searchSportByName(sport.getSportName(), true) !=null) {
			throw new DuplicateException("Member.addSport: sport '" + sport.getSportName() + "' already exists.");
		}
		
		sportList.add(sport);
	}
	
	/**
	 * Search sport by name
	 * @param name name of the sport to be search
	 * @param ignoreCase ignore case if true, false otherwise 
	 * @throws NullPointerException If name is null
	 * @throws EmptyStringException If name is empty
	 * @return Found Sport object, null if not found
	 */
	public Sport searchSportByName(String name, boolean ignoreCase) throws NullPointerException, EmptyStringException {
		if (name == null) {
			throw new NullPointerException("Member.searchSportByName: name is null.");
		}
		
		if (name.isEmpty()) {
			throw new EmptyStringException("Member.searchSportByName: name is empty.");
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
	 * Represent this object itself
	 * @return String
	 */
	public String toString() {
		String result = this.getIdentifier() + "," + this.getName() + "," + (this.isFinancialMember() ? "true" : "false");
		
		for (Sport sport: this.getSportList()) {
			result += "," + sport.getSportName();
 		}
		
		return result;
	}
	
	/**
	 * Called by Club class to save data
	 * @return String represents saved data
	 */
	public String saveData() {
		String result = this.getIdentifier() + "," + this.getName() + "," + (this.isFinancialMember() ? "true" : "false");
		
		for (Sport sport: this.getSportList()) {
			result += "," + sport.getSportName();
 		}
		
		return result;
	}
	
	/**
	 * Get list of sport as string
	 * @return String represents the list of sports separated by comma
	 */
	public String getSportListAsString() {
		String listString = "";
		Iterator<Sport> iterator = this.sportList.iterator();
		
		if (iterator.hasNext()) {
			listString = iterator.next().getSportName();
		}
		
		while (iterator.hasNext()) {
			listString += ", " + iterator.next().getSportName();
		}
		
		return listString;
	}

	/**
	 * Add booking
	 * @param booking Booking object to be added
	 */
	public void addBooking(Booking booking) {
		// insert this booking to appropriate position using insertion sort algorithm
		// or simply call: Collections.sort(this.bookingList);
		int index = 0;
		while (index < this.bookingList.size() && booking.compareTo(this.bookingList.get(index)) > 0) {
			index++;
		}
		this.bookingList.add(index, booking);
	}

	/**
	 * Get booking list of this member
	 * @return ArrayList of bookings
	 */
	public ArrayList<Booking> getBookingList() {
		return this.bookingList;
	}

	/**
	 * Delete booking
	 * @param booking Booking to be deleted
	 */
	public void deleteBooking(Booking booking) {
		if (!this.bookingList.remove(booking)) {
			// TODO: object not found
		}
	}

	/**
	 * Static method to construct a member from a saved string
	 * @param club Referenced Club object
	 * @param line Saved string data
	 * @return Member object or null if failed
	 * @throws Exception if anything goes wrong
	 */
	public static Member constructFromString(Club club, String line) throws Exception {
		String[] stringList = line.split("\\s*,\\s*");
		Member member = null;
		
		try {
			int number = Integer.parseInt(stringList[0]);
			// search for existing sport name
			member = club.searchMemberByID(number);
			if (member != null) {
				throw new DuplicateException("Member number [" + number + "] already exists.");
			}

			String name = stringList[1];
			
			Boolean isFinancial;
			String financialString = stringList[2];
			
			if (financialString.equalsIgnoreCase("true")) {
				isFinancial = true;
			} else if (financialString.equalsIgnoreCase("false")) {
				isFinancial = false;
			} else {
				throw new ParseLineException("Unknown boolean value: '" + financialString + "'");
			}
			
			// construct new member
			member = new Member(name, number, isFinancial);
			
			// read the sports list
			for (int index = 3; index < stringList.length; index++) {
				String sportName = stringList[index];
				// search for sport ignore case
				Sport sport = club.searchSportByName(sportName, true);
				if (sport == null) {	// no such sport
					throw new ParseLineException("No sport found: '" + sportName + "'");
				}
				
				member.addSport(sport);
			}
		} catch (IndexOutOfBoundsException ioobe) {
			throw new ParseLineException("Invalid format! Must be number,name,true|false,sport1,sport2,etc,");
		} catch (NumberFormatException nfe) {
			throw new ParseLineException("Unable to parse number " + nfe.getMessage());
		}
		
		return member;
	}

	/**
	 * Check if member play a specific sport
	 * @param particularSport Sport to be checked
	 * @return true if member plays the sport, false otherwise
	 */
	public boolean playSport(Sport particularSport) {
		if (particularSport == null)
			return false;
		
		return this.sportList.contains(particularSport);
	}

	/**
	 * Search for booking with specific date and time
	 * @param dateTime Specific date and time to be checked
	 * @return Found booking or null if not found
	 */
	public Booking searchBookingByDateAndTime(DateAndTime dateTime) {
		String pattern = "d/M/yyyy H:m";
		for (Booking booking: this.bookingList) {
			if (booking.getStartTime().toString(pattern).equals(dateTime.toString(pattern))) {
				return booking;
			}
		}
		
		return null;
	}

	/**
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public boolean isAvailableForBookingTime(DateAndTime startTime, DateAndTime endTime) {
		for (Booking booking: this.bookingList) {
			if (booking.isOverlappedWith(startTime, endTime)) {
				return false;
			}
		}
		
		return true;
	}
}
