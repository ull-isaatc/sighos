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
	protected ArrayList<GenerationPair> genPairs;

	/**
	 * @param simul
	 * @param nElem
	 * @param cycleIter
	 * @param meta
	 */
	public ElementGenerator(Simulation simul, RandomNumber nElem, CycleIterator cycleIter, MetaFlow meta) {
		super(simul, nElem, cycleIter);
		this.genPairs = new ArrayList<GenerationPair>();
		genPairs.add(new GenerationPair(meta, 1.0));
	}

	/**
	 * @param nElem Number of elements which will be generated.
	 */
	public ElementGenerator(Simulation simul, RandomNumber nElem, CycleIterator cycleIter) {
		this(simul, nElem, cycleIter, new ArrayList<GenerationPair>());
	}
	
	/**
	 * @param nElem Number of elements which will be generated.
	 * @param genPairs Initial list of [metaflows, proportions] pairs.
	 */
	public ElementGenerator(Simulation simul, RandomNumber nElem, CycleIterator cycleIter, ArrayList<GenerationPair> genPairs) {
		super(simul, nElem, cycleIter);
		this.genPairs = genPairs;
	}

	/**
	 * Adds a [metaflow, proportion] pair.
	 * @param meta Metaflow
	 * @param prop Proportion of elements corresponding to this metaflow
	 */
	public void add(MetaFlow meta, double prop) {
		genPairs.add(new GenerationPair(meta, prop));
	}
	
	/* (non-Javadoc)
	 * @see es.ull.cyc.simulation.Generator#createElement()
	 */
	public void createElements() {
        int n = (int)nElem.samplePositiveDouble();
		for (GenerationPair gp : genPairs) {			
	        for (int i = 0 ; i < (Math.round(n * gp.getProp())); i++) {
	    		Element elem = new Element(elemCounter++, simul);
	    		elem.setFlow(gp.getMeta().getFlow(null, elem));
	            elem.start(defLP);
	        }
		}
	}

	public class GenerationPair {
		protected MetaFlow meta;
		protected double prop;
		
		/**
		 * @param meta
		 * @param prop
		 */
		public GenerationPair(MetaFlow meta, double prop) {
			super();
			this.meta = meta;
			this.prop = prop;
		}
		
		/**
		 * @return Returns the meta.
		 */
		public MetaFlow getMeta() {
			return meta;
		}
		/**
		 * @return Returns the prop.
		 */
		public double getProp() {
			return prop;
		}
	}
}
