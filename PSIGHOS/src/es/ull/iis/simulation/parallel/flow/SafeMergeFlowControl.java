/**
 * 
 */
package es.ull.iis.simulation.parallel.flow;

import es.ull.iis.simulation.parallel.FlowExecutor;

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
	public void arrive(FlowExecutor wThread) {
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
