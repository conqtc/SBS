/**
 * 
 * 
 * @author Alex Truong (101265224) 
 * @version 0.1
 */

import java.time.format.*;
import CException.*;

public class Booking implements Comparable<Booking> {
	//
	private Court court;
	
	//
	private Member member;
	
	//
	private DateAndTime startTime;
	
	//
	private DateAndTime endTime;
	
    
    /**
     * Booking constructor
     * @param member Member of the booking
     * @param court Court of the booking
     * @param startTime Start time of the booking
     * @param minutes Minutes of the booking
     * @throws NullPointerException when one of the parameters is null
     * @throws InvalidBookingException when this booking is invalid
     */
    public Booking(Member member, Court court, DateAndTime startTime, int minutes) throws 
                   NullPointerException, InvalidBookingException {
    	if (member == null) {
    		throw new NullPointerException("New Booking: member is null.");
    	}
    	
    	if (court == null) {
    		throw new NullPointerException("New Booking: court is null.");
    	}
    	
    	if (startTime == null) {
    		throw new NullPointerException("New Booking: startTime is null.");
    	}
    	
    	if (!member.isFinancialMember()) {
    		throw new InvalidBookingException("New Booking: member [" + member.getName() + ", " + 
    	                                      member.getIdentifier() + "] is not financial member.");
    	}
    	
    	Sport sport = getSport(member, court);
    	if (sport == null) { 	// there's no sport for this court that member registered to play
    		throw new InvalidBookingException("New Booking for member [" +member.getName() + ", " + 
    	                                      member.getIdentifier() + "] on court [" + court.getNumber() + "]: no sport matched");
    	}
    	
    	if (minutes <= 0) {
    		throw new InvalidBookingException("New Booking for member [" +member.getName() + ", " + 
    	                                      member.getIdentifier() + "] on court [" + court.getNumber() + 
    	                                      "]: booking duration is NOT positive [" + minutes + "]");
    	}
    	
    	if (minutes > sport.getMaximumBookingMinutes()) {
    		throw new InvalidBookingException("New Booking for member [" +member.getName() + ", " + 
    	                                      member.getIdentifier() + "] on court [" + court.getNumber() + 
    	                                      "]: booking duration exceeds maximum for sport [" + 
    	                                      sport.getSportName() + ": " + sport.getMaximumBookingMinutes() + " mins]");
    	}
    	
    	if (DateAndTime.isThePast(startTime)) { // why book for the past?
    		throw new InvalidBookingException("New Booking for member [" +member.getName() + ", " + 
    	                                      member.getIdentifier() + "] on court [" + court.getNumber() + 
    	                                      "]: start time is in the past.");
    	}
    	
    	// advance to 7 days later
    	if (!DateAndTime.isWithinNumberOfDays(startTime, 7)) {	// why book over 7 days in advance?
    		throw new InvalidBookingException("New Booking for member [" +member.getName() + ", " + 
    	                                      member.getIdentifier() + "] on court [" + court.getNumber() + 
    	                                      "]: booking time exceeds 7 days in advance [" + startTime.toString("HH:mm dd-MMM") + "].");
    	}
    	
    	this.endTime = startTime.plusMinutes(minutes);
    	
    	// everything seems fine
    	this.startTime = startTime;
    	this.member = member;
    	this.court = court;
    }
    
    /**
     * Get member
     * @return Member of this booking
     */
    public Member getMember() {
    	return this.member;
    }
    
    /**
     * Get court
     * @return Court of this booking
     */
    public Court getCourt() {
    	return this.court;
    }
    
    /**
     * Check if this booking is overlapped with another
     * @param another Another Booking object to be compared
     * @return true if overlapped, false if not
     */
    public boolean isOverlappedWith(Booking another) {
    	if (another == null) {
    		return false;	// null: safe
    	}
    	
    	if (this.court != another.court) {
    		if (this.member != another.member) {
    			return false;	// different members, different courts: safe
    		}
    	}
    	
    	// either same member different courts or
    	// different members same court or
    	// same member same court
    	return isOverlappedWith(another.startTime, another.endTime);
    }

    /**
     * Check if this booking is overlapped with a period defined by start and end time
     * @param startTime Start time of the booking
     * @param endTime End time of the booking
     * @return true if overlapped, false if not
     */
    public boolean isOverlappedWith(DateAndTime startTime, DateAndTime endTime) {
    	if (startTime == null || endTime == null) {
    		return false;	// null: safe
    	}
    	
    	if (this.startTime.compareTo(startTime) < 0) { // start time < another start time
    		if (this.endTime.compareTo(startTime) > 0) { // and end time > another start time
    			return true;
    		}
    	} else if (this.startTime.compareTo(endTime) < 0) { // start time >= another start time and < another end time
    		return true;
    	}
    	
    	// all else
    	return false;
    }
    
    /**
     * Get Sport of this booking
     * @return Sport object 
     */
    public Sport getSport() {
    	return this.getSport(this.member, this.court);
    }

