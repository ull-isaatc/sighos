/**
 * 
 */
package es.ull.isaatc.simulation.model;

import java.util.Collection;
import java.util.TreeMap;

/**
 * Represents the different roles that can be found in the system. The resources can serve for
 * different purposes, and each purpose is a role.
 * @author Carlos Martin Galan
 */
public class ResourceType extends VariableStoreModelObject implements es.ull.isaatc.simulation.common.ResourceType, VariableHandler {
    /** A brief description of the resource type */
    protected final String description;
    protected TreeMap<String, String> userMethods = new TreeMap<String, String>();
    protected String imports = "";

    static private TreeMap<String, String> userCompleteMethods = new TreeMap<String, String>(); 

    static {
    	userCompleteMethods.put("beforeRoleOn", "public double beforeRoleOn()");
    	userCompleteMethods.put("afterRoleOn", "public void afterRoleOn()");
    	userCompleteMethods.put("beforeRoleOff", "public double beforeRoleOff()");
    	userCompleteMethods.put("afterRoleOff", "public void afterRoleOff()");
    }
    
    /**
     * Creates a new resource type.
     * @param id Resource type's identifier
     * @param model Associated model
     * @param description A short text describing this resource type.
     */
	public ResourceType(int id, Model model, String description) {
		super(id, model);
        this.description = description;
        model.add(this);
		for (String method : userCompleteMethods.keySet())
			userMethods.put(method, "");
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Describable#getDescription()
	 */
	public String getDescription() {
		return description;
	}

    @Override
	public String getObjectTypeIdentifier() {
		return "RT";
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
	public void afterRoleOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterRoleOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double beforeRoleOff() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double beforeRoleOn() {
		// TODO Auto-generated method stub
		return 0;
	}
}
