package es.ull.isaatc.simulation.editor.plugin.designer.graph;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jgraph.graph.DefaultEdge;

import es.ull.isaatc.simulation.editor.plugin.designer.actions.element.PackageFlowPropertiesAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.element.SingleFlowPropertiesAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.element.SplitFlowPropertiesAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.element.TypeBranchFlowPropertiesAction;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.*;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.DesignerDesktop;
import es.ull.isaatc.simulation.editor.project.model.PackageFlow;
import es.ull.isaatc.simulation.editor.project.model.RootFlow;

/**
 * Listener for double clicks in the root flows
 * @author Roberto Muñoz
 */
public class ElementDoubleClickListener extends MouseAdapter {

	private static final ElementDoubleClickListener INSTANCE = new ElementDoubleClickListener();
	
	public static ElementDoubleClickListener getInstance() {
		return INSTANCE;
	}
	
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() != 2) {
			return;
		}
		RootFlowGraph graph = DesignerDesktop.getInstance().getSelectedRootFlowGraph();
		
		if (graph.getSelectionCount() != 1) {
			return;
		}
		Object selectedCell = graph.getSelectionCell();

		if (selectedCell instanceof SingleCell) {
			doSingleCellDoubleClickProcessing();
		}
		if (selectedCell instanceof PackageCell) {
			doPackageCellDoubleClickProcessing((PackageCell) selectedCell);
		}
		if (selectedCell instanceof GroupSplitCell) {
			doSplitCellDoubleClickProcessing();
		}
		if (selectedCell instanceof DefaultEdge) {  // check if is it an edge from a TypeFlow
			SighosCell parent = graph.getRootFlowGraphModel().getEdgeSource((DefaultEdge)selectedCell);
			if (parent instanceof TypeCell)
				doTypeBranchDoubleClickProcessing();
		}
	}

	/**
	 * Processes a double click over a single cell
	 */
	private void doSingleCellDoubleClickProcessing() {
		SingleFlowPropertiesAction.getInstance().actionPerformed(null);
	}

	/**
	 * Processes a double click over a package cell
	 * @param packageCell the package flow the cell contains 
	 */
	private void doPackageCellDoubleClickProcessing(PackageCell packageCell) {
		RootFlow rf = ((PackageFlow)packageCell.getFlow()).getRootFlow();
		if (rf == null) {
			PackageFlowPropertiesAction.getInstance().actionPerformed(null);
			rf = ((PackageFlow)packageCell.getFlow()).getRootFlow();
		}
		DesignerDesktop.getInstance().setSelectedRootFlow(rf);
	}

	/**
	 * Processes a double click over a group split cell
	 */
	private void doSplitCellDoubleClickProcessing() {
		SplitFlowPropertiesAction.getInstance().actionPerformed(null);
	}
	
	/**
	 * Processes a double click over a type branch edge
	 */
	private void doTypeBranchDoubleClickProcessing() {
		TypeBranchFlowPropertiesAction.getInstance().actionPerformed(null);
	}
}
