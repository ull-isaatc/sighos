package es.ull.iis.simulation.sequential;

import es.ull.iis.function.TimeFunctionParams;
import es.ull.iis.simulation.model.DiscreteEvent;

/**
 * An element which creates elements. This is the base class to create a set of similar
 * elements. A generator must be used with a creator: the generator specifies WHEN to create
 * the elements whereas the creator specifies HOW to create them.
 * @author Ivan Castilla Rodrguez
 */
public abstract class ElementGenerator extends EventSourceEngine implements TimeFunctionParams {
    /** Generator's counter */
    private static int counter = 0;
    /** Specifies the way the elements are created. */
    protected final es.ull.iis.simulation.model.ElementGenerator modelGen;
    
    /**
     * Creates an element generator. 
     * @param simul Simulation object.
     * @param modelGen The way the elements are created.
     */
    public ElementGenerator(SequentialSimulationEngine simul, es.ull.iis.simulation.model.ElementGenerator modelGen) {
        super(counter++, simul, "GEN");
        simul.add(this);
        this.modelGen = modelGen;
    }
  
	@Override
	public DiscreteEvent onDestroy() {
		return new DefaultFinalizeEvent();
	}
	
    /**
     * Returns the next timestamp when elements have to be generated. 
     * @return The next timestamp to generate elements. -1 if this generator
     * don't have to create more elements.
     */
	public abstract long nextEvent();
	
	@Override
	public double getTime() {
		return getTs();
	}
	
    /**
     * This event is invoked every time a new set of elements has to be generated. 
     * It simply invokes the <code>creator.create</code> method.
     */
    public class GenerateEvent extends DiscreteEvent {
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
        @Override
		public void event() {
    		int n = (int)modelGen.getNElem().getValue(ElementGenerator.this);
    		n = modelGen.beforeCreateElements(n);
    		final es.ull.iis.simulation.model.Element[] modelElems = modelGen.createElements(n, ts);
    		for (es.ull.iis.simulation.model.Element modelElem : modelElems) {
    			Element elem = new Element(simul, modelElem);
	    		elem.initializeElementVars(modelElem.getType().getElementValues());
	            final DiscreteEvent e = elem.onCreate(simul.getTs());
	            elem.addEvent(e);
    		}
            modelGen.afterCreateElements();
            final long newTs = nextEvent();
            if (newTs == -1) {
    		 	notifyEnd();
            }
			else {
				final GenerateEvent e = new GenerateEvent(newTs);
				addEvent(e);
			}
        }
    }
}
