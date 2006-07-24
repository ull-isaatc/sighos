package es.ull.isaatc.simulation.editor.plugin.designer.graph;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;
import org.jgraph.graph.VertexView;

import sun.security.jca.GetInstance;

import es.ull.isaatc.simulation.editor.plugin.designer.actions.graph.MoveElementsDownAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.graph.MoveElementsLeftAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.graph.MoveElementsRightAction;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.graph.MoveElementsUpAction;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.*;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.ui.DesignerCellViewFactory;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.layouts.LayoutManager;
import es.ull.isaatc.simulation.editor.project.model.*;

public class RootFlowGraph extends JGraph {

	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_MARGIN = 50;

	/** Start cell of the workflow */
	private StartCell startCell;

	/** Finish cell of the workflow */
	private FinishCell finishCell;

	private Point2D point = new Point(20, 20);

	private RootFlowGraphSelectionListener selectionListener;

	public RootFlowGraph() {
		super();
		initialize();
	}

	protected void initialize() {
		setGridMode(JGraph.DOT_GRID_MODE);
		setGridVisible(true);
		setGridEnabled(true);
		setDoubleBuffered(true);
		setGridSize(5);
		setMinimumMove(5);
		setAntiAliased(true);
		setPortsVisible(false);
		setJumpToDefaultPort(true);
		setTolerance(5);
		setAutoResizeGraph(true);
		setAutoscrolls(true);
		getSelectionModel().setChildrenSelectable(false);

		bindKeyMappings();

		addMouseListener(new RootFlowGraphPopupListener(this));
		addMouseListener(ElementDoubleClickListener.getInstance());

		setModel(new RootFlowGraphModel(this));
		ToolTipManager.sharedInstance().registerComponent(this);
		selectionListener = new RootFlowGraphSelectionListener(this
				.getSelectionModel());
		addGraphSelectionListener(selectionListener);
		addFocusListener(new RootFlowGraphFocusListener(this));
		setMarqueeHandler(new RootFlowGraphMarqueeHandler(this));

		getGraphLayoutCache().setFactory(new DesignerCellViewFactory());
		getGraphLayoutCache().setSelectsAllInsertedCells(false);

	}

	public void buildNewGraphContent(Flow flow) {
		startCell = addStartCell();
		finishCell = addFinishCell();
		connect(loadFlow(startCell, flow), finishCell);

	}

	/**
	 * 
	 * @param parent
	 * @param flow
	 * @return the last SighosCell of the flow
	 */
	private SighosCell loadFlow(SighosCell parent, Flow flow) {
		return loadFlow(parent, flow, null);
	}

	private SighosCell loadFlow(SighosCell parent, Flow flow, Object edgeInfo) {
		SighosCell flowCell = null;
		SighosCell lastFlowCell = null;
		if (flow == null)
			return null;
		if (flow instanceof SingleFlow)
			flowCell = lastFlowCell = loadSingleFlow(flow);
		else if (flow instanceof PackageFlow)
			flowCell = lastFlowCell = loadPackageFlow(flow);
		else if (flow instanceof ExitFlow)
			flowCell = loadExitFlow(flow);
		else if (flow instanceof SequenceFlow) {
			SighosCell[] cells = loadSequenceFlow(parent, (SequenceFlow) flow,
					edgeInfo);
			flowCell = cells[0];
			lastFlowCell = cells[1];
		} else if (flow instanceof SimultaneousFlow) {
			SighosCell[] cells = loadSimultaneousFlow((SimultaneousFlow) flow);
			flowCell = cells[0];
			lastFlowCell = cells[1];
		} else if (flow instanceof DecisionFlow) {
			SighosCell[] cells = loadDecisionFlow((DecisionFlow) flow);
			flowCell = cells[0];
			lastFlowCell = cells[1];
		} else if (flow instanceof TypeFlow) {
			SighosCell[] cells = loadTypeFlow((TypeFlow) flow);
			flowCell = cells[0];
			lastFlowCell = cells[1];
		} else if (flow instanceof TypeBranchFlow) {
			return loadFlow(parent, ((TypeBranchFlow) flow).getOption(), flow);
		} else if (flow instanceof DecisionBranchFlow) {
			return loadFlow(parent, ((DecisionBranchFlow) flow).getOption(),
					flow.toString());
		}
		if ((parent != null) && (parent != flowCell)) {
			connect(parent, flowCell, edgeInfo);
		}
		return lastFlowCell;
	}

	/**
	 * Crates the graph for a single flow
	 * 
	 * @param flow
	 * @return the created cell
	 */
	private SighosCell loadSingleFlow(Flow flow) {
		SingleCell cell = addSingleFlow(point);
		cell.setFlow(flow);
		return cell;
	}

