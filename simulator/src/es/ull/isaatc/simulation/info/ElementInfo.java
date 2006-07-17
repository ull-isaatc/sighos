/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Element;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ElementInfo extends SimulationInfo {
	public enum Type {START, FINISH, REQACT, STAACT, ENDACT};
	private Type type;
	private double ts;
	private int value; 
	/**
	 * 
	 */
	private static final long serialVersionUID = -7978808739456928849L;

	/**
	 * @param source
	 * @param type
	 * @param elemId
	 * @param ts
	 * @param value
	 */
	public ElementInfo(Element elem, Type type, double ts, int value) {
		super(elem);
		this.type = type;
		this.ts = ts;
		this.value = value;
	}

	/**
	 * @return Returns the elemId.
	 */
	public int getElemId() {
		return ((Element)source).getIdentifier();
	}

	/**
	 * @return Returns the ts.
	 */
	public double getTs() {
		return ts;
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
