package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Simulation;

public abstract class SimulationInfo {

	final protected Simulation simul;
	
	public SimulationInfo(Simulation simul) {
		this.simul = simul;
	}

	public Simulation getSimul() {
		return simul;
	}
}