	/**
	 * Crates the graph for a package flow
	 * 
	 * @param flow
	 * @return the created cell
	 */
	private SighosCell loadPackageFlow(Flow flow) {
		PackageCell cell = addPackageFlow(point);
		cell.setFlow(flow);
		return cell;
	}

	/**
	 * Crates the graph for an exit flow
	 * 
	 * @param flow
	 * @return the created cell
	 */
	private SighosCell loadExitFlow(Flow flow) {
		ExitCell cell = addExitFlow(point);
		cell.setFlow(flow);
		return cell;
	}

	/**
	 * Creates the graph for a sequence flow
	 * 
	 * @param seq
	 * @return
	 */
	private SighosCell[] loadSequenceFlow(SighosCell parent, SequenceFlow seq,
			Object edgeInfo) {
		SighosCell cells[] = new SighosCell[2];
		// There aren't iterations, split and join aren't inserted
		if ((seq.getIterations() == null)
				|| (seq.getIterations().length() == 0)) {
			cells[0] = loadFlow(parent, seq.getFlowList().remove(0), edgeInfo);
			cells[1] = null;
		} else {
			cells[0] = addGroupSplitFlow(point);
			cells[1] = ((BranchCell)cells[0]).getPairCell();
			cells[0].setUserObject(seq);
		}

		// Insert the sequence
		ArrayList<SighosCell> lastCell = new ArrayList<SighosCell>();
		Iterator<Flow> flowIt = seq.getFlowList().iterator();
		parent = cells[0];
		while (flowIt.hasNext()) {
			parent = loadFlow(parent, flowIt.next());
			lastCell.add(parent);
		}

		// connect the last cell if necessary
		if (cells[1] == null) {
			cells[1] = parent;
			seq.getFlowList().add(0, (Flow) cells[0].getUserObject());
		} else
			connect(parent, cells[1]);
		return cells;
	}

	/**
	 * Creates the graph for a simulataneous flow
	 * 
	 * @param sim
	 */
	private SighosCell[] loadSimultaneousFlow(SimultaneousFlow sim) {
		SighosCell cells[] = new SighosCell[2];
		ArrayList<SighosCell> lastCell = new ArrayList<SighosCell>();
		Iterator<Flow> flowIt = sim.getFlowList().iterator();
		cells[0] = addGroupSplitFlow(point);
		cells[1] = ((BranchCell)cells[0]).getPairCell();
		while (flowIt.hasNext()) {
			lastCell.add(loadFlow(cells[0], flowIt.next()));
		}
		closeOpenRegion(cells[1], lastCell);
		cells[0].setUserObject(sim);
		return cells;
	}

	/**
	 * Creates the graph for a decision flow
	 * 
	 * @param split
	 * @param join
	 * @param options
	 */
	private SighosCell[] loadDecisionFlow(DecisionFlow dec) {
		SighosCell cells[] = new SighosCell[2];
		cells[0] = addDecisionFlow(point);
		cells[1] = ((BranchCell)cells[0]).getPairCell();
		ArrayList<SighosCell> lastCell = new ArrayList<SighosCell>();
		Iterator<DecisionBranchFlow> flowIt = dec.getOptions().keySet()
				.iterator();
		while (flowIt.hasNext()) {
			lastCell.add(loadFlow(cells[0], flowIt.next()));
		}
		closeOpenRegion(cells[1], lastCell);
		return cells;
	}

	/**
	 * Creates the graph for a type flow
	 * 
	 * @param split
	 * @param join
	 * @param options
	 */
	private SighosCell[] loadTypeFlow(TypeFlow type) {
		SighosCell cells[] = new SighosCell[2];
		cells[0] = addTypeFlow(point);
		cells[1] = ((BranchCell)cells[0]).getPairCell();
		ArrayList<SighosCell> lastCell = new ArrayList<SighosCell>();
		Iterator<TypeBranchFlow> flowIt = type.getOptions().keySet().iterator();
		while (flowIt.hasNext()) {
			lastCell.add(loadFlow(cells[0], flowIt.next()));
		}
		closeOpenRegion(cells[1], lastCell);
		return cells;
	}

	/**
	 * Connect each cell to the join cell
	 * 
	 * @param joinCell
	 * @param cells
	 */
	private void closeOpenRegion(SighosCell joinCell, ArrayList<SighosCell> cell) {
		Iterator<SighosCell> cellIt = cell.iterator();
		while (cellIt.hasNext()) {
			connect(cellIt.next(), joinCell);
		}
	}

