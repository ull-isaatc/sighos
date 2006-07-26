package es.ull.isaatc.simulation.editor.plugin.designer.swing.dialog;

import info.clearthought.layout.TableLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

import es.ull.isaatc.simulation.editor.framework.SighosFramework;
import es.ull.isaatc.simulation.editor.framework.actions.swing.SighosDialogComponentAction;
import es.ull.isaatc.simulation.editor.framework.swing.dialog.SighosDialog;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.util.ElementTypeListPanel;
import es.ull.isaatc.simulation.editor.project.model.ElementType;
import es.ull.isaatc.simulation.editor.project.model.TypeBranchFlow;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class TypeBranchPropertiesDialog extends SighosDialog {

	private static final long serialVersionUID = 1L;
	
	private JPanel mainPanel;
	
	private ElementTypeListPanel elementTypeList;

	private TypeBranchFlow tbEditionFlow;
	
	private static TypeBranchPropertiesDialog INSTANCE = new TypeBranchPropertiesDialog(SighosFramework.getInstance());
	
	public static TypeBranchPropertiesDialog getInstance() {
		return INSTANCE;
	}

	public TypeBranchPropertiesDialog(Component parent) {
		super(parent);
	}

	protected void initialize() {
		this.setSize(new Dimension(450, 300));
		setModal(true);
		super.initialize();
		setTitle(ResourceLoader.getMessage("resource_timetable_entry_dialog"));
	}
	
	public void initValues(TypeBranchFlow tbFlow) {
		if (tbFlow == null)
			elementTypeList.setSelection(null);
		else
			elementTypeList.setSelection(tbFlow.getElemTypes());
	}
	
	protected JPanel getMainPanel() {
		mainPanel = new JPanel();
		TableLayout panelLayout = new TableLayout(new double[][] {
				{ TableLayout.FILL},
				{ TableLayout.FILL} });
		panelLayout.setHGap(5);
		panelLayout.setVGap(5);
		mainPanel.setLayout(panelLayout);
		mainPanel.add(getElementTypeList(), "0, 0");
		
		return mainPanel;
	}


	/**
	 * This method initializes elementTypeList	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private ElementTypeListPanel getElementTypeList() {
		if (elementTypeList == null) {
			elementTypeList = new ElementTypeListPanel();
			elementTypeList.setBorder(BorderFactory.createTitledBorder(ResourceLoader.getMessage("resource_timetable_roles")));
			elementTypeList.getList().addFocusListener(new ElementTypeAction(this, null));
		}
		return elementTypeList;		
	}
	
	protected boolean apply() {
		tbEditionFlow = new TypeBranchFlow();
		tbEditionFlow.setElemTypes(new ArrayList<ElementType>(elementTypeList.getETList()));
		return true;
	}

	/**
	 * @return the tbEditionFlow
	 */
	public TypeBranchFlow getTBEditionFlow() {
		return tbEditionFlow;
	}

	/**
	 * @param tbEditionFlow the tbEditionFlow to set
	 */
	public void setTBEditionFlow(TypeBranchFlow tbEditionFlow) {
		this.tbEditionFlow = tbEditionFlow;
	}
	
	class ElementTypeAction extends SighosDialogComponentAction {

		private static final long serialVersionUID = 1L;

		public ElementTypeAction(SighosDialog dialog, Component component) {
			super(dialog, component);
			putValue(Action.LONG_DESCRIPTION, ResourceLoader.getMessage("resource_timetable_roles_long"));
		}
	}	
}
