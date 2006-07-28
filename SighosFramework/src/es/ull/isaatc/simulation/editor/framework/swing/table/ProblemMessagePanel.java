package es.ull.isaatc.simulation.editor.framework.swing.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ProblemMessagePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JScrollPane problemScrollPane;

	private static ProblemTable problemResultsTable = buildProblemMessageTable();

	public ProblemMessagePanel() {
		super();

		buildContent();
		addResizeListener();
	}

	private void buildContent() {
		setLayout(new BorderLayout());

		problemScrollPane = new JScrollPane(problemResultsTable);
		problemScrollPane.setViewportView(problemResultsTable);
		problemScrollPane.getViewport().setBackground(Color.WHITE);
		problemScrollPane.setOpaque(true);

		add(problemScrollPane, BorderLayout.CENTER);
	}

	private void addResizeListener() {
		problemScrollPane.addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent event) {
				// don't care
			}

			public void componentResized(ComponentEvent event) {

			}
		});
	}

	public void setProblemList(List<ProblemTableItem> problemList) {
		problemResultsTable.reset();

		populateProblemListTable(problemList);

		if (isVisible()) {
			repaint();
		} else {
			setVisible(true);
		}
	}

	private void populateProblemListTable(List<ProblemTableItem> problemList) {
		for (int i = 0; i < problemList.size(); i++) {
			problemResultsTable.addMessage(problemList.get(i));
		}
		readjustProblemTableSize();
	}

	public ProblemTable getProblemResultsTable() {
		return problemResultsTable;
	}

	private void readjustProblemTableSize() {
		problemResultsTable
				.setPreferredScrollableViewportSize(problemResultsTable
						.getPreferredSize());
	}

	private static ProblemTable buildProblemMessageTable() {
		ProblemTable table = new ProblemTable();
		return table;
	}
	
	public void reset() {
		problemResultsTable.reset();
	}
}
