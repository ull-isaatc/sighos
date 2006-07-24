/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.swing.menu;

import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.JMenu;

import es.ull.isaatc.simulation.editor.framework.SighosFramework;
import es.ull.isaatc.simulation.editor.framework.plugin.PluginFactory;
import es.ull.isaatc.simulation.editor.framework.plugin.xml.PluginXML;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

/**
 * 
 * @author Roberto Muñoz
 */
class PluginsMenu extends JMenu {

	private static final long serialVersionUID = 1L;

	public PluginsMenu() {
		super(ResourceLoader.getMessage("view"));
		setMnemonic(KeyEvent.VK_V);
		buildInterface();
	}

	protected void buildInterface() {
		// TODO: Hacer que en este menu se carguen todos los plugins existentes
		// hay que hacerlo a partir de la configuración del framework
		Iterator<PluginXML> plIt = SighosFramework.getInstance()
				.getAvaiablePlugins().iterator();
		while (plIt.hasNext()) {
			add(new SighosMenuItem(PluginFactory.getInstance(
					plIt.next().getPluginClass()).getPluginAction()));
		}
	}
}
