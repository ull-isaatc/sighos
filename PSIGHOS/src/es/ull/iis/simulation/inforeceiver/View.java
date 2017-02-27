package es.ull.iis.simulation.inforeceiver;

import es.ull.iis.simulation.model.Model;

public abstract class View extends InfoReceiver {
	
	public View(Model model, String description){
		super(model, description);
	}
	
}
