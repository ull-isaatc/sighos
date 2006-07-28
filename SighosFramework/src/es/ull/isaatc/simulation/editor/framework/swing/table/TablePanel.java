package es.ull.isaatc.simulation.editor.framework.swing.table;

import info.clearthought.layout.TableLayout;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import es.ull.isaatc.simulation.editor.plugin.designer.actions.TableComponentAction;
import es.ull.isaatc.simulation.editor.plugin.designer.swing.table.ComponentTableCellEditor;

public abstract class TablePanel extends JPanel {

	private SighosTable table;

	private JScrollPane scrollTablePane = null;

	private JPanel jButtonPanel = null;

	private JButton jButtonAdd = null;

	private JButton jButtonEdit = null;

	private JButton jButtonDelete = null;

	private TableComponentAction addAction;

	private TableComponentAction editAction;

	private TableComponentAction deleteAction;

	/** Creates the reusable panel. */
	public TablePanel(AbstractTableModel tableModel,
			TableComponentAction addAction, TableComponentAction editAction,
			TableComponentAction deleteAction,
			ComponentTableCellEditor tableCellEditor) {
		super();

		this.addAction = addAction;
		this.editAction = editAction;
		this.deleteAction = deleteAction;

		initialize(tableModel, tableCellEditor);

		addAction.setTable(this.table);
		editAction.setTable(this.table);
		deleteAction.setTable(this.table);
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize(AbstractTableModel tableModel,
			ComponentTableCellEditor tableCellEditor) {
		double size[][] = { { TableLayout.FILL }, { TableLayout.FILL, 30 } };
		this.setLayout(new TableLayout(size));
		add(getScrollTablePane(tableModel, tableCellEditor), "0, 0");
		add(getJButtonPanel(), "0, 1, c, c");

		getTable().addMouseListener(
				new TablePopupListener(getTable(), new TableComponentAction[] {
						addAction, editAction, deleteAction }));
	}

	/**
	 * This method initializes table
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getScrollTablePane(AbstractTableModel tableModel,
			ComponentTableCellEditor tableCellEditor) {
		if (scrollTablePane == null) {
			scrollTablePane = new JScrollPane();
			table = new SighosTable(tableModel, tableCellEditor);
			scrollTablePane.setViewportView(table);
			scrollTablePane.getViewport().setBackground(Color.WHITE);
			scrollTablePane.setOpaque(true);
		}
		return scrollTablePane;
	}

	private JPanel getJButtonPanel() {
		if (jButtonPanel == null) {
			jButtonPanel = new JPanel();
			double size[][] = { { 50, 10, 50, 10, 50 }, { 20 } };
			jButtonPanel.setLayout(new TableLayout(size));
			jButtonPanel.add(getJButtonAdd(), "0, 0");
			jButtonPanel.add(getJButtonEdit(), "2, 0");
			jButtonPanel.add(getJButtonDelete(), "4, 0");
		}
		return jButtonPanel;
	}

	/**
	 * This method initializes jButtonAdd
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonAdd() {
		if (jButtonAdd == null) {
			jButtonAdd = new JButton(addAction);
			jButtonAdd.setMargin(new Insets(0, 0, 0, 0));
		}
		return jButtonAdd;
	}

	/**
	 * This method initializes jButtonEdit
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonEdit() {
		if (jButtonEdit == null) {
			jButtonEdit = new JButton(editAction);
			jButtonEdit.setMargin(new Insets(0, 0, 0, 0));
		}
		return jButtonEdit;
	}

	/**
	 * This method initializes jButtonDelete
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonDelete() {
		if (jButtonDelete == null) {
			jButtonDelete = new JButton(deleteAction);
			jButtonDelete.setMargin(new Insets(0, 0, 0, 0));
		}
		return jButtonDelete;
	}

	/**
	 * @return the table
	 */
	public SighosTable getTable() {
		return table;
	}

	/**
	 * @return the addAction
	 */
	public TableComponentAction getAddAction() {
		return addAction;
	}

	/**
	 * @return the deleteAction
	 */
	public TableComponentAction getDeleteAction() {
		return deleteAction;
	}

	/**
	 * @return the editAction
	 */
	public TableComponentAction getEditAction() {
		return editAction;
	}
}
