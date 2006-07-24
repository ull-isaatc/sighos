package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import org.jgraph.JGraph;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraph;

public class GroupJoinCell extends BranchCell {

	private static final long serialVersionUID = 1L;


	public GroupJoinCell(JGraph graph, Point2D point, GroupSplitCell pairCell) {
		super(graph, point);
		setPairCell(pairCell);
	}

	public void loadPopupMenu(final RootFlowGraph graph, final JPopupMenu popup) {
		popup.addSeparator();
		popup.add(new AbstractAction("Rotate") {
			public void actionPerformed(ActionEvent e) {
//				eventController.fireWFGraphEvent(WFGraphEvent.ROTATE_ELEMENT, graph, this);
			}
		});
	}	
}