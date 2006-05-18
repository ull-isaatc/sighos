/*
 * FictitiousElement.java
 *
 * Created on 20 de enero de 2005, 12:54
 */

package es.ull.cyc.simulation;

/**
 * Clase de elementos que no hacen nada
 * @author Iv�n Castilla Rodr�guez
 */
public class FictitiousElement extends BasicElement {
    
    /** 
     * Crea un nuevo elemento ficticio con identificador -1 
     */
    public FictitiousElement(Simulation simul) {
        this(-1, simul);
    }
    
    /** 
     * Crea un nuevo elemento ficticio con el identificador indicado. 
     * @param id Identificador del elemento.
     */
    public FictitiousElement(int id, Simulation simul) {
        super(id, simul);
    }
    
    protected void startEvents() {        
    }
    
    /**
     * Representaci�n mediante una cadena de caracteres del Elemento
     * @return Una cadena de caracteres con la forma "[#]".
     */
    public String getObjectTypeIdentifier() {
        return "FE";
    }

	protected void saveState() {
		
	}
}
