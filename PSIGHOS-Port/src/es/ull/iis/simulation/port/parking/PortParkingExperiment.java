/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author Iván Castilla
 *
 */
public class PortParkingExperiment extends Experiment {
	private static final int NEXP = 1;
	private static final long ENDTS = 550;
	private static final int PARKING_CAPACITY = 5;
	private static final TimeFunction[] T_TO_QUAY = {
			TimeFunctionFactory.getInstance("ConstantVariate", 50),	
			TimeFunctionFactory.getInstance("ConstantVariate", 60),	
			TimeFunctionFactory.getInstance("ConstantVariate", 70),	
			TimeFunctionFactory.getInstance("ConstantVariate", 80)	
	};
	
	/**
	 */
	public PortParkingExperiment() {
		super("Basic experiment with parking", NEXP);
	}
	@Override
	public Simulation getSimulation(int ind) {
		final PortParkingModel sim =  new PortParkingModel(ind, ENDTS, PARKING_CAPACITY, T_TO_QUAY);
		sim.addInfoReceiver(new PortParkingListener());
		return sim;
	}


	public static void main(String[] args) {
		new PortParkingExperiment().start();
	}
	
}
