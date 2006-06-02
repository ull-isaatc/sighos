/**
 * 
 */
package es.ull.cyc.simulation;

import es.ull.cyc.random.RandomNumber;
import es.ull.cyc.util.CycleIterator;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ElementGenerator extends Generator {
	/** */
	protected MetaFlow meta;
	/** */
	protected RandomNumber nElem;

	/**
	 * @param simul
	 * @param nElem
	 * @param cycle
	 * @param meta
	 */
	public ElementGenerator(Simulation simul, RandomNumber nElem, CycleIterator cycleIter, MetaFlow meta) {
		super(simul, cycleIter);
		this.meta = meta;
		this.nElem = nElem;
	}

	/* (non-Javadoc)
	 * @see es.ull.cyc.simulation.Generator#createElement()
	 */
	public void createElements() {
        int n = (int)nElem.samplePositiveDouble();
        for (int i = 0 ; i < n; i++) {
    		Element elem = new Element(elemCounter++, simul);
    		elem.setFlow(meta.getFlow(null, elem));
            elem.start(defLP);
        }            
	}

}
