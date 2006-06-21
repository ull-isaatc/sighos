/**
 * 
 */
package es.ull.cyc.simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ElementType {
	private int id;
	private String description;
	private MetaFlow flow;
	
	/**
	 * @param id
	 * @param description
	 * @param flow
	 */
	public ElementType(int id, String description, MetaFlow flow) {
		this.id = id;
		this.description = description;
		this.flow = flow;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return Returns the flow.
	 */
	public Flow getFlow(Element e) {
		return flow.getFlow(null, e, id);
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	
}
