/**
 * 
 */
package es.ull.iis.simulation.hta.progression.condition;

import java.util.Collection;
import java.util.TreeSet;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;

/**
 * A condition that meets if the patient includes in his/her state all of the disease progressions specified 
 * @author Iván Castilla Rodríguez
 *
 */
public class PreviousDiseaseProgressionCondition extends Condition<DiseaseProgressionPathway.ConditionInformation> {
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
	public boolean check(DiseaseProgressionPathway.ConditionInformation info) {
		final TreeSet<DiseaseProgression> state = info.getPatient().getState();
		for (DiseaseProgression srcProgression : list)
			if (!state.contains(srcProgression))
				return false;
		return true;
	}

	public TreeSet<DiseaseProgression> getPreviousDiseaseProgressionList() {
		return list;
	}

}
