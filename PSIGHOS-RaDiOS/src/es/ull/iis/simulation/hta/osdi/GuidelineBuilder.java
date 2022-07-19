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
import es.ull.iis.simulation.hta.osdi.wrappers.RangeWrapper;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Guideline;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface GuidelineBuilder {
	public static Guideline getGuidelineInstance(SecondOrderParamsRepository secParams, String guidelineName) throws TranspilerException {
		final Guideline guide = new Guideline(guidelineName, OSDiNames.DataProperty.HAS_DESCRIPTION.getValue(guidelineName, ""), createCondition(secParams, guidelineName));
		createGuidelineRanges(secParams, guide);
		return guide;
	}

	private static Condition<Patient> createCondition(SecondOrderParamsRepository secParams, String guidelineName) {
		final List<String> strConditions = OSDiNames.DataProperty.HAS_CONDITION.getValues(guidelineName);
		final ArrayList<Condition<Patient>> condList = new ArrayList<>();
		for (String strCond : strConditions)
			condList.add(new ExpressionLanguageCondition(strCond));
		// Checks how many conditions were created
		if (condList.size() == 0)
			return new TrueCondition<Patient>();
		if (condList.size() == 1)
			return condList.get(0);
		// If more than one condition were added, merges them with a logical AND
		return new AndCondition<Patient>(condList);
	}
	
	private static void createGuidelineRanges(SecondOrderParamsRepository secParams, Guideline guide) throws TranspilerException {
		final List<String> strRanges = OSDiNames.ObjectProperty.HAS_GUIDELINE_RANGE.getValues(guide.name());
		for (String rangeName : strRanges) {
			// TODO: Allow distributions instead of simply deterministic values
			final double dose = Double.parseDouble(OSDiNames.DataProperty.HAS_DOSE.getValue(rangeName, "0.0")); 
			final double frequency = Double.parseDouble(OSDiNames.DataProperty.HAS_FREQUENCY.getValue(rangeName, "0.0"));
			final String strValue = OSDiNames.DataProperty.HAS_RANGE.getValue(rangeName);
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
