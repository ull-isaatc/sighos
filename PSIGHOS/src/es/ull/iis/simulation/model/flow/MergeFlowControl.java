/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.WorkToken;
import es.ull.iis.simulation.model.flow.MergeFlow;

/**
 * Control of incoming branches per element. Counts how many incoming branches have
 * arrived, how many of them were true, and if this element has ever activated the 
 * outgoing branch, i.e. if at least one of the incoming branches was able to pass. 
 * @author ycallero
 */
public abstract class MergeFlowControl {
	/** True if the outgoing branch was successfully activated */
	protected boolean activated = false;
	/** Current amount of valid arrived branches */
	protected int trueChecked;
	/** The token that is sent to the following step is the flow has to be reset and
	 * has not been activated yet */
	protected WorkToken outgoingFalseToken;
	protected MergeFlow flow;

	public MergeFlowControl(MergeFlow flow) {
		outgoingFalseToken = new WorkToken(false, flow);
		this.flow = flow;
	}
	
	public abstract void arrive(ElementInstance wThread);

	public abstract boolean canReset(int checkValue);
	
	public boolean reset() {
		trueChecked = 0;
		outgoingFalseToken.reset();
		outgoingFalseToken.addFlow(flow);
		activated = false;
		return true;
	}
	
	public void setActivated() {
		activated = true;
	}
	
	public boolean isActivated() {
		return activated;
	}
	
	public int getTrueChecked() {
		return trueChecked;
	}
	
	public WorkToken getOutgoingFalseToken() {
		return outgoingFalseToken;
	}
	
}
