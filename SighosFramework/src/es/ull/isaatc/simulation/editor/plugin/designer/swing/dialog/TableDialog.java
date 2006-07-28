/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.swing.dialog;

import java.awt.Dimension;

import javax.swing.JDialog;

import es.ull.isaatc.simulation.editor.framework.swing.table.TablePanel;


/**
 * @author Roberto Muñoz
 *
 */
public class TableDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public TableDialog() {
		super();
		initialize();
	}

	/**
	 * Initializes this dialog
	 */
	protected void initialize() {
		
		this.setSize(new Dimension(400, 350));
		
		this.setResizable(true);
		this.setModal(true);
	}
	
	public void setTable(TablePanel tablePanel) {
		this.setContentPane(tablePanel);
	}
}
