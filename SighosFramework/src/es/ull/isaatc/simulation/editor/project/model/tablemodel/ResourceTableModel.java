package es.ull.isaatc.simulation.editor.project.model.tablemodel;

import es.ull.isaatc.simulation.editor.project.model.Resource;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class ResourceTableModel extends ModelComponentTableModel {

	private static final long serialVersionUID = 1L;

	public ResourceTableModel() {
		super();

		columnNames = new String[5];
		columnNames[0] = ResourceLoader.getMessage("component_id");
		columnNames[1] = ResourceLoader.getMessage("component_description");
		columnNames[2] = ResourceLoader.getMessage("resource_nelem");
		columnNames[3] = ResourceLoader.getMessage("resource_timetable");
	}

	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			return elements.get(row).getId();
		case 1:
			return elements.get(row).getDescription();
		case 2:
			return ((Resource) elements.get(row)).getNelem();
		case 3:
			return ((Resource) elements.get(row)).getTimeTableTableModel();
		}
		return null;
	}

	public Resource get(int index) {
		return (Resource) (elements.get(index));
	}

	public boolean isCellEditable(int row, int col) {
		switch (col) {
		case 0: // id
			return false;
		case 1: // description
			return true;
		case 2: // nelem
			return true;
		case 3: // time table entries
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
		case 2: // nelem
			((Resource) elements.get(row)).setNelem((Integer) value);
			// modify the next ids
			for (int i = row + 1; i < getRowCount(); i++) {
				Integer id = (Integer)getValueAt(i - 1, 0);
				Integer nelem = (Integer)getValueAt(i - 1, 2);
				if (nelem == 0)
					nelem = 1;
				id += nelem; 
				setValueAt(id, i, 0);
			}
			fireTableDataChanged();
			break;
		}
		fireTableCellUpdated(row, col);
	}
}
