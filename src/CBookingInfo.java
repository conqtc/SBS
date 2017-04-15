import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 *
 */
public class CBookingInfo extends JTextArea {
	
	private SBS app;
	
	private final int blockMargin = 5;
	private Object object;
	private Color blockColor;

    /**
     * 
     * @param file
     */
    public CBookingInfo(SBS app, Object object) {
    	super("-----------------------------------------\n\n\n");
    	this.app = app;
    	this.object = object;
    	this.blockColor = new Color(100, 195, 125); // default
    }
    
    /**
     * 
     * @param object
     */
    public void setObject(Object object) {
    	this.object = object;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(this.getBackground());
        g.fillRect(0,  0, this.getWidth(), this.getHeight());
        g.setColor(this.blockColor);
        g.fillRoundRect(blockMargin,  blockMargin, this.getWidth() - 2*blockMargin, this.getHeight() - 2*blockMargin, 20, 20);
        
        g.setColor(Color.WHITE);
        BufferedImage image = app.getBufferedImage("calendar");
        ArrayList<Booking> bookingList;
        String text;

        int x = 2 * blockMargin + 15;
    	int y = 2 * blockMargin + 15;
    	
    	g.setFont(new Font("SansSerif", Font.BOLD, 10));
    	g.setColor(Color.white);

    	if (object instanceof Booking) {
        	Booking booking = (Booking) object;
        	
    		if (image != null) {
    			 g.drawImage(image, x, y - 8, 24, 24, this);
    		}
    		g.drawString(booking.getMember().getName() + " plays " + booking.getSport().getSportName(), x + 32, y);
    		
    		y += 15;
    		g.drawString(booking.getStartTime().toString("d/M/yyyy H:m") + " - " + booking.getEndTime().toString("H:m"), x + 32, y);
        }
    }

}
