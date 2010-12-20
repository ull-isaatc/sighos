package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

public abstract class SimulationInfo {

	final protected Simulation simul;
	
	public SimulationInfo(Simulation simul) {
		this.simul = simul;
	}

	public Simulation getSimul() {
		return simul;
	}
}
