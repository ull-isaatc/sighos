package es.ull.iis.simulation.sequential;

import java.util.ArrayList;

import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.ElementCreator;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.sequential.ElementGenerator.GenerateEvent;

/**
 * An element which creates elements. This is the base class to create a set of similar
 * elements. A generator must be used with a creator: the generator specifies WHEN to create
 * the elements whereas the creator specifies HOW to create them.
 * @author Ivan Castilla Rodrguez
 */
public abstract class ElementGenerator extends BasicElement {
    /** Generator's counter */
    private static int counter = 0;
    /** Specifies the way the elements are created. */
    protected final ElementCreator creator;
    
    /**
     * Creates an element generator. 
     * @param simul Simulation object.
     * @param creator The way the elements are created.
     */
    public ElementGenerator(Simulation simul, ElementCreator creator) {
        super(counter++, simul);
        simul.add(this);
        this.creator = creator;
    }
  
	@Override
	public String getObjectTypeIdentifier() {    	
        return creator.getObjectTypeIdentifier();        
    }

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.BasicElement#end()
	 */
	@Override
	protected void end() {
	}
	
    /**
     * Returns the next timestamp when elements have to be generated. 
     * @return The next timestamp to generate elements. -1 if this generator
     * don't have to create more elements.
     */
	public abstract long nextEvent();
	
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
    		int n = (int)creator.getNElem().getValue(ElementGenerator.this);
    		n = creator.beforeCreateElements(n);
            for (int i = 0; i < n; i++) {
                double p = Math.random();
                final ArrayList<ElementCreator.GenerationTrio> trios = creator.getGenerationTrios();
                for (ElementCreator.GenerationTrio gt : trios) {
                	p -= gt.getProp();
                	if (p <= 0.0){
                		ElementType et = gt.getElementType();
        	    		Element elem = new Element(simul, et, gt.getFlow());
        	            final DiscreteEvent e = elem.onCreate(simul.getTs());
        	            elem.addEvent(e);
        	            break;
                	}
                }
            }
            creator.afterCreateElements();
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
