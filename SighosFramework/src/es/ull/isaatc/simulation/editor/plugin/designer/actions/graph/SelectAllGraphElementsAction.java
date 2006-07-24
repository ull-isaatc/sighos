package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;

public class SelectAllGraphElementsAction extends SighosExistingGraphAction {

	private static final long serialVersionUID = 1L;

	private static final SelectAllGraphElementsAction INSTANCE = new SelectAllGraphElementsAction();

	{
		putValue(Action.SHORT_DESCRIPTION, " Select all net elements ");
		putValue(Action.NAME, "Select all");
		putValue(Action.LONG_DESCRIPTION, "Select all net elements.");
	}

	private SelectAllGraphElementsAction() {
	};

	public static SelectAllGraphElementsAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			graph.setSelectionCells(graph.getRoots());
		}
	}
}
