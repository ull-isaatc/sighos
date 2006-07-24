package es.ull.isaatc.simulation.editor.plugin.designer.actions.palette;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.menu.Palette;

public class FlowConnectionAction extends SighosPaletteAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Decision flow");
		putValue(Action.LONG_DESCRIPTION, "Add a new decision flow");
		putValue(Action.SMALL_ICON, getPaletteIconByName("arrow"));
		setIdentifier(Palette.FLOW_CONNECTION);
	}

	public String getEnabledTooltipText() {
		return " Add a new decision flow ";
	}

	public String getDisabledTooltipText() {
		return " You must have an open specification, and selected flow graph to use the palette ";
	}
}
