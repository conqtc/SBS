
/**
 * 
 * 
 * @author Alex Truong (101265224)
 * @version 0.1
 */

import java.util.*;
import CException.*;

public class Court implements Comparable<Court> {
	//
	private int number;
	
	//
	private ArrayList<Booking> bookingList;
	
	/**
	 * 
	 */
	public int compareTo(Court other) {
		if (this.number < other.getNumber())
			return -1;
		
		if (this.number > other.getNumber())
			return 1;
		
		return 0;
	}
		
	
	/**
	 * Constructor of Court object
	 * @param number court number id
	 * @throws InvalidNumberException if court number id is invalid
	 */
	public Court(int number) throws InvalidNumberException {
		if (number <= 0) {
			throw new InvalidNumberException("New Court: number is NOT positive.");
		}
		
		this.number = number;
		this.bookingList = new ArrayList<>();
	}
	
	/**
	 * Get court number id
	 * @return court number id
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * Add new booking
	 * @param booking Booking to be added
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
	 * Check if this court is available at the moment
	 * @return true if available, false if not
	 */
	public boolean isAvailableNow() {
		return isAvailableAt(DateAndTime.now());
	}
	
	/**
	 * Check if this court is available at the specific time
	 * @param moment DateAndTime to check if this court is available
	 * @return true if available, false if not
	 */
	public boolean isAvailableAt(DateAndTime moment) {
		for (Booking booking: this.bookingList) {
			if (booking.getStartTime().compareTo(moment) <= 0) { 
			    if (booking.getEndTime().compareTo(moment) >= 0) {
			    	return false;
			    }
			} else {	// no need to check no more since the list is sorted
				break;
			}
		}
		
		return true;
	}
	
	/**
	 * Get next booking for this court
	 * @return Booking of the next booking
	 */
	public Booking getNextBookingNow() {
		DateAndTime nowMoment = DateAndTime.now();
		
		for (Booking booking: this.bookingList) {
			if (booking.getStartTime().compareTo(nowMoment) >= 0) {
				return booking;
			}
		}
		
		return null;
	}

	/**
	 * Get next booking considering from a specific time
	 * @param moment DateAndTime of the moment to check
	 * @return Booking object of the next booking
	 */
	public Booking getNextBookingAt(DateAndTime moment) {
		for (Booking booking: this.bookingList) {
			if (booking.getStartTime().compareTo(moment) >= 0) {
				return booking;
			}
		}
		
		return null;
	}

	/**
	 * Get all bookings for this court
	 * @return ArrayList of Booking
	 */
	public ArrayList<Booking> getBookingList() {
		return this.bookingList;
	}

	/**
	 * Check if this court is available for start time and end time (for the provisional booking)
	 * @param startTime Provisional start time
	 * @param endTime Provisional end time
	 * @return true if available, false if not
	 */
	public boolean isAvailableForBookingTime(DateAndTime startTime, DateAndTime endTime) {
		for (Booking booking: this.bookingList) {
			if (booking.isOverlappedWith(startTime, endTime)) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Delete booking
	 * @param booking Booking object to be deleted
	 */
	public void deleteBooking(Booking booking) {
		if (!this.bookingList.remove(booking)) {
			// TODO: object not found
		}
	}
	
	public String toString() {
		return "Court number: " + this.number;
	} 
}