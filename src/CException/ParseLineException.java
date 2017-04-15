/**
 * Write a description of class ParseLineException here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

package CException;

public class ParseLineException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 10004L;

	/**
     * Constructor for objects of class InvalidNumberException
     */
    public ParseLineException() {
    	super();
    }

    /**
     * Constructor
     * @param description Description of the exception
     */
    public ParseLineException(String description) {
    	super(description);
    }
}
