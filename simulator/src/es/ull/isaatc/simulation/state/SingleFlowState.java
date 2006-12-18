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
	/** If true, the activity is currently being performed */
	protected boolean executing;
	/** If the activity is currently being performed, the resources caught to perform it */
	protected ArrayList<Integer> caughtResources = null;
	
	/**
	 * 
	 * @param flowId This flow's identifier
	 * @param actId The activity which this flow wraps
	 * @param finished If true this activity has been already performed
	 */
	public SingleFlowState(int flowId, int actId, boolean finished, boolean executing) {
		this.flowId = flowId;
		this.actId = actId;
		this.finished = finished;
		this.executing = executing;
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
	 * @return Returns true if this single flow is being currently executing.
	 */
	public boolean isExecuting() {
		return executing;
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
		if (executing)
			for (Integer rtId : caughtResources)
				str.append(" " + rtId);
		return str.toString();
	}
}
