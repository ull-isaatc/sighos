package es.ull.iis.simulation.hta.osdi.builders;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.osdi.OSDiGenericModel;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiDataProperties;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiClasses;
import es.ull.iis.simulation.hta.osdi.ontology.OSDiWrapper;
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
		final OSDiWrapper wrap;

		public OSDiDisease(OSDiGenericModel secParams, String name, String description) throws MalformedOSDiModelException {
			super(secParams, name, description);
			wrap = secParams.getOwlWrapper();
			paramMapping = new TreeMap<>();

			createCostParam(OSDiObjectProperties.HAS_COST, StandardParameter.ANNUAL_COST);
			createCostParam(OSDiObjectProperties.HAS_FOLLOW_UP_COST, StandardParameter.FOLLOW_UP_COST);
			createCostParam(OSDiObjectProperties.HAS_TREATMENT_COST, StandardParameter.TREATMENT_COST);
			createCostParam(OSDiObjectProperties.HAS_DIAGNOSIS_COST, StandardParameter.DISEASE_DIAGNOSIS_COST);
			createUtilityParam();
		}

		/**
		 * Creates the costs associated to a specific disease by extracting the information from the ontology
		 * @param costProperty A specific cost property among those that can be used for a disease
		 * @param paramDescription The type of simulation parameter that should be used for that property 
		 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
		 */
		private void createCostParam(OSDiObjectProperties costProperty, ParameterTemplate paramDescription) throws MalformedOSDiModelException {
			final CostParameterWrapper costParam = wrap.createCostParam(name(), costProperty, paramDescription);
			if (costParam != null) {
				// Checking coherence between type of cost parameter and its temporal behavior. Assumed to be ok if temporal behavior not specified 
				if (StandardParameter.DISEASE_DIAGNOSIS_COST.equals(paramDescription) && !costParam.appliesOneTime()) {
					throw new MalformedOSDiModelException(OSDiClasses.DISEASE, name(), costProperty, "Diagnosis costs directly associated to a disease should be ONE_TIME. Instead, annual found");
				}
				else if (costParam.appliesOneTime()) {
					throw new MalformedOSDiModelException(OSDiClasses.DISEASE, name(), costProperty, "Follow-up, treatment and non specific costs directly associated to a disease should be ANNUAL. Instead, one-time found");
				}
				paramMapping.put(paramDescription, costParam);
			}
		}
		
		/**
		 * Creates the utilities associated to a specific disease by extracting the information from the ontology. Only one annual (dis)utility should be defined.
		 * @param disease A disease
		 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
		 */
		private void createUtilityParam() throws MalformedOSDiModelException {
			final Set<String> utilities = OSDiObjectProperties.HAS_UTILITY.getValues(name(), true);
			if (utilities.size() > 1)
				wrap.printWarning(name(), OSDiObjectProperties.HAS_UTILITY, "A maximum of one annual (dis)utility should be associated to a disease. Using only " + utilities.toArray()[0]);
			else if (utilities.size() == 1) {
				final String utilityName = (String) utilities.toArray()[0];
				UtilityParameterWrapper utilityParam = new UtilityParameterWrapper(wrap, utilityName, "Utility for disease " + name()); 
				if (utilityParam.appliesOneTime())
					throw new MalformedOSDiModelException(OSDiClasses.DISEASE, name(), OSDiObjectProperties.HAS_UTILITY, "Only annual (dis)utilities should be associated to a disease. Instead, one-time found");
				paramMapping.put(utilityParam.isDisutility() ? StandardParameter.ANNUAL_DISUTILITY : StandardParameter.ANNUAL_UTILITY, utilityParam);
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
