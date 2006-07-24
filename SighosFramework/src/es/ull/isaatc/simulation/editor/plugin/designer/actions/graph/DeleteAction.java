package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;

public class DeleteAction extends SighosExistingGraphAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	private static final DeleteAction INSTANCE = new DeleteAction();

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Delete");
		putValue(Action.LONG_DESCRIPTION,
				"Deletes currently selected net elements.");
		putValue(Action.SMALL_ICON, getIconByName("Delete"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_D));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_DELETE, 0));
	}

	private DeleteAction() {
	};

	public static DeleteAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			graph.removeCellsAndTheirEdges(graph.getSelectionCells());
		}
	}

	public String getEnabledTooltipText() {
		return " Delete currently selected net elements ";
	}

	public String getDisabledTooltipText() {
		return " You must have a number of net elements selected"
				+ " to delete them ";
	}
}
