/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.swing.menu;

import es.ull.isaatc.simulation.editor.framework.plugin.SighosPluginMenu;

/**
 * @author Roberto
 *
 */
public class DesignerMenu extends SighosPluginMenu {

	@Override
	protected void buildInterface() {
		//TODO: add menus
		add(new ModelMenu());		
	}
}
