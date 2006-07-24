/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.swing.menu;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;

import es.ull.isaatc.simulation.editor.framework.swing.menu.SighosMenuItem;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.model.*;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

/**
 * 
 * @author Roberto Muñoz
 */
class ModelMenu extends JMenu {

	private static final long serialVersionUID = 1L;

	public ModelMenu() {
		super(ResourceLoader.getMessage("model"));
		setMnemonic(KeyEvent.VK_M);
		buildInterface();
	}

	protected void buildInterface() {
		add(new SighosMenuItem(new CreateRootFlowAction()));
		addSeparator();
	}
}
