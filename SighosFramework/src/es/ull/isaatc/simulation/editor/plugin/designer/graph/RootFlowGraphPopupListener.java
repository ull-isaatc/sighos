package es.ull.isaatc.simulation.editor.plugin.designer.graph;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.cell.SighosCell;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.menu.CellPopupMenu;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.menu.PalettePopupMenu;

import java.util.HashMap;

public class RootFlowGraphPopupListener extends MouseAdapter {
	private static final PalettePopupMenu palettePopup = new PalettePopupMenu();

	private final HashMap<Object, CellPopupMenu> vertexPopupHash = new HashMap<Object, CellPopupMenu>();

	private RootFlowGraph graph;

	public RootFlowGraphPopupListener(RootFlowGraph graph) {
		this.graph = graph;
	}

	public void mousePressed(MouseEvent event) {
		Object cell = graph.getFirstCellForLocation(event.getX(), event.getY());

		if (SwingUtilities.isRightMouseButton(event)) {
			if (cell instanceof SighosCell) {
				getCellPopup(cell).show(graph, event.getX(), event.getY());
			}
			// TODO: show the poup related to the connections
			if (cell == null) {
				palettePopup.show(graph, event.getX(), event.getY());
			}
		}
	}

	private CellPopupMenu getCellPopup(Object cell) {
		if (!vertexPopupHash.containsKey(cell)) {
			vertexPopupHash.put(cell, new CellPopupMenu(graph, (SighosCell) cell));
		}
		return vertexPopupHash.get(cell);
	}
}
