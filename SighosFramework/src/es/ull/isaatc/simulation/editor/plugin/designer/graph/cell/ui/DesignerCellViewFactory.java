package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.ui;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.VertexView;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.*;


public class DesignerCellViewFactory extends DefaultCellViewFactory {

	private static final long serialVersionUID = 1L;

	public VertexView createVertexView(Object cell) {
		if (cell instanceof StartCell)
			return new StartVertexView(cell);
		if (cell instanceof FinishCell)
			return new FinishVertexView(cell);
		if (cell instanceof ExitCell)
			return new FinalVertexView(cell);
		if (cell instanceof GroupSplitCell)
			return new GroupSplitVertexView(cell);
		if (cell instanceof GroupJoinCell)
			return new GroupJoinVertexView(cell);
		if (cell instanceof DecisionCell)
			return new DecisionVertexView(cell);
		if (cell instanceof DecisionJoinCell)
			return new DecisionJoinVertexView(cell);
		if (cell instanceof TypeCell)
			return new TypeVertexView(cell);
		if (cell instanceof TypeJoinCell)
			return new TypeJoinVertexView(cell);
		if (cell instanceof SingleCell)
			return new FlowVertexView(cell);
		if (cell instanceof PackageCell)
			return new PackageFlowVertexView(cell);
		return super.createVertexView(cell);
	}
}
