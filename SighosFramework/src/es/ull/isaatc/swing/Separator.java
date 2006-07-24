/**
 * 
 */
package es.ull.isaatc.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;

/**
 * This class implements a horizontal separator with a selectable
 * thickness. Useful for toolbars, button bars and other bar-like
 * stuff.
 *
 * @author David M. Geary
 * @author Kalle Dalheimer <kalle@dalheimer.de>
 * @version $Id: Separator.java,v 1.1.1.1 2001/02/07 15:23:50 rtfm Exp $
 */
public class Separator extends Component {
	int _thickness = 2;

	/**
	 * Reimplements Component.paint() and draw the line.
	 *
	 * @param g The Graphics object of the separator component.
	 */
	public void paint(Graphics g) {
		Dimension size = getSize();

		g.setColor(SystemColor.controlShadow);
		int y = (size.height / 2) - (_thickness / 2);
		while (y < (size.height / 2)) {
			g.drawLine(0, y, size.width, y);
			++y;
		}
		g.setColor(SystemColor.controlLtHighlight);
		y = size.height / 2;
		while (y < ((size.height / 2) + (_thickness / 2))) {
			g.drawLine(0, y, size.width, y);
			++y;
		}
	}

	/**
	 * Necessary for layout management.
	 *
	 * @return the preferred size
	 */
	public Dimension getPreferredSize() {
		Dimension prefsz = getSize();

		prefsz.height = _thickness;
		return prefsz;
	}

	/**
	 * Set the thickness of the separator line.
	 *
	 * @param thickness the new thickness in pixels.
	 */
	public void setThickness(int thickness) {
		_thickness = thickness;
		invalidate();
	}

	/**
	 * Query the thickness of the separator line.
	 * 
	 * @return the thickness of the separator in pixels.
	 */
	public int getThickness() {
		return _thickness;
	}
}
