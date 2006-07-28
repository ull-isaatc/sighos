package es.ull.isaatc.simulation.editor.plugin.designer.swing;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import es.ull.isaatc.simulation.editor.framework.swing.SighosTabbedPane;
import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;
import es.ull.isaatc.simulation.editor.plugin.designer.SighosDesignerPlugin;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.menu.Palette;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.menu.ToolBarMenu;
import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.project.model.Flow;
import es.ull.isaatc.simulation.editor.project.model.RootFlow;

public class DesignerDesktop extends JPanel implements TableModelListener,
		ListSelectionListener {

	private static final long serialVersionUID = 1L;

	private static HashMap<RootFlow, RootFlowGraphPane> graphs;

	private static JTable rootFlowTable;

	private static DesignerTabbedPane tabbedPane = DesignerTabbedPane
			.getInstance();

	private static Palette palette = Palette.getInstance();

	private static JSplitPane splitPane;
	
	private static DesignerDesktop INSTANCE = null;

	public static DesignerDesktop getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DesignerDesktop();
		}
		return INSTANCE;
	}

	private DesignerDesktop() {
		super();
		graphs = new HashMap<RootFlow, RootFlowGraphPane>();
		initialize();
		ProjectModel.getInstance().getModel().getRootFlowTableModel()
				.addTableModelListener(this);
	}

	public void updateState() {
		rootFlowTable = ((SighosDesignerPlugin) SighosDesignerPlugin
				.getInstance()).getRootFlowTable().getTable();
		if (rootFlowTable != null)
			rootFlowTable.getSelectionModel().addListSelectionListener(this);
	}

	private void initialize() {
		setLayout(new BorderLayout());
		this.add(getToolBar(), BorderLayout.NORTH);
		this.add(getSplitPane(), BorderLayout.CENTER);
	}

	private JToolBar getToolBar() {
		JToolBar toolBar = new ToolBarMenu();
		return toolBar;
	}
	
	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, palette,
					tabbedPane);
			splitPane.setDividerLocation(50);
		}
		return splitPane;
	}

	public void createGraph(RootFlow rf) {
		RootFlowGraphPane rfGraphPane = new RootFlowGraphPane(rf);
		graphs.put(rf, rfGraphPane);
		tabbedPane.add(rfGraphPane);
	}

	public void removeGraph(RootFlow rf) {
		tabbedPane.remove(graphs.get(rf));
	}


	public RootFlowGraph getSelectedRootFlowGraph() {
		return tabbedPane.getSelectedRootFlowGraph();
	}

	public void setSelectedRootFlow(RootFlow rf) {
		if (rf != null)
			tabbedPane.setSelectedComponent(graphs.get(rf));
	}
	
	public boolean validateModel() {
		List<ProblemTableItem> problems = new ArrayList<ProblemTableItem>();
		Iterator<RootFlow> rfIt = graphs.keySet().iterator();
		while (rfIt.hasNext()) {
			RootFlow rf = rfIt.next();
			Flow flow = graphs.get(rf).getGraph().getRootFlowGraphModel().getFlow();
			problems.addAll(graphs.get(rf).getGraph().getRootFlowGraphModel().validate());
			if (problems.size() == 0)  // no errors => continue
				rf.setFlow(flow);			
		}
		SighosTabbedPane.getInstance().getProblemTable().setProblemList(problems);
		return true;
	}
	
	public void reset() {
		tabbedPane.removeAll();
		graphs.clear();
		rootFlowTable = null;
	}
	
	public void tableChanged(TableModelEvent ev) {
		int col = ev.getColumn();
		if (col == 1) { // root flow name changed
			RootFlow rf = ProjectModel.getInstance().getModel()
					.getRootFlowTableModel().get(ev.getFirstRow());
			tabbedPane.setSelectedComponent(graphs.get(rf));
			tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), rf
					.getDescription());
		}
	}

	public void valueChanged(ListSelectionEvent e) {

		if (e.getValueIsAdjusting())
			return;
		if ((e.getSource() == rootFlowTable.getSelectionModel() &&
				rootFlowTable.getSelectedRow() != -1)) {
			RootFlow rf = ProjectModel.getInstance().getModel()
					.getRootFlowTableModel().get(rootFlowTable.getSelectedRow());
			tabbedPane.setSelectedComponent(graphs.get(rf));
		}
	}
}