/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.swing.menu;

import java.awt.event.KeyEvent;

import javax.swing.JMenu;

import es.ull.isaatc.simulation.editor.framework.actions.ExitAction;
import es.ull.isaatc.simulation.editor.framework.actions.project.*;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

/**
 * 
 * @author Roberto Muñoz
 */
class ProjectMenu extends JMenu {

	private static final long serialVersionUID = 1L;

	public ProjectMenu() {
		super(ResourceLoader.getMessage("project"));
		setMnemonic(KeyEvent.VK_P);
		buildInterface();
	}

	protected void buildInterface() {
		add(new SighosMenuItem(new CreateProjectAction()));
		add(new SighosMenuItem(new OpenProjectAction()));
		addSeparator();
		add(new SighosMenuItem(new SaveProjectAction()));
		add(new SighosMenuItem(new SaveProjectAsAction()));
		addSeparator();
		add(new SighosMenuItem(new ValidateModelAction()));
		addSeparator();
		add(new SighosMenuItem(new CloseProjectAction()));
		add(new SighosMenuItem(new ExitAction(this)));
	}
}
