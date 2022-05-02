package es.ull.iis.simulation.hta.osdi;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

public class SecondOrderParamsBuilder {

	// FIXME: Lifetime cost pending to add
	// FIXME: Check ValueParser.splitProbabilityDistribution and later conditions on null value
	public static void createCostParams(SecondOrderParamsRepository secParams, Named instanceName) {
		List<String> costs = OwlHelper.getChildsByClassName(instanceName.name(), OSDiNames.Class.COST.getName());
		for (String costName: costs) {
			// Assumes current year if not specified
			final int year = Integer.parseInt(OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_YEAR.getName(), "" + (new GregorianCalendar()).get(GregorianCalendar.YEAR)));
			// Assumes cost to be 0 if not defined
			final String strAmount = OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_VALUE.getName(), "0.0");
			// Assumes annual behavior if not specified
			final String strTempBehavior = OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getName(), OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL_VALUE.getName());
			final ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strAmount);
			// If defined to have an annual behavior
			if (OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL_VALUE.getName().equals(strTempBehavior)) {
				if (probDistribution != null) {
					secParams.addCostParam(instanceName, 
							OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_DESCRIPTION.getName(), ""),  
							OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_SOURCE.getName(), ""), 
							year, probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
				}
			}
			else {
				if (probDistribution != null) {
					secParams.addTransitionCostParam(instanceName, 
							OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_DESCRIPTION.getName(), ""),  
							OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_SOURCE.getName(), ""), 
							year, probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
				}
				
			}
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
			utility.setName(utilityName);
			utility.setKind(OwlHelper.getDataPropertyValue(utilityName, Constants.DATAPROPERTY_KIND_UTILITY));
			String calculatedMethod = OwlHelper.getDataPropertyValue(utilityName, Constants.DATAPROPERTY_CALCULATEMETHOD);
			utility.setCalculationMethod(calculatedMethod != null && !calculatedMethod.isEmpty() ? calculatedMethod : Constants.DATAPROPERTYVALUE_CALCULATED_METHOD_DEFAULT);
			utility.setTemporalBehavior(OwlHelper.getDataPropertyValue(utilityName, Constants.DATAPROPERTY_TEMPORAL_BEHAVIOR));
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
