package es.ull.iis.simulation.sequential;

import es.ull.iis.simulation.model.DiscreteEvent;

/**
 * Represents the simulation component that carries out events. 
 * @author Carlos Martin Galan
 */
public abstract class EventSource extends TimeStampedSimulationObject {

    /**
     * Creates a basic element. 
     * @param id Element's identifier
     * @param simul Attached simulation object
     */
	public EventSource(int id, Simulation simul, String objTypeId) {
		super(id, simul, objTypeId);
	}

    /**
     * Adds a new event to the element's event list. 
     * @param e New event.
     */    
    public void addEvent(DiscreteEvent e) {
        if (e.getTs() < simul.getTs())
        	error("Causal restriction broken\t" + simul.getTs() + "\t" + e);
        // TODO: Check collateral effects
        else //if (e.getTs() > simul.getTs())
            simul.addWait(e);
//        else
//        	simul.addExecution(e);
    }

    /**
     * Informs the element that it must finish its execution. Thus, a FinalizeEvent is
     * created.
     */
    protected void notifyEnd() {
        addEvent(onDestroy());
    }
    
	public abstract DiscreteEvent onCreate(long ts);
	public abstract DiscreteEvent onDestroy();
	
	@Override
	public long getTs() {
		return simul.getTs();
	}
	
    /**
     * The last event this element executes. It decrements the total amount of elements of the
     * simulation.
     * @author Iván Castilla Rodríguez
     */
    public final class DefaultFinalizeEvent extends DiscreteEvent {
        
        public DefaultFinalizeEvent() {
            super(simul.getTs());
        }
        
        public void event() {
        	debug("Ends execution");
        }
    }

    /**
     * The first event this element executes.
     * @author Iván Castilla Rodríguez
     *
     */
    public final class DefaultStartEvent extends DiscreteEvent {
        public DefaultStartEvent(long ts) {
            super(ts);
        }

        public void event() {
        	debug("Starts Execution");
        }
    }
}
