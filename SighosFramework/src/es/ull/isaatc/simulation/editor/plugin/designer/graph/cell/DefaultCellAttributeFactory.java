package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Map;

import org.jgraph.JGraph;
import org.jgraph.graph.GraphConstants;

/**
 * Provides different methods for initialize the attributes of a cell or edge
 * @author Roberto Muñoz
 */
public class DefaultCellAttributeFactory {

	public transient static final Dimension CIRCLECELLSIZE = new Dimension(30, 30);

	public transient static final Dimension DIAMONDCELLSIZE = new Dimension(35, 35);

	public transient static final Dimension RECTANGLECELLZISE = new Dimension(25, 50);

	/**
	 * Creates the attributes of a cell
	 * @param cell the cell
	 * @param graph the graph that contains the cell
	 * @param point the point where the cell is inserted
	 * @return the attribute map of the cell
	 */
	public static Map createCellAttributes(Object cell, JGraph graph, Point2D point) {

		if (cell instanceof StartCell) {
			return createStartCellAttributes(graph, point);
		} else if (cell instanceof FinishCell) {
			return createFinishCellAttributes(graph, point);
		} else if (cell instanceof ExitCell) {
			return createFinishCellAttributes(graph, point);
		} else if (cell instanceof SingleCell) {
			return createFlowCellAttributes(graph, point);
		} else if (cell instanceof PackageCell) {
			return createFlowCellAttributes(graph, point);
		} else if (cell instanceof GroupSplitCell) {
			return createSplitJoinCellAttributes(graph, point);
		} else if (cell instanceof GroupJoinCell) {
			return createSplitJoinCellAttributes(graph, point);
		} else if (cell instanceof DecisionCell) {
			return createDiamondCellAttributes(graph, point);
		} else if (cell instanceof DecisionJoinCell) {
			return createDiamondCellAttributes(graph, point);
		} else if (cell instanceof TypeCell) {
			return createDiamondCellAttributes(graph, point);
		} else if (cell instanceof TypeJoinCell) {
			return createDiamondCellAttributes(graph, point);
		} else
			return null;
	}

	/**
	 * Initialize an attribute map
	 * @param graph
	 * @param point
	 * @return the new attribute map
	 */
	public static Map initMap(JGraph graph, Point2D point) {
		Map map = new Hashtable();

		if (graph != null) {
			point = graph.snap((Point2D) point.clone());
		} else {
			point = (Point2D) point.clone();
		}
		return map;
	}

	/**
	 * Initialize the attribute map for
	 * @param graph the graph that contains the cell
	 * @param point the point where the cell is inserted
	 * @return
	 */
	public static Map createFlowCellAttributes(JGraph graph, Point2D point) {
		Map map = initMap(graph, point);

		GraphConstants.setBounds(map, new Rectangle2D.Double(point.getX(),
				point.getY(), 0, 0));
		GraphConstants.setAutoSize(map, true);
		GraphConstants.setInset(map, 10);
		GraphConstants.setEditable(map, false);
		GraphConstants.setBorderColor(map, Color.black);
		GraphConstants.setBackground(map, Color.white);
		return map;
	}

	/**
	 * Initialize the attribute map for a start cell
	 * @param graph the graph that contains the cell
	 * @param point the point where the cell is inserted
	 * @return
	 */
	public static Map createStartCellAttributes(JGraph graph, Point2D point) {
		Map map = initMap(graph, point);

		GraphConstants.setBounds(map, new Rectangle2D.Double(point.getX(),
				point.getY(), 30, 30));
		GraphConstants.setSizeable(map, false);
		GraphConstants.setBorderColor(map, Color.black);
		GraphConstants.setBackground(map, Color.black);
		GraphConstants.setOpaque(map, true);
		return map;
	}

	/**
	 * Initialize the attribute map for a finish cell
	 * @param graph the graph that contains the cell
	 * @param point the point where the cell is inserted
	 * @return
	 */
	public static Map createFinishCellAttributes(JGraph graph, Point2D point) {
		Map map = initMap(graph, point);

		GraphConstants.setBounds(map, new Rectangle2D.Double(point.getX(),
				point.getY(), 30, 30));
		GraphConstants.setSizeable(map, false);
		GraphConstants.setBorderColor(map, Color.black);
		GraphConstants.setBackground(map, Color.white);
		GraphConstants.setOpaque(map, true);
		return map;
	}

	/**
	 * Initialize the attribute map for diamond pattern cell
	 * @param graph the graph that contains the cell
	 * @param point the point where the cell is inserted
	 * @return
	 */
	public static Map createDiamondCellAttributes(JGraph graph, Point2D point) {
		Map map = initMap(graph, point);

		GraphConstants.setBounds(map, new Rectangle2D.Double(point.getX(),
				point.getY(), 35, 35));
		GraphConstants.setSizeable(map, false);
		GraphConstants.setEditable(map, false);
		GraphConstants.setBorderColor(map, Color.black);
		GraphConstants.setBackground(map, Color.white);
		GraphConstants.setOpaque(map, true);
		return map;
	}

	/**
	 * Initialize the attribute map for
	 * @param graph the graph that contains the cell
	 * @param point the point where the cell is inserted
	 * @return
	 */
	public static Map createSplitJoinCellAttributes(JGraph graph, Point2D point) {
		Map map = initMap(graph, point);

		GraphConstants.setBounds(map, new Rectangle2D.Double(point.getX(),
				point.getY(), 25, 50));
		GraphConstants.setSizeable(map, false);
		GraphConstants.setEditable(map, false);
		GraphConstants.setBorderColor(map, Color.black);
		GraphConstants.setBackground(map, Color.black);
		GraphConstants.setOpaque(map, true);
		GraphConstantsPlugin.setOrientation(map, GraphConstantsPlugin.VERTICAL);
		return map;
	}

	/**
	 * Initialize the attribute map for an edge
	 * @param graph the graph that contains the cell
	 * @param point the point where the cell is inserted
	 * @return
	 */
	public static Map createEdgeAttributes() {
		Map map = new Hashtable();

		GraphConstants.setLineEnd(map, GraphConstants.ARROW_SIMPLE);
		GraphConstants.setEndFill(map, true);
		GraphConstants.setLineStyle(map, GraphConstants.STYLE_ORTHOGONAL);
		GraphConstants.setLabelAlongEdge(map, true);
		GraphConstants.setBendable(map, true);
		GraphConstants.setBackground(map, Color.black);
		GraphConstants.setDisconnectable(map, true);
		GraphConstants.setConnectable(map, true);
		return map;
	}
}
