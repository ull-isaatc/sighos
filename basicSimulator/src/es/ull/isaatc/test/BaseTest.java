/**
 * 
 */
package es.ull.isaatc.test;

import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.util.Output;

class BaseSim extends StandAloneLPSimulation {

	public BaseSim(int id) {
		super(id, "TEST", 0.0, 100.0);
	}

	@Override
	protected void createModel() {
	}	
}

class BaseExp extends PooledExperiment {
	/**
	 * @param description
	 */
	public BaseExp(String description) {
		super(description, 1);
	}

	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = new BaseSim(ind);
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
