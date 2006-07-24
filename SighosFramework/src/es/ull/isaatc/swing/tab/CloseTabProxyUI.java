/*
 * Created on Jan 27, 2005
 *
 * This file is copyright 2005 by Neal Adam Walker. All rights reservered. 
 * See Licence.txt for details.
 */
package es.ull.isaatc.swing.tab;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.accessibility.Accessible;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.plaf.TabbedPaneUI;

import com.sun.java.swing.plaf.windows.WindowsIconFactory;

/**
 * @author Adam Walker
 *
 */
public class CloseTabProxyUI extends CloseTabPaneUI {

    private TabbedPaneUI delegate = null;
    private boolean closeEnabled = true;
	private BufferedImage closeImgB;
	private BufferedImage closeImgI;
	private JButton closeB;
	protected static final int BUTTONSIZE = 15;
    
    /**
     * 
     */
    public CloseTabProxyUI(TabbedPaneUI d) {
        super();
        delegate = d;
		closeImgB = new BufferedImage(BUTTONSIZE, BUTTONSIZE,
				BufferedImage.TYPE_4BYTE_ABGR);


		closeImgI = new BufferedImage(BUTTONSIZE, BUTTONSIZE,
				BufferedImage.TYPE_4BYTE_ABGR);

		closeB = new JButton();
		closeB.setSize(BUTTONSIZE, BUTTONSIZE);

		WindowsIconFactory.createFrameCloseIcon().paintIcon(closeB,
				closeImgI.createGraphics(), 0, 0);
    }


    public boolean contains(JComponent c, int x, int y) {
        return delegate.contains(c, x, y);
    }
    public Accessible getAccessibleChild(JComponent c, int i) {
        return delegate.getAccessibleChild(c, i);
    }
    public int getAccessibleChildrenCount(JComponent c) {
        return delegate.getAccessibleChildrenCount(c);
    }
    public Dimension getMaximumSize(JComponent c) {
        return delegate.getMaximumSize(c);
    }
    public Dimension getMinimumSize(JComponent c) {
        return delegate.getMinimumSize(c);
    }
    public Dimension getPreferredSize(JComponent c) {
        return delegate.getPreferredSize(c);
    }
    public Rectangle getTabBounds(JTabbedPane pane, int index) {
        Rectangle r = new Rectangle(delegate.getTabBounds(pane, index));
        r.width += 150;//BUTTONSIZE;
        System.out.println(r.width);
        return r;
    }
    public int getTabRunCount(JTabbedPane pane) {
        return delegate.getTabRunCount(pane);
    }
    public void installUI(JComponent c) {
        System.out.println("installUI");
        delegate.installUI(c);
    }
    public void paint(Graphics g, JComponent c) {
        System.out.println("Paint");
        delegate.paint(g, c);
    }
    public int tabForCoordinate(JTabbedPane pane, int x, int y) {
        System.out.println("tabForCoordinate");
        return delegate.tabForCoordinate(pane, x, y);
    }
    public void uninstallUI(JComponent c) {
        delegate.uninstallUI(c);
    }
    public void update(Graphics g, JComponent c) {
        System.out.println("update");
        delegate.update(g, c);
    }
    public boolean isCloseEnabled() {
        return closeEnabled;
    }
    public void setCloseEnabled(boolean closeEnabled) {
        this.closeEnabled = closeEnabled;
    }
    public void setCloseIcon(boolean b){
        setCloseEnabled(b);
    }
	/*public static ComponentUI createUI(JComponent c) {
		return new CloseTabProxyUI(new AquaBasicTabbedPaneUI());
	}*/
}
