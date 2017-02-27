package es.ull.iis.simulation.sequential;

import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.engine.SimulationEngine;

/**
 * Represents the simulation component that carries out events. 
 * @author Carlos Martin Galan
 */
public class EventSourceEngine<ES extends EventSource> extends EngineObject implements es.ull.iis.simulation.model.engine.EventSourceEngine<ES> {
	protected final ES modelEv;
	
    /**
     * Creates a basic element. 
     * @param id Element's identifier
     * @param simul Attached simulation object
     */
	public EventSourceEngine(SimulationEngine simul, ES modelEv, String objTypeId) {
		super(modelEv.getIdentifier(), simul, objTypeId);
		this.modelEv = modelEv; 
	}

    /**
     * Informs the element that it must finish its execution. Thus, a FinalizeEvent is
     * created.
     */
    public void notifyEnd() {
        simul.addEvent(modelEv.onDestroy());
    }
    
}
