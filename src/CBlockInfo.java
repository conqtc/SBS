import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * http://stackoverflow.com/questions/299495/how-to-add-an-image-to-a-jpanel
 *
 */
public class CBlockInfo extends JTextArea {
	
	private SBS app;
	
	private final int blockMargin = 5;
	private Object object;
	private Color blockColor;

    /**
     * 
     * @param file
     */
    public CBlockInfo(SBS app, Object object) {
    	super("-----------------------------------------\n\n\n\n\n\n\n\n\n");
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
    	int y = 2 * blockMargin + 20;
    	
    	g.setFont(new Font("SansSerif", Font.BOLD, 18));
    	g.setColor(Color.YELLOW);

    	if (object instanceof Court) {
        	Court court = (Court) object;
        	bookingList = court.getBookingList();
        	text = String.format("%d", court.getNumber());
        	
        	g.drawString(text, x, y);
        	
        	g.setFont(new Font("SansSerif", Font.BOLD, 10));
        	g.setColor(Color.white);
        	
        	for (Booking booking: bookingList) {
        		y += 20;
        		if (image != null) {
        			 g.drawImage(image, x, y - 8, 24, 24, this);
        		}
        		g.drawString(booking.getMember().getName() + " plays " + booking.getSport().getSportName(), x + 32, y);
        		
        		y += 15;
        		g.drawString(booking.getStartTime().toString("d/M/yyyy H:m") + " - " + booking.getEndTime().toString("H:m"), x + 32, y);
        	}
        } else if (object instanceof Member) {
        	Member member = (Member) object;
        	bookingList = member.getBookingList();
        	text = member.getName();

        	g.drawString(text, x, y);
        	
        	g.setFont(new Font("SansSerif", Font.BOLD, 10));
        	g.setColor(Color.white);
        	
        	for (Booking booking: bookingList) {
        		y += 20;
        		if (image != null) {
        			 g.drawImage(image, x, y - 8, 24, 24, this);
        		}
        		g.drawString("Plays " + booking.getSport().getSportName() + " on court " + booking.getCourt().getNumber(), x + 32, y);
        		
        		y += 15;
        		g.drawString(booking.getStartTime().toString("d/M/yyyy H:m") + " - " + booking.getEndTime().toString("H:m"), x + 32, y);
        	}
        } else if (object instanceof Sport) {
        	Sport sport = (Sport) object;
        	text = sport.getSportName();
        	g.drawString(text, x, y);
        	
        	g.setFont(new Font("SansSerif", Font.BOLD, 10));
        	g.setColor(Color.white);
        	
        	BufferedImage feeImage = app.getBufferedImage("fee");
        	BufferedImage courtImage = app.getBufferedImage("nice_court");
        	
        	y += 32;
        	if (feeImage != null) {
        		g.drawImage(feeImage, x, y - 8, 24, 24, this);
        	}
    		g.drawString("" + sport.getUsageFee() + " usage", x + 32, y - 5);
    		g.drawString("" + sport.getInsuranceFee() + " insurrance", x + 32, y + 10);
    		g.drawString("" + sport.getAffiliationFee() + " affiliation", x + 32, y + 25);
        	
        	y += 48;
        	if (courtImage != null) {
        		g.drawImage(courtImage, x, y - 8, 24, 24, this);
        	}
    		g.drawString("" + sport.getMaximumBookingMinutes() + " minutes", x + 32, y);
    		g.drawString(sport.getCourtListAsString(), x + 32, y + 15);
        }
    }

}
