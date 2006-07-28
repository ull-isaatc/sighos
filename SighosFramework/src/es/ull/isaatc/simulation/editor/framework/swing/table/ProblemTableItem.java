/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.swing.table;

/**
 * @author Roberto Muñoz
 *
 */
public class ProblemTableItem {

	public enum ProblemType { WARNING, ERROR };
	
	private ProblemType type;
	
	private String description;
	
	private String component;
	
	private int id;
	

	/**
	 * 
	 * @param type
	 * @param description
	 * @param component
	 * @param id
	 */
	public ProblemTableItem(ProblemType type, String description, String component, int id) {
		super();
		this.type = type;
		this.description = description;
		this.component = component;
		this.id = id;
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
	 * @return the type
	 */
	public ProblemType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(ProblemType type) {
		this.type = type;
	}
	
	/**
	 * @return the component
	 */
	public String getComponent() {
		return component;
	}

	/**
	 * @param component the component to set
	 */
	public void setComponent(String component) {
		this.component = component;
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

	public String toString() {
		return description;
	}
}
