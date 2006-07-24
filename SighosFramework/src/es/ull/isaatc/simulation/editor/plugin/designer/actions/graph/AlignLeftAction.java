package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraphCellUtilities;

public class AlignLeftAction extends SighosExistingGraphAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	private static final AlignLeftAction INSTANCE = new AlignLeftAction();

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Align along Left Edges");
		putValue(Action.LONG_DESCRIPTION,
				"Align the selected elements along their left edges.");
		putValue(Action.SMALL_ICON, getIconByName("AlignLeft"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_L));
	}

	private AlignLeftAction() {
	};

	public static AlignLeftAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			RootFlowGraphCellUtilities.alignCellsAlongLeft(graph, graph
					.getSelectionCells());
		}
	}

	public String getEnabledTooltipText() {
		return " Align the selected elements along their left edges ";
	}

	public String getDisabledTooltipText() {
		return " You must have a number of net elements selected"
				+ " to align them ";
	}
}
