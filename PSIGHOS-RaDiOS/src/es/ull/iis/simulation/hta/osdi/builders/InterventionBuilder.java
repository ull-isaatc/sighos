/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.builders;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.osdi.OSDiGenericModel;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataItemTypes;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;
import es.ull.iis.simulation.hta.osdi.wrappers.CostParameterWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ParameterModifierWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ParameterWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.UtilityParameterWrapper;
import es.ull.iis.simulation.hta.params.ParameterTemplate;
import es.ull.iis.simulation.hta.params.StandardParameter;

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
		final String description = OSDiDataProperties.HAS_DESCRIPTION.getValue(interventionName, "");
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
		final Set<String> modifications = OSDiObjectProperties.INVOLVES_MODIFICATION.getValues(intervention.name(), true);
		for (String modificationName : modifications) {		
			list.add(new ParameterModifierWrapper(wrap, modificationName, intervention));
		}
		return list;
	}
	
	static class OSDiIntervention extends Intervention {
		private final  ArrayList<ParameterModifierWrapper> modifiers;
		final private Map<ParameterTemplate, ParameterWrapper> paramMapping;

		public OSDiIntervention(OSDiGenericModel model, String name, String description) throws MalformedOSDiModelException {
			super(model, name, description);
			this.modifiers = InterventionBuilder.createModificationParams(model, this);
			this.paramMapping = new TreeMap<>();

			addCostIfDefined(OSDiObjectProperties.HAS_FOLLOW_UP_COST, StandardParameter.FOLLOW_UP_COST, false);
			addCostIfDefined(OSDiObjectProperties.HAS_TREATMENT_COST, StandardParameter.TREATMENT_COST, false);

			final CostParameterWrapper[] onsetAndAnnualCostParameterWrappers = model.createOnsetAndAnnualCostParams(name());
			final UtilityParameterWrapper[] onsetAndAnnualUtilityParameterWrappers = model.createOnsetAndAnnualUtilityParams(name());
			if (onsetAndAnnualCostParameterWrappers[0] != null)
				paramMapping.put(StandardParameter.ONSET_COST, onsetAndAnnualCostParameterWrappers[0]);
			if (onsetAndAnnualCostParameterWrappers[1] != null)
				paramMapping.put(StandardParameter.ANNUAL_COST, onsetAndAnnualCostParameterWrappers[1]);
			if (onsetAndAnnualUtilityParameterWrappers[0] != null)
				paramMapping.put(onsetAndAnnualUtilityParameterWrappers[0].isDisutility() ? StandardParameter.ONSET_DISUTILITY : StandardParameter.ONSET_UTILITY, onsetAndAnnualUtilityParameterWrappers[0]);
			if (onsetAndAnnualUtilityParameterWrappers[1] != null)
				paramMapping.put(onsetAndAnnualUtilityParameterWrappers[1].isDisutility() ? StandardParameter.ANNUAL_DISUTILITY : StandardParameter.ANNUAL_UTILITY, onsetAndAnnualUtilityParameterWrappers[1]);
		}

		/**
		 * Creates and adds a cost parameter if the cost property is defined for the disease in the ontolgoy
		 * @param costProperty The property that defines the cost in the ontology
		 * @param paramDescription The type of simulation parameter that should be used for that property
		 * @param expectedOneTime If true, the cost should be one-time; otherwise, it should be annual
		 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
		 */
		private void addCostIfDefined(OSDiObjectProperties costProperty, ParameterTemplate paramDescription, boolean expectedOneTime) throws MalformedOSDiModelException {
			CostParameterWrapper costParam = ((OSDiGenericModel)model).createCostParam(name(), OSDiClasses.DISEASE, costProperty, paramDescription, expectedOneTime);
			if (costParam != null)
				paramMapping.put(paramDescription, costParam);
		}
		
		@Override
		public void createParameters() {
			for (ParameterModifierWrapper mod : modifiers) {
				mod.registerParameter(model);
			}
			for (ParameterTemplate paramDesc : paramMapping.keySet()) {
				final ParameterWrapper param = paramMapping.get(paramDesc);
				addUsedParameter(paramDesc, param.createParameter(model, paramDesc.getType()));
			}
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
			final Set<String> strSensitivities = OSDiObjectProperties.HAS_SENSITIVITY.getValues(name, true);
			if (strSensitivities.size() == 0) {
				wrap.printWarning(name, OSDiObjectProperties.HAS_SENSITIVITY, "Sensitivity not defined for a screening intervention. Using 1.0");
				sensitivityWrapper = null;
			}
			else {
				final String sensitivityParamName = (String) strSensitivities.toArray()[0];
				if (strSensitivities.size() > 1) {
					wrap.printWarning(name, OSDiObjectProperties.HAS_SENSITIVITY, "Found more than one sensitivity for a screening intervention. Using " + sensitivityParamName);			
				}
				sensitivityWrapper = new ParameterWrapper(wrap, sensitivityParamName, "Sensitivity for " + name); 
				if (!sensitivityWrapper.getDataItemTypes().contains(OSDiDataItemTypes.DI_SENSITIVITY)) {
					wrap.printWarning(sensitivityParamName, OSDiObjectProperties.HAS_DATA_ITEM_TYPE, "Data item types defined for sensitivity do not include " + OSDiDataItemTypes.DI_SENSITIVITY.getInstanceName());			
				}
			}
			// Specificity
			final Set<String> strSpecificities = OSDiObjectProperties.HAS_SPECIFICITY.getValues(name, true);
			if (strSpecificities.size() == 0) {
				wrap.printWarning(name, OSDiObjectProperties.HAS_SPECIFICITY, "Specificity not defined for a screening intervention. Using 1.0");						
				specificityWrapper = null;
			}
			else {
				final String specificityParamName = (String) strSpecificities.toArray()[0];
				if (strSpecificities.size() > 1) {
					wrap.printWarning(name, OSDiObjectProperties.HAS_SPECIFICITY, "Found more than one specificity for a screening intervention. Using " + specificityParamName);			
				}
				specificityWrapper = new ParameterWrapper(wrap, specificityParamName, "Specificity for " + name); 
				if (!specificityWrapper.getDataItemTypes().contains(OSDiDataItemTypes.DI_SPECIFICITY)) {
					wrap.printWarning(specificityParamName, OSDiObjectProperties.HAS_DATA_ITEM_TYPE, "Data item types defined for sensitivity do not include " + OSDiDataItemTypes.DI_SPECIFICITY.getInstanceName());			
				}
			}
			this.modifiers = InterventionBuilder.createModificationParams(model, this);
		}

		@Override
		public void createParameters() {
			if (sensitivityWrapper == null)
				addUsedParameter(StandardParameter.SENSITIVITY, "Assumed sensitivity", "Assumption", 1.0);
			else
				addUsedParameter(StandardParameter.SENSITIVITY, sensitivityWrapper.getDescription(), sensitivityWrapper.getSource(), 
						sensitivityWrapper.getDeterministicValue(), sensitivityWrapper.getProbabilisticValue());
			if (specificityWrapper == null)
				addUsedParameter(StandardParameter.SPECIFICITY, "Assumed sensitivity", "Assumption", 1.0);
			else
				addUsedParameter(StandardParameter.SPECIFICITY, specificityWrapper.getDescription(), specificityWrapper.getSource(),
						specificityWrapper.getDeterministicValue(), specificityWrapper.getProbabilisticValue());
			for (ParameterModifierWrapper mod : modifiers) {
				mod.registerParameter(model);
			}			
		}
		
	}
}
