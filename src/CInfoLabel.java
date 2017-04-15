import javax.swing.*;

/**
 * http://stackoverflow.com/questions/26420428/how-to-word-wrap-text-in-jlabel
 */
public class CInfoLabel extends JTextArea {
    /**
     * Constructor for objects of class CInfoLabel
     */
    public CInfoLabel(String label) {
    	super(label);
    	
		this.setEditable(false);
		this.setWrapStyleWord(true);
		this.setLineWrap(true);
		this.setFocusable(false);
		this.setOpaque(false);
    }
}
