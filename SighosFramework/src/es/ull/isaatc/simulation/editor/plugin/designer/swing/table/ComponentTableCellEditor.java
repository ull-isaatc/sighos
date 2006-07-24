/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.swing.table;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.table.TableCellEditor;

/**
 * @author Roberto Muñoz
 *
 */
public class ComponentTableCellEditor {

	HashMap<Integer, TableCellEditor> cellEditors = new HashMap<Integer, TableCellEditor>();
	
	public void add(int col, TableCellEditor ce) {
		cellEditors.put(col, ce);
	}
	
	public TableCellEditor get(int col) {
		return cellEditors.get(col);
	}
	
	public Iterator<Integer> iterator() {
		return cellEditors.keySet().iterator();
	}	
}
