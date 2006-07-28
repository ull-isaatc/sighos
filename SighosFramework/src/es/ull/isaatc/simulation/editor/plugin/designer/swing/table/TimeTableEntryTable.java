package es.ull.isaatc.simulation.editor.plugin.designer.swing.table;

import javax.swing.table.AbstractTableModel;

import es.ull.isaatc.simulation.editor.framework.swing.table.TablePanel;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.AddTableComponentAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.DeleteTableComponentAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.EditDoubleClickTableComponentAction;
import es.ull.isaatc.simulation.editor.project.model.ComponentType;

public class TimeTableEntryTable extends TablePanel {

	private static final long serialVersionUID = 1L;

	public TimeTableEntryTable(AbstractTableModel tableModel) {
		super(tableModel, new AddTableComponentAction(ComponentType.TIMETABLE), new EditDoubleClickTableComponentAction(ComponentType.TIMETABLE), new DeleteTableComponentAction(ComponentType.TIMETABLE), null);
		getTable().addMouseListener((EditDoubleClickTableComponentAction)getEditAction());
	}
}