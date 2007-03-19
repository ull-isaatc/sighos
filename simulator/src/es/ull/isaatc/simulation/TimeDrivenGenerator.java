/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.util.CycleIterator;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TimeDrivenGenerator extends Generator {
    /** Cycle that controls the generation of elements. */
    protected CycleIterator cycleIter;

	public TimeDrivenGenerator(Simulation simul, BasicElementCreator creator, CycleIterator cycleIter) {
		super(simul, creator);
		this.cycleIter = cycleIter;
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

}
