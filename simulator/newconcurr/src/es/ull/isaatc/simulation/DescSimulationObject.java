/*
 * DescSimulationObject.java
 *
 * Created on 17 de noviembre de 2005, 11:38
 */

package es.ull.isaatc.simulation;

/**
 * An object of the simulation that has a description
 * @author Iván Castilla Rodríguez
 */
public abstract class DescSimulationObject extends SimulationObject {
    protected String description;
    
    /** Creates a new instance of DescSimulationObject */
    public DescSimulationObject(int id, Simulation simul, String description) {
        super(id, simul);
        this.description = description;        
        simul.add(this);
    }
    
    public String getDescription() {
        return description;
    }

    public void print(String message) {
		simul.print(this.toString() + "\t" + getTs() + "\t" + description + "\t" + message);
	}

}
