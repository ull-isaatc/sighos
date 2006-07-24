package es.ull.isaatc.simulation.editor.plugin.designer.swing.dialog;

import info.clearthought.layout.TableLayout;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.xml.bind.JAXBException;

import es.ull.isaatc.simulation.editor.framework.actions.swing.SighosDialogComponentAction;
import es.ull.isaatc.simulation.editor.framework.swing.dialog.SighosDialog;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.XMLEditorPanel;
import es.ull.isaatc.simulation.editor.project.model.XMLModelUtilities;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class SplitFlowPropertiesDialog extends SighosDialog {

	private static final long serialVersionUID = 1L;
	
	private XMLEditorPanel xmlEditorPanel;
	
	private String iterations;
	
	private static SplitFlowPropertiesDialog INSTANCE = new SplitFlowPropertiesDialog();
	
	public static SplitFlowPropertiesDialog getInstance() {
		return INSTANCE;
	}

	private SplitFlowPropertiesDialog() {
		super();
	}
	
	protected void initialize() {
		this.setSize(new Dimension(650, 400));
		setModal(true);
		super.initialize();
		setTitle(ResourceLoader.getMessage("splitflow_properties_dialog"));
	}

	public void initValues(String iterations) {
		setIterations(iterations);
	}


	private void setIterations(String iterations) {
		this.iterations = iterations;
		xmlEditorPanel.setEditorText(iterations);
	}

	protected JPanel getMainPanel() {
		JPanel panel = new JPanel();
		TableLayout panelLayout = new TableLayout(new double[][] {
				{ TableLayout.FILL }, { TableLayout.FILL } });
		panelLayout.setHGap(5);
		panelLayout.setVGap(5);
		panel.setLayout(panelLayout);
		panel.add(getXMLEditorPanel(), "0, 0");
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
	 * @return the iterations
	 */
	public String getIterations() {
		return iterations;
	}

	protected void okButtonAction() {
		iterations = xmlEditorPanel.getEditorText();
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
}
