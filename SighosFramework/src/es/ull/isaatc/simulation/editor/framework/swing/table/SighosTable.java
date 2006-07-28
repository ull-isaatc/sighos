/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.swing.table;

import java.awt.Font;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import es.ull.isaatc.simulation.editor.plugin.designer.swing.table.ComponentTableCellEditor;

/**
 * @author Roberto Muñoz
 *
 */
public class SighosTable extends JTable {

	private static final long serialVersionUID = 1L;

	private ComponentTableCellEditor tableCellEditor;
	
	public SighosTable(TableModel tableModel, ComponentTableCellEditor tableCellEditor) {
		super(tableModel);
		
		this.tableCellEditor = tableCellEditor;
		
		setSelectionMode(0);
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		setFont(new Font("Dialog", Font.PLAIN, 12));
		loadCellEditors();
	}

	private void loadCellEditors() {
		if (tableCellEditor != null) {
			Iterator<Integer> it = tableCellEditor.iterator();
			while (it.hasNext()) {
				int col = it.next();
				getColumnModel().getColumn(col).setCellEditor(tableCellEditor.get(col));
			}
		}
	}

	public boolean getScrollableTracksViewportHeight() { 
        return getPreferredSize().height < getParent().getHeight(); 
    }
}
