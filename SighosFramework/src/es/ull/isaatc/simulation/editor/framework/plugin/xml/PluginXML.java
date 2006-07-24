/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.plugin.xml;

/**
 * Maps a plugin XML avaiable entry
 *  
 * @author Roberto Muñoz
 */
public class PluginXML {

	private String description;
	
	private String pluginClass;

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the pluginClass
	 */
	public String getPluginClass() {
		return pluginClass;
	}

	/**
	 * @param pluginClass the pluginClass to set
	 */
	public void setPluginClass(String pluginClass) {
		this.pluginClass = pluginClass;
	}
}
