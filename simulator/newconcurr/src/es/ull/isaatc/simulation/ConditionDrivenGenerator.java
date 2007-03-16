/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.info.SimulationListener;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ConditionDrivenGenerator extends Generator implements
		SimulationListener {

	/**
	 * @param simul
	 * @param creator
	 */
	public ConditionDrivenGenerator(Simulation simul,
			BasicElementCreator creator) {
		super(simul, creator);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Generator#preprocess()
	 */
	@Override
	public void preprocess() {
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Generator#postprocess()
	 */
	@Override
	public void postprocess() {
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.BasicElement#init()
	 */
	@Override
	protected void init() {
	}

}
