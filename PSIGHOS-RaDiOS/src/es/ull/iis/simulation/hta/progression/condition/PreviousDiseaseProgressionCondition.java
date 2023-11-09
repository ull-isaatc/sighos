/**
 * 
 */
package es.ull.iis.simulation.hta.progression.condition;

import java.util.Collection;
import java.util.TreeSet;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;

/**
 * A condition that meets if the patient includes in his/her state all of the disease progressions specified 
 * @author Iván Castilla Rodríguez
 *
 */
public class PreviousDiseaseProgressionCondition extends Condition<Patient> {
	private final TreeSet<DiseaseProgression> list;

	/**
	 */
	public PreviousDiseaseProgressionCondition(DiseaseProgression srcProgressions) {
		super();
		list = new TreeSet<DiseaseProgression>();
		list.add(srcProgressions);
	}

	/**
	 * @param description
	 */
	public PreviousDiseaseProgressionCondition(Collection<DiseaseProgression> srcProgressions) {
		super();
		list = new TreeSet<DiseaseProgression>();
		list.addAll(srcProgressions);
	}

	@Override
	public boolean check(Patient pat) {
		final TreeSet<DiseaseProgression> state = pat.getState();
		for (DiseaseProgression srcProgression : list)
			if (!state.contains(srcProgression))
				return false;
		return true;
	}

	public TreeSet<DiseaseProgression> getPreviousDiseaseProgressionList() {
		return list;
	}

}
