package es.ull.iis.simulation.sequential;

import java.util.ArrayList;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.engine.EngineObject;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * Represents case instances that make use of activity flows in order to carry out
 * their events.
 * 
 * @author Iván Castilla Rodríguez
 */
public class ElementEngine extends EngineObject implements es.ull.iis.simulation.model.engine.ElementEngine {
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
	public ElementEngine(SequentialSimulationEngine simul, Element modelElem) {
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

	@Override
	public void incInQueue(ElementInstance fe) {
		inQueue.add(fe);
	}

	@Override
	public void decInQueue(ElementInstance fe) {
		inQueue.remove(fe);
	}

	@Override
	public void notifyAvailableElement() {
		for (final ElementInstance fe : inQueue) {
            final RequestResourcesFlow act = (RequestResourcesFlow) fe.getCurrentFlow();
			if (act.isExclusive()) {
	            act.getManager().notifyAvailableElement(fe);
			}
		}
	}

	@Override
    public void notifyEnd() {
        simul.addEvent(modelElem.onDestroy(simul.getTs()));
    }

	@Override
	public void waitProtectedFlow(Flow flow) {
		// Nothing to do		
	}

	@Override
	public void signalProtectedFlow(Flow flow) {
		// Nothing to do		
	}

	@Override
	public ElementInstanceEngine getElementInstance(ElementInstance ei) {
		return new ElementInstanceEngine((SequentialSimulationEngine) simul, ei);
	}
    
}
