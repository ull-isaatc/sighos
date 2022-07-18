/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.ArrayList;
import java.util.List;

import es.ull.iis.simulation.condition.AndCondition;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionLanguageCondition;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Guideline;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface GuidelineBuilder {
	public static Guideline getGuidelineInstance(SecondOrderParamsRepository secParams, String guidelineName) throws TranspilerException {
		final Guideline guide = new Guideline(guidelineName, OSDiNames.DataProperty.HAS_DESCRIPTION.getValue(guidelineName, ""));
		return guide;
	}

	private static Condition<Patient> createCondition(SecondOrderParamsRepository secParams, Guideline guide) {
		final List<String> strConditions = OSDiNames.DataProperty.HAS_CONDITION.getValues(guide.name());
		final ArrayList<Condition<Patient>> condList = new ArrayList<>();
		for (String strCond : strConditions)
			condList.add(new ExpressionLanguageCondition(strCond));
		// After going through for previous manifestations and other conditions, checks how many conditions were created
		if (condList.size() == 0)
			return new TrueCondition<Patient>();
		if (condList.size() == 1)
			return condList.get(0);
		return new AndCondition<Patient>(condList);
	}
}
