package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

public abstract class SimulationInfo {

	final Simulation simul;
	
	SimulationInfo(Simulation simul) {
		this.simul = simul;
	}

	public Simulation getSimul() {
		return simul;
	}
	
	public abstract String toString();
}
