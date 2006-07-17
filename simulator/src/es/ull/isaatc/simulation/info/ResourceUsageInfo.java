/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Resource;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceUsageInfo extends SimulationInfo {
	public enum Type {CAUGHT, RELEASED};
	private Type type;
	private double ts;
	private int elemId;
	private int rtId;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7978808739456928845L;

	/**
	 * @param source
	 * @param type
	 * @param elemId
	 * @param ts
	 * @param value
	 */
	public ResourceUsageInfo(Resource res, Type type, double ts, int elemId, int rtId) {
		super(res);
		this.type = type;
		this.ts = ts;
		this.elemId = elemId;
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
	public int getElemId() {
		return elemId;
	}

	/**
	 * @return Returns the value.
	 */
	public int getRtId() {
		return rtId;
	}

}
