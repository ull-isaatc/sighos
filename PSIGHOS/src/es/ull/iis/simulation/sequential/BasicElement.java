package es.ull.iis.simulation.sequential;

import es.ull.iis.function.TimeFunctionParams;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.EventSource;

/**
 * Represents the simulation component that carries out events. 
 * @author Carlos Martin Galan
 */
public abstract class BasicElement extends TimeStampedSimulationObject implements EventSource, TimeFunctionParams {

    /**
     * Creates a basic element. 
     * @param id Element's identifier
     * @param simul Attached simulation object
     */
	public BasicElement(int id, Simulation simul) {
		super(id, simul);
	}

    /**
     * Starts the element execution.
     * @param ts The time stamp when the starting event of this element is scheduled
     */    
	public DiscreteEvent onCreate(long ts) {
        return new StartEvent(ts);
	}
	
	public DiscreteEvent onDestroy() {
        return new FinalizeEvent();
	}
	
    /**
     * Contains the declaration of the first event/s that the element executes.
     */
    protected abstract void init();
    
    /**
     * Contains the actions the element carried out when it finishes its execution. 
     */
    protected abstract void end();
    
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
    	debug("Finished");
        addEvent(new FinalizeEvent());
    }
    
	@Override
	public String getObjectTypeIdentifier() {
		return "BE";
	}
	
	@Override
	public long getTs() {
		return simul.getTs();
	}
	
	@Override
	public double getTime() {
		return simul.getTs();
	}
	
    /**
     * The last event this element executes. It decrements the total amount of elements of the
     * simulation.
     * @author Iván Castilla Rodríguez
     */
    public final class FinalizeEvent extends DiscreteEvent {
        
        public FinalizeEvent() {
            super(simul.getTs());
        }
        
        public void event() {
        	debug("Ends execution");
        	BasicElement.this.end();
        }
    }

    /**
     * The first event this element executes.
     * @author Iván Castilla Rodríguez
     *
     */
    public final class StartEvent extends DiscreteEvent {
        public StartEvent(long ts) {
            super(ts);
        }

        public void event() {
        	debug("Starts Execution");
            BasicElement.this.init();
        }
    }
}
