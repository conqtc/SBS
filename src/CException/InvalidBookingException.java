/**
 * 
 * 
 * @author Alex Truong (101265224) 
 * @version 0.1
 */
package CException;

public class InvalidBookingException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 10005L;

	/**
     * Constructor for objects of class InvalidBookingException
     */
    public InvalidBookingException() {
    	super();
    }

    /**
     * Constructor
     * @param description Description of the exception
     */
    public InvalidBookingException(String description) {
    	super(description);
    }
}
