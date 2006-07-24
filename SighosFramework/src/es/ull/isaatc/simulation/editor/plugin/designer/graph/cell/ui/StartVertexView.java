package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.ui;

import java.awt.Dimension;
import java.awt.Graphics;

import org.jgraph.graph.CellViewRenderer;

public class StartVertexView extends CircleVertexView {
	
	private static final long serialVersionUID = 1L;

	public static transient StartVertexRenderer renderer = new StartVertexRenderer();
	
	/**
	 * 
	 */
	public StartVertexView() {
		super();
	}

	/**
	 * @param cell
	 */
	public StartVertexView(Object cell) {
		super(cell);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static class StartVertexRenderer extends CircleVertexView.CircleVertexRenderer {
		
		private static final long serialVersionUID = 1L;

		@Override
		protected void drawVertex(Graphics graphics, Dimension size) {
			super.drawVertex(graphics, size);
			fillVertex(graphics, size);
		}
	}
}