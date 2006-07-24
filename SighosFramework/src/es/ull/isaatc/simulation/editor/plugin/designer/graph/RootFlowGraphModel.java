package es.ull.isaatc.simulation.editor.plugin.designer.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.Edge;
import org.jgraph.graph.Port;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.*;
import es.ull.isaatc.simulation.editor.project.model.ContainerFlow;
import es.ull.isaatc.simulation.editor.project.model.DecisionBranchFlow;
import es.ull.isaatc.simulation.editor.project.model.DecisionFlow;
import es.ull.isaatc.simulation.editor.project.model.Flow;
import es.ull.isaatc.simulation.editor.project.model.FlowStack;
import es.ull.isaatc.simulation.editor.project.model.GroupFlow;
import es.ull.isaatc.simulation.editor.project.model.SequenceFlow;
import es.ull.isaatc.simulation.editor.project.model.SimultaneousFlow;
import es.ull.isaatc.simulation.editor.project.model.TypeBranchFlow;
import es.ull.isaatc.simulation.editor.project.model.TypeFlow;

public class RootFlowGraphModel extends DefaultGraphModel {

	private static final long serialVersionUID = 1L;

	private FlowStack traceStack = new FlowStack();

	private RootFlowGraph graph;

	public RootFlowGraphModel(RootFlowGraph graph) {
		super();
		this.graph = graph;
		addGraphModelListener(new RootFlowGraphModelListener());
	}

	public RootFlowGraph getGraph() {
		return this.graph;
	}

	public void remove(Object[] cells) {
		removeCellsAndEdges(getRemovableCellsOf(cells));
	}

	private HashSet<Object> getRemovableCellsOf(Object[] cells) {
		HashSet<Object> removableCells = new HashSet<Object>();

		for (int i = 0; i < cells.length; i++) {
			if (cells[i] instanceof SighosCell) {
				SighosCell element = (SighosCell) cells[i];
				if (element.isRemovable()) {
					removableCells.add(cells[i]);
					if (cells[i] instanceof BranchCell)
						removableCells
								.addAll(Arrays
										.asList(getCellAndTheirEdges(((BranchCell) cells[i])
												.getPairCell())));
				}
			} else {
				removableCells.add(cells[i]);
			}
		}
		return removableCells;
	}

	private void removeCellsAndEdges(final Set<Object> cells) {
		HashSet<Object> cellsAndTheirEdges = new HashSet<Object>(cells);
		cellsAndTheirEdges.addAll(getDescendants(this, cells.toArray()));
		cellsAndTheirEdges.addAll(getEdges(this, cells.toArray()));
		super.remove(cellsAndTheirEdges.toArray());
	}

	private Object[] getCellAndTheirEdges(SighosCell cell) {
		ArrayList<Object> objects = new ArrayList<Object>();
		objects.addAll(Arrays.asList(getOutgoingEdges(this, cell)));
		objects.addAll(Arrays.asList(getIncomingEdges(this, cell)));
		objects.add(cell);
		return objects.toArray();
	}

	public boolean acceptsTarget(Object edge, Object port) {
		return connectionAllowable((Port) ((Edge) edge).getSource(),
				(Port) port, (Edge) edge);
	}

	public boolean acceptsSource(Object edge, Object port) {
		return connectionAllowable((Port) port, (Port) ((Edge) edge)
				.getTarget(), (Edge) edge);
	}

	/**
	 * Returns <code>true</code> if a flow relation can validly be drawn from
	 * the source port to the target port, <code>false</code> otherwise.
	 * 
	 * @param source
	 *            The source port
	 * @param target
	 *            The target port
	 * @return <code>true</code> if conection allowed.
	 */

	public boolean connectionAllowable(Port source, Port target) {
		return connectionAllowable(source, target, null);
	}

	/**
	 * This method returns <code>true</code> if a flow relation can validly be
	 * drawn from the source port to the target port, ignoring for the time
	 * being that the specified edge actually exists.
	 * <p>
	 * This takes care of the fact that the checks for whether an edge can be
	 * connected to a port via the JGraphModel methods of acceptsTarget() and
	 * acceptsSource(). Unfortunately, the methods acceptsTarget() and
	 * acceptsSource() of JGraphModel are called AFTER the edge has been
	 * reconnected. Therefore, we need to ignore the already connected edge in
	 * deciding if connections are valid.
	 * 
	 * @param source
	 *            The source port
	 * @param target
	 *            The target port
	 * @param edgeToIgnore
	 *            The edge to ignore
	 * @return <code>true</code> if connection allowed.
	 */

