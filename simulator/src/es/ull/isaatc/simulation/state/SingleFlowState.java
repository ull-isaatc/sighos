/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * Stores the state of a single flow. The state of a single flow consists on the 
 * activity wrapped, its identifier and the <code>finished</code> atribute. If this 
 * single flow is currently performing its activity, the workgroup used and the set of
 * caught resources are stored too.
 * @author Iván Castilla Rodríguez
 *
 */
public class SingleFlowState extends FlowState {
	/** This flow's identifier */
	protected int flowId;
	/** The activity which this flow wraps */
	protected int actId;
	/** If true this activity has been already performed */
	protected boolean finished;
	/** The identifier of the workgroup currently performing the activity. -1 if the
	 * activity has not been performed. */
	protected int executionWG;
	/** The simulation timestamp when this single flow was requested. */
	protected double arrivalTs;
	/** If the activity is currently being performed, the resources caught to perform it */
	protected ArrayList<Integer> caughtResources = null;
	
	/**
	 * 
	 * @param flowId This flow's identifier
	 * @param actId The activity which this flow wraps
	 * @param finished If true this activity has been already performed
	 */
	public SingleFlowState(int flowId, int actId, boolean finished, int executionWG, double arrivalTs) {
		this.flowId = flowId;
		this.actId = actId;
		this.finished = finished;
		this.executionWG = executionWG;
		this.arrivalTs = arrivalTs;
		caughtResources = new ArrayList<Integer>();
	}

	/**
	 * @return The activity which this flow wraps.
	 */
	public int getActId() {
		return actId;
	}

	/**
	 * @return This flow's identifier.
	 */
	public int getFlowId() {
		return flowId;
	}

	/**
	 * @return If true this activity has been already performed.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * @return the identifier of the workgroup currently performing the activity or 
	 * -1 if the activity has not been performed.
	 */
	public int getExecutionWG() {
		return executionWG;
	}

	/**
	 * Returns the simulation timestamp when this single flow was requested.
	 * @return The simulation timestamp when this single flow was requested.
	 */
	public double getArrivalTs() {
		return arrivalTs;
	}

	/**
	 * Adds a resource to the list of caught resources
	 * @param resId The identifier of a new resource
	 */
	public void add(int resId) {
		caughtResources.add(resId);
	}

	/**
	 * @return If the activity is currently being performed, the resources caught to perform it. null 
	 * in other case
	 */
	public ArrayList<Integer> getCaughtResources() {
		return caughtResources;
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("SF" + flowId + "(A" + actId + ")");
		if (finished)
			str.append("\tFINISHED\r\n");
		else
			str.append("\r\n");
		if (!Double.isNaN(arrivalTs))
			str.append("\tREQUESTED: " + arrivalTs + "\r\n");
		if (executionWG != -1) {
			str.append("WG: " + executionWG + "\r\n");
			for (Integer rtId : caughtResources)
				str.append(" " + rtId);
		}
		return str.toString();
	}
}
