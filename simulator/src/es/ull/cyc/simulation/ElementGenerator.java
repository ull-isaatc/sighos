/**
 * 
 */
package es.ull.cyc.simulation;

import java.util.ArrayList;

import es.ull.cyc.random.RandomNumber;
import es.ull.cyc.util.CycleIterator;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ElementGenerator extends Generator {
	/** Each metaflow that will be generated */
	protected ArrayList<GenerationTrio> genTrio;

	/**
	 * @param simul
	 * @param nElem
	 * @param cycleIter
	 * @param et
	 * @param metaFlow
	 */
	public ElementGenerator(Simulation simul, RandomNumber nElem, CycleIterator cycleIter, ElementType et, MetaFlow metaFlow) {
		super(simul, nElem, cycleIter);
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
	 * @param nElem Number of elements which will be generated.
	 * @param genTrio Initial list of [element type, proportions] pairs.
	 */
	public ElementGenerator(Simulation simul, RandomNumber nElem, CycleIterator cycleIter, ArrayList<GenerationTrio> genPairs) {
		super(simul, nElem, cycleIter);
		this.genTrio = genPairs;
	}

	/**
	 * Adds a [element type, proportion] pair.
	 * @param et Element type
	 * @param prop Proportion of elements corresponding to this metaflow
	 */
	public void add(ElementType et, MetaFlow metaFlow, double prop) {
		genTrio.add(new GenerationTrio(et, metaFlow, prop));
	}
	
	/* (non-Javadoc)
	 * @see es.ull.cyc.simulation.Generator#createElement()
	 */
	public void createElements() {
        int n = (int)nElem.samplePositiveDouble();
		for (GenerationTrio gt : genTrio) {			
	        for (int i = 0 ; i < (Math.round(n * gt.getProp())); i++) {
	    		Element elem = new Element(elemCounter++, simul, gt.getElementType());
	    		elem.setFlow(gt.getMetaFlow().getFlow(null, elem));
	            elem.start(defLP);
	        }
		}
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
