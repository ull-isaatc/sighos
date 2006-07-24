package es.ull.isaatc.simulation.editor.framework.actions.project;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.project.ArchivingThread;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class AnalyseModelAction extends SighosOpenProjectAction implements
		TooltipTogglingWidget {
	private static final long serialVersionUID = 1L;

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, ResourceLoader.getMessage("analyse"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("analyse_long"));
		putValue(Action.SMALL_ICON, getIconByName("Analyse"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_A));
	}

	public void actionPerformed(ActionEvent event) {
		ArchivingThread.getInstance().analyse();
	}

	public String getEnabledTooltipText() {
		return ResourceLoader.getMessage("analyse_enabled");
	}

	public String getDisabledTooltipText() {
		return ResourceLoader.getMessage("analyse_disabled");
	}
}
