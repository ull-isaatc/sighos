package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.SimulationEngine;

public abstract class SimulationInfo {

	final protected SimulationEngine simul;
	
	public SimulationInfo(SimulationEngine simul) {
		this.simul = simul;
	}

	public SimulationEngine getSimul() {
		return simul;
	}
}
