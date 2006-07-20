/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.random.RandomNumber;
import es.ull.isaatc.util.CycleIterator;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ElementGenerator extends Generator {
	/** Each metaflow that will be generated */
	protected ArrayList<GenerationTrio> genTrio;
    /** Cycle that controls the generation of elements. */
    protected CycleIterator cycleIter;
	/** Number of objects created per cycle iteration */
	protected RandomNumber nElem;

	/**
	 * @param simul
	 * @param nElem
     * @param cycleIter Control of the generation cycle.
	 * @param et
	 * @param metaFlow
	 */
	public ElementGenerator(Simulation simul, RandomNumber nElem, CycleIterator cycleIter, ElementType et, MetaFlow metaFlow) {
		super(simul);
        this.nElem = nElem;
        this.cycleIter = cycleIter;
		this.genTrio = new ArrayList<GenerationTrio>();
		genTrio.add(new GenerationTrio(et, metaFlow, 1.0));
	}

	/**
	 * @param nElem Number of elements which will be generated.
	 */
	public ElementGenerator(Simulation simul, RandomNumber nElem, CycleIterator cycleIter) {
		this(simul, nElem, cycleIter, new ArrayList<GenerationTrio>());
	}
	
	/**
	 * @param simul
	 * @param nElem Number of elements which will be generated.
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
	 * Adds a [element type, proportion] pair.
	 * @param et Element type
	 * @param prop Proportion of elements corresponding to this metaflow
	 */
	public void add(ElementType et, MetaFlow metaFlow, double prop) {
		genTrio.add(new GenerationTrio(et, metaFlow, prop));
	}

	@Override
	public void createElements() {
        int n = (int)nElem.samplePositiveDouble();
		for (GenerationTrio gt : genTrio) {			
	        for (int i = 0 ; i < (Math.round(n * gt.getProp())); i++) {
	    		Element elem = new Element(elemCounter++, simul, gt.getElementType());
	    		gt.getMetaFlow().getFlow(null, elem);
	            elem.start(defLP);
	        }
		}
	}

	@Override
	public double nextTs() {
		return cycleIter.next();
	}

	public class GenerationTrio {
		protected ElementType et;
		protected MetaFlow metaFlow;
		protected double prop;
		
		/**
		 * @param meta
		 * @param prop
		 */
		public GenerationTrio(ElementType et, MetaFlow metaFlow, double prop) {
			super();
			this.et = et;
			this.metaFlow = metaFlow;
			this.prop = prop;
		}
		
		/**
		 * @return Returns the element type.
		 */
		public ElementType getElementType() {
			return et;
		}
		
		/**
		 * @return the metaFlow
		 */
		public MetaFlow getMetaFlow() {
			return metaFlow;
		}

		/**
		 * @return Returns the prop.
		 */
		public double getProp() {
			return prop;
		}
	}
}
