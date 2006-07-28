/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.swing.table;


import javax.swing.JTable;

import es.ull.isaatc.simulation.editor.framework.swing.table.TablePanel;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.dialog.TableDialog;
import es.ull.isaatc.simulation.editor.project.model.Activity.WorkGroupTableModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

/**
 * @author Roberto Muñoz
 * 
 */
public class WorkGroupCellEditor extends ActionTableCellEditor {

	private static final long serialVersionUID = 1L;
	
	TableDialog tableDialog = new TableDialog();

	@Override
	protected void editCell(JTable table, int row, int column) {
		WorkGroupTableModel wgModel = (WorkGroupTableModel)table.getValueAt(row, column);
		TablePanel tablePanel = new WorkGroupTable(wgModel);
		tableDialog.setTable(tablePanel);
		tableDialog.setTitle(ResourceLoader.getMessage("activity_workgrouptable_dialog") + " " + table.getValueAt(row, 1));
		tableDialog.setBounds(0, 0, 800, 300);
		tableDialog.setVisible(true); 
	}

	public Object getCellEditorValue() {
		
		return null;
	} 
}
