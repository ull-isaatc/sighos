package es.ull.isaatc.simulation.editor.plugin.designer.actions.palette;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.menu.Palette;

public class SelectionAction extends SighosPaletteAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Selection");
		putValue(Action.LONG_DESCRIPTION, "Graph Element Selection Mode");
		putValue(Action.SMALL_ICON, getPaletteIconByName("SelectionCell"));
		setIdentifier(Palette.SELECTION);
	}

	public String getEnabledTooltipText() {
		return " Graph Element Selection Mode ";
	}

	public String getDisabledTooltipText() {
		return " You must have an open specification, and selected graph to use the palette ";
	}
}
