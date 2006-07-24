package es.ull.isaatc.simulation.editor.plugin.designer.actions.model;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.event.ListSelectionEvent;

import es.ull.isaatc.simulation.editor.plugin.designer.DesignerManager;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.TableComponentAction;
import es.ull.isaatc.simulation.editor.project.model.ComponentType;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class DeleteTableComponentAction extends TableComponentAction {

	private static final long serialVersionUID = 1L;

	public DeleteTableComponentAction(ComponentType componentType) {
		super(componentType);
		putValue(Action.SHORT_DESCRIPTION, ResourceLoader.getMessage("table_delete"));
		putValue(Action.NAME, ResourceLoader.getMessage("table_delete"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("table_delete"));
		setEnabled(false);
	}

	public void actionPerformed(ActionEvent event) {
		DesignerManager.getInstance().deleteTableModelComponent(componentType, table, table.getSelectedRow());
	}

	public void valueChanged(ListSelectionEvent e) {
		if (table.getSelectedRow() == -1)
			setEnabled(false);
		else
			setEnabled(true);
	}
	
	public String getEnabledTooltipText() {
		return ResourceLoader.getMessage("table_delete");
	}

	public String getDisabledTooltipText() {
		return ResourceLoader.getMessage("table_delete");
	}
}