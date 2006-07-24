package es.ull.isaatc.simulation.editor.project.model.tablemodel;

import es.ull.isaatc.simulation.editor.project.model.Activity;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class ActivityTableModel extends ModelComponentTableModel {

	private static final long serialVersionUID = 1L;

	public ActivityTableModel() {
		super();

		columnNames = new String[6];
		columnNames[0] = ResourceLoader.getMessage("component_id");
		columnNames[1] = ResourceLoader.getMessage("component_description");
		columnNames[2] = ResourceLoader.getMessage("activity_priority");
		columnNames[3] = ResourceLoader.getMessage("activity_presential");
		columnNames[4] = ResourceLoader.getMessage("activity_workgroups");
	}

	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			return ((Activity) (elements.get(row))).getId();
		case 1:
			return ((Activity) (elements.get(row))).getDescription();
		case 2:
			return ((Activity) (elements.get(row))).getPriority();
		case 3:
			return ((Activity) (elements.get(row))).isPresencial();
		case 4:
			return ((Activity) (elements.get(row))).getWorkGroupTableModel();
		}
		return null;
	}

	public Activity get(int index) {
		return (Activity) (elements.get(index));
	}

	public boolean isCellEditable(int row, int col) {
		switch (col) {
		case 0: // id
			return false;
		case 1: // description
			return true;
		case 2: // priority
			return true;
		case 3: // presential
			return true;
		case 4: // workgroups
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
		case 2: // priority
			((Activity) elements.get(row)).setPriority((Integer) value);
			break;
		case 3: // presential
			((Activity) elements.get(row)).setPresencial((Boolean) value);
			break;
		}
		fireTableCellUpdated(row, col);
	}

	public void remove(int index) {
		get(index).removeReferences();
		super.remove(index);
	}	
}
