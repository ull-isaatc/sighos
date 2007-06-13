/**
 * 
 */
package es.ull.isaatc.simulation.state;

/**
 * Stores the state of an activity. 
 * <p>NOTE:The state of an activity has been maintained only
 * for backward-compatibility.
 * @author Iván Castilla Rodríguez
 */
public class ActivityState implements State {
	private static final long serialVersionUID = 873645092139635770L;
	/** This activity's identifier */ 
	protected int actId;
	
	/**
	 * @param actId This activity's identifier
	 */
	public ActivityState(int actId) {
		this.actId = actId;
	}

	/**
	 * @return This activity's identifier.
	 */
	public int getActId() {
		return actId;
	}

	@Override
	public String toString() {
		return "A" + actId;
	}
}
