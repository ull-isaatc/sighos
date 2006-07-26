package es.ull.isaatc.simulation;

/**
 * An element which creates elements. This is the base class to create a set of similar
 * elements. The creation of elements is time-dependant, that is, the user defines WHEN and
 * HOW the elements are created by filling the <code>nextTs()</code> and <code>createElements()</code>
 * abstract methods.
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
     */
    public Generator(Simulation simul) {
        super(counter++, simul);
        simul.add(this);
    }
    
    /**
     * Create the elements. This method is invoked each timestamp when elements have to be
     * generated.  
     */
    public abstract void createElements();

    /**
     * Returns the next timestamp when elements have to be generated. 
     * @return The next timestamp to generate elements. NaN if this generator
     * don't have to create more elements.
     */
    public abstract double nextTs();
    
    /**
     * Returns the current element's counter.
     * @return The current element's counter.
     */
    public static int getElemCounter() {
        return elemCounter;
    }
    
    /**
     * Establish a new initial value for the element's counter. 
	 * @param elemCounter A new element's counter value.
	 */
	public static void setElemCounter(int elemCounter) {
		Generator.elemCounter = elemCounter;
	}

	@Override
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
