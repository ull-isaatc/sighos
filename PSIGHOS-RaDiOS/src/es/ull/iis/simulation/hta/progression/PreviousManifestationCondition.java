/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import java.util.Collection;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.Patient;

/**
 * A condition that meets if the patient is suffering all of the manifestations specified 
 * @author Iván Castilla Rodríguez
 *
 */
public class PreviousManifestationCondition extends PathwayCondition {
	private final TreeSet<Manifestation> list;

	/**
	 */
	public PreviousManifestationCondition(Manifestation srcManifestation) {
		super();
		list = new TreeSet<Manifestation>();
		list.add(srcManifestation);
	}

	/**
	 * @param description
	 */
	public PreviousManifestationCondition(Collection<Manifestation> srcManifestations) {
		super();
		list = new TreeSet<Manifestation>();
		list.addAll(srcManifestations);
	}

	@Override
	public boolean check(Patient pat) {
		final TreeSet<Manifestation> state = pat.getState();
		for (Manifestation srcManif : list)
			if (!state.contains(srcManif))
				return false;
		return true;
	}

}
