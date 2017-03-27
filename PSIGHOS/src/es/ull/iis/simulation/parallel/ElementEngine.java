package es.ull.iis.simulation.parallel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.engine.EngineObject;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * Represents case instances that make use of activity flows in order to carry out
 * their events.
 * TODO Comment
 * @author Iván Castilla Rodríguez
 */
public class ElementEngine extends EngineObject implements es.ull.iis.simulation.model.engine.ElementEngine {
	/** Activity queues in which this element is. This list is used to notify the activities
	 * when the element becomes available. */
	protected final ArrayList<ElementInstance> inQueue;
	/** A structure to protect access to shared flows */
	protected final Map<Flow, AtomicBoolean> protectedFlows;
    /** Access control */
    final private AtomicBoolean sem;
	/** The associated {@link Element} */
	private final Element modelElem;
    /** Flag that indicates if the element has finished its execution */
    final private AtomicBoolean endFlag;
	
	/**
	 * Creates a new element.
	 * @param id Element's identifier
	 * @param simul ParallelSimulationEngine object
	 * @param et Element type this element belongs to
	 * @param flow First step of this element's flow
	 */
	public ElementEngine(ParallelSimulationEngine simul, Element modelElem) {
		super(modelElem.getIdentifier(), simul, "E");
		this.modelElem = modelElem;
		protectedFlows = new HashMap<Flow, AtomicBoolean>();
        sem = new AtomicBoolean(false);
        inQueue = new ArrayList<ElementInstance>();
        endFlag = new AtomicBoolean(false);
	}

	/**
	 * Returns the associated {@link Element}
	 * @return the associated {@link Element}
	 */
	public Element getModelElem() {
		return modelElem;
	}

    /**
     * Sends a "wait" signal to the semaphore.
     */    
    protected void waitSemaphore() {
    	while (!sem.compareAndSet(false, true));
    }
    
    /**
     * Sends a "continue" signal to the semaphore.
     */    
    protected void signalSemaphore() {
        sem.set(false);
    }
    
	@Override
    public void notifyEnd() {
    	if (!endFlag.getAndSet(true)) {
            simul.addEvent(modelElem.onDestroy(simul.getTs()));
        }
    }
    
	@Override
	public void incInQueue(ElementInstance fe) {
		synchronized(inQueue) {
			inQueue.add(fe);
		}
	}

	@Override
	public void decInQueue(ElementInstance fe) {
		synchronized(inQueue) {
			inQueue.remove(fe);
		}
	}

	@Override
	public void notifyAvailableElement() {
		synchronized(inQueue) {
			for (final ElementInstance fe : inQueue) {
	            final RequestResourcesFlow act = (RequestResourcesFlow) fe.getCurrentFlow();
				if (act.isExclusive()) {
		            act.getManager().notifyAvailableElement(fe);
				}
			}
		}		
	}
	
	/**
	 * Acquires a semaphore associated to a specific flow
	 * @param flow The flow to be requested
	 */
	public void waitProtectedFlow(Flow flow) {
		waitSemaphore();
		if (!protectedFlows.containsKey(flow)) {
			protectedFlows.put(flow, new AtomicBoolean(true));
			signalSemaphore();
		}
		else {
			signalSemaphore();
			final AtomicBoolean localBool = protectedFlows.get(flow);
			while (!localBool.compareAndSet(false, true));
		}
	}
	
	/**
	 * Releases a semaphore associated to a specific flow
	 * @param flow The flow to be requested
	 */
	public void signalProtectedFlow(Flow flow) {
		protectedFlows.get(flow).set(false);
	}

	@Override
	public ElementInstanceEngine getElementInstance(ElementInstance ei) {
		return new ElementInstanceEngine((ParallelSimulationEngine) simul, ei);
	}
	
}
