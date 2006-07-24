package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell;

import java.awt.geom.Point2D;

import org.jgraph.JGraph;

public class StartCell extends SighosCell {

	private static final long serialVersionUID = 1L;

	/**
	 * @param graph
	 * @param point
	 */
	public StartCell(JGraph graph, Point2D point) {
		super(graph, point);
	}

	/**
	 * @param userObject
	 * @param graph
	 * @param point
	 */
	public StartCell(Object userObject, JGraph graph, Point2D point) {
		super(userObject, graph, point);
	}
}