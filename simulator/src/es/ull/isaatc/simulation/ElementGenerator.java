/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.random.RandomNumber;
import es.ull.isaatc.util.CycleIterator;

/**
 * Creates a certain number of elements cyclically. A <code>CycleIterator</code> indicates the
 * cycle that the generator has to follow. 
 * @author Iván Castilla Rodríguez
 */
public class ElementGenerator extends Generator {
	/** Each metaflow that will be generated */
	protected ArrayList<GenerationTrio> genTrio;
    /** Cycle that controls the generation of elements. */
    protected CycleIterator cycleIter;
	/** Number of objects created per cycle iteration */
	protected RandomNumber nElem;

	/**
	 * Creates an element generator which generates certain number of elements following a cycle. 
	 * @param simul Simulation where this generator is being used
     * @param nElem Number of objects created per cycle iteration.
     * @param cycleIter Control of the generation cycle.
	 */
	public ElementGenerator(Simulation simul, RandomNumber nElem, CycleIterator cycleIter) {
		this(simul, nElem, cycleIter, new ArrayList<GenerationTrio>());
	}
	
	/**
	 * Creates an element generator which generates certain number of elements of a specified type 
	 * following a cycle. The activity flow of each element is build by using the specified metaflow.
	 * @param simul Simulation where this generator is being used.
     * @param nElem Number of objects created per cycle iteration.
     * @param cycleIter Control of the generation cycle.
	 * @param et Type of the created elements.
	 * @param metaFlow Description of the activity flow that the elements carry out.
	 */
	public ElementGenerator(Simulation simul, RandomNumber nElem, CycleIterator cycleIter, ElementType et, MetaFlow metaFlow) {
		super(simul);
        this.nElem = nElem;
        this.cycleIter = cycleIter;
		this.genTrio = new ArrayList<GenerationTrio>();
		genTrio.add(new GenerationTrio(et, metaFlow, 1.0));
	}

	/**
	 * Creates an element generator which generates certain number of elements of a specified type 
	 * following a cycle. This generator is capable of create elements of different types and which
	 * use different metaflows. The amount of elements of each type is computed by using the proportion
	 * as indicated in the generation "trios".
	 * @param simul Simulation where this generator is being used
     * @param nElem Number of objects created per cycle iteration.
     * @param cycleIter Control of the generation cycle.
	 * @param genTrio Initial list of [element type, metaflow, proportions] trios.
	 */
	public ElementGenerator(Simulation simul, RandomNumber nElem, CycleIterator cycleIter, ArrayList<GenerationTrio> genTrio) {
		super(simul);
        this.nElem = nElem;
        this.cycleIter = cycleIter;
		this.genTrio = genTrio;
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

	/**
	 * Creates the elements. This method takes a sample of the distribution which characterizes
	 * the amount of elements to generate, and creates as many elements as this sample shows. The total
	 * of patients is divided into the different generation "trios" that have been defined.
	 */
	@Override
	public void createElements() {
        int n = (int)nElem.samplePositiveDouble();
        for (int i = 0; i < n; i++) {
            double p = Math.random();
            for (GenerationTrio gt : genTrio) {
            	p -= gt.getProp();
            	if (p <= 0.0) {
    	    		Element elem = new Element(elemCounter++, simul, gt.getElementType());
    	    		gt.getMetaFlow().getFlow(null, elem);
    	            elem.start(defLP);
    	            break;
            	}
            }
        }
	}

	/**
	 * Invokes the cycle iterator.
	 * @return The new timestamp to create elements. <code>Double.NaN</code> if the limit time 
	 * has been reached. 
	 */
	@Override
	public double nextTs() {
		return cycleIter.next();
	}

	/**
	 * Description of a set of elements this generator can create.
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
