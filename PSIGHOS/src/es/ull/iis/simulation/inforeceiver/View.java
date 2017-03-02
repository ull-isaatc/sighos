package es.ull.iis.simulation.inforeceiver;

import es.ull.iis.simulation.model.Simulation;

public abstract class View extends InfoReceiver {
	
	public View(Simulation model, String description){
		super(model, description);
	}
	
}
