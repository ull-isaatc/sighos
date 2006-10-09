/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Resource;

/**
 * Information event related to the resources which are caught or released by an element
 * to carry out an activity. This ìece of information indicates the element using the resource
 * and the resource type the resource has been taken for.
 * @author Iván Castilla Rodríguez
 */
public class ResourceUsageInfo extends SimulationObjectInfo {
	private static final long serialVersionUID = -7978808739456928845L;
	/** Possible types of resource information */
	public enum Type {CAUGHT, RELEASED};
	/** Type of this resource information */
	private Type type;
	/** The element that has caught/released the resource */
	private int elemId;
	/** The resource type this resource is used for */
	private int rtId;

	/**
	 * @param res Resource which produces the information
	 * @param type Type of the information
	 * @param ts Timestamp when the information is produced
	 * @param elemId The element catching/releasing the resource
	 * @param rtId The resource type this resource is used for
	 */
	public ResourceUsageInfo(Resource res, Type type, double ts, int elemId, int rtId) {
		super(res, ts);
		this.type = type;
		this.elemId = elemId;
		this.rtId = rtId;
	}

	/**
	 * Returns the type.
	 * @return The type.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the element catching/releasing the resource.
	 * @return The element catching/releasing the resource.
	 */
	public int getElemId() {
		return elemId;
	}

	/**
	 * Returns the resource type this resource is used for.
	 * @return The resource type this resource is used for.
	 */
	public int getRtId() {
		return rtId;
	}

}
