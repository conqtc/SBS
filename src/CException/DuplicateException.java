/**
 * 
 * 
 * @author Alex Truong (101265224) 
 * @version (a version number or a date)
 */

package CException;

public class DuplicateException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 10003L;

	/**
     * Constructor for objects of class DuplicateException
     */
    public DuplicateException() {
    	super();
    }

    /**
     * Constructor
     * @param description Description of the exception
     */
    public DuplicateException(String description) {
    	super(description);
    }
	
}
