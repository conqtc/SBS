
/**
 * Write a description of class InvalidNumberException here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

package CException;

public class InvalidNumberException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 10002L;

	/**
     * Constructor for objects of class InvalidNumberException
     */
    public InvalidNumberException() {
    	super();
    }

    /**
     * Constructor
     * @param description Description of the exception
     */
    public InvalidNumberException(String description) {
    	super(description);
    }
}
