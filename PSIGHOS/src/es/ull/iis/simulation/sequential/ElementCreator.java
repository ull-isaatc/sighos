/**
 * 
 */
package es.ull.iis.simulation.sequential;

import java.util.ArrayList;

import es.ull.iis.simulation.core.flow.InitializerFlow;
import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;

/**
 * Defines the way a generator creates elements when it's time to create them.
 * @author Iván Castilla Rodríguez
 */
public class ElementCreator implements BasicElementCreator, es.ull.iis.simulation.core.ElementCreator<WorkThread> {
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
	public ElementCreator(Simulation sim, TimeFunction nElem, ElementType et, InitializerFlow<WorkThread> flow) {
		this(sim, nElem);
		genTrio.add(new GenerationTrio(et, flow, 1.0));
	}
	
	/**
	 * Creates a creator of elements.
	 * @param sim Simulation this object belongs to.
	 * @param nElem Number of objects created each time this creator is invoked.
	 */
	public ElementCreator(Simulation sim, int nElem) {
		this(sim, TimeFunctionFactory.getInstance("ConstantVariate", nElem));
	}
	
	/**
	 * Creates a creator of a single type of elements.
	 * @param sim Simulation this object belongs to.
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param flow The description of the flow of the elements to be created.
	 */
	public ElementCreator(Simulation sim, int nElem, ElementType et, InitializerFlow<WorkThread> flow) {
		this(sim, TimeFunctionFactory.getInstance("ConstantVariate", nElem), et, flow);
	}
	
	/**
	 * Adds a [element type, metaflow, proportion] trio.
	 * @param et Element type
	 * @param flow Description of the activity flow that the elements carry out.
	 * @param prop Proportion of elements corresponding to this metaflow.
	 */
	public void add(es.ull.iis.simulation.core.ElementType et, InitializerFlow<WorkThread> flow, double prop) {
		genTrio.add(new GenerationTrio((ElementType)et, flow, prop));
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.BasicElementCreator#create(es.ull.iis.simulation.Generator)
	 */
	public void create(Generator gen) {
		int n = (int)nElem.getValue(gen);
		n = beforeCreateElements(n);
        for (int i = 0; i < n; i++) {
            double p = Math.random();
            for (GenerationTrio gt : genTrio) {
            	p -= gt.getProp();
            	if (p <= 0.0){
            		ElementType et = gt.getElementType();
    	    		Element elem = new Element(gen.getSimulation(), et, gt.getFlow());
    	    		elem.initializeElementVars(et.getElementValues());
    	            final BasicElement.DiscreteEvent e = elem.getStartEvent(gen.getTs());
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
		protected final InitializerFlow<WorkThread> flow;
		/** Proportion of elements corresponding to this flow. */
		protected final double prop;
		
		/**
		 * Creates a new kind of elements to generate.
		 * @param et Element type
		 * @param flow Description of the activity flow that the elements carry out.
		 * @param prop Proportion of elements corresponding to this flow.
		 */
		public GenerationTrio(ElementType et, InitializerFlow<WorkThread> flow, double prop) {
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
		public InitializerFlow<WorkThread> getFlow() {
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

	@Override
	public TimeFunction getNElem() {
		return nElem;
	}

}
