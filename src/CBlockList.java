import java.awt.*;
import javax.swing.*;

/**
 * http://www.java2s.com/Tutorial/Java/0240__Swing/AddyourownListCellRenderer.htm
 * List of info blocks with vertical scroll bar
 */
public class CBlockList extends JScrollPane implements ListCellRenderer {
	
	private SBS app;
	
	private JList list;
	
	private Color selectedColor;
	
    /**
     * Constructor for objects of class CBlockList
     */
    public CBlockList(SBS app, DefaultListModel model) {
    	super();
    	
    	this.app = app;
    	
    	this.list = new JList(model);
		this.list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		this.list.setVisibleRowCount(0);
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
		CBlockInfo item = new CBlockInfo(this.app, value);
		  
		if (isSelected) {
			item.setBackground(this.selectedColor);
		} else {
			item.setBackground(list.getBackground());
		}

		return item;
    }
}
