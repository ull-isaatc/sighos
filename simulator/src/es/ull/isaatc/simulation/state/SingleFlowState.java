/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SingleFlowState extends FlowState {
	protected int flowId;
	protected int actId;
	protected boolean finished;
	protected int currentWGId = -1;
	protected ArrayList<Integer> caughtResources = null;
	
	public SingleFlowState(int flowId, int actId, boolean finished) {
		this.flowId = flowId;
		this.actId = actId;
		this.finished = finished;
	}

	/**
	 * @param flowId
	 * @param actId
	 * @param finished
	 * @param currentWGId
	 */
	public SingleFlowState(int flowId, int actId, boolean finished, int currentWGId) {
		this.flowId = flowId;
		this.actId = actId;
		this.finished = finished;
		this.currentWGId = currentWGId;
		caughtResources = new ArrayList<Integer>();
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

	public void add(int resId) {
		caughtResources.add(resId);
	}

	/**
	 * @return Returns the caughtResources.
	 */
	public ArrayList<Integer> getCaughtResources() {
		return caughtResources;
	}

	/**
	 * @return Returns the currentWGId.
	 */
	public int getCurrentWGId() {
		return currentWGId;
	}

	public String toString() {
		StringBuffer str = new StringBuffer("SF" + flowId + "(A" + actId + ")");
		if (finished)
			str.append("\tFINISHED\r\n");
		else
			str.append("\r\n");
		if (currentWGId != -1) {
			str.append("Current WG" + currentWGId + ":");
			for (Integer rtId : caughtResources)
				str.append(" " + rtId);
		}
		return str.toString();
	}
}
