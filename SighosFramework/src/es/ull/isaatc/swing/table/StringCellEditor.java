/**
 * 
 */
package es.ull.isaatc.swing.table;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * @author Roberto Muñoz
 *
 */
public class StringCellEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;

	/** This is the textField that will handle the editing of the cell value */
	JTextField tf;

	public static JTextField getTextField() {
		JTextField component = new JTextField();
		component.setBorder(BorderFactory.createEmptyBorder());
		return component;
		
	}
	
	public StringCellEditor() {
		super(getTextField());
		tf = (JTextField)getComponent();	
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int rowIndex, int vColIndex) {
		tf.setText((String) value);
		return tf;
	}

	public Object getCellEditorValue() {
		return tf.getText();
	}

	public void setValue(Object value) {
		if (value == null) {
			tf.setText("");
		} else {
			tf.setText(value.toString());
		}
	}
}
