/**
 * 
 * 
 * @author Alex Truong (101265224) 
 * @version 0.1
 */

import CException.*;

public class Badminton extends Sport {

	/**
	 * initialize derived object's attributes
	 */
	protected void initializeSportAttributes() throws InvalidNumberException {
		// 3 hours for badminton
		setMaximumBookingMinutes(180);
	}

	/**
	 * Constructor for derived class Badminton
	 * @throws NullPointerException when Null happens
	 * @throws EmptyStringException when Sport name is empty 
	 * @throws InvalidNumberException when fee value is invalid
	 */
    public Badminton() throws NullPointerException, EmptyStringException, InvalidNumberException {
    	super("Badminton");
    }
    
	/**
	 * Constructor for derived Badminton
	 * @param usage Usage fee
	 * @param insurance Insurance fee
	 * @param affiliation Affiliation fee
	 * @throws NullPointerException when Null happens
	 * @throws EmptyStringException when Sport name is empty
	 * @throws InvalidNumberException when the fee values are invalid (negative)
	 */
	public Badminton(double usage, double insurance, double affiliation) throws 
	                 NullPointerException, EmptyStringException, InvalidNumberException {
		super("Badminton", usage, insurance, affiliation);
	}
}
