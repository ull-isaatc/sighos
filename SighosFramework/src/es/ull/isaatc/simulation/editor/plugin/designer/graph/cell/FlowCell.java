package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell;

import java.awt.geom.Point2D;

import org.jgraph.JGraph;

import es.ull.isaatc.simulation.editor.project.model.Flow;

public abstract class FlowCell extends SighosCell {

	private static final long serialVersionUID = 1L;

	/**
	 * @param graph
	 * @param point
	 */
	public FlowCell(JGraph graph, Point2D point) {
		super(graph, point);
	}

	/**
	 * @param userObject
	 * @param graph
	 * @param point
	 */
	public FlowCell(Object userObject, JGraph graph, Point2D point) {
		super(userObject, graph, point);
	}

	/**
	 * @return the flow
	 */
	public Flow getFlow() {
		return (Flow)getUserObject();
	}

	/**
	 * @param flow the flow to set
	 */
	public void setFlow(Flow flow) {
		setUserObject(flow);
	}
}