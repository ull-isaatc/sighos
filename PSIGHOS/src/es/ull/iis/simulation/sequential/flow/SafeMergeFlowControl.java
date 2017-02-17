/**
 * 
 */
package es.ull.iis.simulation.sequential.flow;

import es.ull.iis.simulation.model.flow.MergeFlow;
import es.ull.iis.simulation.sequential.WorkThread;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SafeMergeFlowControl extends MergeFlowControl {
	/** Current amount of arrived branches */
	protected int checked;

	public SafeMergeFlowControl(MergeFlow flow) {
		super(flow);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.MergeFlowControl#arrive(es.ull.iis.simulation.WorkThread)
	 */
	@Override
	public void arrive(WorkThread wThread) {
		if (wThread.isExecutable())
			trueChecked++;
		else
			outgoingFalseToken.addFlow(wThread.getToken().getPath());
		checked++;
	}

	@Override
	public boolean reset() {
		checked = 0;
		return super.reset();
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.MergeFlowControl#canReset()
	 */
	@Override
	public boolean canReset(int checkValue) {
		return (checked == checkValue);
	}

}
