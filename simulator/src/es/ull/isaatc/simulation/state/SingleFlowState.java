/**
 * 
 */
package es.ull.isaatc.simulation.state;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SingleFlowState extends FlowState {
	protected int flowId;
	protected int actId;
	boolean finished;
	
	public SingleFlowState(int flowId, int actId, boolean finished) {
		this.flowId = flowId;
		this.actId = actId;
		this.finished = finished;
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
	 * @return Returns the finished.
	 */
	public boolean isFinished() {
		return finished;
	}
}
