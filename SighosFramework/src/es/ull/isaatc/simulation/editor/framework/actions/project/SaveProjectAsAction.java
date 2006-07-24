package es.ull.isaatc.simulation.editor.framework.actions.project;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.project.ArchivingThread;

public class SaveProjectAsAction extends SighosOpenProjectAction implements
		TooltipTogglingWidget {
	private static final long serialVersionUID = 1L;

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, "Save Project As...");
		putValue(Action.LONG_DESCRIPTION, "Save this project to a different filename ");
		putValue(Action.SMALL_ICON, getIconByName("SaveAs"));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift S"));
	}

	public void actionPerformed(ActionEvent event) {
		ArchivingThread.getInstance().saveAs();
	}

	public String getEnabledTooltipText() {
		return " Save this project to a different filename";
	}

	public String getDisabledTooltipText() {
		return " You must have an open project to save it to a different filename ";
	}
}
