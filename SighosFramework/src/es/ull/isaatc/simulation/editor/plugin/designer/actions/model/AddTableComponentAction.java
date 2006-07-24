package es.ull.isaatc.simulation.editor.plugin.designer.actions.model;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.event.ListSelectionEvent;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.DesignerManager;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.TableComponentAction;
import es.ull.isaatc.simulation.editor.project.model.ComponentType;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class AddTableComponentAction extends TableComponentAction implements
		TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	public AddTableComponentAction(ComponentType componentType) {
		super(componentType);
		putValue(Action.SHORT_DESCRIPTION, ResourceLoader.getMessage("table_add"));
		putValue(Action.NAME, ResourceLoader.getMessage("table_add"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("table_add"));
	}

	public void actionPerformed(ActionEvent event) {
		DesignerManager.getInstance().addTableModelComponent(componentType,
				table);
	}

	public void valueChanged(ListSelectionEvent e) {
	}
	
	public String getEnabledTooltipText() {
		return ResourceLoader.getMessage("table_add");
	}

	public String getDisabledTooltipText() {
		return ResourceLoader.getMessage("table_add");
	}
	
	
}