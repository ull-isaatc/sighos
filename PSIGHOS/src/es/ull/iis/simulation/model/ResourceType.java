/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * The type of a resource. Defines roles or specializations of the resources.<p>
 * A user can define customized code associated to different events where this class
 * is involved:
 * <ul>
 * <li>When a resource is activated for this type: {@link #beforeRoleOn()}, {@link #afterRoleOn()}</li>
 * <li>When a resource is deactivated for this type: {@link #beforeRoleOff()}, {@link #afterRoleOff()}</li>
 * </ul>
 * @author Iván Castilla Rodríguez
 */
public class ResourceType extends ModelObject implements Describable {
    /** A brief description of the resource type */
    protected final String description;

	/**
	 * 
	 */
	public ResourceType(Model model, String description) {
		super(model, model.getResourceTypeList().size(), "RT");
		this.description = description;
		model.add(this);
	}

	@Override
	public String getDescription() {
		return description;
	}

	// User methods
	
	/**
	 * Allows a user for adding customized code before a resource is activated for this type. If this
	 * method returns a value higher than 0, the activation of the resource is delayed such returned value.
	 * @return The delay in activating the resource. No delay is applied if 0 
	 */
	public long beforeRoleOn() {
		return 0;
	}
	
	/**
	 * Allows a user for adding customized code after a resource is activated for this type. 
	 */
	public void afterRoleOn() {	
	}
	
	/**
	 * Allows a user for adding customized code before a resource is deactivated for this type. If this
	 * method returns a value higher than 0, the deactivation of the resource is delayed such returned value.
	 * @return The delay in deactivating the resource. No delay is applied if 0 
	 */
	public long beforeRoleOff() {
		return 0;
	}
	
	/**
	 * Allows a user for adding customized code after a resource is deactivated for this type. 
	 */
	public void afterRoleOff() {
	}

	// End of user methods
	
	/**
	 * Adds n resources of type {@link ResourceType}. This method is useful when you simply want to create a
	 * set of resources that are available all the time as {@link ResourceType}.
	 * @param n Number of generic resources to create.
	 * @return The set of resources created.
	 */
	public Resource[] addGenericResources(int n) {
		final Resource[] res = new Resource[n];
		for (int i = 0; i < n; i++) {
			res[i] = new Resource(model, description + " " + i);
			res[i].addTimeTableEntry(this);
		}
		return res;
	}
}
