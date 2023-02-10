/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.ArrayList;

import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author Iván Castilla
 *
 */
public class PortParkingExperiment extends Experiment {
	private static final int NEXP = 1;
	private static final long ENDTS = 200;
	private static final int PARKING_CAPACITY = 5;
	private static final ArrayList<Double> PATH_TIMES;
	private static final int NQUAYS = 1;
	
	static {
		PATH_TIMES = new ArrayList<>();
		PATH_TIMES.add(2.0);
		PATH_TIMES.add(3.0);	
	}
	
	/**
	 */
	public PortParkingExperiment() {
		super("Basic experiment with parking", NEXP);
	}
	@Override
	public Simulation getSimulation(int ind) {
		// int id, long endTs, int parkingCapacity, ArrayList<Double> pathTimes
		
		final PortParkingModel sim =  new PortParkingModel(ind, ENDTS, PARKING_CAPACITY, PATH_TIMES, NQUAYS);
		sim.addInfoReceiver(new PortParkingListener());
		return sim;
	}


	public static void main(String[] args) {
		new PortParkingExperiment().start();
	}
	
}
