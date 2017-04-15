import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

/**
 * http://stackoverflow.com/questions/299495/how-to-add-an-image-to-a-jpanel
 *
 */
public class CImagePanel extends JPanel{

	private BufferedImage image;
	
	private String title;
	private String subtitle1;
	private String subtitle2;

    /**
     * 
     * @param file
     */
    public CImagePanel(String file, String title, String subtitle1, String subtitle2) {
    	this.title = title;
    	this.subtitle1 = subtitle1;
    	this.subtitle2 = subtitle2;
    	
    	try {                
    		image = ImageIO.read(new File(file));
    	} catch (IOException ex) {}
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
        
        // draw text
        int x = 100;
        int y = this.getHeight() / 2;
        int space = 35;
        
        Font font = new Font("SansSerif", Font.PLAIN, 36);
        g.setFont(font);
        g.setColor(Color.BLUE);
        g.drawString(this.title, x, y - space);
        
        font = new  Font("SansSerif", Font.PLAIN, 18);
        g.setFont(font);
        g.setColor(Color.DARK_GRAY);
        g.drawString(this.subtitle1, x, y);
        g.drawString(this.subtitle2, x, y + space);
    }

}
