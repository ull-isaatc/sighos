package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraphCellUtilities;

public class AlignMiddleAction extends SighosExistingGraphAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	private static final AlignMiddleAction INSTANCE = new AlignMiddleAction();

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Align along Horizontal Centre");
		putValue(Action.LONG_DESCRIPTION,
				"Horizontally align the selected elements along their centre.");
		putValue(Action.SMALL_ICON, getIconByName("AlignMiddle"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_H));
	}

	private AlignMiddleAction() {
	};

	public static AlignMiddleAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			RootFlowGraphCellUtilities.alignCellsAlongHorizontalCentre(graph,
					graph.getSelectionCells());
		}
	}

	public String getEnabledTooltipText() {
		return " Horizontally align the selected elements along their centre ";
	}

	public String getDisabledTooltipText() {
		return " You must have a number of net elements selected"
				+ " to align them ";
	}
}
