package es.ull.iis.simulation.sequential;

import java.util.ArrayList;

import es.ull.iis.function.TimeFunctionParams;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.ElementType;

/**
 * An element which creates elements. This is the base class to create a set of similar
 * elements. A generator must be used with a creator: the generator specifies WHEN to create
 * the elements whereas the creator specifies HOW to create them.
 * @author Ivan Castilla Rodrguez
 */
public abstract class ElementGenerator extends BasicElement implements TimeFunctionParams {
    /** Generator's counter */
    private static int counter = 0;
    /** Specifies the way the elements are created. */
    protected final es.ull.iis.simulation.model.ElementGenerator modelGen;
    
    /**
     * Creates an element generator. 
     * @param simul Simulation object.
     * @param modelGen The way the elements are created.
     */
    public ElementGenerator(Simulation simul, es.ull.iis.simulation.model.ElementGenerator modelGen) {
        super(counter++, simul);
        simul.add(this);
        this.modelGen = modelGen;
    }
  
	@Override
	public String getObjectTypeIdentifier() {    	
        return modelGen.getObjectTypeIdentifier();        
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
            for (int i = 0; i < n; i++) {
                double p = Math.random();
                final ArrayList<es.ull.iis.simulation.model.ElementGenerator.GenerationTrio> trios = modelGen.getGenerationTrios();
                for (es.ull.iis.simulation.model.ElementGenerator.GenerationTrio gt : trios) {
                	p -= gt.getProp();
                	if (p <= 0.0){
                		ElementType et = gt.getElementType();
        	    		Element elem = new Element(simul, simul.getElementType(et), gt.getFlow());
        	            final DiscreteEvent e = elem.onCreate(simul.getTs());
        	            elem.addEvent(e);
        	            break;
                	}
                }
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
