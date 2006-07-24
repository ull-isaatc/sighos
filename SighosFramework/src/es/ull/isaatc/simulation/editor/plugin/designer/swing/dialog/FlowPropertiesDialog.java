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
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.ActivityComboBox;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.ResourceTypeComboBox;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.RootFlowComboBox;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.XMLEditorPanel;
import es.ull.isaatc.simulation.editor.project.model.Activity;
import es.ull.isaatc.simulation.editor.project.model.ResourceType;
import es.ull.isaatc.simulation.editor.project.model.RootFlow;
import es.ull.isaatc.simulation.editor.project.model.XMLModelUtilities;
import es.ull.isaatc.simulation.editor.project.model.Activity.WorkGroup;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;
import es.ull.isaatc.swing.IntegerTextField;
import es.ull.isaatc.swing.table.SighosTable;
import es.ull.isaatc.swing.table.TablePopupListener;

public class FlowPropertiesDialog extends SighosDialog {

	private static final long serialVersionUID = 1L;
	
	private JPanel activityPanel;

	private JPanel rootFlowPanel;
	
	private ActivityComboBox actComboBox;
	
	private RootFlowComboBox rfComboBox;
	
	private XMLEditorPanel xmlEditorPanel;
	
	private Activity activity;
	
	private RootFlow rootFlow;
	
	private String iterations;
	
	private static FlowPropertiesDialog INSTANCE = new FlowPropertiesDialog();
	
	public static FlowPropertiesDialog getInstance() {
		return INSTANCE;
	}

	private FlowPropertiesDialog() {
		super();
	}
	
	protected void initialize() {
		this.setSize(new Dimension(650, 400));
		setModal(true);
		super.initialize();
		setTitle(ResourceLoader.getMessage("flow_properties_dialog"));
	}

	public void initValues(String iterations, Activity activity) {
		setIterations(iterations);
		setActivity(activity);
	}

	public void initValues(String iterations, RootFlow rootFlow) {
		setIterations(iterations);
		setRootFlow(rootFlow);
	}
	
	public void initValues(String iterations) {
		setIterations(iterations);
	}

	private void setActivity(Activity activity) {
		this.activity = activity;
		actComboBox.setActivity(activity);
	}

	private void setRootFlow(RootFlow rootFlow) {
		this.rootFlow = rootFlow;
		rfComboBox.setRootFlow(rootFlow);
	}

	private void setIterations(String iterations) {
		this.iterations = iterations;
		xmlEditorPanel.setEditorText(iterations);
	}

	protected JPanel getMainPanel() {
		JPanel panel = new JPanel();
		TableLayout panelLayout = new TableLayout(new double[][] {
				{ TableLayout.FILL }, { 60, TableLayout.FILL } });
		panelLayout.setHGap(5);
		panelLayout.setVGap(5);
		panel.setLayout(panelLayout);
		panel.add(getPropPane(), "0, 0");
		panel.add(getXMLEditorPanel(), "0, 1");
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
			xmlEditorPanel.setBorder(BorderFactory.createTitledBorder(ResourceLoader.getMessage("flow_properties_iterations")));
//			xmlEditorPanel.getEditor().addFocusListener(new IterationsAction(this, xmlEditorPanel.getEditor()));
		}
		return xmlEditorPanel;		
	}

	/**
	 * This method initializes propertyPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPropPane() {
		if (activityPanel == null) {
			activityPanel = new JPanel();
			TableLayout priorityPanelLayout = new TableLayout(new double[][] {
					{ 100, TableLayout.FILL },
					{ 5.0, TableLayout.FILL, 5.0 } });
			priorityPanelLayout.setHGap(0);
			priorityPanelLayout.setVGap(5);
			activityPanel.setLayout(priorityPanelLayout);
			activityPanel.setBorder(BorderFactory.createEtchedBorder());
			activityPanel.add(new JLabel(ResourceLoader.getMessage("flow_properties_activity")), "0, 1");
			actComboBox = new ActivityComboBox();
			activityPanel.add(actComboBox, "0, 2");
		}
		return activityPanel;
	}

	/**
	 * @return the activity
	 */
	public Activity getActivity() {
		return activity;
	}

	/**
	 * @return the iterations
	 */
	public String getIterations() {
		return iterations;
	}

	/**
	 * @return the rootFlow
	 */
	public RootFlow getRootFlow() {
		return rootFlow;
	}
	
	protected void okButtonAction() {
		iterations = xmlEditorPanel.getEditorText();
		activity = (Activity) actComboBox.getSelectedItem();
		rootFlow = (RootFlow) rfComboBox.getSelectedItem();
		setVisible(false);
	}

	protected void cancelButtonAction() {
		iterations = null;
		setVisible(false);
	}

	class IterationsAction extends SighosDialogComponentAction {

		private static final long serialVersionUID = 1L;

		public IterationsAction(SighosDialog dialog, Component component) {
			super(dialog, component);
			putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("flow_properties_iterations_long"));
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
	
	class ActivityAction extends SighosDialogComponentAction {

		private static final long serialVersionUID = 1L;

		public ActivityAction(SighosDialog dialog, Component component) {
			super(dialog, component);
			putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("flow_properties_activity_long"));
		}
	}

	class RootFlowAction extends SighosDialogComponentAction {

		private static final long serialVersionUID = 1L;

		public RootFlowAction(SighosDialog dialog, Component component) {
			super(dialog, component);
			putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("flow_properties_rootflow_long"));
		}
	}
}