	private void bindKeyMappings() {
		ActionMap map = new ActionMap();
		InputMap inputMap = new InputMap();

		addKeyMapping(map, inputMap, KeyStroke
				.getKeyStroke(KeyEvent.VK_LEFT, 0), MoveElementsLeftAction
				.getInstance());

		addKeyMapping(map, inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
				0), MoveElementsRightAction.getInstance());

		addKeyMapping(map, inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
				MoveElementsUpAction.getInstance());

		addKeyMapping(map, inputMap, KeyStroke
				.getKeyStroke(KeyEvent.VK_DOWN, 0), MoveElementsDownAction
				.getInstance());

		// addKeyMapping(map, inputMap, KeyStroke.getKeyStroke(KeyEvent.VK_A,
		// KeyEvent.CTRL_DOWN_MASK), SelectAllGraphElementsAction
		// .getInstance());

		setActionMap(map);
		setInputMap(JComponent.WHEN_FOCUSED, inputMap);
		setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
	}

	private void addKeyMapping(ActionMap actionMap, InputMap inputMap,
			KeyStroke keystroke, Action action) {
		actionMap.put(action.getValue(Action.NAME), action);
		inputMap.put(keystroke, action.getValue(Action.NAME));
	}

	private StartCell addStartCell() {
		Point2D startPoint = getStartCellDefaultPoint(DefaultCellAttributeFactory.CIRCLECELLSIZE);
		StartCell startCell = new StartCell(this, startPoint);
		addCell(startCell);
		return startCell;
	}

	private Point2D getStartCellDefaultPoint(Dimension size) {
		return snap(new Point(DEFAULT_MARGIN - size.width / 2, 200));
	}

	private FinishCell addFinishCell() {
		Point2D startPoint = getFinishCellDefaultPoint(DefaultCellAttributeFactory.CIRCLECELLSIZE);
		FinishCell finishCell = new FinishCell(this, startPoint);
		addCell(finishCell);
		return finishCell;
	}

	private Point2D getFinishCellDefaultPoint(Dimension size) {
		return snap(new Point(500, 200));
	}

	public void addCell(SighosCell cell) {
		getModel().insert(new Object[] { cell }, null, null, null, null);
	}

	/**
	 * Removes a cell from the graph. If the cell is connected to another cell
	 * then create an edge between the predecessor and succesor of the cell
	 */
	public void removeCell() {
		if (!isSelectionEmpty()) {
			Object[] cells = getSelectionCells();
			cells = getDescendants(cells);
			if (cells.length == 1)
				if (cells[0] instanceof SingleCell) {
					DefaultPort port = (DefaultPort) ((SingleCell) cells[0])
							.getChildren().get(0);

				}
			getModel().remove(cells);
		}
	}

	public void rotateCell() {
		if (!isSelectionEmpty()) {
			Object[] cells = getSelectionCells();
			cells = getDescendants(cells);
			for (int i = 0; i < cells.length; i++)
				if ((cells[i] instanceof GroupSplitCell)
						|| (cells[i] instanceof GroupJoinCell)) {
					Map map = ((GraphCell) cells[i]).getAttributes();
					Rectangle2D bounds = GraphConstants.getBounds(map);
					bounds.setRect(bounds.getX(), bounds.getY(), bounds
							.getHeight(), bounds.getWidth());
					GraphConstantsPlugin.changeOrientation(map);
				}
			clearSelection();
		}
	}

	/**
	 * Insert a new Edge between source and target
	 */
	public void connect(SighosCell source, SighosCell target) {
		if ((source == null) || (target == null))
			return;
		connect(source.getCellPort(), target.getCellPort());
	}

	/**
	 * Insert a new Edge between source and target
	 */
	public void connect(SighosCell source, SighosCell target, Object edgeInfo) {
		if ((source == null) || (target == null))
			return;
		connect(source.getCellPort(), target.getCellPort(), edgeInfo);
	}

	/**
	 * Insert a new Edge between source and target
	 */
	public void connect(Port source, Port target) {
		connect(source, target, null);
	}

	/**
	 * Insert a new Edge between source and target
	 */
	public void connect(Port source, Port target, Object edgeInfo) {
		// Construct Edge with no label
		DefaultEdge edge = createDefaultEdge();
		edge.getAttributes().applyMap(
				DefaultCellAttributeFactory.createEdgeAttributes());
		edge.setUserObject(edgeInfo);
		getGraphLayoutCache().insertEdge(edge, source, target);
	}

	protected DefaultEdge createDefaultEdge() {
		return new DefaultEdge();
	}

	public boolean connectionAllowable(Port source, Port target) {
		return getRootFlowGraphModel().connectionAllowable(source, target);
	}

	public RootFlowGraphModel getRootFlowGraphModel() {
		return (RootFlowGraphModel) getModel();
	}

	public RootFlowGraphMarqueeHandler getRootFlowGraphMarqueeHandler() {
		return (RootFlowGraphMarqueeHandler) getMarqueeHandler();
	}

	public RootFlowGraphSelectionListener getSelectionListener() {
		return selectionListener;
	}

	public CellView getViewFor(GraphCell cell) {
		return graphLayoutCache.getMapping(cell, false);
	}

	public VertexView getVertexViewFor(GraphCell cell) {
		return (VertexView) getViewFor(cell);
	}

	public void moveSelectedElementsLeft() {
		moveSelectedElementsBy(-getGridSize(), 0);
	}

	public void moveSelectedElementsRight() {
		moveSelectedElementsBy(getGridSize(), 0);
	}

	public void moveSelectedElementsUp() {
		moveSelectedElementsBy(0, -getGridSize());
	}

	public void moveSelectedElementsDown() {
		moveSelectedElementsBy(0, getGridSize());
	}

	public void moveElementBy(GraphCell cell, double x, double y) {
		RootFlowGraphCellUtilities.translateView(this, getViewFor(cell), x, y);
	}

	public void moveElementTo(GraphCell cell, double x, double y) {
		double deltaX = x - getCellBounds(cell).getX();
		double deltaY = y - getCellBounds(cell).getY();

		moveElementBy(cell, deltaX, deltaY);
	}

	private void moveSelectedElementsBy(double x, double y) {
		getRootFlowGraphModel().beginUpdate();
		Object[] cells = getSelectionCells();
		for (int i = 0; i < cells.length; i++) {
			moveElementBy((GraphCell) cells[i], x, y);
		}
		getRootFlowGraphModel().endUpdate();
	}

	public void removeCellsAndTheirEdges(Object[] cells) {
		getRootFlowGraphModel().remove(cells);
	}

	public void increaseSelectedVertexSize() {
		changeSelectedVertexSize(getGridSize());
	}

	public void decreaseSelectedVertexSize() {
		changeSelectedVertexSize(-getGridSize());
	}

	public void zoomIn() {
		setScale(getScale() * 1.25);
	}

	public void zoomOut() {
		setScale(getScale() / 1.25);
	}

	public void autoLayout() {
		LayoutManager.getInstance().applyLayout(this);
	}

	private void changeSelectedVertexSize(double baseSize) {
		getRootFlowGraphModel().beginUpdate();
		Object[] cells = getSelectionCells();
		for (int i = 0; i < cells.length; i++) {
			VertexView view = getVertexViewFor((GraphCell) cells[i]);
			RootFlowGraphCellUtilities.resizeView(this, view, baseSize,
					baseSize);
		}
		getRootFlowGraphModel().endUpdate();
	}

	public SingleCell addSingleFlow(Point2D point) {
		SingleCell cell = new SingleCell(this, point);
		addCell(cell);
		return cell;
	}

	public PackageCell addPackageFlow(Point2D point) {
		PackageCell cell = new PackageCell(this, point);
		addCell(cell);
		return cell;
	}

	public GroupSplitCell addGroupSplitFlow(Point2D point) {
		GroupSplitCell cell = new GroupSplitCell(this, point);
		addCell(cell);
		addCell(cell.getPairCell());
		return cell;
	}

	public ExitCell addExitFlow(Point2D point) {
		ExitCell cell = new ExitCell(this, point);
		addCell(cell);
		return cell;
	}

	public DecisionCell addDecisionFlow(Point2D point) {
		DecisionCell cell = new DecisionCell(this, point);
		addCell(cell);
		addCell(cell.getPairCell());
		return cell;
	}

	public TypeCell addTypeFlow(Point2D point) {
		TypeCell cell = new TypeCell(this, point);
		addCell(cell);
		addCell(cell.getPairCell());
		return cell;
	}
}

class RootFlowGraphFocusListener implements FocusListener {

	/*
	 * This jiggery-pokery has been made necessary because ALT-TABbing out of
	 * the application without forcing cleanup of Marquee state (as we now do in
	 * the focusLost() method below) would result in unconnected ghost flows
	 * occasionally appearing on the net when users next tried connecting two
	 * net elements with a flow.
	 */

	private RootFlowGraph graph;

	public RootFlowGraphFocusListener(RootFlowGraph graph) {
		this.graph = graph;
	}

	public void focusGained(FocusEvent event) {
		// deliberately does nothing.
	}

	public void focusLost(FocusEvent event) {
		try {
			graph.getRootFlowGraphMarqueeHandler()
					.connectElementsOrIgnoreFlow();
		} catch (Exception e) {
		}
	}
}