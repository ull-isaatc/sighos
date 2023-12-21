/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.ArrayList;
import java.util.Set;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ParameterModifierWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ParameterWrapper;
import es.ull.iis.simulation.hta.params.Discount;

/**
 * @author Iván Castilla
 * TODO: SCREENING should be generalized to DETECTION
 */
public interface InterventionBuilder {
	public static String DO_NOTHING = "DO_NOTHING";

	public static Intervention getInterventionInstance(OSDiGenericModel model, String interventionName) throws MalformedOSDiModelException {
		final OSDiWrapper wrap = model.getOwlWrapper();
		if (DO_NOTHING.equals(interventionName))
			return new DoNothingIntervention(model);
		final String description = OSDiWrapper.DataProperty.HAS_DESCRIPTION.getValue(interventionName, "");
		final Set<String> superclasses = wrap.getClassesForIndividual(interventionName);
		// TODO: Populate different methods for different interventions
		if (superclasses.contains(OSDiWrapper.InterventionType.SCREENING.getClazz().getShortName())) {
			return new OSDiScreeningIntervention(model, interventionName, description);
		}
		return new OSDiIntervention(model, interventionName, description);
	}
	
	private static ArrayList<ParameterModifierWrapper> createModificationParams(OSDiGenericModel model, Intervention intervention) throws MalformedOSDiModelException {
		final OSDiWrapper wrap = model.getOwlWrapper();
		final ArrayList<ParameterModifierWrapper> list = new ArrayList<>();
		// Collects the modifications associated to the specified intervention
		final Set<String> modifications = OSDiWrapper.ObjectProperty.INVOLVES_MODIFICATION.getValues(intervention.name(), true);
		for (String modificationName : modifications) {		
			list.add(new ParameterModifierWrapper(wrap, modificationName, intervention));
		}
		return list;
	}
	
	static class OSDiIntervention extends Intervention {
		private final  ArrayList<ParameterModifierWrapper> modifiers;

		public OSDiIntervention(OSDiGenericModel model, String name, String description) throws MalformedOSDiModelException {
			super(model, name, description);
			this.modifiers = InterventionBuilder.createModificationParams(model, this);
		}

		@Override
		public void createParameters() {
			for (ParameterModifierWrapper mod : modifiers) {
				mod.registerParameter(model);
			}			
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
		
	}

	
	static class OSDiScreeningIntervention extends Intervention {
		private final ParameterWrapper sensitivityWrapper;
		private final ParameterWrapper specificityWrapper;
		private final  ArrayList<ParameterModifierWrapper> modifiers;

		public OSDiScreeningIntervention(OSDiGenericModel model, String name, String description) throws MalformedOSDiModelException {
			super(model, name, description);
			final OSDiWrapper wrap = model.getOwlWrapper();
			// Sensitivity
			final Set<String> strSensitivities = OSDiWrapper.ObjectProperty.HAS_SENSITIVITY.getValues(name, true);
			if (strSensitivities.size() == 0) {
				wrap.printWarning(name, OSDiWrapper.ObjectProperty.HAS_SENSITIVITY, "Sensitivity not defined for a screening intervention. Using 1.0");
				sensitivityWrapper = null;
			}
			else {
				final String sensitivityParamName = (String) strSensitivities.toArray()[0];
				if (strSensitivities.size() > 1) {
					wrap.printWarning(name, OSDiWrapper.ObjectProperty.HAS_SENSITIVITY, "Found more than one sensitivity for a screening intervention. Using " + sensitivityParamName);			
				}
				sensitivityWrapper = new ParameterWrapper(wrap, sensitivityParamName, "Sensitivity for " + name); 
				if (!sensitivityWrapper.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_SENSITIVITY)) {
					wrap.printWarning(sensitivityParamName, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Data item types defined for sensitivity do not include " + OSDiWrapper.DataItemType.DI_SENSITIVITY.getInstanceName());			
				}
			}
			// Specificity
			final Set<String> strSpecificities = OSDiWrapper.ObjectProperty.HAS_SPECIFICITY.getValues(name, true);
			if (strSpecificities.size() == 0) {
				wrap.printWarning(name, OSDiWrapper.ObjectProperty.HAS_SPECIFICITY, "Specificity not defined for a screening intervention. Using 1.0");						
				specificityWrapper = null;
			}
			else {
				final String specificityParamName = (String) strSpecificities.toArray()[0];
				if (strSpecificities.size() > 1) {
					wrap.printWarning(name, OSDiWrapper.ObjectProperty.HAS_SPECIFICITY, "Found more than one specificity for a screening intervention. Using " + specificityParamName);			
				}
				specificityWrapper = new ParameterWrapper(wrap, specificityParamName, "Specificity for " + name); 
				if (!specificityWrapper.getDataItemTypes().contains(OSDiWrapper.DataItemType.DI_SPECIFICITY)) {
					wrap.printWarning(specificityParamName, OSDiWrapper.ObjectProperty.HAS_DATA_ITEM_TYPE, "Data item types defined for sensitivity do not include " + OSDiWrapper.DataItemType.DI_SPECIFICITY.getInstanceName());			
				}
			}
			this.modifiers = InterventionBuilder.createModificationParams(model, this);
		}

		@Override
		public void registerSecondOrderParameters(SecondOrderParamsRepository model) {
			if (sensitivityWrapper == null)
				RiskParamDescriptions.SENSITIVITY.addUsedParameter(model, this, "Assumed sensitivity", 1.0);
			else
				RiskParamDescriptions.SENSITIVITY.addUsedParameter(model, this, sensitivityWrapper.getDescription(),  
						sensitivityWrapper.getDeterministicValue(), sensitivityWrapper.getProbabilisticValue());
			if (specificityWrapper == null)
				RiskParamDescriptions.SPECIFICITY.addUsedParameter(model, this, "Assumed specificity", 1.0);
			else
				RiskParamDescriptions.SPECIFICITY.addUsedParameter(model, this, specificityWrapper.getDescription(),  
						specificityWrapper.getDeterministicValue(), specificityWrapper.getProbabilisticValue());
			for (ParameterModifierWrapper mod : modifiers) {
				mod.registerParameter(model);
			}			
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
		
	}
}
