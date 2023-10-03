/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.builders;

import java.util.Set;

import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
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

/**
 * @author David Prieto González
 * @author Iván Castilla Rodríguez
 */
public interface ManifestationBuilder {

	public static Manifestation getManifestationInstance(OSDiGenericRepository secParams, Disease disease, String manifestationName) {
		final OSDiWrapper wrap = secParams.getOwlWrapper();
		
		Manifestation manifestation = null;
		final String description = OSDiWrapper.DataProperty.HAS_DESCRIPTION.getValue(manifestationName, "");
		final Set<String> manifClazz = wrap.getClassesForIndividual(manifestationName);
		if (manifClazz.contains(OSDiWrapper.Clazz.ACUTE_MANIFESTATION.getShortName())) {
			manifestation = new AcuteManifestation(secParams, manifestationName, description, disease) {
				@Override
				public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
					createParams((OSDiGenericRepository) secParams, this);
				}
			};			
		}
		else {
			manifestation = new ChronicManifestation(secParams, manifestationName, description,	disease) {
					@Override
					public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
						createParams((OSDiGenericRepository) secParams, this);
					}
			};
		}
		return manifestation;
	}

	private static void createParams(OSDiGenericRepository secParams, Manifestation manifestation) {
		try {
			final OSDiWrapper wrap = secParams.getOwlWrapper();
			createOnsetEndAgeParams(wrap, manifestation);
			createCostParams(wrap, manifestation);
			createUtilityParams(wrap, manifestation);
			createMortalityParams(wrap, manifestation);
			addProbabilityParam(wrap, manifestation, OSDiWrapper.ObjectProperty.HAS_PROBABILITY_OF_DIAGNOSIS, ProbabilityParamDescriptions.PROBABILITY_DIAGNOSIS, 0);
		} catch (MalformedOSDiModelException ex) {
			System.err.println(ex.getMessage());
		}
		
	}
	

	private static void createOnsetEndAgeParams(OSDiWrapper wrap, Manifestation manifestation) throws MalformedOSDiModelException {
		final String onsetAge = OSDiWrapper.ObjectProperty.HAS_ONSET_AGE.getValue(manifestation.name(), true);
		if (onsetAge != null) {
			try {
				final ParameterWrapper param = new ParameterWrapper(wrap, onsetAge, 0);
				OtherParamDescriptions.ONSET_AGE.addParameter(manifestation.getRepository(), manifestation, param.getSource(), param.getDeterministicValue(), param.getProbabilisticValue());
			} catch(MalformedOSDiModelException ex) {
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MANIFESTATION, manifestation.name(), OSDiWrapper.ObjectProperty.HAS_ONSET_AGE, "Error parsing manifestation. Caused by ", ex);
			}
		}
		final String endAge = OSDiWrapper.ObjectProperty.HAS_END_AGE.getValue(manifestation.name(), true);
		if (endAge != null) {
			try {
				final ParameterWrapper param = new ParameterWrapper(wrap, endAge, 0);
				OtherParamDescriptions.END_AGE.addParameter(manifestation.getRepository(), manifestation, param.getSource(), param.getDeterministicValue(), param.getProbabilisticValue());
			} catch(MalformedOSDiModelException ex) {
				throw new MalformedOSDiModelException(OSDiWrapper.Clazz.MANIFESTATION, manifestation.name(), OSDiWrapper.ObjectProperty.HAS_END_AGE, "Error parsing manifestation. Caused by ", ex);
			}
		}
	}

	private static OSDiWrapper.TemporalBehavior createCostParam(OSDiWrapper wrap, String costName, OSDiWrapper.TemporalBehavior expectedTemporalBehavior, Manifestation manifestation) throws MalformedOSDiModelException {
		final CostParameterWrapper costParam = new CostParameterWrapper(wrap, costName, 0.0);
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
		final UtilityParameterWrapper utilityParam = new UtilityParameterWrapper(wrap, utilityName); 
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
	
	private static void addProbabilityParam(OSDiWrapper wrap, Manifestation manifestation, OSDiWrapper.ObjectProperty objProperty, ProbabilityParamDescriptions paramDescription, double defaultValue) throws MalformedOSDiModelException {
		final String paramName = objProperty.getValue(manifestation.name(), true);
		if (paramName != null) {
			final ParameterWrapper param = new ParameterWrapper(wrap, paramName, defaultValue);			
			paramDescription.addParameter(manifestation.getRepository(), manifestation, param.getSource(), 
					param.getDeterministicValue(), param.getProbabilisticValue());
		}
	}

	private static void addOtherParam(OSDiWrapper wrap, Manifestation manifestation, OSDiWrapper.ObjectProperty objProperty, OtherParamDescriptions paramDescription, double defaultValue) throws MalformedOSDiModelException {
		final String paramName = objProperty.getValue(manifestation.name(), true);
		if (paramName != null) {
			final ParameterWrapper param = new ParameterWrapper(wrap, paramName, defaultValue);			
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
		addProbabilityParam(wrap, manifestation, OSDiWrapper.ObjectProperty.HAS_PROBABILITY_OF_DEATH, ProbabilityParamDescriptions.PROBABILITY_DEATH, 0);

		// Acute manifestations are assumed not to involve further mortality parameters
		if (Manifestation.Type.CHRONIC.equals(manifestation.getType())) {
			addOtherParam(wrap, manifestation, OSDiWrapper.ObjectProperty.HAS_INCREASED_MORTALITY_RATE, OtherParamDescriptions.INCREASED_MORTALITY_RATE, 1.0);
			addOtherParam(wrap, manifestation, OSDiWrapper.ObjectProperty.HAS_LIFE_EXPECTANCY_REDUCTION, OtherParamDescriptions.LIFE_EXPECTANCY_REDUCTION, 0.0);
		}
	}

}
