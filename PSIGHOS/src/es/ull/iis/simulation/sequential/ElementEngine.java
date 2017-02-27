package es.ull.iis.simulation.sequential;

import java.util.ArrayList;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * Represents case instances that make use of activity flows in order to carry out
 * their events.
 * 
 * @author Iván Castilla Rodríguez
 */
public class ElementEngine extends EventSourceEngine<Element> implements es.ull.iis.simulation.model.engine.ElementEngine {
	/** Activity queues in which this element is. This list is used to notify the activities
	 * when the element becomes available. */
	protected final ArrayList<FlowExecutor> inQueue = new ArrayList<FlowExecutor>();

	private final es.ull.iis.simulation.model.Element modelElem;
	
	/**
	 * Creates a new element.
	 * @param simul Simulation object
	 * @param et Element type this element belongs to
	 * @param flow First step of this element's flow
	 */
	public ElementEngine(SequentialSimulationEngine simul, es.ull.iis.simulation.model.Element modelElem) {
		super(simul, modelElem, "E");
		this.modelElem = modelElem;
	}

	/**
	 * @return the modelElem
	 */
	public es.ull.iis.simulation.model.Element getModelElem() {
		return modelElem;
	}

	/**
	 * Notifies a new work thread is waiting in an activity queue.
	 * @param wt Work thread waiting in queue.
	 */
	@Override
	public void incInQueue(FlowExecutor fe) {
		inQueue.add(fe);
	}

	/**
	 * Notifies a work thread has finished waiting in an activity queue.
	 * @param wt Work thread that was waiting in a queue.
	 */
	@Override
	public void decInQueue(FlowExecutor fe) {
		inQueue.remove(fe);
	}

	@Override
	public void notifyAvailableElement() {
		// Checks if there are pending activities that haven't noticed the
		// element availability
		for (int i = 0; (modelElem.getCurrent() == null) && (i < inQueue.size()); i++) {
			final FlowExecutor fe = inQueue.get(i);
            final RequestResourcesFlow act = (RequestResourcesFlow) fe.getCurrentFlow();

			if (modelElem.isDebugEnabled())
				modelElem.debug("Calling availableElement()\t" + act + "\t" + act.getDescription());
			fe.availableElement(act);
		}
		
	}
}
