/**
 * 
 */
package es.ull.isaatc.test;

import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.simulation.listener.SimulationListener;

class MinimumSim extends StandAloneLPSimulation {

	public MinimumSim(String description) {
		super(description);
	}

	@Override
	protected void createModel() {
	}	
}

class MinimumExp extends Experiment {
	static final double STARTTS = 0.0;
	static final double ENDTS = 100.0;
	
	/**
	 * @param description
	 * @param nExperiments
	 */
	public MinimumExp(String description, int nExperiments) {
		super(description, nExperiments, STARTTS, ENDTS);
	}

	@Override
	public Simulation getSimulation(int ind) {
		MinimumSim sim = new MinimumSim("Minimum");
		sim.addListener(new MinimumListener(STARTTS, ENDTS));
		return sim;
	}	
}

class MinimumListener implements SimulationListener {
	double startTs;
	double endTs;
	boolean result = true;
	
	MinimumListener(double startTs, double endTs) {
		this.startTs = startTs;
		this.endTs = endTs;
	}

	public void infoEmited(SimulationObjectInfo info) {
	}

	public void infoEmited(SimulationStartInfo info) {
		System.out.print("Minimum test result......................");
		if (info.getSimulation().getStartTs() != startTs)
			result = false;
	}

	public void infoEmited(SimulationEndInfo info) {
		if (info.getSimulation().getEndTs() != endTs)
			result = false;
		if (result)
			System.out.println("OK");
		else
			System.out.println("ERROR");
	}

	public void infoEmited(TimeChangeInfo info) {
	}
	
}

/**
 * Minimum model that can be created. It carries out one simulation which lasts 100 time units.
 * The model used includes no elements, activities, resources... at all.
 * The typical output should be:
 * <pre><code>SIMULATION MODEL CREATED
 * Graph created
 * Activity Managers:
 * [BE0]	0.0	Starts Execution
 * [LP0]	100.0	SIMULATION TIME ADVANCING 100.0
 * [LP0]	100.0	Execution queue freed
 * [LP0]	100.0	SIMULATION TIME FINISHES
 * [LP0]	100.0	Waiting	1	Executing	0
 * SIMULATION COMPLETELY FINISHED</code></pre>
 * This output shows the creation of the empty model, with no activity managers; the start of a basic
 * element which controls the end of the simulation time; and the behaviour of the logical process created
 * to perform the simulation.
 * @author Iván Castilla Rodríguez
 */
public class MinimumTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MinimumExp("Minimum Experiment", 1).start();
	}

}
