/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Element;

/**
 * Information event related to the elements carrying out activities through the system.
 * There are five types of information: <p>
 * <table>
 * <thead><tr>
 * <th>Type</th><th>Description</th><th>Associated value</th>
 * </tr></thead>
 * <tbody>
 * <tr><td>START</td><td>The element starts its execution</td><td>Element type's identifier</td></tr>
 * <tr><td>FINISH</td><td>The element finishes its execution</td><td>Total pending activities</td></tr>
 * <tr><td>REQACT</td><td>The element requests an activity</td><td>Activity's identifier</td></tr>
 * <tr><td>STAACT</td><td>The element starts an activity</td><td>Activity's identifier</td></tr>
 * <tr><td>ENDACT</td><td>The element finishes an activity</td><td>Activity's identifier</td></tr>
 * <tr><td>RESACT</td><td>The element resumes an interrupted activity</td><td>Activity's identifier</td></tr>
 * <tr><td>INTACT</td><td>The element interrupts an activity</td><td>Activity's identifier</td></tr>
 * </tbody>
 * </table>
 * @author Iván Castilla Rodríguez
 *
 */
public class ElementInfo extends SimulationObjectInfo {
	private static final long serialVersionUID = -7978808739456928849L;
	/** Possible types of element information */
	public enum Type {START, FINISH, REQACT, STAACT, ENDACT, RESACT, INTACT};
	/** Type of this element information */
	private Type type;
	/** Value related to this piece of information */ 
	private int value;	

	/**
	 * @param elem Element which produces the information
	 * @param type Type of the information
	 * @param ts Timestamp when the information is produced
	 * @param value Value associated to the information
	 */
	public ElementInfo(Element elem, Type type, double ts, int value) {
		super(elem, ts);
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
