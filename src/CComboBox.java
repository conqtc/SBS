import java.awt.*;
import javax.swing.*;

/**
 * Custom combo box with icon and text based on model is list of members or sports
 */
public class CComboBox extends JPanel implements ListCellRenderer {
	
	private SBS app;
	
	private JComboBox comboBox;
	
	private String caption;
	
	private Color highLightColor;
	
    /**
     * Constructor for objects of class CComboBox
     */
    public CComboBox(SBS app, String caption, DefaultComboBoxModel model) {
    	super(new BorderLayout());
    	
    	this.comboBox = new JComboBox(model);
    	this.app = app;
    	this.caption = caption;
    	this.highLightColor = new Color(8, 112, 224);
    	this.comboBox.setRenderer(this);
    	this.add(new JLabel(caption), BorderLayout.WEST);
    	this.add(this.comboBox, BorderLayout.CENTER);
    }
    
    /**
     * 
     * @return
     */
    public JComboBox getComboBox() {
    	return this.comboBox;
    }

    /**
     * 
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, 
    		                                      boolean isSelected, boolean cellHasFocus) {
    	String text = "";
    	Icon icon = null;
    	
    	if (value instanceof Sport) {
    		Sport sport = (Sport) value;
    		text = sport.getSportName();
    		icon = app.getScaleImageIcon(text.toLowerCase());
    	} else if (value instanceof Member) {
    		Member member = (Member) value;
    		text = member.getName();
    		icon = app.getScaleImageIcon("member");
    	}
    	
    	JLabel label = new JLabel(text, icon, SwingConstants.LEFT);
        label.setOpaque(true);
        
        if (isSelected) {
        	label.setForeground(Color.WHITE);
        	label.setBackground(this.highLightColor);
        }
        
        return label;
    }
}