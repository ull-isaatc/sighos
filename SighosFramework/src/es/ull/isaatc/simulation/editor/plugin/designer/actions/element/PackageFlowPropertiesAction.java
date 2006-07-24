package es.ull.isaatc.simulation.editor.plugin.designer.actions.element;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.swing.TooltipTogglingWidget;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.graph.SighosExistingGraphAction;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.SighosCell;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.dialog.PackageFlowPropertiesDialog;
import es.ull.isaatc.simulation.editor.project.model.PackageFlow;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class PackageFlowPropertiesAction extends SighosExistingGraphAction
		implements TooltipTogglingWidget {

	private static final long serialVersionUID = 1L;
	
	private static final PackageFlowPropertiesAction INSTANCE = new PackageFlowPropertiesAction();

	{
		putValue(Action.SHORT_DESCRIPTION, getDisabledTooltipText());
		putValue(Action.NAME, ResourceLoader.getMessage("flow_properties"));
		putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("flow_properties_long"));
		putValue(Action.SMALL_ICON, getIconByName("Blank"));
	}

	public static PackageFlowPropertiesAction getInstance() {
		return INSTANCE;
	}
	
	public void actionPerformed(ActionEvent event) {
		final RootFlowGraph graph = getRootFlowGraph();
		if (graph != null) {
			PackageFlow f = ((PackageFlow)(((SighosCell)(graph.getSelectionCell())).getUserObject()));
			PackageFlowPropertiesDialog dialog = PackageFlowPropertiesDialog.getInstance(); 
			dialog.initValues(f.getIterations(), f.getRootFlow());
			dialog.setVisible(true);
			if (dialog.getIterations() != null) {
				f.setIterations(dialog.getIterations());
				f.setRootFlow(dialog.getRootFlow(), false);
				SighosCell cell = (SighosCell)graph.getSelectionCell();
				graph.getGraphLayoutCache().editCell(cell, cell.getAttributes());
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
