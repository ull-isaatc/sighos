package es.ull.isaatc.simulation.editor.plugin.designer;

import javax.swing.JComponent;

import es.ull.isaatc.simulation.editor.framework.actions.plugin.LoadPluginAction;
import es.ull.isaatc.simulation.editor.framework.plugin.Plugin;
import es.ull.isaatc.simulation.editor.framework.plugin.SighosPluginMenu;
import es.ull.isaatc.simulation.editor.framework.swing.SighosTabbedPane;
import es.ull.isaatc.simulation.editor.plugin.designer.actions.DesignerLoadPluginAction;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.DesignerDesktop;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.menu.DesignerMenu;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.table.ActivityTable;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.table.ElementTypeTable;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.table.ResourceTable;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.table.ResourceTypeTable;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.table.RootFlowTable;
import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class SighosDesignerPlugin implements Plugin {

	private static final long serialVersionUID = 1L;

	private static DesignerDesktop desktop = null;

	private static DesignerLoadPluginAction loadAction = null;

	private static DesignerMenu menu = null;

	private static ResourceTypeTable resourceTypeTable;

	private static ResourceTable resourceTable;

	private static ActivityTable activityTable;
	
	private static ElementTypeTable elementTypeTable;

	private static RootFlowTable rootFlowTable;

	private static SighosDesignerPlugin INSTANCE = null;

	public static Plugin getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SighosDesignerPlugin();
		}
		return INSTANCE;
	}

	/**
	 * This is the default constructor
	 */
	public SighosDesignerPlugin() {
		super();
		INSTANCE = this;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

	}

	private DesignerDesktop getDesktop() {
		if (desktop == null) {
			desktop = DesignerDesktop.getInstance();
		}
		return desktop;
	}

	private DesignerLoadPluginAction getLoadAction() {
		if (loadAction == null) {
			loadAction = new DesignerLoadPluginAction(INSTANCE);
		}
		return loadAction;
	}

	private DesignerMenu getMenu() {
		if (menu == null) {
			menu = new DesignerMenu();
		}
		return menu;
	}

	private void loadTabs() {
		SighosTabbedPane tabbedPane = SighosTabbedPane.getInstance();
		if (resourceTypeTable == null) {
			resourceTypeTable = new ResourceTypeTable(ProjectModel
					.getInstance().getModel().getResourceTypeTableModel());
			resourceTable = new ResourceTable(ProjectModel.getInstance()
					.getModel().getResourceTableModel());
			activityTable = new ActivityTable(ProjectModel.getInstance()
					.getModel().getActivityTableModel());
			elementTypeTable = new ElementTypeTable(ProjectModel.getInstance()
					.getModel().getElementTypeTableModel());
			rootFlowTable = new RootFlowTable(ProjectModel.getInstance()
					.getModel().getRootFlowTableModel());
		}
		tabbedPane.add(ResourceLoader.getMessage("resourcetypes"),
				resourceTypeTable);
		tabbedPane.add(ResourceLoader.getMessage("resources"), resourceTable);
		tabbedPane.add(ResourceLoader.getMessage("activities"), activityTable);
		tabbedPane.add(ResourceLoader.getMessage("elementtypes"), elementTypeTable);
		tabbedPane.add(ResourceLoader.getMessage("rootflows"), rootFlowTable);
		DesignerDesktop.getInstance().updateState();
	}

	public boolean loadPlugin() {
		loadTabs();
		return true;
	}

	public void removePlugin() {
		// TODO Auto-generated method stub

	}

	public JComponent getPluginDesktop() {
		return getDesktop();
	}

	public LoadPluginAction getPluginAction() {
		return getLoadAction();
	}

	public SighosPluginMenu getPluginMenu() {
		return getMenu();
	}

	public RootFlowTable getRootFlowTable() {
		return rootFlowTable;
	}
}
