/**
 * 
 */
package es.ull.isaatc.simulation.model;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.flow.InitializerFlow;
import es.ull.isaatc.simulation.model.ElementType;

/**
 * Defines the way a generator creates elements when it's time to create them.
 * @author Iván Castilla Rodríguez
 */
public class ElementCreator implements VariableHandler {
	/** Number of objects created each time this creator is invoked. */
	protected final TimeFunction nElem;
	/** Each flow that will be generated */
	protected final ArrayList<GenerationTrio> genTrio;
	/** Associated model */
	protected final Model model;
    protected TreeMap<String, String> userMethods = new TreeMap<String, String>();

	/**
	 * Creates a creator of elements.
	 * @param model Model this object belongs to.
	 * @param nElem Number of objects created each time this creator is invoked.
	 */
	public ElementCreator(Model model, TimeFunction nElem) {
		genTrio = new ArrayList<GenerationTrio>();
		this.nElem = nElem;
		this.model = model;
		userMethods.put("beforeCreateElements", "return n;");
		userMethods.put("afterCreateElements", "");
		userMethods.put("initializeElementVars", "");
	}
	
	/**
	 * Creates a creator of a single type of elements.
	 * @param model Model this object belongs to.
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param flow The description of the flow of the elements to be created.
	 */
	public ElementCreator(Model model, TimeFunction nElem, ElementType et, InitializerFlow flow) {
		this(model, nElem);
		genTrio.add(new GenerationTrio(et, flow, 1.0));
	}
	
	/**
	 * Adds a [element type, metaflow, proportion] trio.
	 * @param et Element type
	 * @param flow Description of the activity flow that the elements carry out.
	 * @param prop Proportion of elements corresponding to this metaflow.
	 */
	public void add(ElementType et, InitializerFlow flow, double prop) {
		genTrio.add(new GenerationTrio(et, flow, prop));
	}

	@Override
	public boolean setMethod(String method, String body) {
		if (userMethods.containsKey(method)) {
			userMethods.put(method, body);
			return true;
		}
		return false;
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
