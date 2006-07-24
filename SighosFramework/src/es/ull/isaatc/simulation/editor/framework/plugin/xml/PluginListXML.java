/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.plugin.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps a plugin XML avaiable entry
 *  
 * @author Roberto Muñoz
 */
public class PluginListXML {

	private List<PluginXML> plugins = new ArrayList<PluginXML>();

	/**
	 * @return the plugins
	 */
	public List<PluginXML> getPlugins() {
		return plugins;
	}

	/**
	 * @param plugins the plugins to set
	 */
	public void setPlugins(List<PluginXML> plugins) {
		this.plugins = plugins;
	}
	
	/**
	 * Add an entry to the list
	 * @param plg
	 */
	public void add(PluginXML plg) {
		plugins.add(plg);
	}

}
