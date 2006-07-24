package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraphCellUtilities;

public class AlignTopAction extends SighosExistingGraphAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;
	
	private static final AlignTopAction INSTANCE = new AlignTopAction();

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Align along Top Edges");
		putValue(Action.LONG_DESCRIPTION,
				"Align the selected elements their top edges.");
		putValue(Action.SMALL_ICON, getIconByName("AlignTop"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_T));
	}

	private AlignTopAction() {
	};

	public static AlignTopAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			RootFlowGraphCellUtilities.alignCellsAlongTop(graph, graph
					.getSelectionCells());
		}
	}

	public String getEnabledTooltipText() {
		return " Align the selected elements along their top edges ";
	}

	public String getDisabledTooltipText() {
		return " You must have a number of net elements selected"
				+ " to align them ";
	}
}