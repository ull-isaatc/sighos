/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.swing.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.project.model.ElementType;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.ElementTypeTableModel;

/**
 * @author Roberto
 *
 */
public class ElementTypeListPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JScrollPane pane;

	private JList list;

	private DefaultListModel listModel;

	public ElementTypeListPanel() {
		super();

		setLayout(new BorderLayout());

		listModel = new DefaultListModel();
		ElementTypeTableModel etModel = ProjectModel.getInstance().getModel()
				.getElementTypeTableModel();
		int last = etModel.getRowCount();
		for (int i = 0; i < last; i++) {
			listModel.addElement(etModel.get(i));
		}
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		pane = new JScrollPane();
		pane.setViewportView(list);
		pane.getViewport().setBackground(Color.WHITE);
		pane.setOpaque(true);
		add(pane, BorderLayout.CENTER);
	}

	/**
	 * @return the list
	 */
	public JList getList() {
		return list;
	}

	/**
	 * 
	 * @return the list with the resource types selected
	 */
	public List<ElementType> getETList() {
		ArrayList<ElementType> etList = new ArrayList<ElementType>();
		Object[] etSelectedList = list.getSelectedValues();
		for (int i = 0; i < etSelectedList.length; i++) {
			etList.add((ElementType) etSelectedList[i]);
		}
		return etList;
	}
	
	public void setSelection(List<ElementType> etList) {
		if (etList == null) {
			list.clearSelection();
			return;
		}
		Iterator<ElementType> etIt = etList.iterator();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		while (etIt.hasNext()) {
			ElementType et = etIt.next();
			ElementType etAux = null;
			int i = 0;
			do {
				etAux = (ElementType)listModel.get(i++);
				if (etAux != et) 
					etAux = null;
			}
			while (etAux == null);
			indices.add(i - 1);
			
		}
		int[] ind = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++)
			ind[i] = indices.get(i);
		list.setSelectedIndices(ind);
	}
}
