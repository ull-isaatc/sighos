package es.ull.isaatc.simulation.editor.plugin.designer.swing.table;

import javax.swing.table.AbstractTableModel;

import es.ull.isaatc.simulation.editor.plugin.designer.DesignerManager;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.AddTableComponentAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.DeleteTableComponentAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.EditTableComponentAction;
import es.ull.isaatc.simulation.editor.project.model.ComponentType;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.RootFlowTableModel;
import es.ull.isaatc.swing.table.TablePanel;

public class RootFlowTable extends TablePanel {

	private static final long serialVersionUID = 1L;

	public RootFlowTable(AbstractTableModel tableModel) {
		super(tableModel, new AddTableComponentAction(ComponentType.ROOT_FLOW), new EditTableComponentAction(ComponentType.ROOT_FLOW), new DeleteTableComponentAction(ComponentType.ROOT_FLOW), new RootFlowTableCellEditor());
		initGraphs((RootFlowTableModel) tableModel);
	}
	
	private void initGraphs(RootFlowTableModel tableModel) {
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			DesignerManager.getInstance().createRootFlow(tableModel.get(i));
		}
	}
}