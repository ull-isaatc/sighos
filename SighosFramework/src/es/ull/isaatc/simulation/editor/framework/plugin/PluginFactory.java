/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Roberto
 * 
 */
public class PluginFactory {

	public static Plugin getInstance(String classPath) {
		try {
			Method getter = Class.forName(classPath).getMethod("getInstance");
			return (Plugin) getter.invoke(null);

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
