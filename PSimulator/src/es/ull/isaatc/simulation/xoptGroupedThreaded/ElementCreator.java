/**
 * 
 */
package es.ull.isaatc.simulation.xoptGroupedThreaded;

import java.util.ArrayList;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.xoptGroupedThreaded.flow.InitializerFlow;

/**
 * Defines the way a generator creates elements when it's time to create them.
 * @author Iván Castilla Rodríguez
 */
public class ElementCreator implements BasicElementCreator, es.ull.isaatc.simulation.common.ElementCreator {
	/** Number of objects created each time this creator is invoked. */
	protected final TimeFunction nElem;
	/** Each flow that will be generated */
	protected final ArrayList<GenerationTrio> genTrio;
	/** Associated simulation */
	protected final Simulation simul;

	/**
	 * Creates a creator of elements.
	 * @param sim Simulation this object belongs to.
	 * @param nElem Number of objects created each time this creator is invoked.
	 */
	public ElementCreator(Simulation sim, TimeFunction nElem) {
		genTrio = new ArrayList<GenerationTrio>();
		this.nElem = nElem;
		simul = sim;
	}
	
	/**
	 * Creates a creator of a single type of elements.
	 * @param sim Simulation this object belongs to.
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param flow The description of the flow of the elements to be created.
	 */
	public ElementCreator(Simulation sim, TimeFunction nElem, ElementType et, InitializerFlow flow) {
		this(sim, nElem);
		genTrio.add(new GenerationTrio(et, flow, 1.0));
	}
	
	/**
	 * Adds a [element type, metaflow, proportion] trio.
	 * @param et Element type
	 * @param flow Description of the activity flow that the elements carry out.
	 * @param prop Proportion of elements corresponding to this metaflow.
	 */
	public void add(es.ull.isaatc.simulation.common.ElementType et, es.ull.isaatc.simulation.common.flow.InitializerFlow flow, double prop) {
		genTrio.add(new GenerationTrio((ElementType)et, (InitializerFlow)flow, prop));
	}

	@Override
	public TimeFunction getNElem() {
		return nElem;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.BasicElementCreator#create(es.ull.isaatc.simulation.Generator)
	 */
	public void create(Generator gen) {
		int n = (int)nElem.getPositiveValue(gen.getTs());
		n = beforeCreateElements(n);
        for (int i = 0; i < n; i++) {
            double p = Math.random();
            for (GenerationTrio gt : genTrio) {
            	p -= gt.getProp();
            	if (p <= 0.0){
            		ElementType et = gt.getElementType();
    	    		Element elem = new Element(Generator.incElemCounter(), gen.getSimulation(), et, gt.getFlow());
    	    		elem.initializeElementVars(et.getElementValues());
    	            BasicElement.DiscreteEvent e = elem.getStartEvent(gen.getTs());
    	            elem.addEvent(e);
    	            break;
            	}
            }
        }
        afterCreateElements();
	}
	
	/**
	 * Allows a user to define actions to be executed before the elements are created. This
	 * method is invoked inside <code>create</code> and can be used to change the current 
	 * amount of elements to be generated. By default, returns the predefined value.
	 * @param n Computed number of elements to create.
	 * @return New number of elements to create.
	 */
	public int beforeCreateElements(int n) {
		return n; 
	};
	
	/**
	 * Allows a user to define actions to be executed after the elements are created.
	 * This method is invoked inside <code>create</code>. By default, does nothing.
	 */
	public void afterCreateElements(){};
	
	/**
	 * Gets the simulation this object belongs to. 
	 * @return The simulation this object belongs to.
	 */
	public Simulation getSimul() { 
		return simul; 
	};
	
	/**
	 * Description of a set of elements a generator can create.
	 * @author Iván Castilla Rodríguez
	 */
	public class GenerationTrio {
		/** Type of the created elements. */
		protected final ElementType et;
		/** Description of the activity flow that the elements carry out. */
		protected final InitializerFlow flow;
		/** Proportion of elements corresponding to this flow. */
		protected final double prop;
		
		/**
		 * Creates a new kind of elements to generate.
		 * @param et Element type
		 * @param flow Description of the activity flow that the elements carry out.
		 * @param prop Proportion of elements corresponding to this flow.
		 */
		public GenerationTrio(ElementType et, InitializerFlow flow, double prop) {
			super();
			this.et = et;
			this.flow = flow;
			this.prop = prop;
		}
		
		/**
		 * Returns the element type.
		 * @return Returns the element type.
		 */
		public ElementType getElementType() {
			return et;
		}
		
		/**
		 * Returns the flow.
		 * @return the flow
		 */
		public InitializerFlow getFlow() {
			return flow;
		}

		/**
		 * Returns the proportion of elements to be created of this kind of elements.
		 * @return Returns the proportion.
		 */
		public double getProp() {
			return prop;
		}
	}


}
