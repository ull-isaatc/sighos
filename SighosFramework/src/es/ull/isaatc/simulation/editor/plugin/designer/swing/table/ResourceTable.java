package es.ull.isaatc.simulation.editor.plugin.designer.swing.table;

import javax.swing.table.AbstractTableModel;

import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.AddTableComponentAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.DeleteTableComponentAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.EditTableComponentAction;
import es.ull.isaatc.simulation.editor.project.model.ComponentType;
import es.ull.isaatc.swing.table.LabelCellRenderer;
import es.ull.isaatc.swing.table.TablePanel;

public class ResourceTable extends TablePanel {

	private static final long serialVersionUID = 1L;

	public ResourceTable(AbstractTableModel tableModel) {
		super(tableModel, new AddTableComponentAction(ComponentType.RESOURCE), new EditTableComponentAction(ComponentType.RESOURCE), new DeleteTableComponentAction(ComponentType.RESOURCE), new ResourceTableCellEditor());
		getTable().getColumnModel().getColumn(3).setCellRenderer(new LabelCellRenderer());
	}
}