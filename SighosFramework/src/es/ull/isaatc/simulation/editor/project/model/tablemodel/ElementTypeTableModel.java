package es.ull.isaatc.simulation.editor.project.model.tablemodel;

import es.ull.isaatc.simulation.editor.project.model.ElementType;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class ElementTypeTableModel extends ModelComponentTableModel {

	private static final long serialVersionUID = 1L;

	public ElementTypeTableModel() {
		super();

		columnNames = new String[3];
		columnNames[0] = ResourceLoader.getMessage("component_id");
		columnNames[1] = ResourceLoader.getMessage("component_description");
	}

	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			return new Integer(elements.get(row).getId());
		case 1:
			return elements.get(row).getDescription();
		}
		return null;
	}

	public ElementType get(int index) {
		return (ElementType) (elements.get(index));
	}

	public boolean isCellEditable(int row, int col) {
		switch (col) {
		case 0: // id
			return false;
		case 1: // description
			return true;
		default:
			return false;
		}
	}

	public void setValueAt(Object value, int row, int col) {
		switch (col) {
		case 0: // id
			elements.get(row).setId((Integer) value);
			break;
		case 1: // description
			elements.get(row).setDescription(value.toString());
			break;
		}
		fireTableCellUpdated(row, col);
	}

	public void remove(int index) {
		get(index).removeReferences();
		super.remove(index);
	}
}
