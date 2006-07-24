package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;

public class MoveElementsRightAction extends SighosExistingGraphAction {

	private static final long serialVersionUID = 1L;

	private static final MoveElementsRightAction INSTANCE = new MoveElementsRightAction();

	{
		putValue(Action.SHORT_DESCRIPTION, " Move selected items right");
		putValue(Action.NAME, "Move Items Right");
		putValue(Action.LONG_DESCRIPTION,
				"Move currently selected thigies right.");
	}

	private MoveElementsRightAction() {
	};

	public static MoveElementsRightAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			graph.moveSelectedElementsRight();
		}
	}
}
