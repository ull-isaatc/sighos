/*
 * ObjetoIdentificado.java
 *
 * Created on 23 de agosto de 2004, 11:56
 */
package es.ull.cyc.simulation;

import es.ull.cyc.util.Printable;

/**
 * Representa un objeto que tiene un identificador único.
 * @author Carlos Martín Galán
 */
public abstract class SimulationObject implements Printable, TimeStamp {
    /** Unique object identifier  */
	protected int id;
    /** Simulation where this object is used in */
    protected Simulation simul = null;
    
	/**
     * Constructor que permite asignarle una descripción al objeto
     * @param descripcion Descripción del objeto
     * @param pl Proceso lógico al que se vincula el objeto
     */
	public SimulationObject(int id, Simulation simul) {
		this.id = id;
        this.simul = simul;
	}

    /**
     * Returns the object's identifier
     * @return The identifier of the object
     */
	public int getIdentifier() {
		return(id);
	}
	
	/**
	 * Returns a String that identifies the type of simulation object.
	 * This should be a 3-or-less character description.
	 * @return A short string describing the type of the simulation object.
	 */
	public abstract String getObjectTypeIdentifier();
	
    /**
     * Getter for property simul.
     * @return Value of property simul.
     */
    public es.ull.cyc.simulation.Simulation getSimul() {
        return simul;
    }
    
    public void print(int type, String shortMessage, String longMessage) {
		simul.print(type, this.toString() + "\t" + getTs() + "\t" + shortMessage, 
				this.toString() + "\t" + getTs() + "\t" + longMessage);
	}
	
	public void print(int type, String message) {
		print(type, message, message);
	}
    
    public String toString() {
    	return new String("[" + getObjectTypeIdentifier() + id + "]");
    }
}
