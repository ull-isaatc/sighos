package es.ull.isaatc.simulation.common.info;

import es.ull.isaatc.simulation.common.Simulation;

public abstract class SimulationInfo {

	final protected Simulation simul;
	
	public SimulationInfo(Simulation simul) {
		this.simul = simul;
	}

	public Simulation getSimul() {
		return simul;
	}
	
	public abstract String toString();
}
