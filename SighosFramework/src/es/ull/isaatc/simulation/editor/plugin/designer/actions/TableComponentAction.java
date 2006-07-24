package es.ull.isaatc.simulation.editor.plugin.designer.actions;

import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;

import es.ull.isaatc.simulation.editor.framework.actions.project.SighosOpenProjectAction;
import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.project.model.ComponentType;

public abstract class TableComponentAction extends SighosOpenProjectAction implements
		TooltipTogglingWidget, ListSelectionListener {
	private static final long serialVersionUID = 1L;

	protected ComponentType componentType;
	
	protected JTable table;

	public TableComponentAction(ComponentType componentType) {
		this.componentType = componentType;
	}
	
	public void setTable(JTable table) {
		this.table = table;
		table.getSelectionModel().addListSelectionListener(this);
	}

	public String getEnabledTooltipText() {
		return " Edit the table components ";
	}
	
	public String getDisabledTooltipText() {
		return " You must have an open project to edit the table ";
	}	
}