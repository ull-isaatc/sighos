/**
 * 
 */
package es.ull.isaatc.test;

import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.util.Output;

class BaseSim extends StandAloneLPSimulation {

	public BaseSim() {
		super("TEST");
	}

	@Override
	protected void createModel() {
	}	
}

class BaseExp extends Experiment {
	/**
	 * @param description
	 */
	public BaseExp(String description) {
		super(description, 1, 0.0, 100.0);
	}

	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = new BaseSim();
		sim.setOutput(new Output(true));
		return sim;
	}	
}

/**
 * Base model for testing
 * @author Iván Castilla Rodríguez
 */
public class BaseTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BaseExp("Base Experiment").start();
	}

}
