/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.EnumSet;
import java.util.Set;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.interventions.ScreeningIntervention;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ParameterWrapper;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla
 * TODO: SCREENING should be generalized to DETECTION
 */
public interface InterventionBuilder {
	public static String DO_NOTHING = "DO_NOTHING";

	public static Intervention getInterventionInstance(OSDiGenericRepository secParams, String interventionName) {
		final OSDiWrapper wrap = secParams.getOwlWrapper();
		if (DO_NOTHING.equals(interventionName))
			return new DoNothingIntervention(secParams);
		final String description = OSDiWrapper.DataProperty.HAS_DESCRIPTION.getValue(interventionName, "");
		final Set<String> superclasses = wrap.getClassesForIndividual(interventionName);
		Intervention intervention = null;
		// TODO: Populate different methods for different interventions
		if (superclasses.contains(OSDiWrapper.InterventionType.SCREENING.getClazz().getShortName())) {			

			intervention = new ScreeningIntervention(secParams, interventionName, description) {
				
				@Override
				public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
					createParamsForScreening((OSDiGenericRepository) secParams, this);										
				}
				
				@Override
				public double getStartingCost(Patient pat, double time, Discount discountRate) {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public double[] getAnnualizedCostWithinPeriod(Patient pat, double initT, double endT,
						Discount discountRate) {
					// TODO Auto-generated method stub
					return discountRate.applyAnnualDiscount(0.0, initT, endT);
				}

				@Override
				public double getTreatmentAndFollowUpCosts(Patient pat, double initT, double endT,
						Discount discountRate) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT,
						Discount discountRate) {
					// TODO Auto-generated method stub
					return discountRate.applyAnnualDiscount(0.0, initT, endT);
				}
			};
		}
		else {
			intervention = new Intervention(secParams, interventionName, description) {

				@Override
				public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
					createParams((OSDiGenericRepository) secParams, this);					
				}

				@Override
				public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public double getStartingCost(Patient pat, double time, Discount discountRate) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public double[] getAnnualizedCostWithinPeriod(Patient pat, double initT, double endT,
						Discount discountRate) {
					// TODO Auto-generated method stub
					return discountRate.applyAnnualDiscount(0.0, initT, endT);
				}

				@Override
				public double getTreatmentAndFollowUpCosts(Patient pat, double initT, double endT,
						Discount discountRate) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT,
						Discount discountRate) {
					// TODO Auto-generated method stub
					return discountRate.applyAnnualDiscount(0.0, initT, endT);
				}
			};
		}
		return intervention;
	}
	
	private static void createParams(OSDiGenericRepository secParams, Intervention intervention) {
		try {
			createModificationParams(secParams, intervention);
		} catch(MalformedOSDiModelException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	private static void createParamsForScreening(OSDiGenericRepository secParams, ScreeningIntervention intervention) {
		try {
			createModificationParams(secParams, intervention);
			createSpecificityAndSensitivity(secParams, intervention);
		} catch(MalformedOSDiModelException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	private static void createSpecificityAndSensitivity(OSDiGenericRepository secParams, ScreeningIntervention intervention) throws MalformedOSDiModelException {
		final OSDiWrapper wrap = secParams.getOwlWrapper();
		// Sensitivity
		final Set<String> strSensitivities = OSDiWrapper.ObjectProperty.HAS_SENSITIVITY.getValues(intervention.name(), true);
		if (strSensitivities.size() == 0) {
			wrap.printWarning(intervention.name(), OSDiWrapper.ObjectProperty.HAS_SENSITIVITY, "Sensitivity not defined for a screening intervention. Using 1.0");						
			ProbabilityParamDescriptions.SENSITIVITY.addParameter(secParams, intervention, "", 1.0);
		}
		else {
			final String sensitivityParamName = (String) strSensitivities.toArray()[0];
			if (strSensitivities.size() > 1) {
				wrap.printWarning(intervention.name(), OSDiWrapper.ObjectProperty.HAS_SENSITIVITY, "Found more than one sensitivity for a screening intervention. Using " + sensitivityParamName);			
			}
			final ParameterWrapper sensitivityWrapper = new ParameterWrapper(wrap, sensitivityParamName, "Sensitivity for " + intervention.name(), EnumSet.of(ExpressionWrapper.SupportedType.CONSTANT)); 
			if (!sensitivityWrapper.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_SENSITIVITY)) {
				wrap.printWarning(sensitivityParamName, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Data item types defined for sensitivity do not include " + OSDiWrapper.DataItemType.DI_SENSITIVITY.getInstanceName());			
			}
			ProbabilityParamDescriptions.SENSITIVITY.addParameter(secParams, intervention, sensitivityWrapper.getDescription(),  
					sensitivityWrapper.getExpression().getConstantValue(), sensitivityWrapper.getProbabilisticValue());
		}
		// Specificity
		final Set<String> strSpecificities = OSDiWrapper.ObjectProperty.HAS_SPECIFICITY.getValues(intervention.name(), true);
		if (strSpecificities.size() == 0) {
			wrap.printWarning(intervention.name(), OSDiWrapper.ObjectProperty.HAS_SPECIFICITY, "Specificity not defined for a screening intervention. Using 1.0");						
			ProbabilityParamDescriptions.SPECIFICITY.addParameter(secParams, intervention, "", 1.0);
		}
		else {
			final String specificityParamName = (String) strSpecificities.toArray()[0];
			if (strSpecificities.size() > 1) {
				wrap.printWarning(intervention.name(), OSDiWrapper.ObjectProperty.HAS_SPECIFICITY, "Found more than one specificity for a screening intervention. Using " + specificityParamName);			
			}
			final ParameterWrapper specificityWrapper = new ParameterWrapper(wrap, specificityParamName, "Specificity for " + intervention.name(), EnumSet.of(ExpressionWrapper.SupportedType.CONSTANT)); 
			if (!specificityWrapper.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_SPECIFICITY)) {
				wrap.printWarning(specificityParamName, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Data item types defined for sensitivity do not include " + OSDiWrapper.DataItemType.DI_SPECIFICITY.getInstanceName());			
			}
			ProbabilityParamDescriptions.SPECIFICITY.addParameter(secParams, intervention, specificityWrapper.getDescription(),  
					specificityWrapper.getExpression().getConstantValue(), specificityWrapper.getProbabilisticValue());
		}
	}
	
	private static void createModificationParams(OSDiGenericRepository secParams, Intervention intervention) throws MalformedOSDiModelException {
		final OSDiWrapper wrap = secParams.getOwlWrapper();
		// Collects the modifications associated to the specified intervention
		final Set<String> modifications = OSDiWrapper.ObjectProperty.INVOLVES_MODIFICATION.getValues(intervention.name(), true);
		for (String modificationName : modifications) {		
			final ParameterWrapper modification = new ParameterWrapper(wrap, modificationName, "", EnumSet.of(ExpressionWrapper.SupportedType.CONSTANT));
			Modification.Type kind = null;
			if (modification.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_CONTINUOUS_VARIABLE))
				kind = Modification.Type.SET;
			else if (modification.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_FACTOR) || modification.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_RELATIVERISK))
				kind = Modification.Type.RR;
			else if (modification.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_MEANDIFFERENCE))
				kind = Modification.Type.DIFF;
			else {
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.PARAMETER, modificationName, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "None of the data item types defined for the modification are currently supported");
			}
			final Set<String> modifiedItems = OSDiWrapper.ObjectProperty.MODIFIES.getValues(modificationName, true);
			for (String indParamName : modifiedItems) {
				intervention.addAttributeModification(indParamName, new Modification(secParams, kind, SecondOrderParamsRepository.getModificationString(intervention, indParamName), 
						modification.getDescription(), modification.getSource(), modification.getExpression().getConstantValue(), modification.getProbabilisticValue()));
			}
		}
	}
}
