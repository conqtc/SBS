
/**
 * 
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import CException.*;

public class Tennis extends Sport {
	/**
	 * Implement abstract method called from constructor
	 */
	protected void initializeSportAttributes() throws InvalidNumberException {
		// 2 hours for Tennis
		setMaximumBookingMinutes(120);
	}
	
	
    /**
     * Constructor for objects of class Tennis
     * @throws NullPointerException If Null
     * @throws EmptyStringException If Empty
     * @throws InvalidNumberException If number is invalid
     */
    public Tennis() throws NullPointerException, EmptyStringException, InvalidNumberException {
    	super("Tennis");
    }
    
	/**
	 * Tennis constructor
	 * @param usage Usage fee
	 * @param insurance Insurance fee
	 * @param affiliation Affiliation fee
	 * @throws NullPointerException If null
	 * @throws EmptyStringException If Empty
	 * @throws InvalidNumberException If number is invalid
	 */
	public Tennis(double usage, double insurance, double affiliation) throws 
	              NullPointerException, EmptyStringException, InvalidNumberException {
		super("Tennis", usage, insurance, affiliation);
	}
}
