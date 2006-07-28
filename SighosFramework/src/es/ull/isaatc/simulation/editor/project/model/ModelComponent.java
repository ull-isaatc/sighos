package es.ull.isaatc.simulation.editor.project.model;

import java.util.ArrayList;
import java.util.List;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;
import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem.ProblemType;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;
import es.ull.isaatc.simulation.editor.util.Validatory;


/**
 * Defines an object that has an associated description and id.
 * 
 * @author Roberto Muñoz
 */
public abstract class ModelComponent implements Comparable, Validatory {
	
	/** Component id */
	protected int id;
	
	/** Component description */
	protected String description = "";

	/** Type of the component */
	protected ComponentType componentType;
	
	/** List of problems for this component */
	protected List<ProblemTableItem> problems = new ArrayList<ProblemTableItem>();

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
	 * @return a component description
	 */
	public abstract String getComponentString();
	
	/** 
	 * @return Object XML data structure representation
	 */
	public abstract Object getXML();
	
	public List<ProblemTableItem> validate() {
		problems.clear();
		if (getDescription().length() == 0)
			problems.add(new ProblemTableItem(ProblemType.WARNING, 
					ResourceLoader.getMessage("component_description_validation"),
					getComponentString(),
					getId()));
		return problems;
	}
	
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
