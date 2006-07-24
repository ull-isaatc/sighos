package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraphCellUtilities;

public class AlignRightAction extends SighosExistingGraphAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	private static final AlignRightAction INSTANCE = new AlignRightAction();

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Align along Right Edges");
		putValue(Action.LONG_DESCRIPTION,
				"Align the selected elements along their right edges.");
		putValue(Action.SMALL_ICON, getIconByName("AlignRight"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_R));
	}

	private AlignRightAction() {
	};

	public static AlignRightAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			RootFlowGraphCellUtilities.alignCellsAlongRight(graph, graph
					.getSelectionCells());
		}
	}

	public String getEnabledTooltipText() {
		return " Align the selected elements along their right edges ";
	}

	public String getDisabledTooltipText() {
		return " You must have a number of net elements selected"
				+ " to align them ";
	}
}
