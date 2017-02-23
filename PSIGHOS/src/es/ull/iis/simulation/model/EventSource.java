/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
public abstract class EventSource extends ModelObject {
	
	public EventSource(Model model, int id, String objectTypeId) {
		super(model, id, objectTypeId);
	}
	
	public abstract DiscreteEvent onCreate(long ts);
	public abstract DiscreteEvent onDestroy();

    /**
     * Informs the element that it must finish its execution. Thus, a FinalizeEvent is
     * created.
     */
    protected void notifyEnd() {
        model.getSimulationEngine().addEvent(onDestroy());
    }
    
    /**
     * The last event this element executes. It decrements the total amount of elements of the
     * simulation.
     * @author Iván Castilla Rodríguez
     */
    public final class DefaultFinalizeEvent extends DiscreteEvent {
        
        public DefaultFinalizeEvent() {
            super(model.getSimulationEngine().getTs());
        }
        
        public void event() {
        	Model.debug(EventSource.this + "\tEnds execution");
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
        	Model.debug(EventSource.this + "\tStarts Execution");
        }
    }
}
