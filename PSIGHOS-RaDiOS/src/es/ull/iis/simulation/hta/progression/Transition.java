/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * Defines a transition from a manifestation to another manifestation
 * @author Iván Castilla
 *
 */
public class Transition implements Comparable<Transition> {
	private final Manifestation srcManifestation;
	private final Manifestation destManifestation;
	
	/**
	 * 
	 */
	public Transition(Manifestation srcManifestation, Manifestation destManifestation) {
		this.srcManifestation = srcManifestation;
		this.destManifestation = destManifestation;
	}

	/**
	 * 
	 */
	public Transition(Manifestation destManifestation) {
		this(null, destManifestation);
	}

	/**
	 * @return the srcManifestation
	 */
	public Manifestation getSrcManifestation() {
		return srcManifestation;
	}

	/**
	 * @return the destManifestation
	 */
	public Manifestation getDestManifestation() {
		return destManifestation;
	}

	@Override
	public int compareTo(Transition o) {
		
		final int comp = srcManifestation.compareTo(o.srcManifestation);
		return (comp != 0) ? comp : destManifestation.compareTo(o.destManifestation);
	}
}