	private boolean connectionAllowable(Port source, Port target,
			Edge edgeToIgnore) {
		boolean rulesAdheredTo = true;

		SighosCell sourceCell = (SighosCell) getParent(source);
		SighosCell targetCell = (SighosCell) getParent(target);

		if (sourceCell == targetCell) {
			rulesAdheredTo = false;
		}

		if (source == null || target == null) {
			return false;
		}

		if (areConnectedAsSourceAndTarget(sourceCell, targetCell, edgeToIgnore)) {
			rulesAdheredTo = false;
		}

		if (!generatesOutgoingFlows(sourceCell, edgeToIgnore)) {
			rulesAdheredTo = false;
		}

		if (!acceptsIncommingFlows(targetCell, edgeToIgnore)) {
			rulesAdheredTo = false;
		}

		return rulesAdheredTo;
	}

	public boolean areConnected(SighosCell sourceCell, SighosCell targetCell) {
		if (areConnectedAsSourceAndTarget(sourceCell, targetCell)) {
			return true;
		}
		if (areConnectedAsSourceAndTarget(targetCell, sourceCell)) {
			return true;
		}
		return false;
	}

	public boolean areConnectedAsSourceAndTarget(SighosCell sourceCell,
			SighosCell targetCell) {
		return areConnectedAsSourceAndTarget(sourceCell, targetCell, null);
	}

	private boolean areConnectedAsSourceAndTarget(SighosCell sourceCell,
			SighosCell targetCell, Edge edgeToIgnore) {
		Iterator setIterator = getEdges(this, new Object[] { sourceCell })
				.iterator();
		while (setIterator.hasNext()) {
			Edge edge = (Edge) setIterator.next();
			if ((getTargetOf(edge) == targetCell && !edge.equals(edgeToIgnore))) {
				return true;
			}
		}
		return false;
	}

	public SighosCell getTargetOf(Edge edge) {
		return ElementUtilities.getTargetOf(this, edge);
	}

	public SighosCell getSourceOf(Edge edge) {
		return ElementUtilities.getSourceOf(this, edge);
	}

	public boolean acceptsIncommingFlows(SighosCell vertex) {
		return acceptsIncommingFlows(vertex, null);
	}

	public boolean acceptsIncommingFlows(SighosCell cell, Edge edge) {
		if (cell instanceof StartCell) {
			return false;
		}
		if (cell instanceof FinishCell && hasIncommingFlow(cell, edge)) {
			return false;
		}
		if (cell instanceof ExitCell && hasIncommingFlow(cell, edge)) {
			return false;
		}
		if (cell instanceof SingleCell && hasIncommingFlow(cell, edge)) {
			return false;
		}
		if (cell instanceof PackageCell && hasIncommingFlow(cell, edge)) {
			return false;
		}
		if (cell instanceof DecisionCell && hasIncommingFlow(cell, edge)) {
			return false;
		}
		if (cell instanceof TypeCell && hasIncommingFlow(cell, edge)) {
			return false;
		}
		if (cell instanceof GroupSplitCell && hasIncommingFlow(cell, edge)) {
			return false;
		}
		return true;
	}

	public boolean hasIncommingFlow(SighosCell cell) {
		return hasIncommingFlow(cell, null);
	}

	public boolean hasIncommingFlow(SighosCell cell, Edge edgeToIgnore) {
		Iterator setIterator = getEdges(this, new Object[] { cell }).iterator();
		while (setIterator.hasNext()) {
			Edge edge = (Edge) setIterator.next();
			if (getTargetOf(edge) == cell && !edge.equals(edgeToIgnore)) {
				return true;
			}
		}
		return false;
	}

	public boolean generatesOutgoingFlows(SighosCell cell) {
		return generatesOutgoingFlows(cell, null);
	}

	public boolean generatesOutgoingFlows(SighosCell cell, Edge edge) {
		if (cell instanceof FinishCell) {
			return false;
		}
		if (cell instanceof ExitCell) {
			return false;
		}
		if (cell instanceof StartCell && hasOutgoingFlow(cell, edge)) {
			return false;
		}
		if (cell instanceof SingleCell && hasOutgoingFlow(cell, edge)) {
			return false;
		}
		if (cell instanceof PackageCell && hasOutgoingFlow(cell, edge)) {
			return false;
		}
		if (cell instanceof DecisionJoinCell && hasOutgoingFlow(cell, edge)) {
			return false;
		}
		if (cell instanceof TypeJoinCell && hasOutgoingFlow(cell, edge)) {
			return false;
		}
		if (cell instanceof GroupJoinCell && hasOutgoingFlow(cell, edge)) {
			return false;
		}
		return true;
	}

	public boolean hasOutgoingFlow(SighosCell cell) {
		return hasOutgoingFlow(cell, null);
	}

