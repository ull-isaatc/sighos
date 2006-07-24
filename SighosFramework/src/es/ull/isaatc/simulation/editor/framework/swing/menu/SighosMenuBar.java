/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.swing.menu;

import javax.swing.JMenuBar;

/**
 * The menu bar of the framework
 * 
 * @author Roberto Muñoz
 */
public class SighosMenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;
	
	private static SighosMenuBar INSTANCE = null;
	
	public static SighosMenuBar getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SighosMenuBar();
		}
		return INSTANCE;
	}

	private SighosMenuBar() {
		super();
		
		add(new ProjectMenu());
		add(new PluginsMenu());
		add(new HelpMenu());		
	}
}
