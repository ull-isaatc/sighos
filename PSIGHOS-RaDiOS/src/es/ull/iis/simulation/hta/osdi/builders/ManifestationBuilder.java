/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.builders;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
import es.ull.iis.simulation.hta.osdi.builders.ManifestationPathwayBuilder.OSDiManifestationPathway;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.wrappers.CostParameterWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ExpressionWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ParameterWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.UtilityParameterWrapper;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.TimeToEventCalculator;

/**
 * @author David Prieto González
 * @author Iván Castilla Rodríguez
 * TODO: Create pathways for manifestation pathways directly defined in the manifestation
 */
public interface ManifestationBuilder {

	public static Manifestation getManifestationInstance(OSDiGenericRepository secParams, String manifestationName, Disease disease, String populationName) throws MalformedOSDiModelException {
		final OSDiWrapper wrap = secParams.getOwlWrapper();
		
		final String description = OSDiWrapper.DataProperty.HAS_DESCRIPTION.getValue(manifestationName, "");
		final Set<String> manifClazz = wrap.getClassesForIndividual(manifestationName);
		if (manifClazz.contains(OSDiWrapper.Clazz.ACUTE_MANIFESTATION.getShortName()))
			return new OSDiManifestation(secParams, manifestationName, description, disease, Manifestation.Type.ACUTE, populationName);			
		return new OSDiManifestation(secParams, manifestationName, description,	disease, Manifestation.Type.CHRONIC, populationName);
	}

	/**
	 * 
	 */
	
	static class OSDiManifestation extends Manifestation {
		final private Map<OtherParamDescriptions, ParameterWrapper> otherParams;
		final private Map<ProbabilityParamDescriptions, ParameterWrapper> probParams;
		final private Map<CostParamDescriptions, CostParameterWrapper> costParams;
		final private Map<UtilityParamDescriptions, UtilityParameterWrapper> utilityParams;
		final private OSDiWrapper wrap;
		
		public OSDiManifestation(OSDiGenericRepository secParams, String name, String description,
				Disease disease, Manifestation.Type type, String populationName) throws MalformedOSDiModelException {
			super(secParams, name, description, disease, type);
			otherParams = new TreeMap<>();
			probParams = new TreeMap<>();
			costParams = new TreeMap<>();
			utilityParams = new TreeMap<>();
			wrap = secParams.getOwlWrapper();

			addOnsetEndAgeParams();
			addCostParams();
			addUtilityParams();
			addMortalityParams();
			addRiskCharacterization();
			addProbabilityParam(OSDiWrapper.ObjectProperty.HAS_PROBABILITY_OF_DIAGNOSIS, ProbabilityParamDescriptions.PROBABILITY_DIAGNOSIS);
			if (Manifestation.Type.CHRONIC.equals(type)) {
				addInitialProportionParam(populationName);
			}

		}

		private void addProbabilityParam(OSDiWrapper.ObjectProperty objProperty, ProbabilityParamDescriptions paramDescription) throws MalformedOSDiModelException {
			final String paramName = objProperty.getValue(name(), true);
			if (paramName != null)
				probParams.put(paramDescription, new ParameterWrapper(wrap, paramName, "", EnumSet.of(ExpressionWrapper.SupportedType.CONSTANT)));			
		}
		
		private void addOtherParam(OSDiWrapper.ObjectProperty objProperty, OtherParamDescriptions paramDescription) throws MalformedOSDiModelException {
			final String paramName = objProperty.getValue(name(), true);
			if (paramName != null)
				otherParams.put(paramDescription, new ParameterWrapper(wrap, paramName, "", EnumSet.of(ExpressionWrapper.SupportedType.CONSTANT)));			
		}
		
		private void addMortalityParams() throws MalformedOSDiModelException {
			// Chronic manifestations may have increased mortality rates, reductions of life expectancy
			// FIXME: Currently, we are accepting even a probability of death. Conceptually this could only happen when the chronic manifestation is preceded by an acute manifestation 
			// (actually, the acute manifestation would be the one with such probability). We are doing so to simplify modeling, but it is inaccurate
			addProbabilityParam(OSDiWrapper.ObjectProperty.HAS_PROBABILITY_OF_DEATH, ProbabilityParamDescriptions.PROBABILITY_DEATH);

			// Acute manifestations are assumed not to involve further mortality parameters
			if (Manifestation.Type.CHRONIC.equals(getType())) {
				addOtherParam(OSDiWrapper.ObjectProperty.HAS_INCREASED_MORTALITY_RATE, OtherParamDescriptions.INCREASED_MORTALITY_RATE);
				addOtherParam(OSDiWrapper.ObjectProperty.HAS_LIFE_EXPECTANCY_REDUCTION, OtherParamDescriptions.LIFE_EXPECTANCY_REDUCTION);
			}
		}
		
