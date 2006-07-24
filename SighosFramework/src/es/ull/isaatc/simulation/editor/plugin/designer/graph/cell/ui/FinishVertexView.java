package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.ui;

import java.awt.Dimension;
import java.awt.Graphics;

import org.jgraph.graph.CellViewRenderer;

public class FinishVertexView extends CircleVertexView {
	
	private static final long serialVersionUID = 1L;

	public static transient FinishVertexRenderer renderer = new FinishVertexRenderer();
	
	/**
	 * 
	 */
	public FinishVertexView() {
		super();
	}

	/**
	 * @param cell
	 */
	public FinishVertexView(Object cell) {
		super(cell);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static class FinishVertexRenderer extends CircleVertexView.CircleVertexRenderer {
		
		private static final long serialVersionUID = 1L;

		@Override
		protected void drawVertex(Graphics graphics, Dimension size) {
			super.drawVertex(graphics, size);
			int diameter = (size.height > size.width) ? size.width : size.height;
			int init = Math.round(diameter / 6);
			int end = Math.round((2 * diameter) / 3);
			graphics.fillOval(init, init, end, end);
		}
	}
}