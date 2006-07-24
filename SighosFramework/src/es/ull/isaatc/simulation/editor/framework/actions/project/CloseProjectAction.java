package es.ull.isaatc.simulation.editor.framework.actions.project;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.project.ArchivingThread;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class CloseProjectAction extends SighosOpenProjectAction implements
		TooltipTogglingWidget {
	private static final long serialVersionUID = 1L;

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, ResourceLoader.getMessage("close"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("close_long"));
		putValue(Action.SMALL_ICON, getIconByName("CloseProject"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_W));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control W"));
	}

	public void actionPerformed(ActionEvent event) {
		ArchivingThread.getInstance().close();
	}

	public String getEnabledTooltipText() {
		return ResourceLoader.getMessage("close_enabled");
	}

	public String getDisabledTooltipText() {
		return ResourceLoader.getMessage("close_disabled");
	}
}
