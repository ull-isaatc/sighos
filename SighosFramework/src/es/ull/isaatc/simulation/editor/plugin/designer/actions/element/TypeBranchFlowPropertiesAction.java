package es.ull.isaatc.simulation.editor.plugin.designer.actions.element;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.jgraph.graph.DefaultEdge;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.graph.SighosExistingGraphAction;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.dialog.TypeBranchPropertiesDialog;
import es.ull.isaatc.simulation.editor.project.model.TypeBranchFlow;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class TypeBranchFlowPropertiesAction extends SighosExistingGraphAction
		implements TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;

	private static final TypeBranchFlowPropertiesAction INSTANCE = new TypeBranchFlowPropertiesAction();

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, ResourceLoader
				.getMessage("typebranch_properties"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader
				.getMessage("typebranch_properties_long"));
		putValue(Action.SMALL_ICON, getIconByName("Blank"));
	}

	public static TypeBranchFlowPropertiesAction getInstance() {
		return INSTANCE;
	}

	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			DefaultEdge edge = (DefaultEdge) graph.getSelectionCell();
			TypeBranchPropertiesDialog dialog = TypeBranchPropertiesDialog
					.getInstance();
			dialog.initValues((TypeBranchFlow) edge.getUserObject());
			dialog.setVisible(true);
			if (dialog.isOkay()) {
				edge.setUserObject(dialog.getTBEditionFlow());
			}
		}
	}

	public String getEnabledTooltipText() {
		return ResourceLoader.getMessage("typebranch_properties_enabled");
	}

	public String getDisabledTooltipText() {
		return ResourceLoader.getMessage("typebranch_properties_disabled");
	}
}
