package es.ull.iis.simulation.inforeceiver;

import java.util.ArrayList;

import es.ull.iis.simulation.model.SimulationEngine;

public abstract class Listener extends InfoReceiver {

	private final ArrayList<Class<?>> generatedInfos = new ArrayList<Class<?>>();
	
	public Listener (SimulationEngine simul, String description) {
		super(simul, description);
	}
	public void addGenerated(Class<?> cl) {
		generatedInfos.add(cl);
	}
	
	public ArrayList<Class<?>> getGeneratedInfos() {
		return generatedInfos;
	}
}
