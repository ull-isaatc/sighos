/**
 * 
 */
package es.ull.isaatc.swing.table;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;

import es.ull.isaatc.swing.IntegerTextField;

/**
 * @author Roberto Muñoz
 *
 */
public class IntegerCellEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;

	/** This is the textField that will handle the editing of the cell value */
	IntegerTextField tf;

	public static IntegerTextField getTextField() {
		IntegerTextField component = new IntegerTextField();
		component.setBorder(BorderFactory.createEmptyBorder());
		return component;
		
	}
	
	public IntegerCellEditor() {
		super(getTextField());
		tf = (IntegerTextField)getComponent();	
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int rowIndex, int vColIndex) {
		tf.setText(value.toString());
		return tf;
	}

	public Object getCellEditorValue() {
		try {
			Integer nelem = Integer.parseInt(tf.getText());
			return nelem;
		} catch (NumberFormatException e) {
			return new Integer(0);
		}
	}

	public void setValue(Object value) {
		if (value == null) {
			tf.setText("0");
		} else {
			tf.setText(value.toString());
		}
	}
}
