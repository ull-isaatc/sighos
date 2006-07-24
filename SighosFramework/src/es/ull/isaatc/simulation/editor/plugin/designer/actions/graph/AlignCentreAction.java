package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraphCellUtilities;

public class AlignCentreAction extends SighosExistingGraphAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	private static final AlignCentreAction INSTANCE = new AlignCentreAction();

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Align along Vertical Centre");
		putValue(Action.LONG_DESCRIPTION,
				"Vertically align the selected elements along their centre.");
		putValue(Action.SMALL_ICON, getIconByName("AlignCentre"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_V));
	}

	private AlignCentreAction() {
	}

	public static AlignCentreAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			RootFlowGraphCellUtilities.alignCellsAlongVerticalCentre(graph,
					graph.getSelectionCells());
		}
	}

	public String getEnabledTooltipText() {
		return " Vertically align the selected elements along their centre ";
	}

	public String getDisabledTooltipText() {
		return " You must have a number of net elements selected"
				+ " to align them ";
	}
}
