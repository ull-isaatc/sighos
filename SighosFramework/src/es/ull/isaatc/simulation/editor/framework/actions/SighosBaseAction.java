/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import es.ull.isaatc.simulation.editor.framework.SighosFramework;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

/**
 * 
 * @author Roberto Muñoz
 */
public abstract class SighosBaseAction extends AbstractAction {

	protected ImageIcon getIconByName(String iconName) {
		return ResourceLoader
				.getImageAsIcon("/es/ull/isaatc/simulation/editor/framework/resources/menuicons/"
						+ iconName + "16.gif");
	}

	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(SighosFramework.getInstance(),
				"The action labelled \"" + getValue(Action.NAME)
						+ "\" is not yet implemented.\n\n"
						+ "We apologise for the inconvenience.", "Oopsies!",
				JOptionPane.INFORMATION_MESSAGE);
	}
}
