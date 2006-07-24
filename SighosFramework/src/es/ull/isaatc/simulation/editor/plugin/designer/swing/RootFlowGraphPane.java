package es.ull.isaatc.simulation.editor.plugin.designer.swing;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.project.model.RootFlow;

public class RootFlowGraphPane extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private RootFlow rf;
	
	private RootFlowGraph graph;
	
	private JScrollPane scrollPane;

	public RootFlowGraphPane(RootFlow rf) {
		super();

		this.rf = rf;
		setLayout(new BorderLayout());
	
		RootFlowGraph newGraph = new RootFlowGraph();
		// the content of the root flow is loaded
		newGraph.buildNewGraphContent(rf.getFlow());
		
		setGraph(newGraph);
	}

	public RootFlowGraph getGraph() {
		return graph;
	}

	private void setGraph(RootFlowGraph graph) {
		this.graph = graph;
		scrollPane = new JScrollPane(graph);
		scrollPane.getViewport().setSize(this.getSize());
		graph.setSize(scrollPane.getViewport().getSize());
		add(scrollPane, BorderLayout.CENTER);
	}

	public RootFlow getRootFlow() {
		return rf;
	}
	
	public String getName() {
		return rf.getDescription();
	}
}
