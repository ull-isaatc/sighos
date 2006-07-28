package es.ull.isaatc.simulation.editor.plugin.designer.swing.menu;

import javax.swing.JPopupMenu;

import es.ull.isaatc.simulation.editor.plugin.designer.actions.palette.*;

public class PalettePopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;

	public PalettePopupMenu() {
		super();
		addMenuItems();
	}

	private void addMenuItems() {
		add(new SingleFlowAction());
		add(new PackageFlowAction());
		add(new ExitFlowAction());
		add(new GroupSplitFlowAction());
		add(new JoinFlowAction());
		add(new DecisionFlowAction());
		add(new TypeFlowAction());
		add(new FlowConnectionAction());
		add(new SelectionAction());
	}
}
