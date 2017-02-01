/**
 * 
 */
package es.ull.iis.simulation.sequential;

import java.util.ArrayDeque;
import java.util.Iterator;

import es.ull.iis.simulation.info.ElementActionInfo;

/**
 * @author Iván Castilla
 *
 */
public class AcquireResources extends BasicStep {
	private final int resourcesId;

	/**
	 * @param simul
	 * @param description
	 */
	public AcquireResources(Simulation simul, String description, int resourcesId) {
		super(simul, description);
		this.resourcesId = resourcesId;
	}

	/**
	 * @param simul
	 * @param description
	 * @param priority
	 */
	public AcquireResources(Simulation simul, String description, int resourcesId, int priority) {
		super(simul, description, priority);
		this.resourcesId = resourcesId;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.core.BasicStep#request(es.ull.iis.simulation.core.WorkThread)
	 */
	@Override
	public void request(WorkThread wThread) {
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
        		debug("Resources can be acquired by\t" + wt.getElement().getIdentifier() + "\t" + wt.getExecutionWG());
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
	public boolean finish(WorkThread wThread) {
		return true;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.sequential.SimulationObject#getObjectTypeIdentifier()
	 */
	@Override
	public String getObjectTypeIdentifier() {
		return "ACQ";
	}

}
