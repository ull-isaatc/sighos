package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;

public class MoveElementsDownAction extends SighosExistingGraphAction {

	private static final long serialVersionUID = 1L;

	private static final MoveElementsDownAction INSTANCE = new MoveElementsDownAction();
	
	{
		putValue(Action.SHORT_DESCRIPTION, " Move selected items down");
		putValue(Action.NAME, "Move Items Down");
		putValue(Action.LONG_DESCRIPTION,
				"Move currently selected thigies down.");
	}

	private MoveElementsDownAction() {
	};

	public static MoveElementsDownAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			graph.moveSelectedElementsDown();
		}
	}
}
