package es.ull.isaatc.simulation.editor.framework.actions.project;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import es.ull.isaatc.simulation.editor.framework.swing.SighosFrameworkDesktop;
import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.project.ProjectFileModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class CreateProjectAction extends SighosNoOpenProjectAction implements
		TooltipTogglingWidget {
	private static final long serialVersionUID = 1L;

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, ResourceLoader.getMessage("create"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("create_long"));
		putValue(Action.SMALL_ICON, getIconByName("New"));
		putValue(Action.MNEMONIC_KEY, new Integer(java.awt.event.KeyEvent.VK_C));
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
	}

	public void actionPerformed(ActionEvent event) {
		ProjectFileModel.getInstance().incrementFileCount();
		SighosFrameworkDesktop.getInstance().newProject();
	}

	public String getEnabledTooltipText() {
		return ResourceLoader.getMessage("create_enabled");
	}

	public String getDisabledTooltipText() {
		return ResourceLoader.getMessage("create_disabled");
	}
}
