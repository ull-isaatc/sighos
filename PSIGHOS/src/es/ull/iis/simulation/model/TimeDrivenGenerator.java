/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.util.DiscreteCycleIterator;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class TimeDrivenGenerator<INF extends Generator.GenerationInfo> extends Generator<INF> implements EventSource {
    /** Cycle that controls the generation of elements. */
    protected final SimulationCycle cycle;
    /** The iterator which moves through the defined cycle */
    protected DiscreteCycleIterator cycleIter;

	public TimeDrivenGenerator(Simulation model, int nElem, SimulationCycle cycle) {
		super(model, model.getTimeDrivenGeneratorList().size(), nElem);
		this.cycle = cycle;
		model.add(this);
	}

	public TimeDrivenGenerator(Simulation model, TimeFunction nElem, SimulationCycle cycle) {
		super(model, model.getTimeDrivenGeneratorList().size(), nElem);
		this.cycle = cycle;
		model.add(this);
	}

	/**
	 * @return the cycle
	 */
	public SimulationCycle getCycle() {
		return cycle;
	}

    /**
     * Returns the next timestamp when elements have to be generated. 
     * @return The next timestamp to generate elements. -1 if this generator
     * don't have to create more elements.
     */
	public long nextEvent() {
		return cycleIter.next();
	}

	@Override
	public DiscreteEvent onDestroy(long ts) {
		return new DiscreteEvent.DefaultFinalizeEvent(this, ts);
	}
	
    public void notifyEnd() {
        simul.addEvent(onDestroy(getTs()));
    }
    
	@Override
	public DiscreteEvent onCreate(long ts) {
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
     * It simply invokes the <code>creator.create</code> method.
     */
    public class GenerateEvent extends DiscreteEvent {
        /**
         * Creates a new element-generation event.
         * @param ts Timestamp when this event must be executed.
         */
        public GenerateEvent(long ts) {
            super(ts);
        }
        
        /**
         * Generates the elements corresponding to this timestamp. After this, 
         * it checks the following event.
         */
        @Override
		public void event() {
    		create(ts);
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
