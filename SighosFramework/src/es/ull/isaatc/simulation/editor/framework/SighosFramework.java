package es.ull.isaatc.simulation.editor.framework;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;
import com.thoughtworks.xstream.XStream;

import es.ull.isaatc.simulation.editor.framework.plugin.xml.PluginListXML;
import es.ull.isaatc.simulation.editor.framework.plugin.xml.PluginXML;
import es.ull.isaatc.simulation.editor.framework.swing.SighosFrameworkDesktop;
import es.ull.isaatc.simulation.editor.framework.swing.menu.SighosMenuBar;
import es.ull.isaatc.simulation.editor.project.ArchivingThread;

public class SighosFramework extends JFrame {

	private static final long serialVersionUID = 1L;

	private static List<PluginXML> avaiablePlugins = null;

	private static SighosFramework INSTANCE = null;

	public static SighosFramework getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SighosFramework();
		}
		return INSTANCE;
	}

	/**
	 * This is the default constructor
	 */
	public SighosFramework() {
		super();
		INSTANCE = this;
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		loadPreferences();
		this.setJMenuBar(SighosMenuBar.getInstance());
		this.setContentPane(SighosFrameworkDesktop.getInstance());
		this.setTitle("Sighos Framework");
		ArchivingThread.getInstance().start();
		installEventListeners();
	}

	/**
	 * Load the application preferences
	 */
	private void loadPreferences() {
		loadAvaiablePlugins();
	}

	/**
	 * Read the plugins configuration file and stores the avaiable plugins
	 */
	private void loadAvaiablePlugins() {
		XStream xstream = new XStream();
		xstream.alias("Plugins", PluginListXML.class);
		xstream.alias("plugin", PluginXML.class);
		xstream.addImplicitCollection(PluginListXML.class, "plugins");
		xstream.setMode(XStream.NO_REFERENCES);
		String fullFileName = "plugin.xml";
		PluginListXML list;
		try {
			list = (PluginListXML) xstream.fromXML(new FileInputStream(
					fullFileName));
			avaiablePlugins = list.getPlugins();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the avaiablePlugins
	 */
	public List<PluginXML> getAvaiablePlugins() {
		if (avaiablePlugins == null)
			avaiablePlugins = new ArrayList<PluginXML>();
		return avaiablePlugins;
	}

	private void installEventListeners() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			private boolean closing = false;

			public void windowClosing(WindowEvent we) {
				synchronized (this) {
					if (!closing) {
						closing = true;
						ArchivingThread.getInstance().exit();
					}
				}
			}
		});
	}

	public static void main(String[] args) {
		try {
			SkinLookAndFeel.setSkin(SkinLookAndFeel
					.loadThemePack(".\\lib\\themepack.zip"));

			UIManager
					.setLookAndFeel("com.l2fprod.gui.plaf.skin.SkinLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
		} catch (Exception e) {
		}
		SighosFramework frame = SighosFramework.getInstance();
		// Set Default Size
		frame.setSize(900, 600);
		// frame.setExtendedState(JFrame.MAXIMIZED_HORIZ);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Show Frame
		frame.setVisible(true);
	}
}
