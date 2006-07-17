/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Resource;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceInfo extends SimulationInfo {
	public enum Type {START, ROLON, ROLOFF};
	private Type type;
	private double ts;
	private int rtId; 
	/**
	 * 
	 */
	private static final long serialVersionUID = -7978808739456928847L;

	/**
	 * @param source
	 * @param type
	 * @param elemId
	 * @param ts
	 * @param value
	 */
	public ResourceInfo(Resource res, Type type, double ts, int rtId) {
		super(res);
		this.type = type;
		this.ts = ts;
		this.rtId = rtId;
	}

	/**
	 * @return Returns the elemId.
	 */
	public int getResId() {
		return ((Resource)source).getIdentifier();
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
	public int getRtId() {
		return rtId;
	}

}
