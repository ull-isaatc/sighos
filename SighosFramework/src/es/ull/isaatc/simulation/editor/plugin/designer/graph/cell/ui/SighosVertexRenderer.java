package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.ui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;

abstract class SighosVertexRenderer extends VertexRenderer {
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		boolean tmp = selected;
		if (isOpaque()) {
			g.setColor(super.getBackground());
			fillVertex(g, getSize());
			g.setColor(super.getForeground());
		}
		try {
			setBorder(null);
			setOpaque(false);
			selected = false;
			super.paint(g);
//			drawVertex(g, getSize());
		} finally {
			selected = tmp;
		}
		if (bordercolor != null) {
			g2.setStroke(new BasicStroke(1));
			g.setColor(bordercolor);
			drawVertex(g, getSize());
		}
		if (selected) {
			g2.setStroke(GraphConstants.SELECTION_STROKE);
			g.setColor(highlightColor);
			drawVertex(g, getSize());
		}
	}

	abstract protected void fillVertex(Graphics graphics, Dimension size);

	abstract protected void drawVertex(Graphics graphics, Dimension size);
}
