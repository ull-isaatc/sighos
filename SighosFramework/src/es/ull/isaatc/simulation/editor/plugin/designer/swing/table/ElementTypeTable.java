package es.ull.isaatc.simulation.editor.plugin.designer.swing.table;

import javax.swing.table.AbstractTableModel;

import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.AddTableComponentAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.DeleteTableComponentAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.EditTableComponentAction;
import es.ull.isaatc.simulation.editor.project.model.ComponentType;
import es.ull.isaatc.swing.table.TablePanel;

public class ElementTypeTable extends TablePanel {

	private static final long serialVersionUID = 1L;

	public ElementTypeTable(AbstractTableModel tableModel) {
		super(tableModel, new AddTableComponentAction(ComponentType.ELEMENT_TYPE), new EditTableComponentAction(ComponentType.ELEMENT_TYPE), new DeleteTableComponentAction(ComponentType.ELEMENT_TYPE), new ResourceTypeTableCellEditor());
	}
}