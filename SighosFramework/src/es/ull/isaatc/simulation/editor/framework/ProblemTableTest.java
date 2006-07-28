package es.ull.isaatc.simulation.editor.framework;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemMessagePanel;
import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;

public class ProblemTableTest extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private ProblemMessagePanel problemPanel = new ProblemMessagePanel();

	private static ProblemTableTest INSTANCE = null;

	public static ProblemTableTest getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ProblemTableTest();
		}
		return INSTANCE;
	}

	/**
	 * This is the default constructor
	 */
	public ProblemTableTest() {
		super();
		INSTANCE = this;
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setTitle("Sighos Framework");
		this.setContentPane(problemPanel);
		List<ProblemTableItem> pList = new ArrayList<ProblemTableItem>();
		for (int i = 0; i < 10; i++) {
			pList.add(new ProblemTableItem(ProblemTableItem.ProblemType.WARNING, "Warning " + i, "Activity", i));
			pList.add(new ProblemTableItem(ProblemTableItem.ProblemType.ERROR, "Error " + i, "Activity", i));
		}
		problemPanel.setProblemList(pList);
	}

	
	public static void main(String[] args) {
		ProblemTableTest frame = ProblemTableTest.getInstance();
		// Set Default Size
		frame.setSize(900, 600);
		// frame.setExtendedState(JFrame.MAXIMIZED_HORIZ);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Show Frame
		frame.setVisible(true);
	}
}
