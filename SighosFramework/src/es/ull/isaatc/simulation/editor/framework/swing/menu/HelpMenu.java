/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.swing.menu;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;

import es.ull.isaatc.simulation.editor.util.ResourceLoader;

class HelpMenu extends JMenu {

	private static final long serialVersionUID = 1L;

	public HelpMenu() {
		super(ResourceLoader.getMessage("help"));
		buildInterface();
	}

	protected void buildInterface() {
		setMnemonic(KeyEvent.VK_H);
//		add(new YAWLMenuItem(new ShowCopyrightDetailAction()));
//		add(new YAWLMenuItem(new ShowAcknowledgementsAction()));
//		add(new YAWLMenuItem(new ShowAboutEditorAction()));
	}
}
