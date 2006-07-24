package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;

public class DecreaseSizeAction extends SighosExistingGraphAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	private static final DecreaseSizeAction INSTANCE = new DecreaseSizeAction();

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Decrease Size");
		putValue(Action.LONG_DESCRIPTION,
				"Decrease size of currently selected net elements.");
		putValue(Action.SMALL_ICON, getIconByName("DecreaseSize"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_D));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_UP,
				InputEvent.CTRL_MASK));
	}

	private DecreaseSizeAction() {
	};

	public static DecreaseSizeAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			graph.decreaseSelectedVertexSize();
		}
	}

	public String getEnabledTooltipText() {
		return " Decrease Size of selected items ";
	}

	public String getDisabledTooltipText() {
		return " You must have a number of net elements selected"
				+ " to decrease their size ";
	}
}
