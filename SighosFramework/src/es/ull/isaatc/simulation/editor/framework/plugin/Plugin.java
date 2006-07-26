/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.plugin;

import javax.swing.JComponent;

import es.ull.isaatc.simulation.editor.framework.actions.plugin.LoadPluginAction;

/**
 * The interface that every plugin must implement
 * @author Roberto Muñoz
 */
public interface Plugin {

	public boolean loadPlugin();

	public void removePlugin();

	public JComponent getPluginDesktop();

	public LoadPluginAction getPluginAction();

	public SighosPluginMenu getPluginMenu();
	
	/**
	 * Reset the plugin.
	 */
	public void reset();
		
}
