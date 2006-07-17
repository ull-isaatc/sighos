/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class GroupFlowState extends FlowState {
	protected ArrayList<FlowState> descendants;
	protected int finished;
	
	public GroupFlowState(int finished) {
		this.finished = finished;
		descendants = new ArrayList<FlowState>();
	}
	
	public void add(FlowState flow) {
		descendants.add(flow);
	}

	/**
	 * @return Returns the descendants.
	 */
	public ArrayList<FlowState> getDescendants() {
		return descendants;
	}

	/**
	 * @return Returns the finished.
	 */
	public int getFinished() {
		return finished;
	}
}
