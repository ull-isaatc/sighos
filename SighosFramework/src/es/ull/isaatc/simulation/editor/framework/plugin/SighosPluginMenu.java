/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.plugin;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JMenu;

/**
 * @author Roberto Muñoz
 */
public abstract class SighosPluginMenu {
	
	private ArrayList<JMenu> menuList = new ArrayList<JMenu>();
	
	private Iterator<JMenu> menuIt = null;
	
	public SighosPluginMenu() {
		super();
		buildInterface();
	}
	
	public JMenu next() {
		if (menuIt == null) {
			menuIt = menuList.iterator();
		}
		if (menuIt.hasNext())
			return menuIt.next();
		return null;
	}
	
	public boolean hasNext() {
		if (menuIt == null) {
			menuIt = menuList.iterator();
		}
		return menuIt.hasNext();
	}
	
	protected void add(JMenu menu) {
		menuList.add(menu);
	}
	
	protected abstract void buildInterface();
}
