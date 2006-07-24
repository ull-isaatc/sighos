/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.swing.table;


import javax.swing.JTable;

import es.ull.isaatc.simulation.editor.plugin.designer.swing.dialog.TableDialog;
import es.ull.isaatc.simulation.editor.project.model.Resource.TimeTableTableModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;
import es.ull.isaatc.swing.table.TablePanel;

/**
 * @author Roberto Muñoz
 * 
 */
public class TimeTableCellEditor extends ActionTableCellEditor {

	private static final long serialVersionUID = 1L;
	
	TableDialog tableDialog = new TableDialog();

	@Override
	protected void editCell(JTable table, int row, int column) {
		TimeTableTableModel ttModel = (TimeTableTableModel)table.getValueAt(row, column);
		TablePanel tablePanel = new TimeTableEntryTable(ttModel);
		tableDialog.setTable(tablePanel);
		tableDialog.setTitle(ResourceLoader.getMessage("resource_timetable_dialog") + " " + table.getValueAt(row, 1));
		tableDialog.setVisible(true); 
	}

	public Object getCellEditorValue() {
		
		return null;
	} 
}
