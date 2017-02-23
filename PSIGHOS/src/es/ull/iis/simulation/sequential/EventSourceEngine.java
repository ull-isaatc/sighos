package es.ull.iis.simulation.sequential;

import es.ull.iis.simulation.model.EventSource;

/**
 * Represents the simulation component that carries out events. 
 * @author Carlos Martin Galan
 */
public abstract class EventSourceEngine extends SimulationObject {
	protected final EventSource modelEv;
	
    /**
     * Creates a basic element. 
     * @param id Element's identifier
     * @param simul Attached simulation object
     */
	public EventSourceEngine(SequentialSimulationEngine simul, EventSource modelEv, String objTypeId) {
		super(modelEv.getIdentifier(), simul, objTypeId);
		this.modelEv = modelEv; 
	}

    /**
     * Informs the element that it must finish its execution. Thus, a FinalizeEvent is
     * created.
     */
    protected void notifyEnd() {
        simul.addEvent(modelEv.onDestroy());
    }
    
}
