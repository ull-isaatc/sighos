/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.swing.util;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.project.model.Activity;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.ActivityTableModel;

/**
 * @author Roberto Muñoz
 * 
 */
public class ActivityComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel comboBoxModel;

	public ActivityComboBox() {
		super();

		comboBoxModel = new DefaultComboBoxModel();
		setModel(comboBoxModel);
	}

	public void setActivity(Activity activity) {
		ActivityTableModel actModel = ProjectModel.getInstance().getModel()
				.getActivityTableModel();
		int last = actModel.getRowCount();
		comboBoxModel.removeAllElements();
		for (int i = 0; i < last; i++) {
			comboBoxModel.addElement(actModel.get(i));
		}
		if (activity != null)
			comboBoxModel.setSelectedItem(activity);
	}
}
