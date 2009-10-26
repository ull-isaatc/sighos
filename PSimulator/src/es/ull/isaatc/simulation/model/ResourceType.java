/**
 * 
 */
package es.ull.isaatc.simulation.model;

import java.util.TreeMap;

import es.ull.isaatc.simulation.Describable;

/**
 * Represents the different roles that can be found in the system. The resources can serve for
 * different purposes, and each purpose is a role.
 * @author Carlos Martin Galan
 */
public class ResourceType extends VariableStoreModelObject implements Describable, VariableHandler {
    /** A brief description of the resource type */
    protected final String description;
    protected TreeMap<String, String> userMethods = new TreeMap<String, String>();

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
        userMethods.put("beforeRoleOn", "return 0.0;");
        userMethods.put("afterRoleOn", "");
        userMethods.put("beforeRoleOff", "return 0.0;");
        userMethods.put("afterRoleOff", "");
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

}
