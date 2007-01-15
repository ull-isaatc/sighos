/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.util.NumberGenerator;

/**
 * Defines the way a generator creates elements when it's time to create them.
 * @author Iván Castilla Rodríguez
 */
public class ElementCreator implements BasicElementCreator {
	/** Number of objects created each time this creator is invoked. */
	protected NumberGenerator nElem;
	/** Each metaflow that will be generated */
	protected ArrayList<GenerationTrio> genTrio;

	/**
	 * Creates a creator of elements.
	 * @param nElem Number of objects created each time this creator is invoked.
	 */
	public ElementCreator(NumberGenerator nElem) {
		genTrio = new ArrayList<GenerationTrio>();
		this.nElem = nElem;
	}
	
	/**
	 * Creates a creator of a single type of elements.
	 * @param nElem Number of objects created each time this creator is invoked.
	 * @param et The type of the elements to be created
	 * @param metaFlow The description of the flow of the elements to be created.
	 */
	public ElementCreator(NumberGenerator nElem, ElementType et, MetaFlow metaFlow) {
		this(nElem);
		genTrio.add(new GenerationTrio(et, metaFlow, 1.0));
	}
	
	/**
	 * Adds a [element type, metaflow, proportion] trio.
	 * @param et Element type
	 * @param metaFlow Description of the activity flow that the elements carry out.
	 * @param prop Proportion of elements corresponding to this metaflow.
	 */
	public void add(ElementType et, MetaFlow metaFlow, double prop) {
		genTrio.add(new GenerationTrio(et, metaFlow, prop));
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.BasicElementCreator#create(es.ull.isaatc.simulation.Generator)
	 */
	public void create(Generator gen) {
		int n = (int)nElem.getPositiveNumber(gen.getTs());
        for (int i = 0; i < n; i++) {
            double p = Math.random();
            for (GenerationTrio gt : genTrio) {
            	p -= gt.getProp();
            	if (p <= 0.0) {
    	    		Element elem = new Element(Generator.incElemCounter(), gen.getSimul(), gt.getElementType());
    	    		gt.getMetaFlow().getFlow(null, elem);
    	            elem.start(gen.getDefLP());
    	            break;
            	}
            }
        }
	}

	/**
	 * Description of a set of elements a generator can create.
	 * @author Iván Castilla Rodríguez
	 */
	public class GenerationTrio {
		/** Type of the created elements. */
		protected ElementType et;
		/** Description of the activity flow that the elements carry out. */
		protected MetaFlow metaFlow;
		/** Proportion of elements corresponding to this metaflow. */
		protected double prop;
		
		/**
		 * Creates a new kind of elements to generate.
		 * @param et Element type
		 * @param metaFlow Description of the activity flow that the elements carry out.
		 * @param prop Proportion of elements corresponding to this metaflow.
		 */
		public GenerationTrio(ElementType et, MetaFlow metaFlow, double prop) {
			super();
			this.et = et;
			this.metaFlow = metaFlow;
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
		 * Returns the metaflow.
		 * @return the metaFlow
		 */
		public MetaFlow getMetaFlow() {
			return metaFlow;
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
