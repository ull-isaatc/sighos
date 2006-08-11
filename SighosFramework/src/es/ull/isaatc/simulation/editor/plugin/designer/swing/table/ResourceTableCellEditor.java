/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.swing.table;

import es.ull.isaatc.swing.table.IntegerCellEditor;
import es.ull.isaatc.swing.table.StringCellEditor;

public class ResourceTableCellEditor extends ComponentTableCellEditor {

	{
		add(1, new StringCellEditor());
		add(2, new IntegerCellEditor());
		add(3, new TimeTableCellEditor());
	}

}