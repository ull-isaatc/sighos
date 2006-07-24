package es.ull.isaatc.simulation.editor.plugin.designer.actions.graph;

import javax.swing.ImageIcon;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import es.ull.isaatc.simulation.editor.framework.actions.SighosBaseAction;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.DesignerDesktop;
import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class SighosExistingGraphAction extends SighosBaseAction implements
		TableModelListener {

	private static final long serialVersionUID = 1L;

	{
		ProjectModel.getInstance().getModel().getRootFlowTableModel().addTableModelListener(this);		
	}

	protected ImageIcon getIconByName(String iconName) {
		return ResourceLoader
				.getImageAsIcon("/es/ull/isaatc/simulation/editor/plugin/designer/resources/menuicons/"
						+ iconName + "16.gif");
	}

	public void tableChanged(TableModelEvent ev) {
		int rc = ProjectModel.getInstance().getModel().getRootFlowTableModel().getRowCount();
		if (rc > 0)
			setEnabled(true);
		else
			setEnabled(false);
	}
	
	protected RootFlowGraph getRootFlowGraph() {
		return DesignerDesktop.getInstance().getSelectedRootFlowGraph();
	}
}
