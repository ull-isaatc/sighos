package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.ui;


import java.awt.Dimension;
import java.awt.Graphics;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

public class PackageFlowVertexView extends VertexView {
	
	private static final long serialVersionUID = 1L;

	public static transient FlowVertexRenderer renderer = new FlowVertexRenderer();

	public PackageFlowVertexView() {
		super();
	}

	public PackageFlowVertexView(Object cell) {
		super(cell);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Calculates an appropriate arc for the corners of the rectangle
	 * for boundary size cases of width and height
	 */
	public static int getArcSize(int width, int height) {
		int arcSize;

		// The arc width of a activity rectangle is 1/5th of the larger
		// of the two of the dimensions passed in, but at most 1/2
		// of the smaller of the two. 1/5 because it looks nice and 1/2
		// so the arc can complete in the given dimension

		if (width <= height) {
			arcSize = height / 5;
			if (arcSize > (width / 2)) {
				arcSize = width / 2;
			}
		} else {
			arcSize = width / 5;
			if (arcSize > (height / 2)) {
				arcSize = height / 2;
			}
		}

		return arcSize;
	}
	
	public static class FlowVertexRenderer extends SighosVertexRenderer {
		
		private static final long serialVersionUID = 1L;

		protected void fillVertex(Graphics graphics, Dimension size) {
			int roundRectArc = PackageFlowVertexView.getArcSize(size.width, size.height);
			graphics.fillRoundRect(0, 0, size.width, size.height, roundRectArc, roundRectArc);
		}

		protected void drawVertex(Graphics graphics, Dimension size) {
			int roundRectArc = PackageFlowVertexView.getArcSize(size.width, size.height);
			graphics.drawRoundRect(0, 0, size.width - 1, size.height - 1, roundRectArc, roundRectArc);
			roundRectArc = PackageFlowVertexView.getArcSize(size.width - 8, size.height - 8);
			graphics.drawRoundRect(4, 4, size.width - 8 - 1, size.height - 8 - 1, roundRectArc, roundRectArc);
		}
	}

}