package es.ull.isaatc.simulation.editor.plugin.designer.actions.model;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.actions.project.SighosOpenProjectAction;
import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.DesignerManager;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class CreateRootFlowAction extends SighosOpenProjectAction implements
		TooltipTogglingWidget {
	private static final long serialVersionUID = 1L;

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, ResourceLoader.getMessage("rootflow_create_enabled"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("rootflow_create_enabled"));
		putValue(Action.SMALL_ICON, getIconByName("New"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_C));
	}

	public void actionPerformed(ActionEvent event) {
		DesignerManager.getInstance().createRootFlow();
	}

	public String getEnabledTooltipText() {
		return ResourceLoader.getMessage("rootflow_create_enabled");
	}

	public String getDisabledTooltipText() {
		return ResourceLoader.getMessage("rootflow_create_disabled");
	}
}
