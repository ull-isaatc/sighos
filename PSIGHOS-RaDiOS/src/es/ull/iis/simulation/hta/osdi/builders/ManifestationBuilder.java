/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.builders;

import java.util.Set;

import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
import es.ull.iis.simulation.hta.osdi.builders.ManifestationPathwayBuilder.OSDiManifestationPathway;
import es.ull.iis.simulation.hta.osdi.exceptions.MalformedOSDiModelException;
import es.ull.iis.simulation.hta.osdi.wrappers.CostParameterWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ParameterWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.UtilityParameterWrapper;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.AcuteManifestation;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
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
			return new OSDiAcuteManifestation(secParams, manifestationName, description, disease);			
		return new OSDiChronicManifestation(secParams, manifestationName, description,	disease, populationName);
	}

	private static void createOnsetEndAgeParams(OSDiWrapper wrap, Manifestation manifestation) throws MalformedOSDiModelException {
		final String onsetAge = OSDiWrapper.ObjectProperty.HAS_ONSET_AGE.getValue(manifestation.name(), true);
		if (onsetAge != null) {
			try {
				final ParameterWrapper param = new ParameterWrapper(wrap, onsetAge, "Onset age for manifestation " + manifestation.name());
				OtherParamDescriptions.ONSET_AGE.addParameter(manifestation.getRepository(), manifestation, param.getSource(), param.getDeterministicValue(), param.getProbabilisticValue());
			} catch(MalformedOSDiModelException ex) {
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MANIFESTATION, manifestation.name(), OSDiWrapper.ObjectProperty.HAS_ONSET_AGE, "Error parsing manifestation. Caused by ", ex);
			}
		}
		final String endAge = OSDiWrapper.ObjectProperty.HAS_END_AGE.getValue(manifestation.name(), true);
		if (endAge != null) {
			try {
				final ParameterWrapper param = new ParameterWrapper(wrap, endAge, "End age for manifestation " + manifestation.name());
				OtherParamDescriptions.END_AGE.addParameter(manifestation.getRepository(), manifestation, param.getSource(), param.getDeterministicValue(), param.getProbabilisticValue());
			} catch(MalformedOSDiModelException ex) {
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MANIFESTATION, manifestation.name(), OSDiWrapper.ObjectProperty.HAS_END_AGE, "Error parsing manifestation. Caused by ", ex);
			}
		}
	}

	private static OSDiWrapper.TemporalBehavior createCostParam(OSDiWrapper wrap, String costName, OSDiWrapper.TemporalBehavior expectedTemporalBehavior, Manifestation manifestation) throws MalformedOSDiModelException {
		final CostParameterWrapper costParam = new CostParameterWrapper(wrap, costName, "Cost for manifestation " + manifestation.name());
		final OSDiWrapper.TemporalBehavior tempBehavior = costParam.getTemporalBehavior();

		// Assuming ANNUAL COSTS by default
		final CostParamDescriptions paramDescription = (OSDiWrapper.TemporalBehavior.ONETIME.equals(tempBehavior)) ? CostParamDescriptions.ONE_TIME_COST : CostParamDescriptions.ANNUAL_COST;
		// Checking coherence of temporal behavior of the cost and the type of manifestation
		if (!OSDiWrapper.TemporalBehavior.NOT_SPECIFIED.equals(expectedTemporalBehavior) && !expectedTemporalBehavior.equals(tempBehavior)) {
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MANIFESTATION, manifestation.name(), OSDiWrapper.ObjectProperty.HAS_COST, "Expected " 
					+ expectedTemporalBehavior.name() + " temporal behavior and obtained " + tempBehavior + " in " + costName);
		}
		paramDescription.addParameter(manifestation.getRepository(), manifestation, costParam.getDescription(), costParam.getSource(), costParam.getYear(),
				costParam.getDeterministicValue(), costParam.getProbabilisticValue());
		return tempBehavior;
	}
	
	/**
	 * Creates the costs associated to a specific manifestation by extracting the information from the ontology. If the manifestation is acute, only one cost should be defined; otherwise, up to two costs 
	 * (one-time and annual) may be defined.
	 * @param secParams Repository
	 * @param manifestation A chronic or acute manifestation
	 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
	 * FIXME: Make a comprehensive error control of cost types for each type of manifestation 
	 */
	public static void createCostParams(OSDiWrapper wrap, Manifestation manifestation) throws MalformedOSDiModelException {
		final Set<String> costs = OSDiWrapper.ObjectProperty.HAS_COST.getValues(manifestation.name(), true);
		// Checking coherence of number of costs and the type of manifestation		
		boolean acute = Manifestation.Type.ACUTE.equals(manifestation.getType());
		if (acute) {
			if (costs.size() > 1)
				wrap.printWarning(manifestation.name(), OSDiWrapper.ObjectProperty.HAS_COST, "Found more than one cost for an acute manifestation. Using " + costs.toArray()[0]);
			// TODO: Make a smarter use of the excess of costs and use only those which meets the conditions, i.e. select one annual cost from all the defined ones
			createCostParam(wrap, (String)costs.toArray()[0], OSDiWrapper.TemporalBehavior.ONETIME, manifestation);
		}
		else {
			if (costs.size() > 2)
				wrap.printWarning(manifestation.name(), OSDiWrapper.ObjectProperty.HAS_COST, "Found more than two costs (one-time and annual) for a chronic manifestation. Using " + costs.toArray()[0] + " and "  + costs.toArray()[1]);
			// TODO: Make a smarter use of the excess of costs and use only those which meets the conditions, i.e. select one annual and one "one-time" cost from all the defined ones
			OSDiWrapper.TemporalBehavior tmpBehavior = createCostParam(wrap, (String)costs.toArray()[0], OSDiWrapper.TemporalBehavior.NOT_SPECIFIED, manifestation);
			tmpBehavior = OSDiWrapper.TemporalBehavior.ANNUAL.equals(tmpBehavior) ? OSDiWrapper.TemporalBehavior.ONETIME : OSDiWrapper.TemporalBehavior.ANNUAL; 
			createCostParam(wrap, (String)costs.toArray()[1], tmpBehavior, manifestation);
		}
	}

	private static OSDiWrapper.TemporalBehavior createUtilityParam(OSDiWrapper wrap, String utilityName, OSDiWrapper.TemporalBehavior expectedTemporalBehavior, Manifestation manifestation) throws MalformedOSDiModelException {
		final UtilityParameterWrapper utilityParam = new UtilityParameterWrapper(wrap, utilityName, "Utility for manifestation " + manifestation.name()); 
		final OSDiWrapper.TemporalBehavior tempBehavior = utilityParam.getTemporalBehavior();

		final boolean isDisutility = OSDiWrapper.UtilityType.DISUTILITY.equals(utilityParam.getType());

		if (!OSDiWrapper.TemporalBehavior.NOT_SPECIFIED.equals(expectedTemporalBehavior) && !expectedTemporalBehavior.equals(tempBehavior)) {
			throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MANIFESTATION, manifestation.name(), OSDiWrapper.ObjectProperty.HAS_UTILITY, "Expected " 
					+ expectedTemporalBehavior.name() + " temporal behavior and obtained " + tempBehavior + " in " + utilityName);
		}
		final UtilityParamDescriptions utilityDesc;
		if (OSDiWrapper.TemporalBehavior.ONETIME.equals(expectedTemporalBehavior)) {
			utilityDesc = isDisutility ? UtilityParamDescriptions.ONE_TIME_DISUTILITY : UtilityParamDescriptions.ONE_TIME_UTILITY;
		}
		else {
			utilityDesc = isDisutility ? UtilityParamDescriptions.DISUTILITY : UtilityParamDescriptions.UTILITY;
		}
		utilityDesc.addParameter(manifestation.getRepository(), manifestation, utilityParam.getSource(),
					utilityParam.getDeterministicValue(), utilityParam.getProbabilisticValue());		
		return tempBehavior;
	}

	/**
	 * Creates the utilities associated to a specific manifestation by extracting the information from the ontology. If the manifestation is acute, only one utility should be defined; otherwise, 
	 * up to two utilities (one-time and annual) may be defined.
	 * @param secParams Repository
	 * @param manifestation A chronic or acute manifestation
	 * @throws MalformedOSDiModelException When there was a problem parsing the ontology
	 * FIXME: Make a comprehensive error control of utility types for each type of manifestation 
	 */
	public static void createUtilityParams(OSDiWrapper wrap, Manifestation manifestation) throws MalformedOSDiModelException {
		final Set<String> utilities = OSDiWrapper.ObjectProperty.HAS_UTILITY.getValues(manifestation.name(), true);
		
		boolean acute = Manifestation.Type.ACUTE.equals(manifestation.getType());
		if (acute) {
			if (utilities.size() > 1)
				wrap.printWarning(manifestation.name(), OSDiWrapper.ObjectProperty.HAS_UTILITY, "Found more than one (dis)utility for an acute manifestation. Using " + utilities.toArray()[0]);
			// TODO: Make a smarter use of the excess of costs and use only those which meets the conditions, i.e. select one annual cost from all the defined ones
			createUtilityParam(wrap, (String)utilities.toArray()[0], OSDiWrapper.TemporalBehavior.ONETIME, manifestation);
		}
		else {
			if (utilities.size() > 2)
				wrap.printWarning(manifestation.name(), OSDiWrapper.ObjectProperty.HAS_UTILITY, "Found more than two (dis)utilities (one-time and annual) for a chronic manifestation. Using " + utilities.toArray()[0] + " and "  + utilities.toArray()[1]);
			// TODO: Make a smarter use of the excess of costs and use only those which meets the conditions, i.e. select one annual and one "one-time" cost from all the defined ones
			OSDiWrapper.TemporalBehavior tmpBehavior = createUtilityParam(wrap, (String)utilities.toArray()[0], OSDiWrapper.TemporalBehavior.NOT_SPECIFIED, manifestation);
			tmpBehavior = OSDiWrapper.TemporalBehavior.ANNUAL.equals(tmpBehavior) ? OSDiWrapper.TemporalBehavior.ONETIME : OSDiWrapper.TemporalBehavior.ANNUAL; 
			createUtilityParam(wrap, (String)utilities.toArray()[1], tmpBehavior, manifestation);
		}
	}
	
	private static void addProbabilityParam(OSDiWrapper wrap, Manifestation manifestation, OSDiWrapper.ObjectProperty objProperty, ProbabilityParamDescriptions paramDescription) throws MalformedOSDiModelException {
		final String paramName = objProperty.getValue(manifestation.name(), true);
		if (paramName != null) {
			final ParameterWrapper param = new ParameterWrapper(wrap, paramName, "");			
			paramDescription.addParameter(manifestation.getRepository(), manifestation, param.getSource(), 
					param.getDeterministicValue(), param.getProbabilisticValue());
		}
	}

	private static void addOtherParam(OSDiWrapper wrap, Manifestation manifestation, OSDiWrapper.ObjectProperty objProperty, OtherParamDescriptions paramDescription) throws MalformedOSDiModelException {
		final String paramName = objProperty.getValue(manifestation.name(), true);
		if (paramName != null) {
			final ParameterWrapper param = new ParameterWrapper(wrap, paramName, "");			
			paramDescription.addParameter(manifestation.getRepository(), manifestation, param.getSource(), 
					param.getDeterministicValue(), param.getProbabilisticValue());
		}
	}
	
	/**
	 * 
	 */
	private static void createMortalityParams(OSDiWrapper wrap, Manifestation manifestation) throws MalformedOSDiModelException {
		// Chronic manifestations may have increased mortality rates, reductions of life expectancy
		// FIXME: Currently, we are accepting even a probability of death. Conceptually this could only happen when the chronic manifestation is preceded by an acute manifestation 
		// (actually, the acute manifestation would be the one with such probability). We are doing so to simplify modeling, but it is inaccurate 
		addProbabilityParam(wrap, manifestation, OSDiWrapper.ObjectProperty.HAS_PROBABILITY_OF_DEATH, ProbabilityParamDescriptions.PROBABILITY_DEATH);

		// Acute manifestations are assumed not to involve further mortality parameters
		if (Manifestation.Type.CHRONIC.equals(manifestation.getType())) {
			addOtherParam(wrap, manifestation, OSDiWrapper.ObjectProperty.HAS_INCREASED_MORTALITY_RATE, OtherParamDescriptions.INCREASED_MORTALITY_RATE);
			addOtherParam(wrap, manifestation, OSDiWrapper.ObjectProperty.HAS_LIFE_EXPECTANCY_REDUCTION, OtherParamDescriptions.LIFE_EXPECTANCY_REDUCTION);
		}
	}
	
	static private void createIncidenceParam(Manifestation manifestation, ParameterWrapper incidenceWrapper) throws MalformedOSDiModelException {
		if (incidenceWrapper != null) {
			final OSDiGenericRepository OSDiParams = ((OSDiGenericRepository)manifestation.getRepository()); 
			final TimeToEventCalculator tte = ManifestationPathwayBuilder.createTimeToEventCalculator(OSDiParams, manifestation, "", incidenceWrapper);
			OSDiManifestationPathway pathway = new OSDiManifestationPathway(OSDiParams, manifestation, new TrueCondition<Patient>(), tte, "", incidenceWrapper);
		}

	}
	
	static class OSDiAcuteManifestation extends AcuteManifestation {
		final private ParameterWrapper incidenceWrapper; 

		public OSDiAcuteManifestation(OSDiGenericRepository secParams, String name, String description,
				Disease disease) throws MalformedOSDiModelException {
			super(secParams, name, description, disease);
			final OSDiWrapper wrap = secParams.getOwlWrapper();

			// Gets the manifestation parameters related to the working model
			final Set<String> manifestationParams = OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION.getValues(name, true);
			if (manifestationParams.size() > 0) {
				String manifParam = (String)manifestationParams.toArray()[0];
				if (manifestationParams.size() > 1) {
					wrap.printWarning(manifParam, OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, "Manifestations should define a single risk characterization. Using " + manifParam);
				}
				incidenceWrapper = new ParameterWrapper(wrap, manifParam, "Developing " + name);;
			}
			else {
				incidenceWrapper = null;
			}
		}

		@Override
		public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
			final OSDiGenericRepository OSDiParams = (OSDiGenericRepository) secParams; 
			try {
				final OSDiWrapper wrap = OSDiParams.getOwlWrapper();
				createOnsetEndAgeParams(wrap, this);
				createCostParams(wrap, this);
				createUtilityParams(wrap, this);
				createMortalityParams(wrap, this);
				addProbabilityParam(wrap, this, OSDiWrapper.ObjectProperty.HAS_PROBABILITY_OF_DIAGNOSIS, ProbabilityParamDescriptions.PROBABILITY_DIAGNOSIS);
				createIncidenceParam(this, incidenceWrapper);
			} catch (MalformedOSDiModelException ex) {
				System.err.println(ex.getMessage());
			}
		}
		
	}
	
	static class OSDiChronicManifestation extends ChronicManifestation {
		final private ParameterWrapper incidenceWrapper; 
		final private ParameterWrapper initialProportionWrapper; 

		public OSDiChronicManifestation(OSDiGenericRepository secParams, String name, String description,
				Disease disease, String populationName) throws MalformedOSDiModelException {
			super(secParams, name, description, disease);
			final OSDiWrapper wrap = secParams.getOwlWrapper();

			// Gets the manifestation parameters related to the working model
			Set<String> manifestationParams = OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION.getValues(name, true);
			if (manifestationParams.size() > 0) {
				String manifParam = (String)manifestationParams.toArray()[0];
				if (manifestationParams.size() > 1) {
					wrap.printWarning(manifParam, OSDiWrapper.ObjectProperty.HAS_RISK_CHARACTERIZATION, "Manifestations should define a single risk characterization. Using " + manifParam);
				}
				incidenceWrapper = new ParameterWrapper(wrap, manifParam, "Developing " + name);;
			}
			else {
				incidenceWrapper = null;
			}

			// Gets the manifestation parameters related to the working model
			manifestationParams = OSDiWrapper.ObjectProperty.HAS_INITIAL_PROPORTION.getValues(name, true);
			if (manifestationParams.size() > 0) {
				String manifParam = (String)manifestationParams.toArray()[0];
				if (manifestationParams.size() > 1) {
					wrap.printWarning(manifParam, OSDiWrapper.ObjectProperty.HAS_INITIAL_PROPORTION, "Manifestations should define a single initial proportion. Using " + manifParam);
				}
				if (!OSDiWrapper.ObjectProperty.IS_PARAMETER_OF_POPULATION.getValues(manifParam, true).contains(populationName))
					throw new MalformedOSDiModelException(OSDiWrapper.Clazz.PARAMETER, manifParam, OSDiWrapper.ObjectProperty.IS_PARAMETER_OF_POPULATION, 
							"Parameters characterizing initial proportions must be related to the population: " + populationName);
				initialProportionWrapper = new ParameterWrapper(wrap, manifParam, "Initial proportion of " + name);
			}
			else {
				initialProportionWrapper = null;
			}
		}

		@Override
		public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
			final OSDiGenericRepository OSDiParams = (OSDiGenericRepository) secParams; 
			try {
				final OSDiWrapper wrap = OSDiParams.getOwlWrapper();
				createOnsetEndAgeParams(wrap, this);
				createCostParams(wrap, this);
				createUtilityParams(wrap, this);
				createMortalityParams(wrap, this);
				addProbabilityParam(wrap, this, OSDiWrapper.ObjectProperty.HAS_PROBABILITY_OF_DIAGNOSIS, ProbabilityParamDescriptions.PROBABILITY_DIAGNOSIS);
				createIncidenceParam(this, incidenceWrapper);
				if (initialProportionWrapper != null)
					ProbabilityParamDescriptions.INITIAL_PROPORTION.addParameter(getRepository(), this, initialProportionWrapper.getSource(), 
							initialProportionWrapper.getDeterministicValue(), initialProportionWrapper.getProbabilisticValue());
			} catch (MalformedOSDiModelException ex) {
				System.err.println(ex.getMessage());
			}
		}
		
	}
}
