import java.awt.*;
import javax.swing.*;

/**
 * //http://stackoverflow.com/questions/2244848/java-imageicon-size
 * @author conqtc
 *
 */
public class CScaleImageIcon extends ImageIcon {
	//
	private int size;
	
	public CScaleImageIcon(String fileName, int size) {
		super(fileName);
		this.size = size;
	}
	
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y ) {
		g.drawImage(getImage(), x, y, size, size, c);
	}

	@Override 
	public int getIconHeight() {
		return size;
	}

	@Override
	public int getIconWidth() {
		return size;
	}
}
