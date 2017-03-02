package es.ull.iis.simulation.inforeceiver;

import java.util.ArrayList;

import es.ull.iis.simulation.model.Simulation;

public abstract class Listener extends InfoReceiver {

	private final ArrayList<Class<?>> generatedInfos = new ArrayList<Class<?>>();
	
	public Listener (Simulation model, String description) {
		super(model, description);
	}
	public void addGenerated(Class<?> cl) {
		generatedInfos.add(cl);
	}
	
	public ArrayList<Class<?>> getGeneratedInfos() {
		return generatedInfos;
	}
}
