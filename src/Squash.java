
/**
 * 
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import CException.*;

public class Squash extends Sport {
	/**
	 * Implement abstract method called from constructor
	 */
	protected void initializeSportAttributes() throws InvalidNumberException {
		// 1 hours for Squash
		setMaximumBookingMinutes(60);
	}

	/**
	 * Squash constructor
	 * @throws NullPointerException If null
	 * @throws EmptyStringException If empty
	 * @throws InvalidNumberException If maximum booking minutes is invalid
	 */
	public Squash() throws NullPointerException, EmptyStringException, InvalidNumberException {
		super("Squash");
	}
	
	/**
	 * Squash constructor
	 * @param usage Usage fee
	 * @param insurance Insurance fee
	 * @param affiliation Affiliation fee
	 * @throws NullPointerException If Null
	 * @throws EmptyStringException If Empty
	 * @throws InvalidNumberException If number is invalid
	 */
	public Squash(double usage, double insurance, double affiliation) throws 
	              NullPointerException, EmptyStringException, InvalidNumberException {
		super("Squash", usage, insurance, affiliation);
	}
}
