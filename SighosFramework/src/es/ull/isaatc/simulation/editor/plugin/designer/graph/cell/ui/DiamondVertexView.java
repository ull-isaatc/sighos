package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Point2D;

import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

/**
 * Creates a diamond shaped graph cell. Correctly calculates perimeter, and
 * manage a shape with different height and width parameters.
 * 
 */
public class DiamondVertexView extends VertexView {

	private static final long serialVersionUID = 1L;

	public static transient DiamondRenderer renderer = new DiamondRenderer();

	public DiamondVertexView() {
		super();
	}

	public DiamondVertexView(Object cell) {
		super(cell);
	}

	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		Point2D center = AbstractCellView.getCenterPoint(this);
		double halfwidth = getBounds().getWidth() / 2;
		double halfheight = getBounds().getHeight() / 2;
		Point2D top = new Point2D.Double(center.getX(), center.getY()
				- halfheight);
		Point2D bottom = new Point2D.Double(center.getX(), center.getY()
				+ halfheight);
		Point2D left = new Point2D.Double(center.getX() - halfwidth, center
				.getY());
		Point2D right = new Point2D.Double(center.getX() + halfwidth, center
				.getY());
		// Special case for intersecting the diamond's points
		if (center.getX() == p.getX()) {
			if (center.getY() > p.getY()) // top point
				return (top);
			return bottom;
		}
		if (center.getY() == p.getY()) {
			if (center.getX() > p.getX()) // left point
				return (left);
			// right point
			return right;
		}
		// In which quadrant will the intersection be?
		// set the slope and offset of the border line accordingly
		Point2D i;
		if (p.getX() < center.getX())
			if (p.getY() < center.getY())
				i = intersection(p, center, top, left);
			else
				i = intersection(p, center, bottom, left);
		else if (p.getY() < center.getY())
			i = intersection(p, center, top, right);
		else
			i = intersection(p, center, bottom, right);
		return i;
	}

	/**
	 * Find the point of intersection of two straight lines (which follow the
	 * equation y=mx+b) one line is an incoming edge and the other is one side
	 * of the diamond.
	 */
	private Point2D intersection(Point2D lineOneStart, Point2D lineOneEnd,
			Point2D lineTwoStart, Point2D lineTwoEnd) {
		double m1 = (lineOneEnd.getY() - lineOneStart.getY())
				/ (lineOneEnd.getX() - lineOneStart.getX());
		double b1 = lineOneStart.getY() - m1 * lineOneStart.getX();
		double m2 = (lineTwoEnd.getY() - lineTwoStart.getY())
				/ (lineTwoEnd.getX() - lineTwoStart.getX());
		double b2 = lineTwoStart.getY() - m2 * lineTwoStart.getX();
		double xinter = (b1 - b2) / (m2 - m1);
		double yinter = m1 * xinter + b1;
		Point2D intersection = getAttributes().createPoint(xinter, yinter);
		return intersection;
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static class DiamondRenderer extends SighosVertexRenderer {

		private static final long serialVersionUID = 1L;

		protected int width;

		protected int height;

		protected int halfWidth;

		protected int halfHeight;

		protected Polygon getPolygon(Dimension size) {
			width = size.width;
			height = size.height;
			halfWidth = Math.round(size.width / 2);
			halfHeight = Math.round(size.height / 2);
			int[] xpoints = { halfWidth, width, halfWidth, 0 };
			int[] ypoints = { 0, halfHeight, height, halfHeight };
			Polygon diamond = new Polygon(xpoints, ypoints, 4);
			return diamond;
		}

		@Override
		protected void fillVertex(Graphics graphics, Dimension size) {
			graphics.fillPolygon(getPolygon(size));
		}

		@Override
		protected void drawVertex(Graphics graphics, Dimension size) {
			graphics.drawPolygon(getPolygon(size));
		}
	}
}
