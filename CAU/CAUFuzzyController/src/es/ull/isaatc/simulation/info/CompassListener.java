/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.BasicElementCreator;
import es.ull.isaatc.simulation.Generator;
import es.ull.isaatc.simulation.info.SimulationListener;
import es.ull.isaatc.util.CycleIterator;

/**
 * This class offers the mechanism necessary for taking samples at a constant rate in a simulation.
 * @author Roberto Muñoz
 */
public abstract class CompassListener implements BasicElementCreator, SimulationListener {

	protected CycleIterator cycleIterator;
	protected double nextTs;
	
	/**
	 * @param cycleIterator
	 */
	public CompassListener(CycleIterator cycleIterator) {
		super();
		this.cycleIterator = cycleIterator;
		nextTs = cycleIterator.next();
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.BasicElementCreator#create(es.ull.isaatc.simulation.Generator)
	 */
	public void create(Generator gen) {
		nextTs = cycleIterator.next();
		takeSample(gen);
	}
	
	/**
	 * @return the nextTs
	 */
	public double getNextTs() {
		return nextTs;
	}

	public abstract void takeSample(Generator gen);
}