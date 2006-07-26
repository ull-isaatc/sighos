/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.swing;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * This class manages the model
 * 
 * @author Roberto Muñoz
 */
public class SighosFrameworkDesktop extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static JComponent currentDesktop = null;
	
	private static JSplitPane splitPane = null;

	private static final SighosFrameworkDesktop INSTANCE = new SighosFrameworkDesktop();

	public static SighosFrameworkDesktop getInstance() {
		return INSTANCE;
	}

	private SighosFrameworkDesktop() {
		super();
		initialize();
	}

	private void initialize() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
//		add(getToolBarPanel(), BorderLayout.NORTH);
		add(getSplitPane(), BorderLayout.CENTER);
	}
	
	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			splitPane.setDividerSize(10);
		    splitPane.setResizeWeight(0.7);
			splitPane.setOneTouchExpandable(true);
		}
		splitPane.setTopComponent(currentDesktop);
		splitPane.setBottomComponent(SighosTabbedPane.getInstance());
	    splitPane.setVisible(false);
		return splitPane;
	}
	
	public void setDesktop(JComponent desktop) {
		currentDesktop = desktop;
		getSplitPane();
		splitPane.resetToPreferredSizes();
		splitPane.setVisible(true);
		validate();
	}
	
	public void reset() {
		splitPane.setVisible(false);
		splitPane.removeAll();
		currentDesktop = null;
		SighosTabbedPane.getInstance().reset();
		validate();
	}
}
