/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.swing.util;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.ResourceTypeTableModel;

/**
 * @author Roberto
 * 
 */
public class ResourceTypeComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel comboBoxModel;

	public ResourceTypeComboBox() {
		super();

		comboBoxModel = new DefaultComboBoxModel();
		ResourceTypeTableModel rtModel = ProjectModel.getInstance().getModel()
				.getResourceTypeTableModel();
		int last = rtModel.getRowCount();
		for (int i = 0; i < last; i++) {
			comboBoxModel.addElement(rtModel.get(i));
		}
		setModel(comboBoxModel);
	}
}
