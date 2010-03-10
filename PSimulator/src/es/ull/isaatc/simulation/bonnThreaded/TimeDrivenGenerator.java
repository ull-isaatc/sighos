/**
 * 
 */
package es.ull.isaatc.simulation.bonnThreaded;

import es.ull.isaatc.simulation.common.SimulationCycle;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.DiscreteCycleIterator;

/**
 * A generator which creates elements following a temporal pattern. 
 * @author Ivan Castilla Rodrguez
 */
public class TimeDrivenGenerator extends Generator implements es.ull.isaatc.simulation.common.TimeDrivenGenerator {
    /** Cycle that controls the generation of elements. */
    protected final Cycle cycle;
    /** The iterator which moves through the defined cycle */
    private DiscreteCycleIterator cycleIter = null;

    /**
     * Creates a generator driven by a time cycle.
     * @param simul Simulation which uses this generator
     * @param creator The way the elements are created every "tic" of the cycle 
     * @param cycle Control of the time between generations 
     */
	public TimeDrivenGenerator(Simulation simul, BasicElementCreator creator, SimulationCycle cycle) {
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
    	long newTs = nextTs();
    	if (newTs == -1)
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
    public long nextTs() {
		return cycleIter.next();
    }

    /**
     * Launches a new generation event (if needed) or a finalize 
     * event (if there is no more generation cycles remain).
     */
	public void beforeCreate() {
		long newTs = nextTs();
    	if (newTs == -1)
		 	notifyEnd();
		else {
			GenerateEvent e = new GenerateEvent(newTs);
			addEvent(e);
		}
	}
}
