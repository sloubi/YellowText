package eu.sloubi.yellowtext;

import com.github.weisj.darklaf.graphics.ThemedColor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

// Copied from
// JTabbedPane with close Icons | Oracle Forums
// https://community.oracle.com/thread/1356993

/**
 * The class which generates the 'X' icon for the tabs. The constructor
 * accepts an icon which is extra to the 'X' icon, so you can have tabs
 * like in JBuilder. This value is null if no extra icon is required.
 */
public class CloseTabIcon implements Icon {
	/**
	 * the x position of the icon.
	 */
	private int xpos;

	/**
	 * the y position of the icon.
	 */
	private int ypos;

	/**
	 * the width the icon.
	 */
	private final int width;

	/**
	 * the height the icon.
	 */
	private final int height;

	/**
	 * the additional fileicon.
	 */
	private final Icon fileIcon;

	/**
	 * true whether the mouse is over this icon, false otherwise.
	 */
	protected boolean mouseover;

	/**
	 * true whether the mouse is pressed on this icon, false otherwise.
	 */
	protected boolean mousepressed;

	/**
	 * Creates a new instance of <code>CloseTabIcon</code>.
	 *
	 * @param fileIcon the additional fileicon, if there is one set
	 */
	public CloseTabIcon(Icon fileIcon) {
		this.fileIcon = fileIcon;
		this.width = 16;
		this.height = 16;
	}

	/**
	 * Draw the icon at the specified location. Icon implementations may use the
	 * Component argument to get properties useful for painting, e.g. the
	 * foreground or background color.
	 *
	 * @param c the component which the icon belongs to
	 * @param g the graphic object to draw on
	 * @param x the upper left point of the icon in the x direction
	 * @param y the upper left point of the icon in the y direction
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		xpos = x;
		ypos = y;
		int yp = y + 6;

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (mouseover) {
			g2.setPaint(new ThemedColor("palette.gray"));
			g2.fillOval(x, y + 3, 12, 12);
		}

		g2.setPaint(mouseover ? Color.WHITE : new ThemedColor("palette.gray"));
		g2.drawLine(x + 3, yp, x + 8, yp + 5);
		g2.drawLine(x + 3, yp + 5, x + 8, yp);

		g2.dispose();
	}

	/**
	 * Returns the icon's width.
	 *
	 * @return an int specifying the fixed width of the icon.
	 */
	@Override
	public int getIconWidth() {
		return Objects.nonNull(fileIcon) ? width + fileIcon.getIconWidth() : width;
	}

	/**
	 * Returns the icon's height.
	 *
	 * @return an int specifying the fixed height of the icon.
	 */
	@Override
	public int getIconHeight() {
		return height;
	}

	/**
	 * Gets the bounds of this icon in the form of a <code>Rectangle</code>
	 * object. The bounds specify this icon's width, height, and location
	 * relative to its parent.
	 *
	 * @return a rectangle indicating this icon's bounds
	 */
	public Rectangle getBounds() {
		return new Rectangle(xpos, ypos, width, height);
	}
}