    /**
     * Get Sport of this booking
     * @param member Member
     * @param court Court
     * @return Sport object, null if no sport found
     */
    private Sport getSport(Member member, Court court) {
    	for (Sport sport: member.getSportList()) {
    		if (sport.getCourtList().indexOf(court) >= 0) {
    			// TODO: it might return more than 1 since 2 sports might have same court?
    			return sport;
    		}
    	}
    	
    	return null;
    }
    
    /**
     * Booking constructor
     * @param member Provisional member
     * @param court Provisional Court
     * @throws NullPointerException if member or court is null
     */
    public Booking(Member member, Court court) throws NullPointerException {
    	if (member == null) {
    		throw new NullPointerException("New Booking: member is null.");
    	}
    	
    	if (court == null) {
    		throw new NullPointerException("New Booking: court is null.");
    	}
    	
    	this.member = member;
    	this.court = court;
    }
    
    /**
     * Implement Comparable interface
     * @param anotherBooking Another booking object to compare to
     * @return 1 if bigger, 0 if equal, -1 otherwise
     */
    public int compareTo(Booking anotherBooking) {
    	// another booking is null: come first
    	if (anotherBooking == null) {
    		return -1;
    	}
    	
    	// same object: equal
    	if (this == anotherBooking) {
    		return 0;
    	}
    	
    	// start time is null: come later
    	if (this.startTime == null) {
    		return 1;
    	}
    	
    	// compare start time
    	int compareStartTime = this.startTime.compareTo(anotherBooking.startTime);
    	if (compareStartTime == 0) {	// same start time
    		if (this.endTime == null) {	// end time is null
    			return 1;	// come later
    		} else {	// compare end time
    			return this.endTime.compareTo(anotherBooking.endTime);
    		}
    	} else {
    		return compareStartTime;
    	}
    }

    /**
     * Register this booking with its Member and Court
     */
	public void registerWithMemberAndCourt() {
		this.member.addBooking(this);
		this.court.addBooking(this);
	}

	/**
	 * Get start time
	 * @return DateAndTime object represents start time
	 */
	public DateAndTime getStartTime() {
		return this.startTime;
	}

	/**
	 * Get end time
	 * @return DateAndTime object represents end time
	 */
	public DateAndTime getEndTime() {
		return this.endTime;
	}
	
	/**
	 * override toString method
	 * @return String represent this Booking object
	 */
	public String toString() {
		return this.member.getName() +
			   " court " + this.court.getNumber() + 
			   ", " + this.startTime.toString("H:m") +
			   " to " + this.endTime.toString("H:m d/M/yyyy") +
			   ", " + this.getSport().getSportName();
	}

	/**
	 * Deregister this booking from its member and court, used when deleting booking
	 */
	public void deregisterFromMemberAndCourt() {
		this.member.deleteBooking(this);
		this.court.deleteBooking(this);
	}

	/**
	 * Get save data of this booking object
	 * @return String data to be saved
	 */
	public String saveData() {
		return this.member.getIdentifier() + 
			   "," + this.court.getNumber() + 
			   "," + this.startTime.toString("d/M/yyyy") +
			   "," + this.startTime.toString("H:m") +
			   "," + this.endTime.toString("H:m");
	}

	/**
	 * Static method to construct a Booking object from a saved string data
	 * @param club Referenced Club object
	 * @param line Saved string data
	 * @return Booking object, null if unable to create one
	 * @throws Exception When something goes wrong
	 */
	public static Booking constructFromString(Club club, String line) throws Exception {
		String[] stringList = line.split("\\s*,\\s*");
		Booking booking = null;
		
		try {
			int memberID = Integer.parseInt(stringList[0]);
			Member member = club.searchMemberByID(memberID);
			if (member == null)
				throw new Exception("No member found for ID [" + memberID + "]");
			
			int courtID = Integer.parseInt(stringList[1]);
			Court court = club.searchCourtByNumber(courtID);
			if (court == null)
				throw new Exception("No court found for ID [" + courtID + "]");

			String date = stringList[2];
			String start = stringList[3];
			String end = stringList[4];

			String pattern = "d/M/yyyy H:m";
			DateAndTime startTime = DateAndTime.fromString(date + " " + start, pattern);
			DateAndTime endTime = DateAndTime.fromString(date + " " + end, pattern);
			booking = new Booking(member, court, startTime, (int)DateAndTime.betweenInMinutes(startTime, endTime));
		} catch (IndexOutOfBoundsException ioobe) {
			throw new ParseLineException("Invalid format! Must be memberid,courtid,date,starttime,endtime");
		} catch (NumberFormatException nfe) {
			throw new ParseLineException("Unable to parse number " + nfe.getMessage());
		} catch (DateTimeParseException dtpe) {
			throw new ParseLineException("Unable to parse date and time " + dtpe.getMessage());
		}
		
		return booking;
	}
}