		private void addInitialProportionParam(String populationName) throws MalformedOSDiModelException {
			final Set<String> manifestationParams = OSDiWrapper.ObjectProperty.HAS_INITIAL_PROPORTION.getValues(name(), true);
			if (manifestationParams.size() > 0) {
				final String manifParam = (String)manifestationParams.toArray()[0];
				if (manifestationParams.size() > 1) {
					wrap.printWarning(manifParam, OSDiWrapper.ObjectProperty.HAS_INITIAL_PROPORTION, "Manifestations should define a single initial proportion. Using " + manifParam);
				}
				if (!OSDiWrapper.ObjectProperty.IS_PARAMETER_OF_POPULATION.getValues(manifParam, true).contains(populationName))
					throw new MalformedOSDiModelException(OSDiWrapper.Clazz.PARAMETER, manifParam, OSDiWrapper.ObjectProperty.IS_PARAMETER_OF_POPULATION, 
							"Parameters characterizing initial proportions must be related to the population: " + populationName);
				probParams.put(ProbabilityParamDescriptions.INITIAL_PROPORTION, new ParameterWrapper(wrap, manifParam, "Initial proportion of " + name(), EnumSet.of(ExpressionWrapper.SupportedType.CONSTANT)));
			}
		}
		
		private void addOnsetEndAgeParams() throws MalformedOSDiModelException {
			final String onsetAge = OSDiWrapper.ObjectProperty.HAS_ONSET_AGE.getValue(name(), true);
			if (onsetAge != null) {
				try {
					otherParams.put(OtherParamDescriptions.ONSET_AGE, new ParameterWrapper(wrap, onsetAge, "Onset age for manifestation " + name(), EnumSet.of(ExpressionWrapper.SupportedType.CONSTANT)));
				} catch(MalformedOSDiModelException ex) {
					throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MANIFESTATION, name(), OSDiWrapper.ObjectProperty.HAS_ONSET_AGE, "Error parsing manifestation. Caused by ", ex);
				}
			}
			final String endAge = OSDiWrapper.ObjectProperty.HAS_END_AGE.getValue(name(), true);
			if (endAge != null) {
				try {
					otherParams.put(OtherParamDescriptions.END_AGE, new ParameterWrapper(wrap, endAge, "End age for manifestation " + name(), EnumSet.of(ExpressionWrapper.SupportedType.CONSTANT)));
				} catch(MalformedOSDiModelException ex) {
					throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MANIFESTATION, name(), OSDiWrapper.ObjectProperty.HAS_END_AGE, "Error parsing manifestation. Caused by ", ex);
				}
			}
		}

		private void addRiskCharacterization() throws MalformedOSDiModelException {
			// Gets the manifestation parameters related to the working model
			Set<String> manifestationParams = OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION.getValues(name(), true);
			if (manifestationParams.size() > 0) {
				String manifParam = (String)manifestationParams.toArray()[0];
				if (manifestationParams.size() > 1) {
					wrap.printWarning(manifParam, OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, "Manifestations should define a single risk characterization. Using " + manifParam);
				}
				final ParameterWrapper incidenceWrapper = new ParameterWrapper(wrap, manifParam, "Developing " + name(), EnumSet.of(ExpressionWrapper.SupportedType.CONSTANT));
				OSDiGenericRepository OSDiParams = (OSDiGenericRepository) getRepository();
				final TimeToEventCalculator tte = ManifestationPathwayBuilder.createTimeToEventCalculator(OSDiParams, this, "", incidenceWrapper);
				final OSDiManifestationPathway pathway = new OSDiManifestationPathway(OSDiParams, this, new TrueCondition<Patient>(), tte, "", incidenceWrapper);
			}
		}

		private OSDiWrapper.TemporalBehavior addCostParam(String costName, OSDiWrapper.TemporalBehavior expectedTemporalBehavior) throws MalformedOSDiModelException {
			final CostParameterWrapper costParam = new CostParameterWrapper(wrap, costName, "Cost for manifestation " + name(), EnumSet.of(ExpressionWrapper.SupportedType.CONSTANT));
			final OSDiWrapper.TemporalBehavior tempBehavior = costParam.getTemporalBehavior();

			// Assuming ANNUAL COSTS by default
			final CostParamDescriptions paramDescription = (OSDiWrapper.TemporalBehavior.ONETIME.equals(tempBehavior)) ? CostParamDescriptions.ONE_TIME_COST : CostParamDescriptions.ANNUAL_COST;
			// Checking coherence of temporal behavior of the cost and the type of manifestation
			if (!OSDiWrapper.TemporalBehavior.NOT_SPECIFIED.equals(expectedTemporalBehavior) && !expectedTemporalBehavior.equals(tempBehavior)) {
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MANIFESTATION, name(), OSDiWrapper.ObjectProperty.HAS_COST, "Expected " 
						+ expectedTemporalBehavior.name() + " temporal behavior and obtained " + tempBehavior + " in " + costName);
			}
			costParams.put(paramDescription, costParam);
			return tempBehavior;
		}
		
		/**
		 * Creates the costs associated to a specific manifestation by extracting the information from the ontology. If the manifestation is acute, only one cost should be defined; otherwise, up to two costs 
		 * (one-time and annual) may be defined.
		 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
		 * FIXME: Make a comprehensive error control of cost types for each type of manifestation 
		 */
		public void addCostParams() throws MalformedOSDiModelException {
			final Set<String> costs = OSDiWrapper.ObjectProperty.HAS_COST.getValues(name(), true);
			if (costs.size() == 0) {
				wrap.printWarning(name(), OSDiWrapper.ObjectProperty.HAS_COST, "No cost defined for a manifestation. Using 0 as a default value");				
			}
			else {
				// Checking coherence of number of costs and the type of manifestation		
				boolean acute = Manifestation.Type.ACUTE.equals(getType());
				if (acute) {
					if (costs.size() > 1)
						wrap.printWarning(name(), OSDiWrapper.ObjectProperty.HAS_COST, "Found more than one cost for an acute manifestation. Using " + costs.toArray()[0]);
					// TODO: Make a smarter use of the excess of costs and use only those which meets the conditions, i.e. select one annual cost from all the defined ones
					addCostParam((String)costs.toArray()[0], OSDiWrapper.TemporalBehavior.ONETIME);
				}
				else {
					if (costs.size() > 2)
						wrap.printWarning(name(), OSDiWrapper.ObjectProperty.HAS_COST, "Found more than two costs (one-time and annual) for a chronic manifestation. Using " + costs.toArray()[0] + " and "  + costs.toArray()[1]);
					// TODO: Make a smarter use of the excess of costs and use only those which meets the conditions, i.e. select one annual and one "one-time" cost from all the defined ones
					OSDiWrapper.TemporalBehavior tmpBehavior = addCostParam((String)costs.toArray()[0], OSDiWrapper.TemporalBehavior.NOT_SPECIFIED);
					tmpBehavior = OSDiWrapper.TemporalBehavior.ANNUAL.equals(tmpBehavior) ? OSDiWrapper.TemporalBehavior.ONETIME : OSDiWrapper.TemporalBehavior.ANNUAL; 
					addCostParam((String)costs.toArray()[1], tmpBehavior);
				}
			}
		}
		
		private OSDiWrapper.TemporalBehavior addUtilityParam(String utilityName, OSDiWrapper.TemporalBehavior expectedTemporalBehavior) throws MalformedOSDiModelException {
			final UtilityParameterWrapper utilityParam = new UtilityParameterWrapper(wrap, utilityName, "Utility for manifestation " + name(),EnumSet.of(ExpressionWrapper.SupportedType.CONSTANT)); 
			final OSDiWrapper.TemporalBehavior tempBehavior = utilityParam.getTemporalBehavior();

			final boolean isDisutility = OSDiWrapper.UtilityType.DISUTILITY.equals(utilityParam.getType());

			if (!OSDiWrapper.TemporalBehavior.NOT_SPECIFIED.equals(expectedTemporalBehavior) && !expectedTemporalBehavior.equals(tempBehavior)) {
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MANIFESTATION, name(), OSDiWrapper.ObjectProperty.HAS_UTILITY, "Expected " 
						+ expectedTemporalBehavior.name() + " temporal behavior and obtained " + tempBehavior + " in " + utilityName);
			}
			final UtilityParamDescriptions utilityDesc;
			if (OSDiWrapper.TemporalBehavior.ONETIME.equals(expectedTemporalBehavior)) {
				utilityDesc = isDisutility ? UtilityParamDescriptions.ONE_TIME_DISUTILITY : UtilityParamDescriptions.ONE_TIME_UTILITY;
			}
			else {
				utilityDesc = isDisutility ? UtilityParamDescriptions.DISUTILITY : UtilityParamDescriptions.UTILITY;
			}
			utilityParams.put(utilityDesc, utilityParam);
			return tempBehavior;
		}

		/**
		 * Creates the utilities associated to a specific manifestation by extracting the information from the ontology. If the manifestation is acute, only one utility should be defined; otherwise, 
		 * up to two utilities (one-time and annual) may be defined.
		 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
		 * FIXME: Make a comprehensive error control of utility types for each type of manifestation 
		 */
		public void addUtilityParams() throws MalformedOSDiModelException {
			final Set<String> utilities = OSDiWrapper.ObjectProperty.HAS_UTILITY.getValues(name(), true);
			if (utilities.size() == 0) {
				wrap.printWarning(name(), OSDiWrapper.ObjectProperty.HAS_UTILITY, "No utility defined for a manifestation. Creating a 0 disutility as a default value");				
			}
			else {
				boolean acute = Manifestation.Type.ACUTE.equals(getType());
				if (acute) {
					if (utilities.size() > 1)
						wrap.printWarning(name(), OSDiWrapper.ObjectProperty.HAS_UTILITY, "Found more than one (dis)utility for an acute manifestation. Using " + utilities.toArray()[0]);
					// TODO: Make a smarter use of the excess of costs and use only those which meets the conditions, i.e. select one annual cost from all the defined ones
					addUtilityParam((String)utilities.toArray()[0], OSDiWrapper.TemporalBehavior.ONETIME);
				}
				else {
					if (utilities.size() > 2)
						wrap.printWarning(name(), OSDiWrapper.ObjectProperty.HAS_UTILITY, "Found more than two (dis)utilities (one-time and annual) for a chronic manifestation. Using " + utilities.toArray()[0] + " and "  + utilities.toArray()[1]);
					// TODO: Make a smarter use of the excess of costs and use only those which meets the conditions, i.e. select one annual and one "one-time" cost from all the defined ones
					OSDiWrapper.TemporalBehavior tmpBehavior = addUtilityParam((String)utilities.toArray()[0], OSDiWrapper.TemporalBehavior.NOT_SPECIFIED);
					if (utilities.size() > 1) {
						tmpBehavior = OSDiWrapper.TemporalBehavior.ANNUAL.equals(tmpBehavior) ? OSDiWrapper.TemporalBehavior.ONETIME : OSDiWrapper.TemporalBehavior.ANNUAL; 
						addUtilityParam((String)utilities.toArray()[1], tmpBehavior);
					}
				}
			}
		}
		
		@Override
		public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
			for (ProbabilityParamDescriptions desc : probParams.keySet()) {
				final ParameterWrapper probParam = probParams.get(desc);					
				desc.addParameter(secParams, this, probParam.getSource(), probParam.getDeterministicValue(), probParam.getProbabilisticValue());
			}
			for (OtherParamDescriptions desc : otherParams.keySet()) {
				final ParameterWrapper otherParam = otherParams.get(desc);					
				desc.addParameter(secParams, this, otherParam.getSource(), otherParam.getDeterministicValue(), otherParam.getProbabilisticValue());
			}
			if (costParams.size() == 0) {
				CostParamDescriptions.ANNUAL_COST.addParameter(secParams, this, "Not defined. Assumption", secParams.getStudyYear(), 0.0);
			}
			else {
				for (CostParamDescriptions desc : costParams.keySet()) {
					final CostParameterWrapper costParam = costParams.get(desc);					
					desc.addParameter(secParams, this, costParam.getSource(), costParam.getYear(), costParam.getDeterministicValue(), costParam.getProbabilisticValue());
				}				
			}
			if (utilityParams.size() == 0) {
				UtilityParamDescriptions.DISUTILITY.addParameter(secParams, this, "Not defined. Assumption", 0.0);
			}
			else {
				for (UtilityParamDescriptions desc : utilityParams.keySet()) {
					final UtilityParameterWrapper utilityParam = utilityParams.get(desc);					
					desc.addParameter(secParams, this, utilityParam.getSource(), utilityParam.getDeterministicValue(), utilityParam.getProbabilisticValue());
				}
			}
		}
		
	}
}
