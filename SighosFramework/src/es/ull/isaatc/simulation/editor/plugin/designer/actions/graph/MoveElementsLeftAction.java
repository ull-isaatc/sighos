package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;

public class MoveElementsLeftAction extends SighosExistingGraphAction {

	private static final long serialVersionUID = 1L;

	private static final MoveElementsLeftAction INSTANCE = new MoveElementsLeftAction();

	{
		putValue(Action.SHORT_DESCRIPTION, " Move selected items left");
		putValue(Action.NAME, "Move Items Left");
		putValue(Action.LONG_DESCRIPTION,
				"Move currently selected thigies left.");
	}

	private MoveElementsLeftAction() {
	};

	public static MoveElementsLeftAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			graph.moveSelectedElementsLeft();
		}
	}
}
