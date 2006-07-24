package es.ull.isaatc.simulation.editor.framework.actions.plugin;

import java.awt.event.ActionEvent;

import es.ull.isaatc.simulation.editor.framework.actions.project.SighosOpenProjectAction;
import es.ull.isaatc.simulation.editor.framework.plugin.Plugin;
import es.ull.isaatc.simulation.editor.framework.plugin.PluginHandler;

public class LoadPluginAction extends SighosOpenProjectAction {
	private static final long serialVersionUID = 1L;
	
	private Plugin plugin;

	public LoadPluginAction(Plugin plugin) {
		this.plugin = plugin;		
	}
	
	public void actionPerformed(ActionEvent event) {
		PluginHandler.getInstance().loadPlugin(plugin);
	}
}
