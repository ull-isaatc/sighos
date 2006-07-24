/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.swing.util;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.project.model.RootFlow;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.RootFlowTableModel;

/**
 * @author Roberto Muñoz
 * 
 */
public class RootFlowComboBox extends JComboBox {

	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel comboBoxModel;

	public RootFlowComboBox() {
		super();

		comboBoxModel = new DefaultComboBoxModel();
		setModel(comboBoxModel);
	}

	public void setRootFlow(RootFlow rootFlow) {
		RootFlowTableModel rfModel = ProjectModel.getInstance().getModel()
				.getRootFlowTableModel();
		int last = rfModel.getRowCount();
		comboBoxModel.removeAllElements();
		for (int i = 0; i < last; i++) {
			comboBoxModel.addElement(rfModel.get(i));
		}
		if (rootFlow != null)
			comboBoxModel.setSelectedItem(rootFlow);
	}
}
