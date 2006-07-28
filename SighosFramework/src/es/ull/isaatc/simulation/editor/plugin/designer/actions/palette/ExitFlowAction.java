package es.ull.isaatc.simulation.editor.plugin.designer.actions.palette;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.menu.Palette;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class ExitFlowAction extends SighosPaletteAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, ResourceLoader.getMessage("exitflow"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("exitflow_long"));
		putValue(Action.SMALL_ICON, getPaletteIconByName("ExitCell"));
		setIdentifier(Palette.FINAL_FLOW);
	}

	public String getEnabledTooltipText() {
		return ResourceLoader.getMessage("exitflow_enabled");
	}

	public String getDisabledTooltipText() {
		return ResourceLoader.getMessage("palette_disabled");
	}
}
