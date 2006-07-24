/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JComponent;

import es.ull.isaatc.simulation.editor.framework.actions.project.SighosOpenProjectAction;
import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.project.ArchivingThread;
import es.ull.isaatc.simulation.editor.project.ProjectFileModel;

/**
 * 
 * @author Roberto Muñoz
 */
public class ExitAction extends SighosOpenProjectAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	{
		putValue(Action.SHORT_DESCRIPTION, getEnabledTooltipText());
		putValue(Action.NAME, "Exit");
		putValue(Action.LONG_DESCRIPTION, "Exit the application.");
		putValue(Action.SMALL_ICON, getIconByName("Blank"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_X));
	}

	public ExitAction(JComponent menu) {
	}

	public void actionPerformed(ActionEvent event) {
		ArchivingThread.getInstance().exit();
	}

	public void projectFileModelStateChanged(int state) {
		switch (state) {
		case ProjectFileModel.BUSY: {
			setEnabled(false);
			break;
		}
		default: {
			setEnabled(true);
		}
		}
	}

	public String getEnabledTooltipText() {
		return " Exit the application ";
	}

	public String getDisabledTooltipText() {
		return " You cannot exit the application until there the current file operation completes ";
	}
}
