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
 * The main class to launch simulation experiments on the laundry
 * @author Iván Castilla Rodríguez
 *
 */
public class LaundryMain extends Experiment {
	private final static TimeUnit LIST_TIME_UNIT = TimeUnit.MINUTE;
	private final static TimeStamp TIME_GAP = new TimeStamp(TimeUnit.MINUTE, 3);

	/**
	 * @param nExperiments
	 */
	public LaundryMain(int nExperiments) {
		super("Hospital laundry experimentation", nExperiments);
	}

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
		new LaundryMain(1).start();
	}

}
