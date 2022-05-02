package es.ull.iis.simulation.hta.osdi;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.Cost;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.radios.transforms.ValueTransform;
import es.ull.iis.simulation.hta.radios.wrappers.ProbabilityDistribution;

public class SecondOrderParamsBuilder {

	public static void createCostParams(SecondOrderParamsRepository secParams, String objectName) {
		List<String> costs = OwlHelper.getChildsByClassName(objectName, OSDiNames.Class.COST.getName());
		for (String costName: costs) {
			// Assumes current year if not specified
			final int year = Integer.parseInt(OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_YEAR.getName(), "" + (new GregorianCalendar()).get(GregorianCalendar.YEAR)));
			// Assumes annual behavior if not specified
			String strTempBehavior = OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getName(), OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL_VALUE.getName());
			// If defined to have an annual behavior
			if (OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL_VALUE.getName().equals(strTempBehavior)) {
				ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(annualCost);				
			}
			
			String annualCost = null;
			String onetimeCost = null;
			Integer yearAnnualCost = null;
			Integer yearOnetimeCost = null;
			for (Cost cost : manifJSON.getCosts()) {
				if (annualCost == null && Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE.equals(cost.getTemporalBehavior())) {
					annualCost = cost.getAmount();
					yearAnnualCost = !StringUtils.isEmpty(cost.getYear()) ? Integer.parseInt(cost.getYear()) : (new GregorianCalendar()).get(GregorianCalendar.YEAR);
				} else if (onetimeCost == null && Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE.equals(cost.getTemporalBehavior())) {
					onetimeCost = cost.getAmount();
					yearOnetimeCost = !StringUtils.isEmpty(cost.getYear()) ? Integer.parseInt(cost.getYear()) : (new GregorianCalendar()).get(GregorianCalendar.YEAR);
				}
			}		
			
			if (annualCost != null) {
				ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(annualCost);
				if (probabilityDistribution != null) {
					manif.getParamsRepository().addCostParam(manif, "Cost for " + manif, Constants.CONSTANT_EMPTY_STRING, yearAnnualCost, 
							probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForCost());
				}
			}
			
			if (onetimeCost != null) {
				ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(onetimeCost);
				if (probabilityDistribution != null) {
					manif.getParamsRepository().addTransitionCostParam(manif, "Punctual cost for " + manif, Constants.CONSTANT_EMPTY_STRING, yearOnetimeCost, probabilityDistribution.getDeterministicValue(),
							probabilityDistribution.getProbabilisticValueInitializedForCost());
				}
			}
			
			final SecondOrderCostParam cost = new SecondOrderCostParam(secParams, costName, 
					OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_DESCRIPTION.getName(), ""),  
					OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_SOURCE.getName(), ""), 
					year, 0.0);
			cost.setAmount(OwlHelper.getDataPropertyValue(costName, Constants.DATAPROPERTY_AMOUNT));
			cost.setTemporalBehavior(OwlHelper.getDataPropertyValue(costName, Constants.DATAPROPERTY_TEMPORAL_BEHAVIOR));
			secParams.addCostParam(cost);
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
