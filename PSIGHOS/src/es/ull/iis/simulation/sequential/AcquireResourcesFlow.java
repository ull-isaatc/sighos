/**
 * 
 */
package es.ull.iis.simulation.sequential;

import java.util.ArrayDeque;
import java.util.Iterator;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.sequential.flow.SingleFlow;

/**
 * @author Iván Castilla
 *
 */
public class AcquireResourcesFlow extends SingleFlow {
	private final int resourcesId;

	/**
	 * @param simul
	 * @param description
	 */
	public AcquireResourcesFlow(Simulation simul, String description, int resourcesId) {
		super(simul, description);
		if (resourcesId <= 0)
			simul.error("Trying to create a negative resource Id in acquire resources flow: " + description);
		this.resourcesId = resourcesId;
	}

	/**
	 * @param simul
	 * @param description
	 * @param priority
	 */
	public AcquireResourcesFlow(Simulation simul, String description, int resourcesId, int priority) {
		super(simul, description, priority);
		if (resourcesId <= 0)
			simul.error("Trying to create a negative resource Id in acquire resources flow: " + description);
		this.resourcesId = resourcesId;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.core.BasicStep#request(es.ull.iis.simulation.core.WorkThread)
	 */
	@Override
	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (beforeRequest(wThread.getElement())) {
					final Element elem = wThread.getElement();
					simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, elem, ElementActionInfo.Type.REQACT, elem.getTs()));
					if (elem.isDebugEnabled())
						elem.debug("Requests\t" + this + "\t" + description);
					if (validElement(wThread)) {
						// There are enough resources to perform the activity
						final ArrayDeque<Resource> solution = isFeasible(wThread); 
						if (solution != null) {
							carryOut(wThread, solution);
						}
						else {
							queueAdd(wThread); // The element is introduced in the queue
						}
					} else {
						queueAdd(wThread); // The element is introduced in the queue
					}
				}
				else {
					wThread.setExecutable(false, this);
					next(wThread);
				}
			}
			else {
				wThread.updatePath(this);
				next(wThread);
			}
		} else
			wThread.notifyEnd();
	}


	@Override
	public void carryOut(WorkThread wThread, ArrayDeque<Resource> solution) {
		final Element elem = wThread.getElement();
		wThread.getSingleFlow().afterStart(elem);
		wThread.acquireResources(solution, resourcesId);
		simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, elem, ElementActionInfo.Type.STAACT, elem.getTs()));
		elem.addEvent(elem.new FinishFlowEvent(elem.getTs(), wThread.getSingleFlow(), wThread));
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.core.BasicStep#isFeasible(es.ull.iis.simulation.core.WorkThread)
	 */
	@Override
	public ArrayDeque<Resource> isFeasible(WorkThread wt) {
    	if (!stillFeasible)
    		return null;
        Iterator<ActivityWorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup wg = iter.next();
        	ArrayDeque<Resource> solution = wg.isFeasible(wt); 
            if (solution != null) {
                wt.setExecutionWG(wg);
        		wt.getElement().debug("Can carry out \t" + this + "\t" + wt.getExecutionWG());
                return solution;
            }            
        }
        stillFeasible = false;
        return null;
	}

	// TODO: Consider removal from superclass
	@Override
	public boolean validElement(WorkThread wThread) {
		return true;
	}

	// TODO Consider removal from superclass
	@Override
	public void finish(WorkThread wThread) {
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.sequential.SimulationObject#getObjectTypeIdentifier()
	 */
	@Override
	public String getObjectTypeIdentifier() {
		return "ACQ";
	}

}
