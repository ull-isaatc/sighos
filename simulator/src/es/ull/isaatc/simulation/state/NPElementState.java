/**
 * 
 */
package es.ull.isaatc.simulation.state;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class NPElementState extends ElementState {
	protected int parentElemId;
	protected int parentSFId;

	/**
	 * @param parentState
	 * @param parentElemId
	 * @param parentSFId
	 */
	public NPElementState(ElementState parentState, int parentElemId, int parentSFId) {
		super(parentState.getElemId(), parentState.getElemTypeId(), parentState.getFlowState(), parentState.getCurrentActId(),  
    			parentState.getCurrentWGId(), parentState.getPending(), parentState.getRequested());
		this.parentElemId = parentElemId;
		this.parentSFId = parentSFId;
		caughtResources = parentState.getCaughtResources();
	}

	/**
	 * @return Returns the parentElemId.
	 */
	public int getParentElemId() {
		return parentElemId;
	}

	/**
	 * @return Returns the parentSFId.
	 */
	public int getParentSFId() {
		return parentSFId;
	}

}