	public boolean hasOutgoingFlow(SighosCell cell, Edge edgeToIgnore) {
		Iterator setIterator = getEdges(this, new Object[] { cell }).iterator();
		while (setIterator.hasNext()) {
			Edge edge = (Edge) setIterator.next();
			if (getSourceOf(edge) == cell && !edge.equals(edgeToIgnore)) {
				return true;
			}
		}
		return false;
	}

	public void checkGraphStructure() {
		Flow f = getFlow();
		System.out.println(f.getXML());
	}

	public Flow getFlow() {
		getFlow((SighosCell) getRootAt(0));
		return traceStack.pop().getFlow();
	}

	/**
	 * 
	 * @param cell
	 */
	public void getFlow(SighosCell cell) {
		Object edges[]; // outgoing edges
		SighosCell nextCell = null; // next cell to evaluate

		if (cell == null)
			return;

		edges = getOutgoingEdges(this, cell);

		if (edges.length > 0)
			nextCell = getEdgeTarget((Edge) edges[0]);

		if (traceStack.empty()) {
			traceStack.push(1, new SequenceFlow());
		}

		if (cell instanceof FinishCell) { // finish the flow
			nextCell = null;
		} else if (cell instanceof SingleCell) {
			((ContainerFlow) traceStack.top().getFlow())
					.addFlow(cell.getFlow());
		} else if (cell instanceof PackageCell) {
			((ContainerFlow) traceStack.top().getFlow())
					.addFlow(cell.getFlow());
		} else if (cell instanceof ExitCell) {
			((ContainerFlow) traceStack.top().getFlow())
					.addFlow(cell.getFlow());
			traceStack.branchFinished();
			return;
		} else if (cell instanceof GroupSplitCell) {
			GroupFlow group = null;
			if (edges.length == 1) // sequence flow
				group = new SequenceFlow();
			else // simultaneous flow
				group = new SimultaneousFlow();
			((ContainerFlow) traceStack.top().getFlow()).addFlow(group);
			traceStack.push(edges.length, group);
			// Get the flow for each branch
			for (Object edge : edges)
				getFlow(getEdgeTarget((Edge) edge));
			// Continue with the rest of the flow
			edges = getOutgoingEdges(this, ((BranchCell) cell).getPairCell());
			if (edges.length > 0)
				nextCell = getEdgeTarget((Edge) edges[0]);
			else
				nextCell = null;
		} else if (cell instanceof DecisionCell) {
			DecisionFlow dec = new DecisionFlow();
			((ContainerFlow) traceStack.top().getFlow()).addFlow(dec);
			traceStack.push(edges.length, dec);
			// Get the flow for each branch
			for (Object edge : edges) {
				DecisionBranchFlow dbFlow = new DecisionBranchFlow();
				dbFlow.setProb(Float
						.parseFloat(((DefaultEdge) edge).toString()));
				((DecisionFlow) traceStack.top().getFlow()).addOption(dbFlow);
				traceStack.push(1, dbFlow);
				getFlow(getEdgeTarget((Edge) edge));
			}
			// Continue with the rest of the flow
			edges = getOutgoingEdges(this, ((BranchCell) cell).getPairCell());
			if (edges.length > 0)
				nextCell = getEdgeTarget((Edge) edges[0]);
			else
				nextCell = null;
		} else if (cell instanceof TypeCell) {
			TypeFlow type = new TypeFlow();
			((ContainerFlow) traceStack.top().getFlow()).addFlow(type);
			traceStack.push(edges.length, type);

			// Get the flow for each branch
			for (Object edge : edges) {
				TypeBranchFlow tbFlow = (TypeBranchFlow) ((DefaultEdge)edge).getUserObject();
				((TypeFlow) traceStack.top().getFlow()).addOption(tbFlow);
				traceStack.push(1, tbFlow);
				getFlow(getEdgeTarget((Edge) edge));
			}
			// Continue with the rest of the flow
			edges = getOutgoingEdges(this, ((BranchCell) cell).getPairCell());
			if (edges.length > 0)
				nextCell = getEdgeTarget((Edge) edges[0]);
			else
				nextCell = null;
		} else if ((cell instanceof DecisionJoinCell)
				|| (cell instanceof TypeJoinCell)
				|| (cell instanceof GroupJoinCell)) {
			traceStack.branchFinished(); // finish a branch
			return;
		} else if (cell instanceof FinishCell) {
			return;
		}

		getFlow(nextCell);
	}

	/**
	 * @param edge
	 * @return the cell target of the edge
	 */
	public SighosCell getEdgeTarget(Edge edge) {
		return (SighosCell) getParent(getTarget(edge));
	}
	
	/**
	 * @param edge
	 * @return the cell source of the edge
	 */
	public SighosCell getEdgeSource(Edge edge) {
		return (SighosCell) getParent(getSource(edge));
	}
}
