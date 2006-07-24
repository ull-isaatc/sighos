package es.ull.isaatc.simulation.editor.plugin.designer.swing.menu;

import javax.swing.JPopupMenu;

import es.ull.isaatc.simulation.editor.plugin.designer.actions.element.*;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.graph.DeleteAction;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.PackageCell;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.SighosCell;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.SingleCell;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.GroupSplitCell;


public class CellPopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;

	private RootFlowGraph graph;
	
	private SighosCell cell;
	
	public CellPopupMenu(RootFlowGraph graph, SighosCell cell) {
		super();
		this.graph = graph;
		this.cell = cell;
		addMenuItems();
	}

	private void addMenuItems() {

		if (cell.isRemovable()) {
			add(new SighosPopupMenuItem(DeleteAction.getInstance()));
			addSeparator();
		}
		if (graph.getSelectionCount() != 1) {
			return;
		}
		if (cell instanceof SingleCell) {
			buildSingleFlowPropertiesItem();
		}
		else if (cell instanceof PackageCell) {
			buildPackageFlowPropertiesItem();
		}
		else if (cell instanceof GroupSplitCell) {
			buildSplitFlowPropertiesItem();
		}
	}

	private void buildSingleFlowPropertiesItem() {
		add(new SighosPopupMenuItem(SingleFlowPropertiesAction.getInstance()));
	}
	
	private void buildPackageFlowPropertiesItem() {
		add(new SighosPopupMenuItem(PackageFlowPropertiesAction.getInstance()));
	}

	private void buildSplitFlowPropertiesItem() {
		add(new SighosPopupMenuItem(SplitFlowPropertiesAction.getInstance()));
	}
	
}
