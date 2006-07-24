/**
 * 
 */
package es.ull.isaatc.swing.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 * @author Roberto Muñoz
 *
 */
public class LabelCellRenderer extends JPanel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	private JLabel label = new JLabel();

	private JButton customEditorButton = new JButton("...");

	public LabelCellRenderer() {
		super();
		setLayout(new BorderLayout());
		label.setOpaque(false);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		customEditorButton.setMargin(new Insets(0, 0, 0, 0));
		add(label);
		add(customEditorButton, BorderLayout.EAST);
		setBackground(Color.WHITE);
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (value == null)
			label.setText("");
		else
			label.setText(value.toString());
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}
		return this;
	}
}
