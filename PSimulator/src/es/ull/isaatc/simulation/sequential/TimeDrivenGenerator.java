/**
 * 
 */
package es.ull.isaatc.simulation.sequential;

import es.ull.isaatc.simulation.model.ModelCycle;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.CycleIterator;

/**
 * A generator which creates elements following a temporal pattern. 
 * @author Ivan Castilla Rodrguez
 */
public class TimeDrivenGenerator extends Generator {
    /** Cycle that controls the generation of elements. */
    protected final Cycle cycle;
    /** The iterator which moves through the defined cycle */
    private CycleIterator cycleIter = null;

    /**
     * Creates a generator driven by a time cycle.
     * @param simul Simulation which uses this generator
     * @param creator The way the elements are created every "tic" of the cycle 
     * @param cycle Control of the time between generations 
     */
	public TimeDrivenGenerator(Simulation simul, BasicElementCreator creator, ModelCycle cycle) {
		super(simul, creator);
		this.cycle = cycle.getCycle();
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.BasicElement#init()
	 */
	@Override
	protected void init() {
		cycleIter = cycle.iterator(simul.getInternalStartTs(), simul.getInternalEndTs());
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
    public double nextTs() {
		return cycleIter.next();
    }

    /**
     * Launches a new generation event (if needed) or a finalize 
     * event (if there is no more generation cycles remain).
     */
	@Override
	public void beforeCreate() {
		double newTs = nextTs();
		if (Double.isNaN(newTs))
		 	notifyEnd();
		else {
			GenerateEvent e = new GenerateEvent(newTs);
			addEvent(e);
		}
	}
}
