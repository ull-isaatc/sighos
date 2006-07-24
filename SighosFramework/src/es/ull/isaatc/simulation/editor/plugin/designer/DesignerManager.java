/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import es.ull.isaatc.simulation.editor.plugin.designer.swing.DesignerDesktop;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.DesignerTabbedPane;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.dialog.TimeTableEntryDialog;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.dialog.WorkGroupDialog;
import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.project.model.Activity;
import es.ull.isaatc.simulation.editor.project.model.ComponentType;
import es.ull.isaatc.simulation.editor.project.model.ElementType;
import es.ull.isaatc.simulation.editor.project.model.Resource;
import es.ull.isaatc.simulation.editor.project.model.ResourceType;
import es.ull.isaatc.simulation.editor.project.model.RootFlow;
import es.ull.isaatc.simulation.editor.project.model.Activity.WorkGroup;
import es.ull.isaatc.simulation.editor.project.model.Activity.WorkGroupTableModel;
import es.ull.isaatc.simulation.editor.project.model.Resource.TimeTable;
import es.ull.isaatc.simulation.editor.project.model.Resource.TimeTableTableModel;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.ActivityTableModel;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.ElementTypeTableModel;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.ResourceTypeTableModel;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.RootFlowTableModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

/**
 * @author Roberto Muñoz
 * 
 */
public class DesignerManager {

	private static DesignerManager INSTANCE = null;

