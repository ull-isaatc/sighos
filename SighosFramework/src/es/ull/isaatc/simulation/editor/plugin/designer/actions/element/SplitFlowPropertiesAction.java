package es.ull.isaatc.simulation.editor.plugin.designer.actions.element;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.graph.SighosExistingGraphAction;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.SighosCell;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.dialog.SplitFlowPropertiesDialog;
import es.ull.isaatc.simulation.editor.project.model.Flow;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class SplitFlowPropertiesAction extends SighosExistingGraphAction
		implements TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;
	
	private static final SplitFlowPropertiesAction INSTANCE = new SplitFlowPropertiesAction();

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, ResourceLoader.getMessage("flow_properties"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("flow_properties_long"));
		putValue(Action.SMALL_ICON, getIconByName("Blank"));
	}

	public static SplitFlowPropertiesAction getInstance() {
		return INSTANCE;
	}
	
	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			Flow f = ((Flow)(((SighosCell)(graph.getSelectionCell())).getUserObject()));
			SplitFlowPropertiesDialog dialog = SplitFlowPropertiesDialog.getInstance(); 
			dialog.initValues(f.getIterations());
			dialog.setVisible(true);
			if (dialog.getIterations() != null) {
				f.setIterations(dialog.getIterations());
			}
		}
	}

	public String getEnabledTooltipText() {
		return ResourceLoader.getMessage("flow_properties_enabled");
	}

	public String getDisabledTooltipText() {
		return ResourceLoader.getMessage("flow_properties_disabled");
	}
}
