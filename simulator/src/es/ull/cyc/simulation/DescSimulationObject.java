/*
 * DescSimulationObject.java
 *
 * Created on 17 de noviembre de 2005, 11:38
 */

package es.ull.cyc.simulation;

import es.ull.cyc.util.*;

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

    public void print(Output.MessageType type, String shortMessage, String longMessage) {
		simul.print(type, this.toString() + "\t" + getTs() + "\t" + shortMessage, 
				this.toString() + "\t" + getTs() + "\t" + description + "\t" + longMessage);
	}

}
