package es.ull.isaatc.simulation.editor.project.model.tablemodel;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import es.ull.isaatc.simulation.editor.util.ModelComponent;
import es.ull.isaatc.simulation.editor.util.ModelComponentList;

public abstract class ModelComponentTableModel extends AbstractTableModel {
	/** counter for object's id */
	protected int nextId = 1;

	/** Column names */
	protected String[] columnNames;

	/** Elements in the table */
	protected ModelComponentList elements = new ModelComponentList();

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

	public List getXML() {
		return elements.getXML();
	}

}
