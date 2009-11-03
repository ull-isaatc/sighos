package es.ull.isaatc.simulation.common.inforeceiver;

import java.util.ArrayList;

import es.ull.isaatc.simulation.common.Model;
import es.ull.isaatc.simulation.common.info.SimulationInfo;

public abstract class InfoReceiver {

	private final ArrayList<Class<?>> entrance = new ArrayList<Class<?>>();
	private Model simul = null;
	private String description = null;
	
	public InfoReceiver (Model simul, String description) {
		this.simul = simul;
		this.description = description;
	}
	
	public abstract void infoEmited(SimulationInfo info);
	
	public void addEntrance(Class<?> cl) {
		entrance.add(cl);
	}
	
	public String toString() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<Class<?>> getEntrance() {
		return entrance;
	}

	public Model getSimul() {
		return simul;
	}

	public void setSimul(Model simul) {
		this.simul = simul;
	}

    public boolean isDebugMode() {
    	return (simul.isDebugEnabled());
	}
    
    public void debug (String message) {
    	System.out.println(description + " DEBUG MESSAGE:\n" + message + description
    			+ " END DEBUG MESSAGE.");
    }
}
