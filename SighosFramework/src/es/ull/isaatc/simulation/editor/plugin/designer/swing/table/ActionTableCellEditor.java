/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.swing.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * @author Roberto
 *
 */
public abstract class ActionTableCellEditor extends AbstractCellEditor
		implements TableCellEditor, ActionListener {

	private JPanel panel = new JPanel(new BorderLayout());

	private JLabel label = new JLabel();

	private JButton customEditorButton = new JButton("...");

	protected JTable table;

	protected int row, column;

	public ActionTableCellEditor() {
		label.setBackground(Color.WHITE);
		label.setOpaque(true);
		customEditorButton.addActionListener(this);

		// ui-tweaking 
		customEditorButton.setFocusable(false);
		customEditorButton.setFocusPainted(false);
		customEditorButton.setMargin(new Insets(0, 0, 0, 0));
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		label.setText(value.toString());
		panel.add(label);
		panel.add(customEditorButton, BorderLayout.EAST);
		this.table = table;
		this.row = row;
		this.column = column;
		return panel;
	}

	public final void actionPerformed(ActionEvent e) {
		super.cancelCellEditing();
		editCell(table, row, column);
	}

	protected abstract void editCell(JTable table, int row, int column);

}