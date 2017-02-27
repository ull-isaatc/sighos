package es.ull.iis.simulation.inforeceiver;

import java.util.ArrayList;

import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.model.Model;

public abstract class InfoReceiver {

	private final ArrayList<Class<?>> entrance = new ArrayList<Class<?>>();
	protected final Model model;
	private final String description;
	
	public InfoReceiver (Model model, String description) {
		this.model = model;
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

	public Model getModel() {
		return model;
	}

    public boolean isDebugMode() {
    	return (Model.isDebugEnabled());
	}
    
    public void debug (String message) {
    	System.out.println(description + " DEBUG MESSAGE:\n" + message + description
    			+ " END DEBUG MESSAGE.");
    }
}
