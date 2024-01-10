/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.builders;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.osdi.OSDiGenericModel;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataItemTypes;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.ParameterWrapper;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;
import es.ull.iis.simulation.hta.params.ParameterTemplate;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;

/**
 * @author David Prieto González
 * @author Iván Castilla Rodríguez
 */
public interface DiseaseProgressionBuilder {

	public static DiseaseProgression getDiseaseProgressionInstance(OSDiGenericModel secParams, String progressionIRI, Disease disease, String populationIRI) throws MalformedOSDiModelException {
		final OSDiWrapper wrap = secParams.getOwlWrapper();
		
		final String description = OSDiDataProperties.HAS_DESCRIPTION.getValue(progressionIRI, "");
		final Set<String> progressionClazz = wrap.getClassesForIndividual(progressionIRI);
		if (progressionClazz.contains(OSDiClasses.ACUTE_MANIFESTATION.getShortName()))
			return new OSDiDiseaseProgression(secParams, progressionIRI, description, disease, DiseaseProgression.Type.ACUTE_MANIFESTATION, populationIRI);
		else if (progressionClazz.contains(OSDiClasses.CHRONIC_MANIFESTATION.getShortName()))
			return new OSDiDiseaseProgression(secParams, progressionIRI, description,	disease, DiseaseProgression.Type.CHRONIC_MANIFESTATION, populationIRI);
		return new OSDiDiseaseProgression(secParams, progressionIRI, description,	disease, DiseaseProgression.Type.STAGE, populationIRI);
	}

	/**
	 * 
	 */
	
	static class OSDiDiseaseProgression extends DiseaseProgression {
		final private Map<ParameterTemplate, ParameterWrapper> paramMapping;
		final private OSDiWrapper wrap;
		
		public OSDiDiseaseProgression(OSDiGenericModel model, String name, String description,
				Disease disease, DiseaseProgression.Type type, String populationIRI) throws MalformedOSDiModelException {
			super(model, name, description, disease, type);
			paramMapping = new TreeMap<>();
			wrap = model.getOwlWrapper();

			addCostIfDefined(OSDiObjectProperties.HAS_FOLLOW_UP_COST, StandardParameter.FOLLOW_UP_COST, false);
			addCostIfDefined(OSDiObjectProperties.HAS_TREATMENT_COST, StandardParameter.TREATMENT_COST, false);

			final ParameterWrapper[] onsetAndAnnualCostParameterWrappers = model.createOnsetAndAnnualCostParams(name());
			final ParameterWrapper[] onsetAndAnnualUtilityParameterWrappers = model.createOnsetAndAnnualUtilityParams(name());
			// Checking coherence of number of costs and the type of manifestation		
			if (DiseaseProgression.Type.ACUTE_MANIFESTATION.equals(getType())) {
				if (onsetAndAnnualCostParameterWrappers[0] != null)
					paramMapping.put(StandardParameter.ONSET_COST, onsetAndAnnualCostParameterWrappers[0]);
				else if (onsetAndAnnualCostParameterWrappers[1] != null)
					throw new MalformedOSDiModelException(OSDiClasses.ACUTE_MANIFESTATION, name(), OSDiObjectProperties.HAS_COST, "Expected one-time cost and obtained annual cost in " + onsetAndAnnualCostParameterWrappers[1].getOriginalIndividualIRI());
				if (onsetAndAnnualUtilityParameterWrappers[0] != null)
					paramMapping.put(OSDiDataItemTypes.DI_DISUTILITY.equals(onsetAndAnnualUtilityParameterWrappers[0].getDataItemType()) ? StandardParameter.ONSET_DISUTILITY : StandardParameter.ONSET_UTILITY, onsetAndAnnualUtilityParameterWrappers[0]);
				else if (onsetAndAnnualUtilityParameterWrappers[1] != null)
					throw new MalformedOSDiModelException(OSDiClasses.ACUTE_MANIFESTATION, name(), OSDiObjectProperties.HAS_UTILITY, "Expected one-time (dis)utility and obtained annual (dis)utility in " + onsetAndAnnualUtilityParameterWrappers[1].getOriginalIndividualIRI());
			}
			else {
				if (onsetAndAnnualCostParameterWrappers[0] != null)
					paramMapping.put(StandardParameter.ONSET_COST, onsetAndAnnualCostParameterWrappers[0]);
				if (onsetAndAnnualCostParameterWrappers[1] != null)
					paramMapping.put(StandardParameter.ANNUAL_COST, onsetAndAnnualCostParameterWrappers[1]);
				if (onsetAndAnnualUtilityParameterWrappers[0] != null)
					paramMapping.put(OSDiDataItemTypes.DI_DISUTILITY.equals(onsetAndAnnualUtilityParameterWrappers[0].getDataItemType()) ? StandardParameter.ONSET_DISUTILITY : StandardParameter.ONSET_UTILITY, onsetAndAnnualUtilityParameterWrappers[0]);
				if (onsetAndAnnualUtilityParameterWrappers[1] != null)
					paramMapping.put(OSDiDataItemTypes.DI_DISUTILITY.equals(onsetAndAnnualUtilityParameterWrappers[1].getDataItemType()) ? StandardParameter.ANNUAL_DISUTILITY : StandardParameter.ANNUAL_UTILITY, onsetAndAnnualUtilityParameterWrappers[1]);
			}

			createUsedParameter(OSDiObjectProperties.HAS_PROBABILITY_OF_DIAGNOSIS, StandardParameter.DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS);
			// Chronic manifestations may have increased mortality rates, reductions of life expectancy
			// FIXME: Currently, we are accepting even a probability of death. Conceptually this could only happen when the chronic manifestation is preceded by an acute manifestation 
			// (actually, the acute manifestation would be the one with such probability). We are doing so to simplify modeling, but it is inaccurate
			createUsedParameter(OSDiObjectProperties.HAS_PROBABILITY_OF_DEATH, StandardParameter.DISEASE_PROGRESSION_RISK_OF_DEATH);
			createUsedParameter(OSDiObjectProperties.HAS_ONSET_AGE, StandardParameter.DISEASE_PROGRESSION_ONSET_AGE);
			createUsedParameter(OSDiObjectProperties.HAS_END_AGE, StandardParameter.DISEASE_PROGRESSION_END_AGE);

			// Acute manifestations are assumed not to involve further mortality parameters
			if (!DiseaseProgression.Type.ACUTE_MANIFESTATION.equals(getType())) {
				createUsedParameter(OSDiObjectProperties.HAS_INCREASED_MORTALITY_RATE, StandardParameter.INCREASED_MORTALITY_RATE);
				createUsedParameter(OSDiObjectProperties.HAS_LIFE_EXPECTANCY_REDUCTION, StandardParameter.LIFE_EXPECTANCY_REDUCTION);
				createInitialProportionParam(populationIRI);
			}
		}

