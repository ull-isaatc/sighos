package es.ull.isaatc.simulation.editor.plugin.designer.graph;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.DecisionCell;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.SighosCell;

/**
 * Listener for changes in the model
 * 
 * @author Roberto Muñoz
 */
public class RootFlowGraphModelListener implements GraphModelListener {

	/**
	 * When an edge is inserted or changed its properties are actualized
	 */
	public void graphChanged(GraphModelEvent ev) {
		Object changed[] = ev.getChange().getChanged();
		if ((changed.length == 1) && (ev.getChange().getRemoved() == null)) {
			if (changed[0] instanceof DefaultEdge) {
				DefaultEdge edge = (DefaultEdge) changed[0];
				SighosCell cell = (SighosCell) DefaultGraphModel
						.getSourceVertex((GraphModel) ev.getSource(), edge);
				if (cell instanceof DecisionCell) { // enable the edition
													// property
					GraphConstants.setEditable(edge.getAttributes(), true);
					if (edge.getUserObject() instanceof String) {
						return;
					}
					edge.setUserObject(new String("0.0"));
				} else {
					GraphConstants.setEditable(edge.getAttributes(), false);
				}
				if (ev.getChange().getInserted() == null) { // edge changes its parent
					edge.setUserObject(null); // the user object is removed
				}
				// Refresh the view
				((RootFlowGraphModel)ev.getSource()).getGraph().getGraphLayoutCache().graphChanged(ev.getChange());
			}
		}
	}

}
