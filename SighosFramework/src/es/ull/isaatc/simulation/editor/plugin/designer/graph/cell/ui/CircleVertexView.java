package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

public class CircleVertexView extends VertexView {
	
	private static final long serialVersionUID = 1L;

	public static transient CircleVertexRenderer renderer = new CircleVertexRenderer();

	public CircleVertexView() {
		super();
	}

	public CircleVertexView(Object cell) {
		super(cell);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Returns the intersection of the bounding rectangle and the
	 * straight line between the source and the specified point p.
	 * The specified point is expected not to intersect the bounds.
	 */
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		Rectangle2D r = getBounds();

		double x = r.getX();
		double y = r.getY();
		double a = (r.getWidth() + 1) / 2;
		double b = (r.getHeight() + 1) / 2;

		// x0,y0 - center of ellipse
		double x0 = x + a;
		double y0 = y + b;

		// x1, y1 - point
		double x1 = p.getX();
		double y1 = p.getY();

		// calculate straight line equation through point and ellipse center
		double dx = x1 - x0;
		double dy = y1 - y0;

		if (dx == 0)
			return new Point((int) x0, (int) (y0 + b * dy / Math.abs(dy)));

		double d = dy / dx;
		double h = y0 - d * x0;

		// calculate intersection
		double e = a * a * d * d + b * b;
		double f = -2 * x0 * e;
		double g = a * a * d * d * x0 * x0 + b * b * x0 * x0 - a * a * b * b;

		double det = Math.sqrt(f * f - 4 * e * g);

		// two solutions (perimeter points)
		double xout1 = (-f + det) / (2 * e);
		double xout2 = (-f - det) / (2 * e);
		double yout1 = d * xout1 + h;
		double yout2 = d * xout2 + h;

		double dist1Squared = Math.pow((xout1 - x1), 2)
				+ Math.pow((yout1 - y1), 2);
		double dist2Squared = Math.pow((xout2 - x1), 2)
				+ Math.pow((yout2 - y1), 2);

		// correct solution
		double xout, yout;

		if (dist1Squared < dist2Squared) {
			xout = xout1;
			yout = yout1;
		} else {
			xout = xout2;
			yout = yout2;
		}

		return getAttributes().createPoint(xout, yout);
	}

	public static class CircleVertexRenderer extends SighosVertexRenderer {
		
		private static final long serialVersionUID = 1L;

		@Override
		protected void fillVertex(Graphics graphics, Dimension size) {
			graphics.fillOval(1, 1, size.width - 2, size.height - 2);

		}

		@Override
		protected void drawVertex(Graphics graphics, Dimension size) {
			graphics.drawOval(1, 1, size.width - 2, size.height - 2);
		}
	}
}