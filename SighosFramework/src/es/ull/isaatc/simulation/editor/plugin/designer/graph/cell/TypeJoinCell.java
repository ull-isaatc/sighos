package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell;

import java.awt.geom.Point2D;

import org.jgraph.JGraph;

public class TypeJoinCell extends BranchCell {

	private static final long serialVersionUID = 1L;

	/**
	 * @param graph
	 * @param point
	 * @param pairCell
	 */
	public TypeJoinCell(JGraph graph, Point2D point, TypeCell pairCell) {
		super(graph, point);
		setPairCell(pairCell);
	}
}