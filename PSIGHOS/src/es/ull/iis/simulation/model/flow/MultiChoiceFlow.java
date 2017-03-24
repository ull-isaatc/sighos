package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;


/**
 * A conditional flow which allows all outgoing branches which meet their condition to be activated.
 * Successors are evaluated in order. The rest of branches produce a false work thread.<p>   
 * Meets the Multi-Choice pattern (WFP6). 
 * Successors are evaluated in order.
 * @author ycallero
 */
public class MultiChoiceFlow extends ConditionalFlow {
	
	/**
	 * Creates a new MultiChoiceFlow.
	 */
	public MultiChoiceFlow(Simulation model) {
		super(model);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.BasicFlow#next(es.ull.iis.simulation.FlowExecutor)
	 */
	@Override
	public void next(ElementInstance wThread) {
		super.next(wThread);
		if (wThread.isExecutable())
			for (int i = 0; i < successorList.size(); i++) {
				boolean res = conditionList.get(i).check(wThread);
				wThread.getElement().addRequestEvent(successorList.get(i), wThread.getSubsequentElementInstance(res, this, wThread.getToken()));
			}
		else
			for (int i = 0; i < successorList.size(); i++)
				wThread.getElement().addRequestEvent(successorList.get(i), wThread.getSubsequentElementInstance(false, this, wThread.getToken()));
		wThread.notifyEnd();
	}
}
