package es.ull.isaatc.simulation.editor.framework.actions.project;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.project.ArchivingThread;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class ValidateModelAction extends SighosOpenProjectAction implements
		TooltipTogglingWidget {
	private static final long serialVersionUID = 1L;

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, ResourceLoader.getMessage("validate"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("validate_long"));
		putValue(Action.SMALL_ICON, getIconByName("Validate"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_V));
	}

	public void actionPerformed(ActionEvent event) {
		ArchivingThread.getInstance().validate();
	}

	public String getEnabledTooltipText() {
		return ResourceLoader.getMessage("validate_enabled");
	}

	public String getDisabledTooltipText() {
		return ResourceLoader.getMessage("validate_disabled");
	}
}
