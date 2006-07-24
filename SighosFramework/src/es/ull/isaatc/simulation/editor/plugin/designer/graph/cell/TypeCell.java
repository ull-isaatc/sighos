package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell;

import java.awt.geom.Point2D;

import org.jgraph.JGraph;

public class TypeCell extends BranchCell {

	private static final long serialVersionUID = 1L;

	/**
	 * @param graph
	 * @param point
	 */
	public TypeCell(JGraph graph, Point2D point) {
		super(graph, point);
		Point2D ppoint = (Point2D) point.clone();
		ppoint.setLocation(point.getX(), point.getY() + 50);
		setPairCell(new TypeJoinCell(graph, ppoint, this));
	}
}