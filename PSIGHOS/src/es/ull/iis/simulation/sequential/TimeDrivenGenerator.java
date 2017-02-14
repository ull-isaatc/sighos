/**
 * 
 */
package es.ull.iis.simulation.sequential;

import es.ull.iis.util.Cycle;
import es.ull.iis.util.DiscreteCycleIterator;

/**
 * A generator which creates elements following a temporal pattern. 
 * @author Ivan Castilla Rodrguez
 */
public class TimeDrivenGenerator extends ElementGenerator {
    /** The iterator which moves through the defined cycle */
    private final DiscreteCycleIterator cycleIter;

    /**
     * Creates a generator driven by a time cycle.
     * @param simul Simulation which uses this generator
     * @param creator The way the elements are created every "tic" of the cycle 
     * @param cycle Control of the time between generations 
     */
	public TimeDrivenGenerator(Simulation simul, es.ull.iis.simulation.model.TimeDrivenGenerator generator) {
		super(simul, generator);
		final Cycle cycle = generator.getCycle().getCycle();
		cycleIter = cycle.iterator(simul.getInternalStartTs(), simul.getInternalEndTs());
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.BasicElement#init()
	 */
	@Override
	protected void init() {
    	final long newTs = nextEvent();
    	if (newTs == -1)
            notifyEnd();
        else {
            GenerateEvent e = new GenerateEvent(newTs);
            addEvent(e);
        }
	}

	@Override
    public long nextEvent() {
		return cycleIter.next();
    }

}
