/*
 * ModelComponent.java
 *
 * Created on 17 de noviembre de 2005, 11:06
 */

package es.ull.isaatc.simulation.editor.util;

import es.ull.isaatc.simulation.editor.project.model.ComponentType;

/**
 * Defines an object that has an associated description and id.
 * 
 * @author Roberto Muñoz
 */
public abstract class ModelComponent implements Comparable {
	
	/** Component id */
	protected int id;
	
	/** Component description */
	protected String description = "";

	/** Type of the component */
	protected ComponentType componentType;

	
	public ModelComponent(ComponentType componentType) {
		super();
		this.componentType = componentType;
	}
	
	/**
	 * @param description
	 */
	public ModelComponent(ComponentType componentType, String description) {
		super();
		this.description = description;
		this.componentType = componentType;
	}

	/**
	 * @return the componentType
	 */
	public ComponentType getComponentType() {
		return componentType;
	}

	/**
	 * @param componentType the componentType to set
	 */
	public void setComponentType(ComponentType componentType) {
		this.componentType = componentType;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	
	/** 
	 * @return Object XML data structure representation
	 */
	public abstract Object getXML();
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object obj) {
		if (obj instanceof Integer) {
			return (new Integer(id).compareTo((Integer)obj));
		}
		return compareTo(new Integer(((ModelComponent) obj).getId()));
	}
}
