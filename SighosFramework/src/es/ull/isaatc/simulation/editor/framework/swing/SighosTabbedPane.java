package es.ull.isaatc.simulation.editor.framework.swing;

import javax.swing.JTabbedPane;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemMessagePanel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class SighosTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	private static SighosTabbedPane INSTANCE = null;
	
	private static ProblemMessagePanel problemTable = new ProblemMessagePanel();

	public static SighosTabbedPane getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SighosTabbedPane();
			INSTANCE.add(ResourceLoader.getMessage("problems"), problemTable);
		}
		return INSTANCE;
	}
	
	/**
	 * @return the problemTable
	 */
	public ProblemMessagePanel getProblemTable() {
		return problemTable;
	}
	
	public void reset() {
		removeAll();
		problemTable.reset();
		add(ResourceLoader.getMessage("problems"), problemTable);
	}
}