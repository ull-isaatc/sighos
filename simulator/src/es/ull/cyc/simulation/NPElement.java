/*
 * ElementoFicticioNP.java
 *
 * Created on 8 de noviembre de 2004, 18:28
 */

package es.ull.cyc.simulation;

/**
 * Element that carries out the non-presential activities instead of the "real"
 * elemnent.
 * @author Iván Castilla Rodríguez
 */
public class NPElement extends Element {
    /** "Real" element */
    protected Element parent;

    /**
     * Creates a "non-presential" element. 
     * @param parent "Real" element which this element is attached to
     * @param f Single flow that contains the non-presential activity
     */
    public NPElement(Element parent, SingleFlow f) {
    	// FIXME: Quizás debería usar un identificador específico
        super(parent.getIdentifier(), parent.getSimul());
        this.parent = parent;
        setFlow(new SingleFlow(f, this, f.getActivity()));
    }

    /**
     * Establish a new element's timestamp. The new timestamp must be greater or 
     * equal than the previous one. Updates the parent timestamp too.
     * @param ts New value of property ts.
     */
    public void setTs(double ts) {
        super.setTs(ts);
        if (parent.getTs() < ts)
            parent.setTs(ts);
    }
    
    /**
     * Realiza todas las operaciones necesarias para terminar la ejecución del
     * elemento debido al fin de la simulación.
     */
    protected void notifyEndSimulation() {
        super.notifyEndSimulation();
        parent.notifyEndSimulation();
    }
    
    public void saveState() {
    	// The non-presential element don't save its state. This task is already done 
    	// in the corresponding presential element
    }
    
    /**
     * Getter for property parent.
     * @return Value of property parent.
     */
    public es.ull.cyc.simulation.Element getParent() {
        return parent;
    }
    
    public String getObjectTypeIdentifier() {
        return "NPE";
    }
    
    public String toString() {
    	return new String("(" + parent + ")" + super.toString());
    }

}
