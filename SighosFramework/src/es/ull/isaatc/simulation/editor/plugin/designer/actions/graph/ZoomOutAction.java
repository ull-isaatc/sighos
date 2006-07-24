package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;

public class ZoomOutAction extends SighosExistingGraphAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	private static final ZoomOutAction INSTANCE = new ZoomOutAction();

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Zoom out");
		putValue(Action.LONG_DESCRIPTION,
				"Zoom out the current selected graph.");
		putValue(Action.SMALL_ICON, getIconByName("ZoomOut"));
		putValue(Action.MNEMONIC_KEY, new Integer(
				java.awt.event.KeyEvent.VK_MINUS));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_DOWN, InputEvent.CTRL_MASK));
	}

	private ZoomOutAction() {
	}

	public static ZoomOutAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			graph.zoomOut();
		}
	}

	public String getEnabledTooltipText() {
		return " Zoom out the current graph ";
	}

	public String getDisabledTooltipText() {
		return " You must have a graph selected to zoom out ";
	}
}
