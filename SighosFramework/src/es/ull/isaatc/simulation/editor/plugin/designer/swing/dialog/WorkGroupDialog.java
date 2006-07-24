package es.ull.isaatc.simulation.editor.plugin.designer.swing.dialog;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.xml.bind.JAXBException;

import es.ull.isaatc.simulation.editor.framework.actions.swing.SighosDialogComponentAction;
import es.ull.isaatc.simulation.editor.framework.swing.dialog.SighosDialog;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.ResourceTypeComboBox;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.XMLEditorPanel;
import es.ull.isaatc.simulation.editor.project.model.ResourceType;
import es.ull.isaatc.simulation.editor.project.model.XMLModelUtilities;
import es.ull.isaatc.simulation.editor.project.model.Activity.WorkGroup;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;
import es.ull.isaatc.swing.IntegerTextField;
import es.ull.isaatc.swing.table.SighosTable;
import es.ull.isaatc.swing.table.TablePopupListener;

public class WorkGroupDialog extends SighosDialog {

	private static final long serialVersionUID = 1L;

	private WorkGroup wgEditionEntry;

	private JPanel propertyPanel;
	
	private JTextField description;

	private IntegerTextField priority;

	private XMLEditorPanel xmlEditorPanel;

	private JScrollPane rolesTable;

	private RolesNeededTableModel rnTableModel;

	public WorkGroupDialog(WorkGroup wgEntry) {
		super();
		wgEditionEntry = (WorkGroup) wgEntry.clone();
		initValues();

	}

	protected void initialize() {
		this.setSize(new Dimension(650, 400));
		setModal(true);
		super.initialize();
		setTitle(ResourceLoader.getMessage("activity_workgroup_dialog"));
	}

	private void initValues() {
		xmlEditorPanel.setEditorText(wgEditionEntry.getDuration());
		rnTableModel.setSelection(wgEditionEntry.getResourceType());
		description.setText(wgEditionEntry.getDescription());
		priority.setText("" + wgEditionEntry.getPriority());
	}

	protected JPanel getMainPanel() {
		JPanel panel = new JPanel();
		TableLayout panelLayout = new TableLayout(new double[][] {
				{ TableLayout.FILL, 0.4 }, { 60, TableLayout.FILL } });
		panelLayout.setHGap(5);
		panelLayout.setVGap(5);
		panel.setLayout(panelLayout);
		panel.add(getPropPane(), "0, 0, 1, 0");
		panel.add(getXMLEditorPanel(), "0, 1");
		panel.add(getRolesPane(), "1, 1");
		return panel;
	}

	/**
	 * This method initializes xmlEditorPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private XMLEditorPanel getXMLEditorPanel() {
		if (xmlEditorPanel == null) {
			xmlEditorPanel = new XMLEditorPanel();
			xmlEditorPanel.setBorder(BorderFactory.createTitledBorder(ResourceLoader.getMessage("activity_workgroup_duration")));
			xmlEditorPanel.getEditor().addFocusListener(new DurationAction(this, xmlEditorPanel.getEditor()));
		}
		return xmlEditorPanel;
	}

	/**
	 * This method initializes rolesTable
	 * 
	 * @return javax.swing.JPanel
	 */
	private JScrollPane getRolesPane() {
		if (rolesTable == null) {
			rolesTable = new JScrollPane();
			rolesTable.setBorder(BorderFactory
					.createTitledBorder(ResourceLoader
							.getMessage("activity_workgroup_roles")));
			rnTableModel = new RolesNeededTableModel();
			rnTableModel.add(new ResourceType(), 1);
			final SighosTable table = new SighosTable(rnTableModel, null);
			table.getColumnModel().getColumn(0).setCellEditor(
					new DefaultCellEditor(new ResourceTypeComboBox()));
			AbstractAction deleteAction = new AbstractAction() {
				{
					putValue(Action.SHORT_DESCRIPTION, ResourceLoader
							.getMessage("table_delete"));
					putValue(Action.NAME, ResourceLoader
							.getMessage("table_delete"));
					putValue(Action.LONG_DESCRIPTION, ResourceLoader
							.getMessage("table_delete"));
				}

				public void actionPerformed(ActionEvent ev) {
					((RolesNeededTableModel) table.getModel()).remove(table
							.getSelectedRow());
				}
			};
			table.addMouseListener(new TablePopupListener(table,
					new AbstractAction[] { deleteAction }));

			rolesTable.setViewportView(table);
			rolesTable.getViewport().setBackground(Color.WHITE);
			table.addFocusListener(new RolesAction(this, table));
			rolesTable.setOpaque(true);
		}
		return rolesTable;
	}

