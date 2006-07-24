package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell;

import java.awt.geom.Point2D;

import org.jgraph.JGraph;

public abstract class BranchCell extends FlowCell {

	private static final long serialVersionUID = 1L;
	
	private BranchCell pairCell;

	/**
	 * @param graph
	 * @param point
	 */
	public BranchCell(JGraph graph, Point2D point) {
		super(graph, point);
	}

	/**
	 * @param userObject
	 * @param graph
	 * @param point
	 */
	public BranchCell(Object userObject, JGraph graph, Point2D point) {
		super(userObject, graph, point);
	}
	
	public String toString() {
		return null;
	}

	/**
	 * @return the pairCell
	 */
	public BranchCell getPairCell() {
		return pairCell;
	}

	/**
	 * @param pairCell the pairCell to set
	 */
	public void setPairCell(BranchCell pairCell) {
		this.pairCell = pairCell;
	}
}