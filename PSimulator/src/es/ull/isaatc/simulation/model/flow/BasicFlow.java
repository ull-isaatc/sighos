/**
 * 
 */
package es.ull.isaatc.simulation.model.flow;

import java.util.Collection;
import java.util.TreeMap;

import es.ull.isaatc.simulation.common.Element;
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
    protected TreeMap<String, String> userMethods = new TreeMap<String, String>();
    protected String imports = "";

    static private TreeMap<String, String> userCompleteMethods = new TreeMap<String, String>(); 

    static {
    	userCompleteMethods.put("beforeRequest", "public boolean beforeRequest(Element e)");
    }
	
	/**
	 * Create a new basic flow.
	 * @param model The model this flow belongs to.
	 */
	public BasicFlow(Model model) {
		super(counter++, model);
		for (String method : userCompleteMethods.keySet())
			userMethods.put(method, "");
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
	public void setParent(es.ull.isaatc.simulation.common.flow.StructuredFlow parent) {
		this.parent = (StructuredFlow)parent;
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

	@Override
	public String getBody(String method) {
		return userMethods.get(method);
	}

	@Override
	public String getImports() {
		return imports;
	}

	@Override
	public void setImports(String imports) {
		this.imports = imports;
	}

	@Override
	public Collection<String> getMethods() {
		return userMethods.keySet();
	}

	@Override
	public String getCompleteMethod(String method) {
		return userCompleteMethods.get(method);
	}
	
	@Override
	public boolean beforeRequest(Element e) {
		return false;
	}

}
