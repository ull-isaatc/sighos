package grafical.sighos.plugin.tree.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class TreeViewerPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static TreeViewerPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public TreeViewerPlugin() {

		plugin = this;
		try {
			resourceBundle= ResourceBundle.getBundle("grafical.sighos.plugin.TreeviewerPluginResources");
			
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static TreeViewerPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle= TreeViewerPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}
	
	public static ImageDescriptor getImageDescriptor(String name) {
		String iconPath = "icons/";
		try {
			URL url = new URL("file", null, "C:/Users/kimi/workspace/grafical.sighos.plugin/");
			URL urlFinal = new URL( url, iconPath + name);
			return ImageDescriptor.createFromURL(urlFinal);
		} catch (MalformedURLException e) {
			// should not happen
			return ImageDescriptor.getMissingImageDescriptor();
			/*
			 * 
			 *  String ID = "rcp.eclipse";
                 public static final String MY_IMAGE_ID = 
                                  "image.myimage"
	        Bundle bundle = Platform.getBundle(ID);

	        ImageDescriptor myImage = ImageDescriptor.createFromURL(
	              FileLocator.find(bundle,
	                               new Path("icons/myImage..gif"),
	                                        null));
	        registry.put(MY_IMAGE_ID, myImage)*/
			
			
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}

