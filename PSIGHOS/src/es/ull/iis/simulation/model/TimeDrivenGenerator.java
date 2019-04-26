/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.util.cycle.DiscreteCycleIterator;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class TimeDrivenGenerator<INF extends Generator.GenerationInfo> extends Generator<INF> implements EventSource {
    /** Cycle that controls the generation of elements. */
    protected final SimulationCycle cycle;
    /** The iterator which moves through the defined cycle */
    protected DiscreteCycleIterator cycleIter;

	public TimeDrivenGenerator(final Simulation model, final int nElem, final SimulationCycle cycle) {
		super(model, model.getTimeDrivenGeneratorList().size(), nElem);
		this.cycle = cycle;
		model.add(this);
	}

	public TimeDrivenGenerator(final Simulation model, final TimeFunction nElem, final SimulationCycle cycle) {
		super(model, model.getTimeDrivenGeneratorList().size(), nElem);
		this.cycle = cycle;
		model.add(this);
	}

	/**
	 * Returns the cycle that drives the generation
	 * @return the cycle that drives the generation
	 */
	public SimulationCycle getCycle() {
		return cycle;
	}

    /**
     * Returns the next timestamp when elements have to be generated. 
     * @return The next timestamp to generate elements. -1 if this generator
     * don't have to create more elements.
     */
	protected long nextEvent() {
		return cycleIter.next();
	}

	@Override
	public DiscreteEvent onDestroy(final long ts) {
		return new DiscreteEvent.DefaultFinalizeEvent(this, ts);
	}
	
	@Override
    public void notifyEnd() {
        simul.addEvent(onDestroy(getTs()));
    }
    
	@Override
	public DiscreteEvent onCreate(final long ts) {
		cycleIter = cycle.getCycle().iterator(simul.getStartTs(), Long.MAX_VALUE);
    	final long newTs = nextEvent();
    	if (newTs == -1)
            return onDestroy(ts);
        else {
            return new GenerateEvent(newTs);
        }
	}
	
    /**
     * This event is invoked every time a new set of elements has to be generated. 
     * It simply invokes the {@link Generator#create(long)} method.
     */
    public class GenerateEvent extends DiscreteEvent {
        /**
         * Creates a new element-generation event.
         * @param ts Timestamp when this event must be executed.
         */
        public GenerateEvent(final long ts) {
            super(ts);
        }
        
        /**
         * Generates the elements corresponding to this timestamp. After this, 
         * it checks the following event.
         */
        @Override
		public void event() {
    		create();
            final long newTs = nextEvent();
            if (newTs == -1) {
    		 	notifyEnd();
            }
			else {
				final GenerateEvent e = new GenerateEvent(newTs);
				simul.addEvent(e);
			}
        }
    }
    
}
