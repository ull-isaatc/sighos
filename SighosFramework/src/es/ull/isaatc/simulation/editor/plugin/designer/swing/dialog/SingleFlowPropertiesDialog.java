package es.ull.isaatc.simulation.editor.plugin.designer.swing.dialog;

import info.clearthought.layout.TableLayout;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.xml.bind.JAXBException;

import es.ull.isaatc.simulation.editor.framework.SighosFramework;
import es.ull.isaatc.simulation.editor.framework.actions.swing.SighosDialogComponentAction;
import es.ull.isaatc.simulation.editor.framework.swing.dialog.SighosDialog;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.ActivityComboBox;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.XMLEditorPanel;
import es.ull.isaatc.simulation.editor.project.model.Activity;
import es.ull.isaatc.simulation.editor.project.model.XMLModelUtilities;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class SingleFlowPropertiesDialog extends SighosDialog {

	private static final long serialVersionUID = 1L;
	
	private JPanel activityPanel;

	private ActivityComboBox actComboBox;
	
	private XMLEditorPanel xmlEditorPanel;
	
	private Activity activity;
	
	private String iterations;
	
	private static SingleFlowPropertiesDialog INSTANCE = new SingleFlowPropertiesDialog(SighosFramework.getInstance());
	
	public static SingleFlowPropertiesDialog getInstance() {
		return INSTANCE;
	}

	private SingleFlowPropertiesDialog(Component parent) {
		super(parent);
	}
	
	protected void initialize() {
		this.setSize(new Dimension(650, 400));
		setModal(true);
		super.initialize();
		setTitle(ResourceLoader.getMessage("singleflow_properties_dialog"));
	}

	public void initValues(String iterations, Activity activity) {
		setIterations(iterations);
		setActivity(activity);
	}

	private void setActivity(Activity activity) {
		this.activity = activity;
		actComboBox.setActivity(activity);
	}

	private void setIterations(String iterations) {
		this.iterations = iterations;
		xmlEditorPanel.setEditorText(iterations);
	}

	protected JPanel getMainPanel() {
		JPanel panel = new JPanel();
		TableLayout panelLayout = new TableLayout(new double[][] {
				{ TableLayout.FILL }, { 45, TableLayout.FILL } });
		panelLayout.setHGap(5);
		panelLayout.setVGap(5);
		panel.setLayout(panelLayout);
		panel.add(getActivityPanel(), "0, 0");
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
	 * This method initializes activityPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getActivityPanel() {
		if (activityPanel == null) {
			activityPanel = new JPanel();
			actComboBox = new ActivityComboBox();
			TableLayout priorityPanelLayout = new TableLayout(new double[][] {
					{ 5.0, 0.30, TableLayout.FILL, 5.0 },
					{ 5.0, 25, TableLayout.FILL } });
			priorityPanelLayout.setHGap(0);
			priorityPanelLayout.setVGap(5);
			activityPanel.setLayout(priorityPanelLayout);
			activityPanel.setBorder(BorderFactory.createEtchedBorder());
			activityPanel.add(new JLabel(ResourceLoader.getMessage("flow_properties_activity")), "1, 1");
			activityPanel.add(actComboBox, "2, 1");
			actComboBox.addFocusListener(new ActivityAction(this, actComboBox));
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

	protected boolean apply() {
		iterations = xmlEditorPanel.getEditorText();
		activity = (Activity) actComboBox.getSelectedItem();
		return true;
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
}
