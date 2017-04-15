import java.awt.*;
import javax.swing.*;

/**
 * List of info blocks with vertical scroll bar
 */
public class CBookingList extends JScrollPane implements ListCellRenderer {
	
	private SBS app;
	
	private JList list;
	
	private Color selectedColor;
	
    /**
     * Constructor for objects of class CBlockList
     */
    public CBookingList(SBS app, DefaultListModel model) {
    	super();
    	
    	this.app = app;
    	
    	this.list = new JList(model);
		this.list.setVisibleRowCount(0);
		this.list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		this.list.setCellRenderer(this);

		this.setViewportView(this.list);
		
		this.selectedColor = new Color(164, 226, 118);
    }
    
    /**
     * 
     * @return
     */
    public JList getList() {
    	return this.list;
    }
    
    /**
     * 
     * @param color
     */
    public void setListBackgound(Color color) {
    	this.list.setBackground(color);
    }
    
    /**
     * 
     */
    public Component getListCellRendererComponent(JList list, Object value, int index,
    	                                          boolean isSelected, boolean cellHasFocus) {
		CBookingInfo item = new CBookingInfo(this.app, value);
		
		if (isSelected) {
			item.setBackground(this.selectedColor);
		} else {
			item.setBackground(this.list.getBackground());
		}

		return item;
    }
}