		/**
		 * Creates and adds a cost parameter if the cost property is defined for the disease progression in the ontolgoy
		 * @param costProperty The property that defines the cost in the ontology
		 * @param paramDescription The type of simulation parameter that should be used for that property
		 * @param expectedOneTime If true, the cost should be one-time; otherwise, it should be annual
		 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
		 */
		private void addCostIfDefined(OSDiObjectProperties costProperty, ParameterTemplate paramDescription, boolean expectedOneTime) throws MalformedOSDiModelException {
			final ParameterWrapper costParam = ((OSDiGenericModel)model).createCostParam(name(), OSDiClasses.DISEASE, costProperty, paramDescription, expectedOneTime);
			if (costParam != null)
				paramMapping.put(paramDescription, costParam);
		}
		
		private void createUsedParameter(OSDiObjectProperties objProperty, ParameterTemplate paramDescription) throws MalformedOSDiModelException {
			final String paramName = objProperty.getValue(name(), true);
			if (paramName != null)
				paramMapping.put(paramDescription, wrap.getParameterWrapper(paramName, ""));			
		}
		
		private void createInitialProportionParam(String populationName) throws MalformedOSDiModelException {
			final Set<String> manifestationParams = OSDiObjectProperties.HAS_INITIAL_PROPORTION.getValues(name(), true);
			if (manifestationParams.size() > 0) {
				final String manifParam = (String)manifestationParams.toArray()[0];
				if (manifestationParams.size() > 1) {
					wrap.printWarning(manifParam, OSDiObjectProperties.HAS_INITIAL_PROPORTION, "Manifestations should define a single initial proportion. Using " + manifParam);
				}
				if (!OSDiObjectProperties.IS_PARAMETER_OF_POPULATION.getValues(manifParam, true).contains(populationName))
					throw new MalformedOSDiModelException(OSDiClasses.PARAMETER, manifParam, OSDiObjectProperties.IS_PARAMETER_OF_POPULATION, 
							"Parameters characterizing initial proportions must be related to the population: " + populationName);
				paramMapping.put(StandardParameter.DISEASE_PROGRESSION_INITIAL_PROPORTION, wrap.getParameterWrapper(manifParam, "Initial proportion of " + name()));
			}
		}
		
		@Override
		public void createParameters() {
			for (ParameterTemplate paramDesc : paramMapping.keySet()) {
				final ParameterWrapper param = paramMapping.get(paramDesc);
				addUsedParameter(paramDesc, param.createParameter(model, paramDesc.getType()));
			}
		}
		
	}
}
