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
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.RootFlowComboBox;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.XMLEditorPanel;
import es.ull.isaatc.simulation.editor.project.model.RootFlow;
import es.ull.isaatc.simulation.editor.project.model.XMLModelUtilities;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class PackageFlowPropertiesDialog extends SighosDialog {

	private static final long serialVersionUID = 1L;
	
	private JPanel rootFlowPanel;

	private RootFlowComboBox rfComboBox;
	
	private XMLEditorPanel xmlEditorPanel;
	
	private RootFlow rootFlow;
	
	private String iterations;
	
	private static PackageFlowPropertiesDialog INSTANCE = new PackageFlowPropertiesDialog(SighosFramework.getInstance());
	
	public static PackageFlowPropertiesDialog getInstance() {
		return INSTANCE;
	}

	private PackageFlowPropertiesDialog(Component parent) {
		super(parent);
	}
	
	protected void initialize() {
		this.setSize(new Dimension(650, 400));
		setModal(true);
		super.initialize();
		setTitle(ResourceLoader.getMessage("singleflow_properties_dialog"));
	}

	public void initValues(String iterations, RootFlow rootFlow) {
		setIterations(iterations);
		setRootFlow(rootFlow);
	}

	private void setRootFlow(RootFlow rf) {
		this.rootFlow = rf;
		rfComboBox.setRootFlow(rf);
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
		panel.add(getRootFlowPanel(), "0, 0");
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
	 * This method initializes rootFlowPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getRootFlowPanel() {
		if (rootFlowPanel == null) {
			rootFlowPanel = new JPanel();
			rfComboBox = new RootFlowComboBox();
			TableLayout priorityPanelLayout = new TableLayout(new double[][] {
					{ 5.0, 0.30, TableLayout.FILL, 5.0 },
					{ 5.0, 25, TableLayout.FILL } });
			priorityPanelLayout.setHGap(0);
			priorityPanelLayout.setVGap(5);
			rootFlowPanel.setLayout(priorityPanelLayout);
			rootFlowPanel.setBorder(BorderFactory.createEtchedBorder());
			rootFlowPanel.add(new JLabel(ResourceLoader.getMessage("flow_properties_rootflow")), "1, 1");
			rootFlowPanel.add(rfComboBox, "2, 1");
			rfComboBox.addFocusListener(new RootFlowAction(this, rfComboBox));
		}
		return rootFlowPanel;
	}

	/**
	 * @return the rootFlow
	 */
	public RootFlow getRootFlow() {
		return rootFlow;
	}

	/**
	 * @return the iterations
	 */
	public String getIterations() {
		return iterations;
	}

	protected boolean apply() {
		iterations = xmlEditorPanel.getEditorText();
		rootFlow = (RootFlow) rfComboBox.getSelectedItem();
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
	
	class RootFlowAction extends SighosDialogComponentAction {

		private static final long serialVersionUID = 1L;

		public RootFlowAction(SighosDialog dialog, Component component) {
			super(dialog, component);
			putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("flow_properties_rootflow_long"));
		}
	}
}
