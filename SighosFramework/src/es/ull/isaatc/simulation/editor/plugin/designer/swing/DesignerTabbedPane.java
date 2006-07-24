package es.ull.isaatc.simulation.editor.plugin.designer.swing;

import java.awt.event.MouseEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.menu.Palette;
import es.ull.isaatc.swing.tab.CloseListener;
import es.ull.isaatc.swing.tab.CloseTabbedPane;

public class DesignerTabbedPane extends CloseTabbedPane implements ChangeListener {

	private static final long serialVersionUID = 1L;
	
	public static int NONE_GRAPH_SELECTED = 0;
	
	public static int SOME_GRAPH_SELECTED = 1; 
	
	private static DesignerTabbedPane INSTANCE = null;

	public static DesignerTabbedPane getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DesignerTabbedPane();
		}
		return INSTANCE;
	}

	private DesignerTabbedPane() {
		super();
		addChangeListener(this);
		addCloseListener(new CloseListener() {
			public void closeOperation(MouseEvent e, int overTabIndex) {
				DesignerTabbedPane designerDesktop = DesignerTabbedPane.this;
				designerDesktop.remove(overTabIndex);
			}
		});
	}
	
	public void setSelectedComponent(RootFlowGraphPane rfGraphPane) {
		try {
			super.setSelectedComponent(rfGraphPane);
		}
		catch (IllegalArgumentException ex) {
			add(rfGraphPane);
			super.setSelectedComponent(rfGraphPane);
		}
	}
	
	public RootFlowGraph getSelectedRootFlowGraph() {
		return ((RootFlowGraphPane)getSelectedComponent()).getGraph();
	}
	
	public void stateChanged(ChangeEvent e) {
		if (getSelectedIndex() != -1)
			Palette.getInstance().setEnabled(true);
		else
			Palette.getInstance().setEnabled(false);
	}
}
