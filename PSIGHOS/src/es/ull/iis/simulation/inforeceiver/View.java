package es.ull.iis.simulation.inforeceiver;

import es.ull.iis.simulation.model.SimulationEngine;

public abstract class View extends InfoReceiver {
	
	public View(SimulationEngine simul, String description){
		super(simul, description);
	}
	
}
