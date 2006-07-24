package es.ull.isaatc.simulation.editor.plugin.designer.graph;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.GraphSelectionModel;

import es.ull.isaatc.simulation.editor.plugin.designer.actions.graph.*;
import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.SighosCell;

public class RootFlowGraphSelectionListener implements GraphSelectionListener {
	private GraphSelectionModel model;

	public RootFlowGraphSelectionListener(GraphSelectionModel model) {
		this.model = model;
	}

	public void valueChanged(GraphSelectionEvent event) {
		updateActions();
	}

	public void forceActionUpdate() {
		updateActions();
	}

	private void updateActions() {
		if (model.isSelectionEmpty()) {
			disableActions();
		} else {
			enableActionsIfAppropriate();
		}
	}

	private void enableActionsIfAppropriate() {
		enableDeleteActionsIfAppropriate();
		enableResizeActionsIfAppropriate();
		enableAlignmentActionsIfAppropriate();
	}

	private void disableActions() {
		enableDeleteActions(false);
		enableResizeActions(false);
		enableAlignmentActions(false);
	}

	private void enableDeleteActionsIfAppropriate() {
		Object[] elements = model.getSelectionCells();

		for (int i = 0; i < elements.length; i++) {
			if (!(elements[i] instanceof SighosCell)) {
				enableDeleteActions(true);
				return;
			}

			SighosCell element = (SighosCell) elements[i];
			if (element.isRemovable()) {
				enableDeleteActions(true);
				return;
			}
		}
		enableDeleteActions(false);
	}

	private void enableDeleteActions(boolean enabled) {
		// CutAction.getInstance().setEnabled(enabled);
		DeleteAction.getInstance().setEnabled(enabled);
	}

	private void enableResizeActionsIfAppropriate() {
		enableResizeActions(true);
	}

	private void enableResizeActions(boolean enabled) {
		IncreaseSizeAction.getInstance().setEnabled(enabled);
		DecreaseSizeAction.getInstance().setEnabled(enabled);
	}

	private void enableAlignmentActionsIfAppropriate() {
		int validElementCount = 0;
		Object[] elements = model.getSelectionCells();

		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof SighosCell) {
				validElementCount++;
			}
		}
		if (validElementCount >= 2) {
			enableAlignmentActions(true);
			return;
		}
		enableAlignmentActions(false);
	}

	private void enableAlignmentActions(boolean enabled) {
		AlignLeftAction.getInstance().setEnabled(enabled);
		AlignRightAction.getInstance().setEnabled(enabled);
		AlignCentreAction.getInstance().setEnabled(enabled);
		AlignTopAction.getInstance().setEnabled(enabled);
		AlignMiddleAction.getInstance().setEnabled(enabled);
		AlignBottomAction.getInstance().setEnabled(enabled);
	}
}
