/**
 * Write a description of class EmptyStringException here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

package CException;

public class EmptyStringException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 10001L;

	/**
     * Constructor for objects of class EmptyStringException
     */
    public EmptyStringException() {
    	super();
    }

    /**
     * Constructor
     * @param description Description of the exception
     */
    public EmptyStringException(String description) {
    	super(description);
    }
}
