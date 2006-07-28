package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;

import org.jgraph.graph.CellViewRenderer;

public class DoubleDiamondVertexView extends DiamondVertexView {

	private static final long serialVersionUID = 1L;

	public static transient DoubleDiamondRenderer renderer = new DoubleDiamondRenderer();

	public DoubleDiamondVertexView() {
		super();
	}

	public DoubleDiamondVertexView(Object cell) {
		super(cell);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static class DoubleDiamondRenderer extends DiamondVertexView.DiamondRenderer {

		private static final long serialVersionUID = 1L;

		protected Polygon getInnerPolygon(Dimension size) {
			width = size.width - 4;
			height = size.height - 4;
			halfWidth = Math.round(size.width / 2);
			halfHeight = Math.round(size.height / 2);
			int[] xpoints = { halfWidth, width, halfWidth, 4 };
			int[] ypoints = { 4, halfHeight, height, halfHeight };
			Polygon diamond = new Polygon(xpoints, ypoints, 4);
			return diamond;
		}
		
		@Override
		protected void fillVertex(Graphics graphics, Dimension size) {
			graphics.fillPolygon(getPolygon(size));
		}

		@Override
		protected void drawVertex(Graphics graphics, Dimension size) {
			super.drawVertex(graphics, size);
			graphics.drawPolygon(getInnerPolygon(size));
		}
	} 
}
