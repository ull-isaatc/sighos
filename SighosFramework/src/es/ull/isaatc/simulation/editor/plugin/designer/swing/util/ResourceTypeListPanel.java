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
import es.ull.isaatc.simulation.editor.project.model.ResourceType;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.ResourceTypeTableModel;

/**
 * @author Roberto
 *
 */
public class ResourceTypeListPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JScrollPane pane;

	private JList list;

	private DefaultListModel listModel;

	public ResourceTypeListPanel() {
		super();

		setLayout(new BorderLayout());

		listModel = new DefaultListModel();
		ResourceTypeTableModel rtModel = ProjectModel.getInstance().getModel()
				.getResourceTypeTableModel();
		int last = rtModel.getRowCount();
		for (int i = 0; i < last; i++) {
			listModel.addElement(rtModel.get(i));
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
	public List<ResourceType> getRTList() {
		ArrayList<ResourceType> rtList = new ArrayList<ResourceType>();
		Object[] rtSelectedList = list.getSelectedValues();
		for (int i = 0; i < rtSelectedList.length; i++) {
			rtList.add((ResourceType) rtSelectedList[i]);
		}
		return rtList;
	}
	
	public void setSelection(List<ResourceType> rtList) {
		Iterator<ResourceType> rtIt = rtList.iterator();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		while (rtIt.hasNext()) {
			ResourceType rt = rtIt.next();
			ResourceType rtAux = null;
			int i = 0;
			do {
				rtAux = (ResourceType)listModel.get(i++);
				if (rtAux != rt) 
					rtAux = null;
			}
			while (rtAux == null);
			indices.add(i - 1);
			
		}
		int[] ind = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++)
			ind[i] = indices.get(i);
		list.setSelectedIndices(ind);
	}
}
