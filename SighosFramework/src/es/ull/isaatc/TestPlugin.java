package es.ull.isaatc;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import es.ull.isaatc.simulation.editor.framework.plugin.Plugin;
import es.ull.isaatc.simulation.editor.framework.plugin.PluginFactory;
import es.ull.isaatc.simulation.editor.framework.plugin.SighosPluginMenu;
import es.ull.isaatc.simulation.editor.plugin.designer.SighosDesignerPlugin;

/**
 * 
 */

/**
 * @author Roberto
 *
 */
public class TestPlugin {

	
	
	public TestPlugin() {
		JFrame frame = new JFrame();
		PluginFactory.getInstance("es.ull.isaatc.simulation.editor.plugin.designer.SighosDesignerPlugin");
		Plugin pl = SighosDesignerPlugin.getInstance();
		frame.setContentPane(pl.getPluginDesktop());
		JMenuBar mb = new JMenuBar();
		SighosPluginMenu menu = pl.getPluginMenu();
		while (menu.hasNext()) {
			mb.add(menu.next());
		}
		frame.setJMenuBar(mb);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new TestPlugin();
	}
}
