package es.ull.isaatc.simulation.editor.plugin.designer.actions.palette;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.menu.Palette;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class TypeFlowAction extends SighosPaletteAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, ResourceLoader.getMessage("typeflow"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("typeflow_long"));
		putValue(Action.SMALL_ICON, getPaletteIconByName("TypeCell"));
		setIdentifier(Palette.TYPE_NODE);
	}

	public String getEnabledTooltipText() {
		return ResourceLoader.getMessage("typeflow_enabled");
	}

	public String getDisabledTooltipText() {
		return ResourceLoader.getMessage("palette_disabled");
	}
}