	public static DesignerManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DesignerManager();
		}
		return INSTANCE;
	}

	public void addTableModelComponent(ComponentType componentType, JTable table) {
		int index = 0;
		switch (componentType) {
		case RESOURCE_TYPE:
			ProjectModel.getInstance().getModel().getResourceTypeTableModel()
					.add(new ResourceType());
			index = ProjectModel.getInstance().getModel()
					.getResourceTypeTableModel().getRowCount() - 1;
			break;
		case RESOURCE:
			ProjectModel.getInstance().getModel().getResourceTableModel().add(
					new Resource());
			index = ProjectModel.getInstance().getModel()
					.getResourceTableModel().getRowCount() - 1;
			break;
		case TIMETABLE:
			TimeTableTableModel ttModel = ((TimeTableTableModel) table
					.getModel());
			TimeTable tt = new TimeTable();
			ttModel.add(tt);
			showTimeTableEntryDialog(ttModel, ttModel.getRowCount() - 1, tt);
			return;
		case ACTIVITY:
			ProjectModel.getInstance().getModel().getActivityTableModel().add(
					new Activity());
			index = ProjectModel.getInstance().getModel()
					.getActivityTableModel().getRowCount() - 1;
			break;
		case WORKGROUP:
			WorkGroupTableModel wgModel = ((WorkGroupTableModel) table
					.getModel());
			WorkGroup wg = new WorkGroup();
			wgModel.add(wg);
			showWorkGroupDialog(wgModel, wgModel.getRowCount() - 1, wg);
			return;
		case ELEMENT_TYPE:
			ProjectModel.getInstance().getModel().getElementTypeTableModel()
					.add(new ElementType());
			index = ProjectModel.getInstance().getModel()
					.getElementTypeTableModel().getRowCount() - 1;
			break;
		case ROOT_FLOW:
			createRootFlow();
			index = ProjectModel.getInstance().getModel()
					.getRootFlowTableModel().getRowCount() - 1;
			break;
		default:
			System.out.println("No implementado aún");
		}
		editTableModelComponent(componentType, table, index);
	}

	public void editTableModelComponent(ComponentType componentType,
			JTable table, int index) {
		switch (componentType) {
		case RESOURCE_TYPE:
			break;
		case RESOURCE:
			break;
		case TIMETABLE:
			showTimeTableEntryDialog(((TimeTableTableModel) table.getModel()),
					index, ((TimeTableTableModel) table.getModel()).get(index));
			return;
		case ACTIVITY:
			break;
		case WORKGROUP:
			showWorkGroupDialog(((WorkGroupTableModel) table.getModel()),
					index, ((WorkGroupTableModel) table.getModel()).get(index));
			return;
		case ELEMENT_TYPE:
			break;
		case ROOT_FLOW:
			break;
		default:
			System.out.println("No implementado aún");
		}

		if (table.editCellAt(index, 1))
			table.changeSelection(index, 1, false, false);

	}

	public void deleteTableModelComponent(ComponentType componentType,
			JTable table, int index) {
		switch (componentType) {
		case RESOURCE_TYPE:
			deleteResourceType(index);
			break;
		case RESOURCE:
			ProjectModel.getInstance().getModel().getResourceTableModel()
					.remove(index);
			break;
		case TIMETABLE:
			((TimeTableTableModel) table.getModel()).remove(index);
			return;
		case ACTIVITY:
			deleteActivity(index);
			break;
		case WORKGROUP:
			((WorkGroupTableModel) table.getModel()).remove(index);
			return;
		case ELEMENT_TYPE:
			deleteElementType(index);
			break;
		case ROOT_FLOW:
			deleteRootFlow(index);
			break;
		default:
			System.out.println("No implementado aún");
		}
		index = (index > 0) ? index - 1 : 0;
		if (table.getRowCount() > 0)
			table.setRowSelectionInterval(index, index);
	}

	private void deleteResourceType(int index) {
		int res = JOptionPane.YES_OPTION;
		ResourceTypeTableModel rtModel = ProjectModel.getInstance().getModel()
				.getResourceTypeTableModel();
		if (rtModel.get(index).hasReferences()) {
			res = JOptionPane.showConfirmDialog(null, ResourceLoader
					.getMessage("resource_type_delete_conflict_message"),
					ResourceLoader.getMessage("resource_type_delete_conflict"),
					JOptionPane.YES_NO_OPTION);
		}
		if (res == JOptionPane.YES_OPTION) {
			rtModel.remove(index);
		}
	}
	
	private void deleteActivity(int index) {
		int res = JOptionPane.YES_OPTION;
		ActivityTableModel actModel = ProjectModel.getInstance().getModel()
				.getActivityTableModel();
		if (actModel.get(index).hasReferences()) {
			res = JOptionPane.showConfirmDialog(null, ResourceLoader
					.getMessage("activity_delete_conflict_message"),
					ResourceLoader.getMessage("activity_delete_conflict"),
					JOptionPane.YES_NO_OPTION);
		}
		if (res == JOptionPane.YES_OPTION) {
			actModel.remove(index);
		}
	}

	private void deleteRootFlow(int index) {
		int res = JOptionPane.YES_OPTION;
		RootFlowTableModel rfModel = ProjectModel.getInstance().getModel()
				.getRootFlowTableModel();
		if (rfModel.get(index).hasReferences()) {
			res = JOptionPane.showConfirmDialog(null, ResourceLoader
					.getMessage("rootflow_delete_conflict_message"),
					ResourceLoader.getMessage("rootflow_delete_conflict"),
					JOptionPane.YES_NO_OPTION);
		}
		if (res == JOptionPane.YES_OPTION) {
			DesignerDesktop.getInstance().removeGraph(rfModel.get(index));
			rfModel.remove(index);
		}
	}

	private void deleteElementType(int index) {
		int res = JOptionPane.YES_OPTION;
		ElementTypeTableModel etModel = ProjectModel.getInstance().getModel()
				.getElementTypeTableModel();
		if (etModel.get(index).hasReferences()) {
			res = JOptionPane.showConfirmDialog(null, ResourceLoader
					.getMessage("element_type_delete_conflict_message"),
					ResourceLoader.getMessage("element_type_delete_conflict"),
					JOptionPane.YES_NO_OPTION);
		}
		if (res == JOptionPane.YES_OPTION) {
			etModel.remove(index);
		}
	}
	
	private void showTimeTableEntryDialog(TimeTableTableModel ttModel,
			int index, TimeTable tt) {
		TimeTableEntryDialog ttDialog = new TimeTableEntryDialog(tt);
		ttDialog.setVisible(true);
		TimeTable editionTT = ttDialog.getTimeTable();
		if (editionTT != null) {
			ttModel.setValueAt(editionTT.getRTList(), index, 0);
			ttModel.setValueAt(editionTT.getCycle(), index, 1);
			ttModel.setValueAt(editionTT.getDuration(), index, 2);
			ttModel.fireTableDataChanged();
		}
	}

	private void showWorkGroupDialog(WorkGroupTableModel wgModel, int index,
			WorkGroup wg) {
		WorkGroupDialog wgDialog = new WorkGroupDialog(wg);
		wgDialog.setVisible(true);
		WorkGroup editionWG = wgDialog.getWorkGroup();
		if (editionWG != null) {
			wgModel.setValueAt(editionWG.getId(), index, 0);
			wgModel.setValueAt(editionWG.getDescription(), index, 1);
			wgModel.setValueAt(editionWG.getResourceType(), index, 2);
			wgModel.setValueAt(editionWG.getPriority(), index, 3);
			wgModel.setValueAt(editionWG.getDuration(), index, 4);
			wgModel.fireTableDataChanged();
		}
	}
	
	public void createRootFlow() {
		RootFlow rf = new RootFlow("New root flow");
		ProjectModel.getInstance().getModel().getRootFlowTableModel().add(rf);
		createRootFlow(rf);
	}
	
	public void createRootFlow(RootFlow rf) {
		DesignerDesktop.getInstance().createGraph(rf);
	}

	public void removeRootFlow(int index) {
	}
	
	public boolean validateModel() {
		DesignerDesktop.getInstance().validateModel();
		return true;
	}
}

