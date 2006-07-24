package es.ull.isaatc.simulation.editor.framework.actions.project;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.project.ArchivingThread;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class OpenProjectAction extends SighosNoOpenProjectAction implements
		TooltipTogglingWidget {
	private static final long serialVersionUID = 1L;

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, ResourceLoader.getMessage("open"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("open_long"));
		putValue(Action.SMALL_ICON, getIconByName("Open"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_O));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
	}

	public void actionPerformed(ActionEvent event) {
		ArchivingThread.getInstance().open();
	}

	public String getEnabledTooltipText() {
		return ResourceLoader.getMessage("open_enabled");
	}

	public String getDisabledTooltipText() {
		return ResourceLoader.getMessage("open_disabled");
	}
}
