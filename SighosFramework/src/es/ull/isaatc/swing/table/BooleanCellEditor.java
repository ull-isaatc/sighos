/**
 * 
 */
package es.ull.isaatc.swing.table;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 * @author Roberto Muñoz
 *
 */
public class BooleanCellEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;

	/** This is the combobox that will handle the editing of the cell value */
	JComboBox cb;

	public static JComboBox getComboBox() {
		Boolean values[] = new Boolean[2];
		values[0] = true;
		values[1] = false;
		JComboBox component = new JComboBox(values);
		component.setBorder(BorderFactory.createEmptyBorder());
		return component;
		
	}
	
	public BooleanCellEditor() {
		super(getComboBox());
		cb = (JComboBox)getComponent();	
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int rowIndex, int vColIndex) {
		cb.setSelectedItem(value);
		return cb;
	}

	public Object getCellEditorValue() {
		return cb.getSelectedItem();
	}

	public void setValue(Object value) {
		if (value == null) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedItem(value);
		}
	}
}
