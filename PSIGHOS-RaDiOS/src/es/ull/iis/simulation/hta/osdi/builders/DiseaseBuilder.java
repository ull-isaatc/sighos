package es.ull.iis.simulation.hta.osdi.builders;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.osdi.OSDiGenericModel;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiObjectProperties;
import es.ull.iis.simulation.hta.osdi.wrappers.CostParameterWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ParameterWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.UtilityParameterWrapper;
import es.ull.iis.simulation.hta.params.ParameterTemplate;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;

/**
 * Allows the creation of a {@link StandardDisease} based on the information stored in the ontology
 * @author Iván Castilla Rodríguez
 * @author David Prieto González
 */
public interface DiseaseBuilder {
	/**
	 * Creates an instance of a disease from the information stored in the ontology. The population is required because some parameters are population-dependant; 
	 * e.g. initial proportion of manifestations should be related both to a manifestation and a population.
	 * @param secParams Common parameters repository
	 * @param diseaseIRI Name of the disease, used as IRI in the ontology
	 * @param populationIRI Name of the population, used as IRI in the ontology. 
	 * @return An instance of a disease 
	 * @throws MalformedOSDiModelException 
	 */
	public static Disease getDiseaseInstance(OSDiGenericModel secParams, String diseaseIRI, String populationIRI) throws MalformedOSDiModelException {
		final Disease disease = new OSDiDisease(secParams, diseaseIRI, OSDiDataProperties.HAS_DESCRIPTION.getValue(diseaseIRI, ""));
		// Build developments
		final Set<String> developments = OSDiClasses.DEVELOPMENT.getIndividuals(true);
		for (String developmentName : developments) {
			DevelopmentBuilder.getDevelopmentInstance(secParams, developmentName, disease);
		}
		
		// Build manifestations
		final Set<String> progressions = OSDiClasses.DISEASE_PROGRESSION.getIndividuals(true);
		for (String progressionIRI: progressions) {
			DiseaseProgressionBuilder.getDiseaseProgressionInstance(secParams, progressionIRI, disease, populationIRI);
		}
		// Build progression pathways after creating all the manifestations
		for (String progressionIRI: progressions) {
			final DiseaseProgression progression = disease.getDiseaseProgression(progressionIRI);
			DiseaseProgressionRiskBuilder.getPathwayInstance(secParams, progression);
			// Also include exclusions among progressions
			final Set<String> exclusions = OSDiObjectProperties.EXCLUDES_MANIFESTATION.getValues(progressionIRI);
			for (String excludedManif : exclusions) {
				disease.addExclusion(progression, disease.getDiseaseProgression(excludedManif));
			}
		}
		return disease;
	}

	static class OSDiDisease extends Disease {
		final private Map<ParameterTemplate, ParameterWrapper> paramMapping;

		public OSDiDisease(OSDiGenericModel model, String name, String description) throws MalformedOSDiModelException {
			super(model, name, description);
			paramMapping = new TreeMap<>();

			// Create parameters
			addCostIfDefined(OSDiObjectProperties.HAS_COST, StandardParameter.ANNUAL_COST, false);
			addCostIfDefined(OSDiObjectProperties.HAS_FOLLOW_UP_COST, StandardParameter.FOLLOW_UP_COST, false);
			addCostIfDefined(OSDiObjectProperties.HAS_TREATMENT_COST, StandardParameter.TREATMENT_COST, false);
			addCostIfDefined(OSDiObjectProperties.HAS_DIAGNOSIS_COST, StandardParameter.DISEASE_DIAGNOSIS_COST, true);
			
			final UtilityParameterWrapper utilityParam = model.createUtilityParam(name(), OSDiClasses.DISEASE, OSDiObjectProperties.HAS_UTILITY, false);
			if (utilityParam != null)
				paramMapping.put(utilityParam.isDisutility() ? StandardParameter.ANNUAL_DISUTILITY : StandardParameter.ANNUAL_UTILITY, utilityParam);
		}

		/**
		 * Creates and adds a cost parameter if the cost property is defined for the disease in the ontolgoy
		 * @param costProperty The property that defines the cost in the ontology
		 * @param paramDescription The type of simulation parameter that should be used for that property
		 * @param expectedOneTime If true, the cost should be one-time; otherwise, it should be annual
		 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
		 */
		private void addCostIfDefined(OSDiObjectProperties costProperty, ParameterTemplate paramDescription, boolean expectedOneTime) throws MalformedOSDiModelException {
			final CostParameterWrapper costParam = ((OSDiGenericModel)model).createCostParam(name(), OSDiClasses.DISEASE, costProperty, paramDescription, expectedOneTime);
			if (costParam != null)
				paramMapping.put(paramDescription, costParam);
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
