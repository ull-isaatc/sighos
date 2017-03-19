package es.ull.iis.simulation.parallel;

/**
 * An element which creates elements. This is the base class to create a set of similar
 * elements. A generator must be used with a creator: the generator specifies WHEN to create
 * the elements whereas the creator specifies HOW to create them.
 * @author Ivan Castilla Rodrguez
 */
public abstract class Generator extends BasicElement {
    /** Generator's counter */
    private static int counter = 0;
    /** Specifies the way the elements are created. */
    protected final BasicElementCreator creator;
    
    /**
     * Creates an element generator. 
     * @param simul ParallelSimulationEngine object.
     * @param creator The way the elements are created.
     */
    public Generator(ParallelSimulationEngine simul, BasicElementCreator creator) {
        super(counter++, simul);
        simul.add(this);
        this.creator = creator;
    }
  
    /**
     * Determines the actions performed by this generator before invoking the
     * corresponding <code>BasicElementCreator.create</code>  
     */
    public abstract void beforeCreate();

	@Override
	public String getObjectTypeIdentifier() {    	
        return "GEN";        
    }

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.BasicElement#end()
	 */
	@Override
	protected void end() {
	}
	
    /**
     * This event is invoked every time a new set of elements has to be generated. 
     * It simply invokes the <code>creator.create</code> method.
     */
    public final class GenerateEvent extends DiscreteEvent {
        /**
         * Creates a new element-generation event.
         * @param ts Timestamp when this event must be executed.
         */
        public GenerateEvent(long ts) {
            super(ts);
        }
        
        /**
         * Generates the elements corresponding to this timestamp. After this, 
         * it checks the following event.
         */
        public void event() {
        	beforeCreate();
        	creator.create(Generator.this);
        }
    }
}
