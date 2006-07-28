/*
 * Created on 7/05/2004
 * YAWLEditor v1.0 
 *
 * Modified on 26/07/2006
 * SighosFramework v1.0
 * 
 * @author Lindsay Bradford
 * @author Roberto Muñoz
 * 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package es.ull.isaatc.simulation.editor.framework.swing.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class ProblemTable extends JTable {

	private static final long serialVersionUID = 1L;

	static final Font FONT = new Font("Dialog", Font.PLAIN, 12);

	private static final ProblemTableCellRenderer renderer = new ProblemTableCellRenderer();

	{
		setFont(FONT);
	}

	public ProblemTable() {
		this(new MessageTableModel());
	}

	public ProblemTable(TableModel model) {
		super(model);
		initialize();
	}

	public void setEvenRowColor(Color evenRowColor) {
		renderer.setEvenRowColor(evenRowColor);
	}

	public void setOddRowColor(Color oddRowColor) {
		renderer.setOddRowColor(oddRowColor);
	}

	public Color getEvenRowColor() {
		return renderer.getEvenRowColor();
	}

	public Color getOddRowColor() {
		return renderer.getOddRowColor();
	}

	public TableCellRenderer getDefaultRenderer(Class ColumnClass) {
		return renderer;
	}

	private void initialize() {
		this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		this.setSelectionMode(0);
		setMinimumSize(new Dimension((int) getPreferredSize().getWidth(), 0));
		this.setRowHeight(18);
		getColumnModel().getColumn(0).setCellRenderer(new ProblemTableCellRenderer());
		getColumnModel().getColumn(0).setMinWidth(200);
		getColumnModel().getColumn(1).setMaxWidth(200);
		getColumnModel().getColumn(1).setMinWidth(50);
	}

	public void addMessage(ProblemTableItem message) {
		getMessageModel().addMessage(message);
	}

	private MessageTableModel getMessageModel() {
		return (MessageTableModel) getModel();
	}

	public void reset() {
		setModel(new MessageTableModel());
	}

	public int getMessageHeight() {
		return getFontMetrics(getFont()).getHeight();
	}

	static class ProblemTableCellRenderer implements TableCellRenderer {
		
		private static final long serialVersionUID = 1L;
		
		private static final Color DEFAULT_EVEN_ROW_COLOR = Color.WHITE;

		private static final Color DEFAULT_ODD_ROW_COLOR = Color.CYAN;
		
		private static Icon warningIcon = ResourceLoader.getImageAsIcon("/es/ull/isaatc/simulation/editor/framework/resources/warn16.gif");
		
		private static Icon errorIcon = ResourceLoader.getImageAsIcon("/es/ull/isaatc/simulation/editor/framework/resources/error16.gif");

		private Color evenRowColor = DEFAULT_EVEN_ROW_COLOR;

		private Color oddRowColor = DEFAULT_ODD_ROW_COLOR;
		
		private JPanel panel = new JPanel();

		private JLabel label = new JLabel();
		
		private JLabel auxLabel = new JLabel();

		private JLabel icon = new JLabel();

		public ProblemTableCellRenderer() {
			super();
			panel.setLayout(new BorderLayout(10, 0));
			label.setOpaque(false);
			label.setHorizontalAlignment(SwingConstants.LEFT);
			panel.add(icon, BorderLayout.WEST);
			panel.add(label, BorderLayout.CENTER);
			panel.setOpaque(true);
			auxLabel.setOpaque(true);
			auxLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}

		public void setEvenRowColor(Color evenRowColor) {
			this.evenRowColor = evenRowColor;
		}

		public void setOddRowColor(Color oddRowColor) {
			this.oddRowColor = oddRowColor;
		}

		public Color getEvenRowColor() {
			return this.evenRowColor;
		}

		public Color getOddRowColor() {
			return this.oddRowColor;
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			Component c;
			if (column == 0) {
				ProblemTableItem elem = (ProblemTableItem)value;
				label.setText(elem.getDescription());
				switch (elem.getType()) {
				case WARNING : {
					icon.setIcon(warningIcon);
					break;
				}
				case ERROR : {
					icon.setIcon(errorIcon);
					break;
				}
				}
				c = panel;
			}
			else if (column == 1) {
				auxLabel.setText(value.toString());
				c = auxLabel;
			}
			else {
				if ((Integer)value == 0)
					auxLabel.setText("");
				else
					auxLabel.setText(value.toString());
				c = auxLabel;
			}
				
			if (isSelected) {
				c.setForeground(table.getSelectionForeground());
				c.setBackground(table.getSelectionBackground());
			} else {
				c.setForeground(table.getForeground());
				c.setBackground(table.getBackground());
			}
			c.setBackground(row % 2 == 0 ? evenRowColor : oddRowColor);
			return c;
		}
	}

	public static class MessageTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private LinkedList<ProblemTableItem> messages = new LinkedList<ProblemTableItem>();

		private static final String[] COLUMN_LABELS = {
			ResourceLoader.getMessage("problem_description"),
			ResourceLoader.getMessage("problem_component"),
			ResourceLoader.getMessage("problem_component_id")};

		public static final int DESCRIPTION_COLUMN = 0;
		public static final int COMPONENT_COLUMN = 1;
		public static final int ID_COLUMN = 2;

		public int getColumnCount() {
			return COLUMN_LABELS.length;
		}

		public String getColumnName(int columnIndex) {
			return COLUMN_LABELS[columnIndex];
		}

		public int getRowCount() {
			if (messages != null) {
				return messages.size();
			}
			return 0;
		}

		public Object getValueAt(int row, int col) {
			switch (col) {
			case DESCRIPTION_COLUMN: {
				return messages.get(row);
			}
			case COMPONENT_COLUMN: {
				return messages.get(row).getComponent();
			}
			case ID_COLUMN: {
				return messages.get(row).getId();
			}
			}
			return null;
		}

		public void addMessage(ProblemTableItem message) {
			messages.add(message);
			this.fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
		}
	}
}
