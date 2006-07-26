/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.plugin;

import es.ull.isaatc.simulation.editor.framework.swing.SighosFrameworkDesktop;
import es.ull.isaatc.simulation.editor.framework.swing.menu.SighosMenuBar;

/**
 * Manages the plugins
 * 
 * @author Roberto Muñoz
 */
public class PluginHandler {

	private Plugin currentPlugin = null;

	private static final PluginHandler INSTANCE = new PluginHandler();

	public static PluginHandler getInstance() {
		return INSTANCE;
	}

	public Plugin getCurrentPlugin() {
		return currentPlugin;
	}

	public void loadPlugin(Plugin plugin) {
		if (currentPlugin == plugin)
			return;

		removeCurrentPlugin();
		this.currentPlugin = plugin;

		//TODO: check the model for things to do
		// before enables the menus

		/** Set the plugin desktop as the framework desktop */
		SighosFrameworkDesktop.getInstance().setDesktop(
				plugin.getPluginDesktop());

		/** Set the plugin menu as the framework menu */
		loadPluginMenu(plugin.getPluginMenu());
		
		plugin.loadPlugin();
	}

	private void removeCurrentPlugin() {
		if (currentPlugin != null)
			removePluginMenu(currentPlugin.getPluginMenu());
		currentPlugin = null;
	}

	public void loadPluginMenu(SighosPluginMenu pluginMenu) {
		SighosMenuBar menuBar = SighosMenuBar.getInstance();
		while (pluginMenu.hasNext()) {
			menuBar.add(pluginMenu.next(), menuBar.getComponentCount() - 1);
		}
		menuBar.validate();
	}

	public void removePluginMenu(SighosPluginMenu pluginMenu) {
		SighosMenuBar menuBar = SighosMenuBar.getInstance();
		while (pluginMenu.hasNext()) {
			menuBar.remove(pluginMenu.next());
		}
		menuBar.validate();
	}
	
	public void reset() {
		removeCurrentPlugin();
	}
}
