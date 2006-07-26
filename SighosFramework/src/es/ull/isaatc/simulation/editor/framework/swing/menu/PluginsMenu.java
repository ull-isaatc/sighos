/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.swing.menu;

import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.JMenu;

import es.ull.isaatc.simulation.editor.framework.SighosFramework;
import es.ull.isaatc.simulation.editor.framework.plugin.Plugin;
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
		Iterator<PluginXML> plIt = SighosFramework.getInstance()
				.getAvaiablePlugins().iterator();
		while (plIt.hasNext()) {
			Plugin pl = PluginFactory.getInstance(plIt.next().getPluginClass());
			SighosFramework.getInstance().addPlugin(pl);
			add(new SighosMenuItem(pl.getPluginAction()));
		}
	}
}
