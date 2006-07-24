/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.actions.swing;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Action;

import es.ull.isaatc.simulation.editor.framework.actions.SighosBaseAction;
import es.ull.isaatc.simulation.editor.framework.swing.dialog.SighosDialog;

/**
 * @author Roberto Muñoz
 *
 */
public class SighosDialogComponentAction extends SighosBaseAction implements FocusListener {

	private static final long serialVersionUID = 1L;
	
	private SighosDialog dialog;
	
	protected Component component;
	
	/**
	 * 
	 */
	public SighosDialogComponentAction(SighosDialog dialog, Component component) {
		super();
		this.dialog = dialog;
		this.component = component;
	}

	public void focusGained(FocusEvent e) {
		dialog.setComponentLabel((String) getValue(Action.LONG_DESCRIPTION));
	}

	public void focusLost(FocusEvent e) {
		dialog.setComponentLabel("");
		if (!validate()) {
			component.requestFocus();
		}
	}
	
	protected boolean validate() {
		return true;
	}
}
