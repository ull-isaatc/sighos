package es.ull.iis.simulation.inforeceiver;

import java.util.ArrayList;

import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.model.Simulation;

public abstract class InfoReceiver {

	private final ArrayList<Class<?>> entrance = new ArrayList<Class<?>>();
	private final String description;
	
	public InfoReceiver (String description) {
		this.description = description;
	}
	
	public abstract void infoEmited(SimulationInfo info);
	
	public void addEntrance(Class<?> cl) {
		entrance.add(cl);
	}
	
	public String toString() {
		return description;
	}

	public ArrayList<Class<?>> getEntrance() {
		return entrance;
	}

    public boolean isDebugMode() {
    	return (Simulation.isDebugEnabled());
	}
    
    public void debug (String message) {
    	System.out.println(description + " DEBUG MESSAGE:\n" + message + description
    			+ " END DEBUG MESSAGE.");
    }
}
