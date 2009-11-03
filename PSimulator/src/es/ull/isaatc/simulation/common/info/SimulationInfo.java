package es.ull.isaatc.simulation.common.info;

import es.ull.isaatc.simulation.common.Model;

public abstract class SimulationInfo {

	final protected Model simul;
	
	public SimulationInfo(Model simul) {
		this.simul = simul;
	}

	public Model getSimul() {
		return simul;
	}
	
	public abstract String toString();
}
