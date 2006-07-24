package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.graph.CellViewRenderer;

public class TypeVertexView extends DoubleDiamondVertexView {

	private static final long serialVersionUID = 1L;

	public static transient TypeRenderer renderer = new TypeRenderer();

	public TypeVertexView() {
		super();
	}

	public TypeVertexView(Object cell) {
		super(cell);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static class TypeRenderer extends DiamondVertexView.DiamondRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		protected void drawVertex(Graphics graphics, Dimension size) {
			super.drawVertex(graphics, size);
			Graphics2D g2 = (Graphics2D) graphics;
			int halfWidth = Math.round(size.width / 2);
			int halfHeight = Math.round(size.height / 2);
			graphics.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(2));
			int x_width = halfWidth / 3 ;
			int x_height = halfHeight / 3;
			graphics.drawLine(halfWidth - x_width, halfHeight - x_height + 2, halfWidth + x_width, halfHeight - x_height + 2);				
			graphics.drawLine(halfWidth, halfHeight - x_height + 2, halfWidth, halfHeight + x_height + 2);
		}
	} 
}
