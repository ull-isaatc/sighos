package es.ull.isaatc.simulation.editor.framework.swing;

import javax.swing.JTabbedPane;

public class SighosTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	private static SighosTabbedPane INSTANCE = null;

	public static SighosTabbedPane getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SighosTabbedPane();
		}
		return INSTANCE;
	}
	
	public void reset() {
		removeAll();
	}
}