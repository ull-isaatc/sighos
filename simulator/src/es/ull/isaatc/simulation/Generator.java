/*
 * GeneradorElementos.java
 *
 * Created on 18 de agosto de 2005, 12:30
 */

package es.ull.isaatc.simulation;

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
    
    /**
     * Creates an element generator. 
     * @param simul Simulation object.
     * @param nElem Number of objects created per cycle iteration.
     */
    public Generator(Simulation simul) {
        super(counter++, simul);
        simul.add(this);
    }
    
    /**
     * Create the elements. This method is invoked each generation cycle  
     */
    public abstract void createElements();

    /**
     * Returns the next timestamp when elements have to be generated. 
     * @return The next timestamp to generate elements. NaN if this generator
     * don't have to create more elements.
     */
    public abstract double nextTs();
    
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
    
	@Override
    protected void init() {
    	double newTs = nextTs();
    	if (Double.isNaN(newTs))
            notifyEnd();
        else {
        	ts = newTs;
            GenerateEvent e = new GenerateEvent(ts);
            addEvent(e);
        }
    }
    
	@Override
	protected void end() {
	}
	
    public void saveState() {
    
    }
    
    /**
     * This event is invoked each generation cycle. Creates the corresponding 
     * elements and launch a new generation event (if needed) or a finalize 
     * event (if there is no more generation cycles remain).
     */
    class GenerateEvent extends DiscreteEvent {
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
        	createElements();
            double newTs = nextTs();
            if (Double.isNaN(newTs))
                notifyEnd();
            else {
                GenerateEvent e = new GenerateEvent(newTs);
                addEvent(e);
            }
        }
    }
}