	/**
	 * This method initializes propertyPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPropPane() {
		if (propertyPanel == null) {
			propertyPanel = new JPanel();
			TableLayout priorityPanelLayout = new TableLayout(new double[][] {
					{ 100, 30, TableLayout.FILL },
					{ 5.0, 20.0, 20.0, 5.0 } });
			priorityPanelLayout.setHGap(0);
			priorityPanelLayout.setVGap(5);
			propertyPanel.setLayout(priorityPanelLayout);
			propertyPanel.setBorder(BorderFactory.createEtchedBorder());
			propertyPanel.add(new JLabel(ResourceLoader.getMessage("component_description")), "0, 1");
			propertyPanel.add(new JLabel(ResourceLoader.getMessage("activity_workgroup_priority")), "0, 2");
			description = new JTextField();
			priority = new IntegerTextField();
			priority.addFocusListener(new PriorityAction(this, priority));
			propertyPanel.add(description, "1, 1, 2, 1");
			propertyPanel.add(priority, "1, 2");
		}
		return propertyPanel;
	}

	protected void okButtonAction() {
		wgEditionEntry.setDescription(description.getText());
		wgEditionEntry.setDuration(xmlEditorPanel.getEditorText());
		wgEditionEntry.setResourceType(rnTableModel.getHashMap());
		wgEditionEntry.setPriority(priority.getValue());
		setVisible(false);
	}

	protected void cancelButtonAction() {
		wgEditionEntry = null;
		setVisible(false);
	}

	public WorkGroup getWorkGroup() {
		return wgEditionEntry;
	}
	/**
	* This method should return an instance of this class which does 
	* NOT initialize it's GUI elements. This method is ONLY required by
	* Jigloo if the superclass of this class is abstract or non-public. It 
	* is not needed in any other situation.
	 */
	public static Object getGUIBuilderInstance() {
		return new WorkGroupDialog(Boolean.FALSE);
	}
	
	/**
	 * This constructor is used by the getGUIBuilderInstance method to
	 * provide an instance of this class which has not had it's GUI elements
	 * initialized (ie, initGUI is not called in this constructor).
	 */
	public WorkGroupDialog(Boolean initGUI) {
		super();
	}

	class DurationAction extends SighosDialogComponentAction {

		private static final long serialVersionUID = 1L;

		public DurationAction(SighosDialog dialog, Component component) {
			super(dialog, component);
			putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("activity_workgroup_duration_long"));
		}
		
		protected boolean validate() {
			JTextArea editor = (JTextArea) component;
			if (editor.getText().equals(""))
				return true;
			try {
				XMLModelUtilities.validate("es.ull.isaatc.simulation.editor.project.model.validation.ValidationObjectFactory", XMLModelUtilities.COMPONENT_XSD, "<component>" + editor.getText() + "</component>");
				return true;
			} catch (JAXBException e) {
				xmlEditorPanel.setErrorText(e.getMessage());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	
	class RolesAction extends SighosDialogComponentAction {

		private static final long serialVersionUID = 1L;

		public RolesAction(SighosDialog dialog, Component component) {
			super(dialog, component);
			putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("activity_workgroup_roles_long"));
		}
	}

	class PriorityAction extends SighosDialogComponentAction {

		private static final long serialVersionUID = 1L;

		public PriorityAction(SighosDialog dialog, Component component) {
			super(dialog, component);
			putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("activity_workgroup_priority_long"));
		}
	}


	class RolesNeededTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		/** Column names */
		protected String[] columnNames;

		/** Selected roles */
		protected List<ResourceType> selectedRoles = new ArrayList<ResourceType>();

		protected List<Integer> rolUnits = new ArrayList<Integer>();

		protected WorkGroup wg;

		public RolesNeededTableModel() {
			super();

			columnNames = new String[2];
			columnNames[0] = ResourceLoader
					.getMessage("activity_workgroup_dialog_role");
			columnNames[1] = ResourceLoader
					.getMessage("activity_workgroup_dialog_needed");
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return selectedRoles.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return selectedRoles.get(row);
			case 1:
				return rolUnits.get(row);
			}
			return null;
		}

		public boolean isCellEditable(int row, int col) {
			switch (col) {
			case 0: // rol description
				return true;
			case 1: // units
				return true;
			default:
				return false;
			}

		}

		public void setValueAt(Object value, int row, int col) {
			switch (col) {
			case 0: // resource type
				if (selectedRoles.size() - 1 != row) {
					selectedRoles.remove(row);
					selectedRoles.add(row, (ResourceType) value);
				} else {
					selectedRoles.add(row, (ResourceType) value);
					rolUnits.add(0);
				}
				break;
			case 1: // units
				if (selectedRoles.size() - 1 != row)
					rolUnits.remove(row);
				rolUnits.add(row, Integer.valueOf((String) value));
				break;
			}
		}

		public void add(ResourceType obj, int units) {
			selectedRoles.add(obj);
			rolUnits.add(units);
			fireTableDataChanged();
		}

		public void remove(int index) {
			selectedRoles.remove(index);
			rolUnits.remove(index);
			fireTableDataChanged();
		}

		public void setSelection(HashMap<ResourceType, Integer> selection) {
			Iterator<ResourceType> it = selection.keySet().iterator();
			while (it.hasNext()) {
				ResourceType rt = it.next();
				int pos = selectedRoles.size() - 1;
				selectedRoles.add(pos, rt);
				rolUnits.add(pos, selection.get(rt));

			}
		}

		public HashMap<ResourceType, Integer> getHashMap() {
			HashMap<ResourceType, Integer> hash = new HashMap<ResourceType, Integer>();
			for (int i = 0; i < selectedRoles.size() - 1; i++)
				hash.put(selectedRoles.get(i), rolUnits.get(i));
			return hash;
		}
	}
}
