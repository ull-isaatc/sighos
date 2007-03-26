/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.CycleIterator;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TimeDrivenGenerator extends Generator {
    /** Cycle that controls the generation of elements. */
    protected Cycle cycle;
    /** The iterator which moves through the defined cycle */
    private CycleIterator cycleIter = null;

    /**
     * 
     * @param simul Simulation which uses this generator
     * @param creator The way the elements are created every "tic" of the cycle 
     * @param cycle Control of the time between generations 
     */
	public TimeDrivenGenerator(Simulation simul, BasicElementCreator creator, Cycle cycle) {
		super(simul, creator);
		this.cycle = cycle;
	}

	@Override
	public void preprocess() {
	}

	@Override
	public void postprocess() {
        double newTs = nextTs();
        if (Double.isNaN(newTs))
            notifyEnd();
        else {
            GenerateEvent e = new GenerateEvent(newTs);
            addEvent(e);
        }
	}

	@Override
	protected void init() {
		cycleIter = cycle.iterator(simul.getStartTs(), simul.getEndTs());
    	double newTs = nextTs();
    	if (Double.isNaN(newTs))
            notifyEnd();
        else {
        	ts = newTs;
            GenerateEvent e = new GenerateEvent(ts);
            addEvent(e);
        }
	}

    /**
     * Returns the next timestamp when elements have to be generated. 
     * @return The next timestamp to generate elements. NaN if this generator
     * don't have to create more elements.
     */
    private double nextTs() {
		return cycleIter.next();
    }

}
