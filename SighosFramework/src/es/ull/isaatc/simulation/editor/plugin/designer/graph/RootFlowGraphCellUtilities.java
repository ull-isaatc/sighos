package es.ull.isaatc.simulation.editor.plugin.designer.graph;

import java.util.Map;
import java.util.HashMap;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

import org.jgraph.graph.CellView;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexView;


public class RootFlowGraphCellUtilities {

	public static void translateView(RootFlowGraph net, CellView view, double x,
			double y) {
		Rectangle2D oldBounds = view.getBounds();
		Rectangle2D.Double newBounds = new Rectangle2D.Double(oldBounds.getX()
				+ x, oldBounds.getY() + y, oldBounds.getWidth(), oldBounds
				.getHeight());
		if (view instanceof VertexView) {
			((VertexView) view).setBounds(newBounds);
		}
		if (view instanceof EdgeView) {
			EdgeView edgeView = (EdgeView) view;
			for (int i = 0; i < edgeView.getPointCount(); i++) {
				Point2D oldPoint = edgeView.getPoint(i);
				Point2D.Double newPoint = new Point2D.Double(oldPoint.getX()
						+ x, oldPoint.getY() + y);
				edgeView.setPoint(i, newPoint);
			}
		}
		net.setGridEnabled(false);
		applyViewChange(net, view);
		net.setGridEnabled(true);
	}

	public static void moveViewToLocation(RootFlowGraph net, CellView view,
			double x, double y) {
		Rectangle2D oldBounds = view.getBounds();
		Rectangle2D.Double newBounds = new Rectangle2D.Double(x, y, oldBounds
				.getWidth(), oldBounds.getHeight());
		if (view instanceof VertexView) {
			((VertexView) view).setBounds(newBounds);
		}
		net.setGridEnabled(false);
		applyViewChange(net, view);
		net.setGridEnabled(true);
	}

	public static void resizeViews(RootFlowGraph net, CellView[] views,
			double width, double height) {
		for (int i = 0; i < views.length; i++) {
			resizeView(net, views[i], width, height);
		}
	}

	public static void resizeView(RootFlowGraph net, CellView view, double width,
			double height) {
		Rectangle2D oldBounds = view.getBounds();

		Rectangle2D.Double newBounds = new Rectangle2D.Double(oldBounds.getX(),
				oldBounds.getY(), oldBounds.getWidth() + width, oldBounds
						.getHeight()
						+ height);

		net.setGridEnabled(false);

		HashMap map = new HashMap();
		GraphConstants.setBounds(map, newBounds);

		net.getGraphLayoutCache().editCell(view.getCell(), map);
		net.setGridEnabled(true);
	}

	public static void applyViewChange(RootFlowGraph graph, CellView view) {
		CellView[] allViews = VertexView
				.getDescendantViews(new CellView[] { view });
		Map attributes = GraphConstants.createAttributes(allViews, null);
		graph.getModel().edit(attributes, null, null, null);
	}

	public static void alignCellsAlongTop(RootFlowGraph graph, Object[] cells) {
		// a retrofit of source from jgraphpad.
		if (cells != null) {
			Rectangle2D r = graph.getCellBounds(cells);
			graph.getRootFlowGraphModel().beginUpdate();
			for (int i = 0; i < cells.length; i++) {
				Rectangle2D bounds = graph.getCellBounds(cells[i]);
				graph.moveElementBy((GraphCell) cells[i], 0, (-1 * bounds
						.getY())
						+ r.getY());
			}
			graph.getRootFlowGraphModel().endUpdate();
		}
	}

	public static void alignCellsAlongHorizontalCentre(RootFlowGraph graph,
			Object[] cells) {
		// a retrofit of source from jgraphpad.
		if (cells != null) {
			Rectangle2D r = graph.getCellBounds(cells);
			double cy = r.getHeight() / 2;
			graph.getRootFlowGraphModel().beginUpdate();
			for (int i = 0; i < cells.length; i++) {
				Rectangle2D bounds = graph.getCellBounds(cells[i]);
				graph.moveElementBy((GraphCell) cells[i], 0, (-1 * bounds
						.getY())
						+ r.getY() + cy - bounds.getHeight() / 2);
			}
			graph.getRootFlowGraphModel().endUpdate();
		}
	}

	public static void alignCellsAlongBottom(RootFlowGraph graph, Object[] cells) {
		// a retrofit of source from jgraphpad.
		if (cells != null) {
			Rectangle2D r = graph.getCellBounds(cells);
			graph.getRootFlowGraphModel().beginUpdate();
			for (int i = 0; i < cells.length; i++) {
				Rectangle2D bounds = graph.getCellBounds(cells[i]);
				graph.moveElementBy((GraphCell) cells[i], 0, (-1 * bounds
						.getY())
						+ r.getY() + r.getHeight() - bounds.getHeight());
			}
			graph.getRootFlowGraphModel().endUpdate();
		}
	}

	public static void alignCellsAlongLeft(RootFlowGraph graph, Object[] cells) {
		// a retrofit of source from jgraphpad.
		if (cells != null) {
			Rectangle2D r = graph.getCellBounds(cells);
			graph.getRootFlowGraphModel().beginUpdate();
			for (int i = 0; i < cells.length; i++) {
				Rectangle2D bounds = graph.getCellBounds(cells[i]);
				graph.moveElementBy((GraphCell) cells[i], (-1 * bounds.getX())
						+ r.getX(), 0);
			}
			graph.getRootFlowGraphModel().endUpdate();
		}
	}

	public static void alignCellsAlongVerticalCentre(RootFlowGraph graph,
			Object[] cells) {
		// a retrofit of source from jgraphpad.
		if (cells != null) {
			Rectangle2D r = graph.getCellBounds(cells);
			double cx = r.getWidth() / 2;
			graph.getRootFlowGraphModel().beginUpdate();
			for (int i = 0; i < cells.length; i++) {
				Rectangle2D bounds = graph.getCellBounds(cells[i]);
				graph.moveElementBy((GraphCell) cells[i], (-1 * bounds.getX())
						+ r.getX() + cx - bounds.getWidth() / 2, 0);
			}
			graph.getRootFlowGraphModel().endUpdate();
		}
	}

	public static void alignCellsAlongRight(RootFlowGraph graph, Object[] cells) {
		// a retrofit of source from jgraphpad.
		if (cells != null) {
			Rectangle2D r = graph.getCellBounds(cells);
			graph.getRootFlowGraphModel().beginUpdate();
			for (int i = 0; i < cells.length; i++) {
				Rectangle2D bounds = graph.getCellBounds(cells[i]);
				graph.moveElementBy((GraphCell) cells[i], (-1 * bounds.getX())
						+ r.getX() + r.getWidth() - bounds.getWidth(), 0);
			}
			graph.getRootFlowGraphModel().endUpdate();
		}
	}
}
