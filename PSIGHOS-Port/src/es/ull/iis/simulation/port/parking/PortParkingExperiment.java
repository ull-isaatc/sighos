/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author Iván Castilla
 *
 */
public class PortParkingExperiment extends Experiment {
	private static final int NEXP = 1;
	private static final long ENDTS = 6440;
	private static final String FILE_NAME = System.getProperty("user.dir") + "\\resources\\bootstrap_data.csv";
	
	/**
	 */
	public PortParkingExperiment() {
		super("Basic experiment with parking", NEXP);
	}
	@Override
	public Simulation getSimulation(int ind) {
		final PortParkingModel sim =  new PortParkingModel(ind, ENDTS, FILE_NAME);
//		final PortTest sim =  new PortTest(ind, ENDTS, PARKING_CAPACITY);
		sim.addInfoReceiver(new PortParkingListener());
		return sim;
	}


	public static void main(String[] args) {
		new PortParkingExperiment().start();
	}
	
}
