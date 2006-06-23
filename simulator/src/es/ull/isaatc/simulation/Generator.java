/*
 * GeneradorElementos.java
 *
 * Created on 18 de agosto de 2005, 12:30
 */

package es.ull.isaatc.simulation;

import es.ull.isaatc.random.RandomNumber;
import es.ull.isaatc.util.CycleIterator;

/**
 * Creates elements cyclically. The element generator is controlled by a 
 * CycleIterator. Each cycle iteration the generator creates elements.
 * @author Iván Castilla Rodríguez
 */
public abstract class Generator extends BasicElement {
    /** Created-element counter. This way each element has a different identifier. */
	protected static int elemCounter = 0;
    /** Generator's counter */
    private static int counter = 0;
    /** Cycle that controls the generation of elements. */
    protected CycleIterator cycleIter;
	/** Number of objects created per cycle iteration */
	protected RandomNumber nElem;
    
    /**
     * Creates an element generator. 
     * @param simul Simulation object.
     * @param nElem Number of objects created per cycle iteration.
     * @param cycleIter Control of the generation cycle.
     */
    public Generator(Simulation simul, RandomNumber nElem, CycleIterator cycleIter) {
        super(counter++, simul);
        this.cycleIter = cycleIter;
        this.nElem = nElem;
    }
    
    /**
     * Create the elements. This method is invoked each generation cycle  
     */
    public abstract void createElements();
    
    /**
     * Returns the current element counter.
     * @return Value of property counter.
     */
    public static int getElemCounter() {
        return elemCounter;
    }
    
    /**
	 * @param elemCounter The elemCounter to set.
	 */
	public static void setElemCounter(int elemCounter) {
		Generator.elemCounter = elemCounter;
	}

	public String getObjectTypeIdentifier() {    	
        return "GEN";        
    }
    
    protected void startEvents() {
    	double newTs = cycleIter.next();
    	if (Double.isNaN(newTs))
            notifyEnd();
        else {
        	ts = newTs;
            GenerateEvent e = new GenerateEvent(ts);
            addEvent(e);
        }
    }
    
    public void saveState() {
    
    }
    
    /**
     * This event is invoked each generation cycle. Creates the corresponding 
     * elements and launch a new generation event (if needed) or a finalize 
     * event (if there is no more generation cycles remain).
     */
    class GenerateEvent extends Event {
        /**
         * Creates a new element-generation event.
         * @param ts Timestamp when this event must be executed.
         */
        GenerateEvent(double ts) {
            super(ts, Generator.this.defLP);
        }
        
        /**
         * Generates the elements corresponding to this timestamp. After this, 
         * it checks the following event.
         */
        public void event() {
            double newTs = cycleIter.next();
        	createElements();
            if (Double.isNaN(newTs))
                notifyEnd();
            else {
                GenerateEvent e = new GenerateEvent(newTs);
                addEvent(e);
            }
        }
    }
}
