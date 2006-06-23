/**
 * 
 */
package es.ull.isaatc.simulation.results;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ActivityStatistics implements StatisticData {
	protected int actId;
	protected int flowId;
	protected int elemId;
	
	/**
	 * @param actId
	 * @param elemId
	 */
	public ActivityStatistics(int actId, int flowId, int elemId) {
		this.actId = actId;
		this.flowId = flowId;
		this.elemId = elemId;
	}

	/**
	 * @return Returns the actId.
	 */
	public int getActId() {
		return actId;
	}

	/**
	 * @return Returns the flowId.
	 */
	public int getFlowId() {
		return flowId;
	}

	/**
	 * @return Returns the elemId.
	 */
	public int getElemId() {
		return elemId;
	}

}
