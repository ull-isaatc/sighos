/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import es.ull.iis.simulation.condition.AndCondition;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionLanguageCondition;
import es.ull.iis.simulation.hta.osdi.wrappers.RangeWrapper;
import es.ull.iis.simulation.hta.outcomes.Guideline;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface GuidelineBuilder {
	public static Guideline getGuidelineInstance(OSDiGenericModel model, String guidelineName) throws TranspilerException {
		final Guideline guide = new Guideline(model, guidelineName, OSDiDataProperties.HAS_DESCRIPTION.getValue(guidelineName, ""), createCondition(model, guidelineName));
		createGuidelineRanges(model, guide);
		return guide;
	}

	private static Condition<DiseaseProgressionPathway.ConditionInformation> createCondition(OSDiGenericModel model, String guidelineName) {
		final Set<String> strConditions = OSDiObjectProperties.HAS_CONDITION_EXPRESSION.getValues(guidelineName);
		final ArrayList<Condition<DiseaseProgressionPathway.ConditionInformation>> condList = new ArrayList<>();
		for (String strCond : strConditions)
			// FIXME: Conditions are now expressions may be expressed in different languages. See OSDiDataProperties.HAS_CONDITION_LANGUAGE
			condList.add(new ExpressionLanguageCondition(strCond));
		// Checks how many conditions were created
		if (condList.size() == 0)
			return new TrueCondition<DiseaseProgressionPathway.ConditionInformation>();
		if (condList.size() == 1)
			return condList.get(0);
		// If more than one condition were added, merges them with a logical AND
		return new AndCondition<DiseaseProgressionPathway.ConditionInformation>(condList);
	}
	
	private static void createGuidelineRanges(OSDiGenericModel model, Guideline guide) throws TranspilerException {
		final List<String> strRanges = OSDiDataProperties.HAS_RANGE.getValues(guide.name());
		for (String rangeName : strRanges) {
			// TODO: Allow distributions instead of simply deterministic values
			final double dose = Double.parseDouble(OSDiDataProperties.HAS_DOSE.getValue(guide.name(), "0.0")); 
			final double frequency = Double.parseDouble(OSDiDataProperties.HAS_FREQUENCY.getValue(guide.name(), "0.0"));
			final String strValue = OSDiDataProperties.HAS_RANGE.getValue(rangeName, "");
			if (strValue == null) {
				throw new TranspilerException("Range (data property 'has_range' not defined for GuidelineRange instance " + rangeName);
			}
			final RangeWrapper wrapper = new RangeWrapper(strValue);
			switch(wrapper.getType()) {
			case DURATION:
				break;
			case RANGE:
				guide.addRange(wrapper.getRangeLimits()[0], wrapper.getRangeLimits()[1], frequency, dose);
				break;
			case SPECIFIC:
				break;
			case UNITS:
			default:
				break;
			
			}
		}
	}
}
