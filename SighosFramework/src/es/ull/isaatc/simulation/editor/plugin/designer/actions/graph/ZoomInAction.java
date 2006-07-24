package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;

public class ZoomInAction extends SighosExistingGraphAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	private static final ZoomInAction INSTANCE = new ZoomInAction();

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Zoom in");
		putValue(Action.LONG_DESCRIPTION, "Zoom in the current selected graph.");
		putValue(Action.SMALL_ICON, getIconByName("ZoomIn"));
		putValue(Action.MNEMONIC_KEY, new Integer(
				java.awt.event.KeyEvent.VK_PLUS));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
				KeyEvent.VK_DOWN, InputEvent.CTRL_MASK));
	}

	private ZoomInAction() {
	}

	public static ZoomInAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			graph.zoomIn();
		}
	}

	public String getEnabledTooltipText() {
		return " Zoom in the current graph ";
	}

	public String getDisabledTooltipText() {
		return " You must have a graph selected to zoom in ";
	}
}
