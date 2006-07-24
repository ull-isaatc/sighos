package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell;

import java.awt.geom.Point2D;

import javax.swing.JPopupMenu;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.Port;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;
import es.ull.isaatc.simulation.editor.project.model.Flow;

/**
 * @author Roberto Muñoz
 * 
 */
public abstract class SighosCell extends DefaultGraphCell {

	public SighosCell(JGraph graph, Point2D point) {
		this(null, graph, point);
	}

	public SighosCell(Object userObject, JGraph graph, Point2D point) {
		super(userObject);
		addPort();
		getAttributes().applyMap(
				DefaultCellAttributeFactory.createCellAttributes(this, graph,
						point));
	}

	public void loadPopupMenu(final RootFlowGraph graph, final JPopupMenu popup) {

	}

	public Port getCellPort() {
		for (int i = 0; i < getChildCount(); i++) {
			Object port = getChildAt(i);
			if (port instanceof Port)
				return (Port) port;
		}
		return null;
	}

	// FIXME: hacer que sea abstract
	public boolean isRemovable() {
		return true;
	}
	
	public Flow getFlow() {
		return (Flow)getUserObject();
	}
}
