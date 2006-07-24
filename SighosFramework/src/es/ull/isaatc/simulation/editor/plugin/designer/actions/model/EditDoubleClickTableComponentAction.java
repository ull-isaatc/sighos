package es.ull.isaatc.simulation.editor.plugin.designer.actions.model;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import es.ull.isaatc.simulation.editor.plugin.designer.DesignerManager;
import es.ull.isaatc.simulation.editor.project.model.ComponentType;

public class EditDoubleClickTableComponentAction extends
		EditTableComponentAction implements MouseListener {

	private static final long serialVersionUID = 1L;

	public EditDoubleClickTableComponentAction(ComponentType componentType) {
		super(componentType);
	}

	public void actionPerformed(ActionEvent event) {
		DesignerManager.getInstance().editTableModelComponent(componentType,
				table, table.getSelectedRow());
	}

	public void mouseClicked(MouseEvent e) {
		if ((e.getSource() == table) && (e.getClickCount() == 2)) {
			DesignerManager.getInstance().editTableModelComponent(componentType,
					table, table.getSelectedRow());
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
}