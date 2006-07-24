package es.ull.isaatc.simulation.editor.plugin.designer.actions;

import javax.swing.Action;
import javax.swing.KeyStroke;

import es.ull.isaatc.simulation.editor.framework.actions.plugin.LoadPluginAction;
import es.ull.isaatc.simulation.editor.framework.plugin.Plugin;
import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;

public class DesignerLoadPluginAction extends LoadPluginAction implements
		TooltipTogglingWidget {

	public DesignerLoadPluginAction(Plugin plugin) {
		super(plugin);
	}

	private static final long serialVersionUID = 1L;

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Designer");
		putValue(Action.LONG_DESCRIPTION, "Enables Sighos Designer plugin");
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_D));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control D"));
	}

	public String getEnabledTooltipText() {
		return " Enables Sighos Designer plugin ";
	}

	public String getDisabledTooltipText() {
		return " You must have an open project in order to edit it ";
	}
}
