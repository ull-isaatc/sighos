/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.ArrayList;

import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;

/**
 * Defines the way a generator creates elements when it's time to create them.
 * @author Iván Castilla Rodríguez
 */
public class ElementGenerator extends ModelObject implements EventSource {
	/** Number of objects created each time this creator is invoked. */
	protected final TimeFunction nElem;
	/** Each flow that will be generated */
	protected final ArrayList<GenerationTrio> genTrio;

	/**
	 * Creates a creator of elements.
	 * @param nElem Number of objects created each time this creator is invoked.
	 */
	public ElementGenerator(Model model, TimeFunction nElem) {
		super(model, "GEN");
		genTrio = new ArrayList<GenerationTrio>();
		this.nElem = nElem;
		model.add(this);
	}
	
	/**
	 * Creates a creator of a single type of elements.
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param flow The description of the flow of the elements to be created.
	 */
	public ElementGenerator(Model model, TimeFunction nElem, ElementType et, InitializerFlow flow) {
		this(model, nElem);
		genTrio.add(new GenerationTrio(et, flow, 1.0));
	}
	
	/**
	 * Creates a creator of elements.
	 * @param sim Simulation this object belongs to.
	 * @param nElem Number of objects created each time this creator is invoked.
	 */
	public ElementGenerator(Model model, int nElem) {
		this(model, TimeFunctionFactory.getInstance("ConstantVariate", nElem));
	}
	
	/**
	 * Creates a creator of a single type of elements.
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param flow The description of the flow of the elements to be created.
	 */
	public ElementGenerator(Model model, int nElem, ElementType et, InitializerFlow flow) {
		this(model, TimeFunctionFactory.getInstance("ConstantVariate", nElem), et, flow);
	}
	
	/**
	 * Adds a [element type, metaflow, proportion] trio.
	 * @param et Element type
	 * @param flow Description of the activity flow that the elements carry out.
	 * @param prop Proportion of elements corresponding to this metaflow.
	 */
	public void add(ElementType et, InitializerFlow flow, double prop) {
		genTrio.add(new GenerationTrio((ElementType)et, flow, prop));
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
	}
	
	/**
	 * Allows a user to define actions to be executed after the elements are created.
	 * This method is invoked inside <code>create</code>. By default, does nothing.
	 */
	public void afterCreateElements() {}

	public TimeFunction getNElem() {
		return nElem;
	}

	/**
	 * @return the genTrio
	 */
	public ArrayList<GenerationTrio> getGenerationTrios() {
		return genTrio;
	}

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
		protected GenerationTrio(ElementType et, InitializerFlow flow, double prop) {
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
