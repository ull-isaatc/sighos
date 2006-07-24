package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class ApplySugiyamaLayoutAction extends SighosExistingGraphAction
		implements TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	private static final ApplySugiyamaLayoutAction INSTANCE = new ApplySugiyamaLayoutAction();

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, ResourceLoader.getMessage("sugiyama"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader
				.getMessage("sugiyama_long"));
		putValue(Action.SMALL_ICON, getIconByName("Sugiyama"));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L,
				InputEvent.CTRL_DOWN_MASK));
	}

	private ApplySugiyamaLayoutAction() {
	}

	public static ApplySugiyamaLayoutAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			graph.autoLayout();
		}
	}

	public String getEnabledTooltipText() {
		return ResourceLoader.getMessage("sugiyama_enabled");
	}

	public String getDisabledTooltipText() {
		return ResourceLoader.getMessage("sugiyama_disabled");
	}
}
