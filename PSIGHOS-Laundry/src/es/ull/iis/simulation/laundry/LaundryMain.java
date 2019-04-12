/**
 * 
 */
package es.ull.iis.simulation.laundry;

import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author icasrod
 *
 */
public class LaundryMain extends Experiment {
	private final static long TIME_GAP = 3; 

	/**
	 * @param description
	 * @param nExperiments
	 * @param parallel
	 */
	public LaundryMain(String description, int nExperiments, boolean parallel) {
		super(description, nExperiments, parallel);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.model.Experiment#getSimulation(int)
	 */
	@Override
	public Simulation getSimulation(int ind) {
		final LaundrySimulation sim = new LaundrySimulation(ind);
		sim.addInfoReceiver(new StdInfoView());
		sim.addInfoReceiver(new WashingUsageListener(sim, TIME_GAP));
		return sim;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new LaundryMain("Hospital laundry experimentation", 1, false).start();
	}

}
