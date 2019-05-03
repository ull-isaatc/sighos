/**
 * 
 */
package es.ull.iis.simulation.laundry;

import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.laundry.listener.WashingUsageListener;
import es.ull.iis.simulation.laundry.listener.WashingWaitingListener;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author icasrod
 *
 */
public class LaundryMain extends Experiment {
	private final static TimeUnit LIST_TIME_UNIT = TimeUnit.MINUTE;
	private final static TimeStamp TIME_GAP = new TimeStamp(TimeUnit.MINUTE, 3);

	/**
	 * @param description
	 * @param nExperiments
	 * @param parallel
	 */
	public LaundryMain(String description, int nExperiments) {
		super(description, nExperiments);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.model.Experiment#getSimulation(int)
	 */
	@Override
	public Simulation getSimulation(int ind) {
		final LaundrySimulation sim = new LaundrySimulation(ind);
		sim.addInfoReceiver(new StdInfoView());
		sim.addInfoReceiver(new WashingUsageListener(sim, TIME_GAP));
		sim.addInfoReceiver(new WashingWaitingListener(sim, LIST_TIME_UNIT));
		return sim;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new LaundryMain("Hospital laundry experimentation", 1).start();
	}

}
