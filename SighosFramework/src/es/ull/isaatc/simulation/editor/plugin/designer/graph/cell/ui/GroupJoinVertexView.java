package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.ui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.GraphConstantsPlugin;

public class GroupJoinVertexView extends VertexView {

	private static final long serialVersionUID = 1L;

	public static transient JoinRenderer renderer = new JoinRenderer();

	public GroupJoinVertexView() {
		super();
	}

	public GroupJoinVertexView(Object cell) {
		super(cell);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public static class JoinRenderer extends SighosVertexRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		protected void fillVertex(Graphics graphics, Dimension size) {

		}

		@Override
		protected void drawVertex(Graphics graphics, Dimension size) {
			int x1, y1, x2, y2;
			int lx1, ly1, lx2, ly2, lx3, ly3;
			String orientation = GraphConstantsPlugin.getOrientation(view
					.getAllAttributes());
			if (orientation.equals(GraphConstantsPlugin.VERTICAL)) {
				int littleWidht = Math.round((size.width - 2) / 3);
				int bigWidth = Math.round((2 * (size.width - 2)) / 3);
				x1 = littleWidht + 1;
				y1 = 1;
				x2 = bigWidth + 1;
				y2 = size.height - 2;
				lx1 = 1;
				ly1 = 1;
				lx2 = littleWidht + 1;
				ly2 = Math.round((size.height - 2) / 2);
				lx3 = 1;
				ly3 = size.height - 1;
			} else {
				int littleHeight = Math.round((size.height - 2) / 3);
				int bigHeight = Math.round((2 * (size.height - 2)) / 3);
				x1 = 1;
				y1 = littleHeight + 1;
				x2 = size.width - 2;
				y2 = bigHeight + 1;
				lx1 = 1;
				ly1 = 1;
				lx2 = Math.round((size.width - 2) / 2);
				ly2 = littleHeight + 1;
				lx3 = size.width - 1;
				ly3 = 1;
			}
			graphics.drawRect(1, 1, size.width - 2, size.height - 2);
			Graphics2D g2 = (Graphics2D) graphics;
			g2.setStroke(new BasicStroke((float) 0.5));
			graphics.fillRect(x1, y1, x2, y2);
			graphics.drawLine(lx1, ly1, lx2, ly2);
			graphics.drawLine(lx2, ly2, lx3, ly3);

		}
	}
}
