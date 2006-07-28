package es.ull.isaatc.simulation.editor.plugin.designer.swing.table;

import javax.swing.table.AbstractTableModel;

import es.ull.isaatc.simulation.editor.framework.swing.table.TablePanel;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.AddTableComponentAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.DeleteTableComponentAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.EditTableComponentAction;
import es.ull.isaatc.simulation.editor.project.model.ComponentType;
import es.ull.isaatc.swing.table.LabelCellRenderer;

public class ActivityTable extends TablePanel {

	private static final long serialVersionUID = 1L;

	public ActivityTable(AbstractTableModel tableModel) {
		super(tableModel, new AddTableComponentAction(ComponentType.ACTIVITY), new EditTableComponentAction(ComponentType.ACTIVITY), new DeleteTableComponentAction(ComponentType.ACTIVITY), new ActivityTableCellEditor());
		getTable().getColumnModel().getColumn(4).setCellRenderer(new LabelCellRenderer());
	}
}