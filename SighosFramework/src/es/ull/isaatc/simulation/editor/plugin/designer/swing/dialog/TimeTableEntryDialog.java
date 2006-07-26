package es.ull.isaatc.simulation.editor.plugin.designer.swing.dialog;

import info.clearthought.layout.TableLayout;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.bind.JAXBException;

import es.ull.isaatc.simulation.editor.framework.actions.swing.SighosDialogComponentAction;
import es.ull.isaatc.simulation.editor.framework.swing.dialog.SighosDialog;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.CommonFreqComboBox;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.ResourceTypeListPanel;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.XMLEditorPanel;
import es.ull.isaatc.simulation.editor.project.model.XMLModelUtilities;
import es.ull.isaatc.simulation.editor.project.model.Resource.TimeTable;
import es.ull.isaatc.simulation.editor.project.model.Resource.TimeTable.Duration;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;
import es.ull.isaatc.simulation.xml.CommonFreq;

public class TimeTableEntryDialog extends SighosDialog {

	private static final long serialVersionUID = 1L;
	
	private JPanel mainPanel;
	
	private TimeTable ttEditionEntry;

	private JPanel durationPanel;
	
	private JTextField durationValue;
	
	private CommonFreqComboBox durationTimeUnit;
	
	private XMLEditorPanel xmlEditorPanel;
	
	private ResourceTypeListPanel resourceTypeList;

	public TimeTableEntryDialog(Component parent, TimeTable ttEntry) {
		super(parent);
		ttEditionEntry = (TimeTable)ttEntry.clone();
		initValues();
		
	}

	protected void initialize() {
		this.setSize(new Dimension(500, 400));
		setModal(true);
		super.initialize();
		setTitle(ResourceLoader.getMessage("resource_timetable_entry_dialog"));
	}
	
	private void initValues() {
		xmlEditorPanel.setEditorText(ttEditionEntry.getCycle());
		resourceTypeList.setSelection(ttEditionEntry.getRTList());
		durationTimeUnit.setSelectedItem(ttEditionEntry.getDuration().getTimeUnit().value());
		durationValue.setText("" + ttEditionEntry.getDuration().getValue());
	}
	
	protected JPanel getMainPanel() {
		mainPanel = new JPanel();
		TableLayout panelLayout = new TableLayout(new double[][] {
				{ TableLayout.FILL, 0.4},
				{ TableLayout.FILL, 0.35 } });
		panelLayout.setHGap(5);
		panelLayout.setVGap(5);
		mainPanel.setLayout(panelLayout);
		mainPanel.add(getXMLEditorPanel(), "0, 0, 0, 1");
		mainPanel.add(getResourceTypeList(), "1, 0");
		mainPanel.add(getDurationPanel(), "1, 1");
		
		return mainPanel;
	}

	/**
	 * This method initializes xmlEditorPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private XMLEditorPanel getXMLEditorPanel() {
		if (xmlEditorPanel == null) {
			xmlEditorPanel = new XMLEditorPanel();
			xmlEditorPanel.setBorder(BorderFactory.createTitledBorder(ResourceLoader.getMessage("resource_timetable_cycle")));
			xmlEditorPanel.getEditor().addFocusListener(new CycleAction(this, xmlEditorPanel.getEditor()));
		}
		return xmlEditorPanel;		
	}

	/**
	 * This method initializes resourceTypeList	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private ResourceTypeListPanel getResourceTypeList() {
		if (resourceTypeList == null) {
			resourceTypeList = new ResourceTypeListPanel();
			resourceTypeList.setBorder(BorderFactory.createTitledBorder(ResourceLoader.getMessage("resource_timetable_roles")));
			resourceTypeList.getList().addFocusListener(new RolesAction(this, null));
		}
		return resourceTypeList;		
	}
	
	/**
	 * This method initializes durationPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getDurationPanel() {
		if (durationPanel == null) {
			durationPanel = new JPanel();
			TableLayout durationPanelLayout = new TableLayout(new double[][] {
					{ TableLayout.FILL, TableLayout.FILL },
					{ 20.0, 20.0, TableLayout.FILL} });
			durationPanelLayout.setHGap(0);
			durationPanelLayout.setVGap(5);
			durationPanel.setLayout(durationPanelLayout);
			durationPanel.setBorder(BorderFactory.createTitledBorder(ResourceLoader.getMessage("resource_timetable_duration")));
			durationPanel.add(new JLabel(ResourceLoader.getMessage("resource_timetable_duration_timeunit")), "0,  0");
			durationPanel.add(new JLabel(ResourceLoader.getMessage("resource_timetable_duration_value")), "0, 1");
			durationTimeUnit = new CommonFreqComboBox();
			durationPanel.add(durationTimeUnit, "1,  0");
			durationValue = new JTextField();
			durationPanel.add(durationValue, "1, 1");
		}
		return durationPanel;
	}

	private Duration getDuration() {
		Duration dur = new Duration();
		dur.setTimeUnit(CommonFreq.valueOf((String)durationTimeUnit.getSelectedItem()));
		dur.setValue(Integer.valueOf(durationValue.getText()));
		return dur;
	}

	protected boolean apply() {
		ttEditionEntry.setRTList(resourceTypeList.getRTList());
		ttEditionEntry.setCycle(xmlEditorPanel.getEditorText());
		ttEditionEntry.setDuration(getDuration());
		return true;
	}

	public TimeTable getTimeTable() {
		return ttEditionEntry;
	}

	class CycleAction extends SighosDialogComponentAction {

		private static final long serialVersionUID = 1L;

		public CycleAction(SighosDialog dialog, Component component) {
			super(dialog, component);
			putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("resource_timetable_cycle_long"));
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
			putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("resource_timetable_roles_long"));
		}
	}

	class DurationAction extends SighosDialogComponentAction {

		private static final long serialVersionUID = 1L;

		public DurationAction(SighosDialog dialog, Component component) {
			super(dialog, component);
			putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("resource_timetable_duration_long"));
		}
	}
	
}
