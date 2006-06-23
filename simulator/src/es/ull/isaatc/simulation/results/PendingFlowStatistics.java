/**
 * 
 */
package es.ull.isaatc.simulation.results;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PendingFlowStatistics implements StatisticData {
	public final static int SINFLOW = 0;
	public final static int SECFLOW = 1;
	public final static int SIMFLOW = 2;
	public final static int ACTFLOW = 3; 
	protected int elemId;
	protected int type;
	protected int value;
	
	/**
	 * @param elemId
	 * @param type
	 * @param value
	 */
	public PendingFlowStatistics(int elemId, int type, int value) {
		this.elemId = elemId;
		this.type = type;
		this.value = value;
	}

	/**
	 * @return Returns the elemId.
	 */
	public int getElemId() {
		return elemId;
	}

	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return Returns the value.
	 */
	public int getValue() {
		return value;
	}
	
	
}
