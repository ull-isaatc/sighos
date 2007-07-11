/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.state.SimulationState;

/**
 * A simulation with standard AM configuration which creates a LP per AM.
 * This class is not finished and should not be used.
 * @see StandardAMSimulation
 * @author Iván Castilla Rodríguez
 */
public abstract class SimpleLPSimulation extends StandardAMSimulation {

	/**
	 * Creates a new instance of Simulation
	 * 
	 * @param id This simulation's identifier
	 * @param description A short text describing this simulation.
	 */
	public SimpleLPSimulation(int id, String description) {
		super(id, description);
	}
	
	public SimpleLPSimulation(int id, String description, double startTs, double endTs) {
		super(id, description, startTs, endTs);
	}

	public SimpleLPSimulation(int id, String description, SimulationState previousState, double endTs) {
		super(id, description, previousState, endTs);
	}
	
	@Override
	protected void createLogicalProcesses() {
		logicalProcessList = new LogicalProcess[activityManagerList.size() + 1];
		for (int i = 0; i < activityManagerList.size(); i++)
			logicalProcessList[i] = new LogicalProcess(this, startTs, endTs);
		// Creo el último proceso lógico, que servirá de "cajón de sastre"
		logicalProcessList[activityManagerList.size()] = new LogicalProcess(this, startTs, endTs);
	}

}
