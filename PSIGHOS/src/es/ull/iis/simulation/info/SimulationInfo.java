package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Simulation;

public abstract class SimulationInfo {

	final protected Simulation model;
	
	public SimulationInfo(Simulation model) {
		this.model = model;
	}

	public Simulation getModel() {
		return model;
	}
}
