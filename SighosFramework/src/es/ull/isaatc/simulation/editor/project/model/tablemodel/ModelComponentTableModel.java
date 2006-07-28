package es.ull.isaatc.simulation.editor.project.model.tablemodel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;
import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem.ProblemType;
import es.ull.isaatc.simulation.editor.project.model.ModelComponent;
import es.ull.isaatc.simulation.editor.project.model.ModelComponentList;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;
import es.ull.isaatc.simulation.editor.util.Validatory;

public abstract class ModelComponentTableModel extends AbstractTableModel implements Validatory {
	/** counter for object's id */
	protected int nextId = 1;

	/** Column names */
	protected String[] columnNames;

	/** Elements in the table */
	protected ModelComponentList elements = new ModelComponentList();
	
	/** List of problems for this TableModel */
	protected List<ProblemTableItem> problems = new ArrayList<ProblemTableItem>();


	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return elements.size();
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public int getNextId() {
		return nextId++;
	}
	
	public void initNextId(int id) {
		nextId = id + 1;
	}

	public void add(ModelComponent obj) {
		obj.setId(getNextId());
		elements.add(obj);
		fireTableDataChanged();
	}

	public void remove(int index) {
		elements.remove(index);
		fireTableDataChanged();
	}

	public ModelComponent get(int index) {
		return elements.get(index);
	}
	
	public ModelComponent search(int id) {
		return elements.search(id);
	}

	public ModelComponentList getElements() {
		return elements;
	}
	
	/**
	 * @param elements the elements to set
	 */
	public void setElements(ModelComponentList elements) {
		this.elements = elements;
	}
	
	/**
	 * @return a component description
	 */
	public abstract String getComponentString();
	
	public List getXML() {
		return elements.getXML();
	}
	
	public List<ProblemTableItem> validate() {
		problems.clear();
		if (elements.size() == 0)
			problems.add(new ProblemTableItem(ProblemType.ERROR, 
					ResourceLoader.getMessage("component_nelem_validation"),
					getComponentString(), 0));
			
		for (int i = 0; i < elements.size(); i++) {
			ModelComponent mc = elements.get(i);
			int rep = elements.search(mc.getDescription()).size();
			if (rep > 1)  // more than one ModelComponent have the same description
				problems.add(new ProblemTableItem(ProblemType.ERROR, 
						ResourceLoader.getMessage("component_repetition_validation") + " (" + rep + ")",
						mc.getComponentString(),
						mc.getId()));
			problems.addAll(mc.validate());
		}
		return problems;
	}
}
