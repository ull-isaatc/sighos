package es.ull.iis.simulation.sequential.flow;

import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.WorkThread;


/**
 * A conditional flow which allows all outgoing branches which meet their condition to be activated.
 * Successors are evaluated in order. The rest of branches produce a false work thread.<p>   
 * Meets the Multi-Choice pattern (WFP6). 
 * Successors are evaluated in order.
 * @author ycallero
 */
public class MultiChoiceFlow extends ConditionalFlow implements es.ull.iis.simulation.core.flow.MultiChoiceFlow<WorkThread> {
	
	/**
	 * Creates a new MultiChoiceFlow.
	 * @param simul Simulation this flow belongs to
	 */
	public MultiChoiceFlow(Simulation simul) {
		super(simul);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.BasicFlow#next(es.ull.iis.simulation.WorkThread)
	 */
	@Override
	public void next(WorkThread wThread) {
		super.next(wThread);
		if (wThread.isExecutable())
			for (int i = 0; i < successorList.size(); i++) {
				boolean res = conditionList.get(i).check(wThread.getElement());
				wThread.getElement().addRequestEvent(successorList.get(i), wThread.getInstanceSubsequentWorkThread(res, this, wThread.getToken()));
			}
		else
			for (int i = 0; i < successorList.size(); i++)
				wThread.getElement().addRequestEvent(successorList.get(i), wThread.getInstanceSubsequentWorkThread(false, this, wThread.getToken()));
		wThread.notifyEnd();
	}
}
