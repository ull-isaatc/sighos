/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Resource;

/**
 * Information event related to the resources which become available and unavailable.
 * There are three types of information: <p>
 * <table>
 * <thead><tr>
 * <th>Type</th><th>Description</th><th>Associated value</th>
 * </tr></thead>
 * <tbody>
 * <tr><td>START</td><td>The resource starts its execution</td><td>Total of timetable entries</td></tr>
 * <tr><td>ROLON</td><td>The resource becomes available</td><td>Resource type this resource becomes available for</td></tr>
 * <tr><td>ROLOFF</td><td>The resource becomes unavailable</td><td>Resource type this resource becomes unavailable for</td></tr>
 * </tbody>
 * </table>
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceInfo extends SimulationObjectInfo {
	private static final long serialVersionUID = -7978808739456928847L;
	/** Possible types of resource information */
	public enum Type {START, ROLON, ROLOFF};
	/** Type of this resource information */
	private Type type;
	/** Value related to this piece of information */ 
	private int value; 

	/**
	 * @param res Resource which produces the information
	 * @param type Type of the information
	 * @param ts Timestamp when the information is produced
	 * @param value Value associated to the information
	 */
	public ResourceInfo(Resource res, Type type, double ts, int value) {
		super(res, ts);
		this.type = type;
		this.value = value;
	}

	/**
	 * @return Returns the type.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return Returns the value.
	 */
	public int getValue() {
		return value;
	}

}
