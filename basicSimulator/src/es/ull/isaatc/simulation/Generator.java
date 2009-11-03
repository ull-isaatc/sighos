package es.ull.isaatc.simulation;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * An element which creates elements. This is the base class to create a set of similar
 * elements. The creation of elements is time-dependant, that is, the user defines WHEN and
 * HOW the elements are created by filling the <code>nextTs()</code> and <code>createElements()</code>
 * abstract methods.
 * @author Ivn Castilla Rodrguez
 */
public abstract class Generator extends BasicElement {
    /** Created-element counter. This way each element has a different identifier. */
	protected static AtomicInteger elemCounter = new AtomicInteger(0);
    /** Generator's counter */
    private static int counter = 0;
    /** Specifies the way the elements are created. */
    protected BasicElementCreator creator;
    
    /**
     * Creates an element generator. 
     * @param simul Simulation object.
     * @param creator The way the elements are created.
     */
    public Generator(Simulation simul, BasicElementCreator creator) {
        super(counter++, simul);
        simul.add(this);
        this.creator = creator;
    }
    
    /**
     * Things to do before the elements are created. 
     */
    public abstract void preprocess();
    /**
     * Things to do after the elements are created. 
     */
    public abstract void postprocess();

    /**
     * Returns the current element's counter.
     * @return The current element's counter.
     */
    public static int getElemCounter() {
        return elemCounter.get();
    }
    
    /**
     * Establish a new initial value for the element's counter. 
	 * @param elemCounter A new element's counter value.
	 */
	public static void setElemCounter(int elemCounter) {
		Generator.elemCounter.set(elemCounter);
	}

	/**
	 * Returns and increases the element's counter in one step.
	 * @return The current element's counter.
	 */
	public static int incElemCounter() {
		return elemCounter.getAndIncrement();
	}

	@Override
	public String getObjectTypeIdentifier() {    	
        return "GEN";        
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
        	preprocess();
        	creator.create(Generator.this);
        	postprocess();
        }
    }
}