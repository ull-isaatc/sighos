package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.ui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.graph.CellViewRenderer;

public class FinalVertexView extends CircleVertexView {
	
	private static final long serialVersionUID = 1L;

	public static transient StartVertexRenderer renderer = new StartVertexRenderer();
	
	/**
	 * 
	 */
	public FinalVertexView() {
		super();
	}

	/**
	 * @param cell
	 */
	public FinalVertexView(Object cell) {
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
			Graphics2D g2 = (Graphics2D) graphics;
			g2.setStroke(new BasicStroke(2));
			int diameter = (size.height > size.width) ? size.width : size.height;
			int quadDiameter = Math.round(diameter / 4);
			int radius = Math.round(diameter / 2);
			graphics.drawLine(radius - quadDiameter, radius - quadDiameter, radius + quadDiameter, radius + quadDiameter);				
			graphics.drawLine(radius - quadDiameter, radius + quadDiameter, radius + quadDiameter, radius - quadDiameter);			
		}		
	}

}