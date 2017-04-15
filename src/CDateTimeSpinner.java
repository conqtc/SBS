import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.JSpinner.*;

/**
 * http://stackoverflow.com/questions/654342/is-there-any-good-and-free-date-and-time-picker-available-for-java-swing
 * 
 */
public class CDateTimeSpinner extends JPanel {
	
	public enum SpinnerType {
		DateOnly,
		TimeOnly,
		DateAndTime
	}
	
	private String caption;
	private JSpinner spinner;
	private DateEditor dateEditor;
	
    /**
     * Constructor for objects of class CDateTimeSpinner
     */
    public CDateTimeSpinner(String caption, SpinnerType type) {
    	super(new BorderLayout());
    	
    	this.spinner = new JSpinner(new SpinnerDateModel());
    	this.caption = caption;
    	
    	String pattern = "";
    	
    	switch (type) {
    	case DateOnly:
    		pattern = "d/M/yyyy";
    		break;
    	case TimeOnly:
    		pattern = "HH:mm";
    		break;
    	case DateAndTime:
    		pattern = "d/M/yyyy HH:mm";
    		break;
    	}
    	
    	dateEditor = new DateEditor(spinner, pattern);
    	spinner.setEditor(this.dateEditor);
    	
    	this.add(new JLabel(caption), BorderLayout.WEST);
    	this.add(this.spinner, BorderLayout.CENTER);
    }
    
    /**
     * 
     * @return
     */
    public DateAndTime getDateAndTime() {
    	Date date = this.dateEditor.getModel().getDate();
    	
    	return new DateAndTime(date.toInstant());
    }
}
