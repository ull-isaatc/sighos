package es.ull.iis.simulation.parallel.flow;

import es.ull.iis.simulation.parallel.Simulation;
import es.ull.iis.simulation.parallel.WorkThread;


/**
 * A conditional flow which allows all outgoing branches which meet their condition to be activated.
 * Successors are evaluated in order. The rest of branches produce a false work thread.<p>   
 * Meets the Multi-Choice pattern (WFP6). 
 * Successors are evaluated in order.
 * @author ycallero
 */
public class MultiChoiceFlow extends ConditionalFlow implements es.ull.iis.simulation.core.flow.MultiChoiceFlow {
	
	/**
	 * Creates a new MultiChoiceFlow.
	 * @param simul Simulation this flow belongs to
	 */
	public MultiChoiceFlow(Simulation simul) {
		super(simul);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.BasicFlow#next(es.ull.isaatc.simulation.WorkThread)
	 */
	@Override
	public void next(WorkThread wThread) {
		super.next(wThread);
		if (wThread.isExecutable())
			for (int i = 0; i < successorList.size(); i++) {
				boolean res = conditionList.get(i).check(wThread.getElement());
				wThread.getInstanceSubsequentWorkThread(res, this, wThread.getToken()).requestFlow(successorList.get(i));
			}
		else
			for (int i = 0; i < successorList.size(); i++)
				wThread.getInstanceSubsequentWorkThread(false, this, wThread.getToken()).requestFlow(successorList.get(i));
		wThread.notifyEnd();
	}
}
