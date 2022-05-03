package es.ull.iis.simulation.hta.osdi;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

public class SecondOrderParamsBuilder {

	/**
	 * Creates a cost associated to a specific instance by extracting the information from the ontology. Only one cost should exist for such instance.
	 * @param secParams Repository
	 * @param instance The instance (manifestation, disease...)
	 * @throws TranspilerException When there was a problem parsing the ontology
	 */
	public static void createCostParam(SecondOrderParamsRepository secParams, Named instance) throws TranspilerException {
		List<String> costs = OwlHelper.getChildsByClassName(instance.name(), OSDiNames.Class.COST.getName());
		if (costs.size() > 1)
			throw new TranspilerException("Only one cost should be associated to instance \"" + instance.name() + "\". Instead, " + costs.size() + " found");
		String costName = costs.get(0);
		// Assumes current year if not specified
		final int year = Integer.parseInt(OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_YEAR.getName(), "" + (new GregorianCalendar()).get(GregorianCalendar.YEAR)));
		// Assumes cost to be 0 if not defined
		final String strValue = OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_VALUE.getName(), "0.0");
		// Assumes annual behavior if not specified
		final String strTempBehavior = OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getName(), OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL_VALUE.getName());
		final ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strValue);
		if (probDistribution == null)
			throw new TranspilerException("Error parsing regular expression \"" + strValue + "\" for instance \"" + instance.name() + "\"");
		// If defined to have an annual behavior
		if (OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL_VALUE.getName().equals(strTempBehavior)) {
			secParams.addCostParam(instance, 
					OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_DESCRIPTION.getName(), ""),  
					OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_SOURCE.getName(), ""), 
					year, probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
		}
		else {
			secParams.addTransitionCostParam(instance, 
					OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_DESCRIPTION.getName(), ""),  
					OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_SOURCE.getName(), ""), 
					year, probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
		}
	}
	
	/**
	 * Creates a utility associated to a specific instance by extracting the information from the ontology. Only one utility should exist for such instance.
	 * @param secParams Repository
	 * @param instance The instance (manifestation, disease...)
	 * @throws TranspilerException When there was a problem parsing the ontology
	 */
	public static void createUtilityParam(SecondOrderParamsRepository secParams, Named instance) throws TranspilerException {
		List<String> utilities = OwlHelper.getChildsByClassName(instance.name(), OSDiNames.Class.UTILITY.getName());
		if (utilities.size() > 1)
			throw new TranspilerException("Only one utility should be associated to instance \"" + instance.name() + "\". Instead, " + utilities.size() + " found");
		String utilityName = utilities.get(0);
		// Assumes current year if not specified
		final int year = Integer.parseInt(OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_YEAR.getName(), "" + (new GregorianCalendar()).get(GregorianCalendar.YEAR)));
		// Assumes cost to be 0 if not defined
		final String strValue = OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_VALUE.getName(), "0.0");
		// Assumes annual behavior if not specified
		final String strTempBehavior = OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getName(), OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL_VALUE.getName());
		// Assumes that it is a utility (not a disutility) if not specified
		final String strType = OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_UTILITY_KIND.getName(), OSDiNames.DataPropertyRange.KIND_UTILITY_UTILITY.getName());
		// Assumes a default calculation method specified in Constants if not specified
		final String strCalcMethod = OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_CALCULATION_METHOD.getName(), Constants.UTILITY_DEFAULT_CALCULATION_METHOD);
		final ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strValue);
		if (probDistribution == null)
			throw new TranspilerException("Error parsing regular expression \"" + strValue + "\" for instance \"" + instance.name() + "\"");
		// FIXME: To complete from here. IT is required to fix UtilityCalculator first to take into account both utilities and disutilities, and both onetime and annual
		// If defined to have an annual behavior
		if (OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL_VALUE.getName().equals(strTempBehavior)) {
			secParams.addCostParam(instance, 
					OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_DESCRIPTION.getName(), ""),  
					OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_SOURCE.getName(), ""), 
					year, probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
		}
		else {
			secParams.addTransitionCostParam(instance, 
					OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_DESCRIPTION.getName(), ""),  
					OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_SOURCE.getName(), ""), 
					year, probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
		}
	}
	
	public static String recalculatePropabilityField(String manifestationName, String dataPropertyValue, String dataPropertyValueDistro) {
		String mf = "";
		if (OwlHelper.getDataPropertyValue(manifestationName, dataPropertyValue) != null) {
			mf += OwlHelper.getDataPropertyValue(manifestationName, dataPropertyValue);
		} 		
		if (!mf.isEmpty() && OwlHelper.getDataPropertyValue(manifestationName, dataPropertyValueDistro) != null) {
			mf += Constants.CONSTANT_HASHTAG + OwlHelper.getDataPropertyValue(manifestationName, dataPropertyValueDistro);
		} else if (mf.isEmpty() && OwlHelper.getDataPropertyValue(manifestationName, dataPropertyValueDistro) != null) {
			mf += OwlHelper.getDataPropertyValue(manifestationName, dataPropertyValueDistro);
		}
		return !mf.isEmpty() ? mf : null;
	}
	

	public static List<Utility> getUtilities(String objectName) {
		List<Utility> result = new ArrayList<>();
		List<String> utilities = OwlHelper.getChildsByClassName(objectName, Constants.CLASS_UTILITY);
		for (String utilityName: utilities) { 
			Utility utility = new Utility();
			utility.setValue(recalculatePropabilityField(utilityName, Constants.DATAPROPERTY_VALUE, Constants.DATAPROPERTY_VALUE_DISTRIBUTION));
			result.add(utility);
		}
		return !result.isEmpty() ? result : null;
	}

	public static List<Guideline> getGuidelines(String objectName) {
		List<Guideline> result = new ArrayList<>();
		List<String> guidelines = OwlHelper.getChildsByClassName(objectName, Constants.CLASS_GUIDELINES);
		for (String guidelineName: guidelines) {
			Guideline guideline = new Guideline();
			guideline.setName(guidelineName);
			guideline.setConditions(OwlHelper.getDataPropertyValue(guidelineName, Constants.DATAPROPERTY_CONDITIONS));
			guideline.setDose(OwlHelper.getDataPropertyValue(guidelineName, Constants.DATAPROPERTY_DOSE));
			guideline.setFrequency(OwlHelper.getDataPropertyValue(guidelineName, Constants.DATAPROPERTY_FREQUENCY));
			guideline.setHoursIntervals(OwlHelper.getDataPropertyValue(guidelineName, Constants.DATAPROPERTY_HOURSINTERVAL));
			guideline.setRange(OwlHelper.getDataPropertyValue(guidelineName, Constants.DATAPROPERTY_RANGE));
			guideline.setUtilities(getUtilities(guidelineName));
			result.add(guideline);
		}
		return !result.isEmpty() ? result : null;
	}
}
