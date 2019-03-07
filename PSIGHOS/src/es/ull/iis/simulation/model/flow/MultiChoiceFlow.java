package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;


/**
 * A conditional flow which allows all outgoing branches which meet their condition to be activated.
 * Successors are evaluated in order. The rest of branches produce a false element instance.<p>   
 * Meets the Multi-Choice pattern (WFP6). 
 * Successors are evaluated in order.
 * @author ycallero
 */
public class MultiChoiceFlow extends ConditionalFlow {
	
	/**
	 * Creates a new MultiChoiceFlow.
	 * @param model The simulation model this flow belongs to
	 */
	public MultiChoiceFlow(final Simulation model) {
		super(model);
	}

	@Override
	public void next(ElementInstance ei) {
		super.next(ei);
		if (ei.isExecutable())
			for (int i = 0; i < successorList.size(); i++) {
				boolean res = conditionList.get(i).check(ei);
				ei.getElement().addRequestEvent(successorList.get(i), ei.getSubsequentElementInstance(res, this, ei.getToken()));
			}
		else
			for (int i = 0; i < successorList.size(); i++)
				ei.getElement().addRequestEvent(successorList.get(i), ei.getSubsequentElementInstance(false, this, ei.getToken()));
		ei.notifyEnd();
	}
}
