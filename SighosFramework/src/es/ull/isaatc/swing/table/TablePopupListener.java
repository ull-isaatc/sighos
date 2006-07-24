/**
 * 
 */
package es.ull.isaatc.swing.table;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * @author Roberto Muñoz
 * 
 */
public class TablePopupListener extends MouseAdapter {
	private TablePopupMenu popupMenu;

	private SighosTable table;

	public TablePopupListener(SighosTable table, AbstractAction actions[]) {
		this.table = table;
		popupMenu = new TablePopupMenu(actions);
	}

	public void mousePressed(MouseEvent event) {
		if (SwingUtilities.isRightMouseButton(event)) {
			popupMenu.show(table, event.getX(), event.getY());
		}
	}
	
	public SighosTable getTable() {
		return table;
	}

	/**
	 * 
	 * @author Roberto Muñoz
	 *
	 */
	static class TablePopupMenu extends JPopupMenu {

		private static final long serialVersionUID = 1L;

		public TablePopupMenu(AbstractAction actions[]) {
			for (int i = 0; i < actions.length; i++)
				add(actions[i]);
		}
	}
}
