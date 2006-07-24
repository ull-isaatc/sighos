package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;

public class IncreaseSizeAction extends SighosExistingGraphAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	private static final IncreaseSizeAction INSTANCE = new IncreaseSizeAction();
	
	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Increase Size");
		putValue(Action.LONG_DESCRIPTION,
				"Increase size of currently selected net elements.");
		putValue(Action.SMALL_ICON, getIconByName("IncreaseSize"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_I));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_DOWN, InputEvent.CTRL_MASK));
	}

	private IncreaseSizeAction() {
	}

	public static IncreaseSizeAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			graph.increaseSelectedVertexSize();
		}
	}

	public String getEnabledTooltipText() {
		return " Increase Size of selected items ";
	}

	public String getDisabledTooltipText() {
		return " You must have a number of net elements selected"
				+ " to increase their size ";
	}
}
