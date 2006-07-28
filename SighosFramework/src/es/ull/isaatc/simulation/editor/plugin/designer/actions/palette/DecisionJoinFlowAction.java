package es.ull.isaatc.simulation.editor.plugin.designer.actions.palette;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class DecisionJoinFlowAction extends SighosPaletteAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, ResourceLoader.getMessage("decisionjoinflow"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("decisionjoinflow_long"));
		putValue(Action.SMALL_ICON, getPaletteIconByName("DecisionJoinCell"));
//		setIdentifier(Palette.DECISION_JOIN_NODE);
	}

	public String getEnabledTooltipText() {
		return ResourceLoader.getMessage("decisionjoinflow_enabled");
	}

	public String getDisabledTooltipText() {
		return ResourceLoader.getMessage("palette_disabled");
	}
}
