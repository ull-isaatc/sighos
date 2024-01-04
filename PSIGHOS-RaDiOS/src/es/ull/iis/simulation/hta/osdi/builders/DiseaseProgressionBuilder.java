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
		
		public OSDiDiseaseProgression(OSDiGenericModel secParams, String name, String description,
				Disease disease, DiseaseProgression.Type type, String populationIRI) throws MalformedOSDiModelException {
			super(secParams, name, description, disease, type);
			paramMapping = new TreeMap<>();
			wrap = secParams.getOwlWrapper();

			createCostParams();
			createUtilityParams();
			createUsedParameter(OSDiObjectProperties.HAS_PROBABILITY_OF_DIAGNOSIS, StandardParameter.DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS);
			// Chronic manifestations may have increased mortality rates, reductions of life expectancy
			// FIXME: Currently, we are accepting even a probability of death. Conceptually this could only happen when the chronic manifestation is preceded by an acute manifestation 
			// (actually, the acute manifestation would be the one with such probability). We are doing so to simplify modeling, but it is inaccurate
			createUsedParameter(OSDiObjectProperties.HAS_PROBABILITY_OF_DEATH, StandardParameter.DISEASE_PROGRESSION_RISK_OF_DEATH);
			createUsedParameter(OSDiObjectProperties.HAS_ONSET_AGE, StandardParameter.DISEASE_PROGRESSION_ONSET_AGE);
			createUsedParameter(OSDiObjectProperties.HAS_END_AGE, StandardParameter.DISEASE_PROGRESSION_END_AGE);

			// Acute manifestations are assumed not to involve further mortality parameters
			if (DiseaseProgression.Type.CHRONIC_MANIFESTATION.equals(getType())) {
				createUsedParameter(OSDiObjectProperties.HAS_INCREASED_MORTALITY_RATE, StandardParameter.INCREASED_MORTALITY_RATE);
				createUsedParameter(OSDiObjectProperties.HAS_LIFE_EXPECTANCY_REDUCTION, StandardParameter.LIFE_EXPECTANCY_REDUCTION);
			}
			else {
				createInitialProportionParam(populationIRI);
			}
		}

		/**
		 * Creates the costs associated to a specific disease by extracting the information from the ontology
		 * @param costProperty A specific cost property among those that can be used for a disease
		 * @param paramDescription The type of simulation parameter that should be used for that property 
		 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
		 */
		private void createCostParam(OSDiObjectProperties costProperty, ParameterTemplate paramDescription, boolean expectedOneTime) throws MalformedOSDiModelException {
			final CostParameterWrapper costParam = wrap.createCostParam(name(), costProperty, paramDescription);
			if (costParam != null) {
				// Checking coherence between type of cost parameter and its temporal behavior. 
				if (costParam.appliesOneTime() != expectedOneTime)
					throw new MalformedOSDiModelException(OSDiClasses.DISEASE, name(), costProperty, "The cost was expected to be " + (expectedOneTime ? "one-time" : "annual") + "instead, " + (expectedOneTime ? "annual" :"one-time")+ " found");
				paramMapping.put(paramDescription, costParam);
			}
		}
		
		/**
		 * Creates the costs associated to a specific manifestation by extracting the information from the ontology. If the manifestation is acute, only one cost should be defined; otherwise, up to two costs 
		 * (one-time and annual) may be defined.
		 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
		 */
		public void createCostParams() throws MalformedOSDiModelException {
			
			createCostParam(OSDiObjectProperties.HAS_FOLLOW_UP_COST, StandardParameter.FOLLOW_UP_COST, false);
			createCostParam(OSDiObjectProperties.HAS_TREATMENT_COST, StandardParameter.TREATMENT_COST, false);

			final Set<String> costs = OSDiObjectProperties.HAS_COST.getValues(name(), true);
			String[] costsArray = new String[costs.size()];
			costsArray = costs.toArray(costsArray);
			if (costs.size() == 0) {
				wrap.printWarning(name(), OSDiObjectProperties.HAS_COST, "No cost defined for a disease progression. Using 0 as a default value");				
			}
			else {
				// Checking coherence of number of costs and the type of manifestation		
				if (DiseaseProgression.Type.ACUTE_MANIFESTATION.equals(getType())) {
					CostParameterWrapper costParam = null;
					if (costsArray.length == 1) {
						costParam = new CostParameterWrapper(wrap, costsArray[0], "Cost for " + name());
						if (!costParam.appliesOneTime())
							throw new MalformedOSDiModelException(OSDiClasses.ACUTE_MANIFESTATION, name(), OSDiObjectProperties.HAS_COST, "Expected one-time cost and obtained annual cost in " + costsArray[0]);
					}
					else {
						for (int i = 0; i < costsArray.length && costParam == null; i++) {
							costParam = new CostParameterWrapper(wrap, costsArray[i], "Cost for " + name());
							if (!costParam.appliesOneTime()) {
								costParam = null;
							}
						}
						if (costParam != null)
							wrap.printWarning(name(), OSDiObjectProperties.HAS_COST, "Found more than one cost for an acute manifestation. Using " + costParam.getOriginalIndividualIRI());
					}
					if (costParam != null)
						paramMapping.put(StandardParameter.ONSET_COST, costParam);
				}
				else {
					CostParameterWrapper onsetCostParam = null;
					CostParameterWrapper annualCostParam = null;
					for (int i = 0; i < costsArray.length && (onsetCostParam == null || annualCostParam == null); i++) {
						CostParameterWrapper costParam = new CostParameterWrapper(wrap, costsArray[i], "Cost for " + name());
						if (costParam.appliesOneTime()) {
							if (onsetCostParam != null)
								wrap.printWarning(name(), OSDiObjectProperties.HAS_COST, "Found more than one one-time cost for a chronic manifestation. Using " + onsetCostParam.getOriginalIndividualIRI());
							else
								onsetCostParam = costParam;
						}
						else {
							if (annualCostParam != null)
								wrap.printWarning(name(), OSDiObjectProperties.HAS_COST, "Found more than one annual cost for a chronic manifestation. Using " + annualCostParam.getOriginalIndividualIRI());
							else
								annualCostParam = costParam;
						}
					}
					if (onsetCostParam != null)
						paramMapping.put(StandardParameter.ONSET_COST, onsetCostParam);
					if (annualCostParam != null)
						paramMapping.put(StandardParameter.ANNUAL_COST, annualCostParam);
				}
			}
		}

		/**
		 * Creates the utilities associated to a specific manifestation by extracting the information from the ontology. If the manifestation is acute, only one utility should be defined; otherwise, 
		 * up to two utilities (one-time and annual) may be defined.
		 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
		 */
		public void createUtilityParams() throws MalformedOSDiModelException {
			final Set<String> utilities = OSDiObjectProperties.HAS_UTILITY.getValues(name(), true);
			String[] utilitiesArray = new String[utilities.size()];
			utilitiesArray = utilities.toArray(utilitiesArray);
			if (utilities.size() == 0) {
				wrap.printWarning(name(), OSDiObjectProperties.HAS_UTILITY, "No utility defined for a manifestation. Creating a 0 disutility as a default value");				
			}
			else {
				// Checking coherence of number of utilities and the type of manifestation		
				if (DiseaseProgression.Type.ACUTE_MANIFESTATION.equals(getType())) {
					UtilityParameterWrapper utilityParam = null;
					if (utilitiesArray.length == 1) {
						utilityParam = new UtilityParameterWrapper(wrap, utilitiesArray[0], "(Dis)utility for " + name());
						if (!utilityParam.appliesOneTime())
							throw new MalformedOSDiModelException(OSDiClasses.ACUTE_MANIFESTATION, name(), OSDiObjectProperties.HAS_UTILITY, "Expected one-time (dis)utility and obtained annual (dis)utility in " + utilitiesArray[0]);
					}
					else {
						for (int i = 0; i < utilitiesArray.length && utilityParam == null; i++) {
							utilityParam = new UtilityParameterWrapper(wrap, utilitiesArray[i], "(Dis)utility for " + name());
							if (!utilityParam.appliesOneTime()) {
								utilityParam = null;
							}
						}
						if (utilityParam != null)
							wrap.printWarning(name(), OSDiObjectProperties.HAS_UTILITY, "Found more than one (dis)utility for an acute manifestation. Using " + utilityParam.getOriginalIndividualIRI());
					}
					if (utilityParam != null)
						paramMapping.put(utilityParam.isDisutility() ? StandardParameter.ONSET_DISUTILITY : StandardParameter.ONSET_UTILITY, utilityParam);
				}
				else {
					UtilityParameterWrapper onsetUtilityParam = null;
					UtilityParameterWrapper annualUtilityParam = null;
					for (int i = 0; i < utilitiesArray.length && (onsetUtilityParam == null || annualUtilityParam == null); i++) {
						UtilityParameterWrapper utilityParam = new UtilityParameterWrapper(wrap, utilitiesArray[i], "(Dis)utility for " + name());
						if (utilityParam.appliesOneTime()) {
							if (onsetUtilityParam != null)
								wrap.printWarning(name(), OSDiObjectProperties.HAS_UTILITY, "Found more than one one-time (dis)utility for a chronic manifestation. Using " + onsetUtilityParam.getOriginalIndividualIRI());
							else
								onsetUtilityParam = utilityParam;
						}
						else {
							if (annualUtilityParam != null)
								wrap.printWarning(name(), OSDiObjectProperties.HAS_UTILITY, "Found more than one annual (dis)utility for a chronic manifestation. Using " + annualUtilityParam.getOriginalIndividualIRI());
							else
								annualUtilityParam = utilityParam;
						}
					}
					if (onsetUtilityParam != null)
						paramMapping.put(onsetUtilityParam.isDisutility() ? StandardParameter.ONSET_DISUTILITY : StandardParameter.ONSET_UTILITY, onsetUtilityParam);
					if (annualUtilityParam != null)
						paramMapping.put(annualUtilityParam.isDisutility() ? StandardParameter.ANNUAL_DISUTILITY : StandardParameter.ANNUAL_UTILITY, annualUtilityParam);
				}
			}
		}
		

		private void createUsedParameter(OSDiObjectProperties objProperty, ParameterTemplate paramDescription) throws MalformedOSDiModelException {
			final String paramName = objProperty.getValue(name(), true);
			if (paramName != null)
				paramMapping.put(paramDescription, new ParameterWrapper(wrap, paramName, ""));			
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
				paramMapping.put(StandardParameter.DISEASE_PROGRESSION_INITIAL_PROPORTION, new ParameterWrapper(wrap, manifParam, "Initial proportion of " + name()));
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
