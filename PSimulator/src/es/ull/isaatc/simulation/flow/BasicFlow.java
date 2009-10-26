/**
 * 
 */
package es.ull.isaatc.simulation.flow;

import java.util.TreeMap;

import es.ull.isaatc.simulation.model.Model;
import es.ull.isaatc.simulation.model.ModelObject;


/**
 * Basic implementation of a flow. Defines the default behavior of most methods. 
 * @author Iván Castilla Rodríguez
 */
public abstract class BasicFlow extends ModelObject implements Flow {
	/** Generator of unique identifiers */
	private static int counter = 0;
	/** The structured flow containing this flow. */
	protected StructuredFlow parent = null;
    protected TreeMap<String, String> userMethods;
	
	/**
	 * Create a new basic flow.
	 * @param model The model this flow belongs to.
	 */
	public BasicFlow(Model model) {
		super(counter++, model);
		model.add(this);
        userMethods = new TreeMap<String, String>();
        userMethods.put("beforeRequest", "return true;");
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#getParent()
	 */
	public StructuredFlow getParent() {
		return parent;
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#setParent(es.ull.isaatc.simulation.StructuredFlow)
	 */
	public void setParent(StructuredFlow parent) {
		this.parent = parent;
	}

	
	@Override
	public String getObjectTypeIdentifier() {
		return "F";
	}
	
	@Override
	public boolean setMethod(String method, String body) {
		if (userMethods.containsKey(method)) {
			userMethods.put(method, body);
			return true;
		}
		return false;
	}
}
