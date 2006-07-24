package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;

public class MoveElementsUpAction extends SighosExistingGraphAction {

	private static final long serialVersionUID = 1L;

	private static final MoveElementsUpAction INSTANCE = new MoveElementsUpAction();

	{
		putValue(Action.SHORT_DESCRIPTION, " Move selected items up");
		putValue(Action.NAME, "Move Items Up");
		putValue(Action.LONG_DESCRIPTION, "Move currently selected thigies up.");
	}

	private MoveElementsUpAction() {
	};

	public static MoveElementsUpAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			graph.moveSelectedElementsUp();
		}
	}
}
