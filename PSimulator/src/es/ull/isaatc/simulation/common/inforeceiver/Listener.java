package es.ull.isaatc.simulation.common.inforeceiver;

import java.util.ArrayList;

import es.ull.isaatc.simulation.common.Simulation;

public abstract class Listener extends InfoReceiver {

	private final ArrayList<Class<?>> generatedInfos = new ArrayList<Class<?>>();
	
	public Listener (Simulation simul, String description) {
		super(simul, description);
	}
	public void addGenerated(Class<?> cl) {
		generatedInfos.add(cl);
	}
	
	public ArrayList<Class<?>> getGeneratedInfos() {
		return generatedInfos;
	}
}