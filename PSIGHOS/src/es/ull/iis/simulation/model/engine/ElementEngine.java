/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import java.util.ArrayList;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ElementEngine extends EngineObject implements EventSourceEngine {
	/** Activity queues in which this element is. This list is used to notify the activities
	 * when the element becomes available. */
	protected final ArrayList<ElementInstance> inQueue = new ArrayList<ElementInstance>();
	/** The associated {@link Element} */
	private final Element modelElem;

	/**
	 * Creates a new element.
	 * @param simul ParallelSimulationEngine object
	 * @param et Element type this element belongs to
	 * @param flow First step of this element's flow
	 */
	public ElementEngine(SimulationEngine simul, Element modelElem) {
		super(modelElem.getIdentifier(), simul, "E");
		this.modelElem = modelElem;
	}

	/**
	 * Returns the associated {@link Element}
	 * @return the associated {@link Element}
	 */
	public Element getModelElem() {
		return modelElem;
	}

	/**
	 * Notifies a new flow executor is waiting in an activity queue.
	 * @param fe Flow executor waiting in queue.
	 */
	public void incInQueue(ElementInstance fe) {
		inQueue.add(fe);
	}

	/**
	 * Notifies a flow executor has finished waiting in an activity queue.
	 * @param fe Flow executor that was waiting in a queue.
	 */
	public void decInQueue(ElementInstance fe) {
		inQueue.remove(fe);
	}

	public void notifyAvailableElement() {
		for (final ElementInstance fe : inQueue) {
            final RequestResourcesFlow act = (RequestResourcesFlow) fe.getCurrentFlow();
			if (act.isInExclusiveActivity()) {
	            act.getManager().notifyAvailableElement(fe);
			}
		}
	}

	@Override
    public void notifyEnd() {
        simul.addEvent(modelElem.onDestroy(simul.getTs()));
    }

	/**
	 * Acquires a semaphore associated to a specific flow. 
	 * Useful only for parallel implementations
	 * @param flow The flow to be requested
	 */
	public void waitProtectedFlow(Flow flow) {
		// Nothing to do		
	}

	/**
	 * Releases a semaphore associated to a specific flow
	 * Useful only for parallel implementations
	 * @param flow The flow to be requested
	 */
	public void signalProtectedFlow(Flow flow) {
		// Nothing to do		
	}

	public ElementInstanceEngine getElementInstance(ElementInstance ei) {
		return new ElementInstanceEngine(simul, ei);
	}
	
}
