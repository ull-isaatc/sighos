package es.ull.iis.simulation.info;

import es.ull.iis.simulation.model.Model;

public abstract class SimulationInfo {

	final protected Model model;
	
	public SimulationInfo(Model model) {
		this.model = model;
	}

	public Model getModel() {
		return model;
	}
}
