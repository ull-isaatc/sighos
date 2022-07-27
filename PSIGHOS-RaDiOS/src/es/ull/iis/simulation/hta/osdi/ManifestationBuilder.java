/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.GregorianCalendar;
import java.util.List;

import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.utils.Constants;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.AcuteManifestation;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.StandardDisease;

/**
 * @author David Prieto González
 * @author Iván Castilla Rodríguez
 */
public interface ManifestationBuilder {

	public static Manifestation getManifestationInstance(SecondOrderParamsRepository secParams, StandardDisease disease, String manifestationName) {
		Manifestation manifestation = null;
		final String type = OSDiNames.DataProperty.HAS_MANIFESTATION_KIND.getValue(manifestationName, OSDiNames.DataPropertyRange.MANIFESTATION_KIND_CHRONIC.getDescription());
		final String description = OSDiNames.DataProperty.HAS_DESCRIPTION.getValue(manifestationName, "");
		if (OSDiNames.DataPropertyRange.MANIFESTATION_KIND_CHRONIC.getDescription().equals(type)) {
			manifestation = new ChronicManifestation(secParams, manifestationName, description,	disease) {
					@Override
					public void registerSecondOrderParameters() {
						createParams(secParams, this);
					}
			};
		}
		else {
			manifestation = new AcuteManifestation(secParams, manifestationName, description, disease) {
					@Override
					public void registerSecondOrderParameters() {
						createParams(secParams, this);
					}
			};			
		}
		return manifestation;
	}

	private static void createParams(SecondOrderParamsRepository secParams, Manifestation manifestation) {
		try {
			createOnsetEndAgeParams(secParams, manifestation);
			createCostParams(secParams, manifestation);
			createUtilityParams(secParams, manifestation);
			createMortalityParams(secParams, manifestation);
			createProbabilityDiagnosisParam(secParams, manifestation);
		} catch(TranspilerException ex) {
			System.err.println(ex.getMessage());
		}
		
	}
	

	private static void createOnsetEndAgeParams(SecondOrderParamsRepository secParams, Manifestation manifestation) throws TranspilerException {
		String strAge = OSDiNames.DataProperty.HAS_ONSET_AGE.getValue(manifestation.name());
		if (strAge != null) {
			ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strAge);
			if (probDistribution == null)
				throw new TranspilerException(OSDiNames.Class.MANIFESTATION, manifestation.name(), OSDiNames.DataProperty.HAS_ONSET_AGE, strAge);
			OtherParamDescriptions.ONSET_AGE.addParameter(secParams, manifestation, "", probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValue());			
		}
		
		strAge = OSDiNames.DataProperty.HAS_END_AGE.getValue(manifestation.name());
		if (strAge != null) {
			ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strAge);
			if (probDistribution == null)
				throw new TranspilerException(OSDiNames.Class.MANIFESTATION, manifestation.name(), OSDiNames.DataProperty.HAS_END_AGE, strAge);
			OtherParamDescriptions.END_AGE.addParameter(secParams, manifestation, "", probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValue());			
		}
		
	}
	
	/**
	 * Creates the costs associated to a specific manifestation by extracting the information from the ontology. If the manifestation is acute, only one cost should be defined; otherwise, up to two costs 
	 * (one-time and annual) may be defined.
	 * @param secParams Repository
	 * @param manifestation A chronic or acute manifestation
	 * @throws TranspilerException When there was a problem parsing the ontology
	 * FIXME: Make a comprehensive error control of cost types for each type of manifestation 
	 */
	public static void createCostParams(SecondOrderParamsRepository secParams, Manifestation manifestation) throws TranspilerException {
		List<String> costs = OSDiNames.Class.COST.getDescendantsOf(manifestation.name());
		boolean acute = Manifestation.Type.ACUTE.equals(manifestation.getType());
		if (acute) {
			if (costs.size() > 1 )
				throw new TranspilerException("Only one cost should be associated to the acute manifestation \"" + manifestation.name() + "\". Instead, " + costs.size() + " found");
		}
		else {
			if (costs.size() > 2 )
				throw new TranspilerException("A maximum of two costs (one-time and annual) should be associated to the chronic manifestation \"" + manifestation.name() + "\". Instead, " + costs.size() + " found");
		}
		for (String costName : costs) {
			// Assumes current year if not specified
			final int year = Integer.parseInt(OSDiNames.DataProperty.HAS_YEAR.getValue(costName, "" + (new GregorianCalendar()).get(GregorianCalendar.YEAR)));
			// Assumes cost to be 0 if not defined
			final String strValue = OSDiNames.DataProperty.HAS_VALUE.getValue(costName, "0.0");
			// Assumes annual behavior if not specified
			final String strTempBehavior = OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getValue(costName, OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription());
			final ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strValue);
			if (probDistribution == null)
				throw new TranspilerException(OSDiNames.Class.MANIFESTATION, manifestation.name(), OSDiNames.DataProperty.HAS_VALUE, strValue);
			CostParamDescriptions param = null;
			if (acute) {
				param = CostParamDescriptions.ONE_TIME_COST;
			}
			else {				
				// If defined to be applied one time
				if (OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ONETIME.getDescription().equals(strTempBehavior)) {
					param = CostParamDescriptions.ONE_TIME_COST;
				}
				else if (OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription().equals(strTempBehavior)) {
					param = CostParamDescriptions.ANNUAL_COST;
				}
			}
			if (param != null) {
				param.addParameter(secParams, manifestation, 
						OSDiNames.DataProperty.HAS_DESCRIPTION.getValue(costName, ""),  
						OSDiNames.getSource(costName), 
						year, probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
			}
			else {
				throw new TranspilerException(OSDiNames.Class.COST, costName, OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR, strTempBehavior);
			}
		}
	}

	/**
	 * Creates the utilities associated to a specific manifestation by extracting the information from the ontology. If the manifestation is acute, only one utility should be defined; otherwise, 
	 * up to two utilities (one-time and annual) may be defined.
	 * @param secParams Repository
	 * @param manifestation A chronic or acute manifestation
	 * @throws TranspilerException When there was a problem parsing the ontology
	 * FIXME: Make a comprehensive error control of utility types for each type of manifestation 
	 */
	public static void createUtilityParams(SecondOrderParamsRepository secParams, Manifestation manifestation) throws TranspilerException {
		List<String> utilities = OSDiNames.Class.UTILITY.getDescendantsOf(manifestation.name());
		boolean acute = Manifestation.Type.ACUTE.equals(manifestation.getType());
		if (acute) {
			if (utilities.size() > 1)
				throw new TranspilerException("Only one (dis)utility should be associated to the acute manifestation \"" + manifestation.name() + "\". Instead, " + utilities.size() + " found");
		}
		else {
			if (utilities.size() > 2)
				throw new TranspilerException("A maximum of two (dis)utilities (one-time and annual) should be associated to the chronic manifestation \"" + manifestation.name() + "\". Instead, " + utilities.size() + " found");
		}
		for (String utilityName : utilities) {
			// Assumes annual behavior if not specified
			final String strTempBehavior = OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getValue(utilityName, OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription());
			// Assumes that it is a utility (not a disutility) if not specified
			final String strType = OSDiNames.DataProperty.HAS_UTILITY_KIND.getValue(utilityName, OSDiNames.DataPropertyRange.UTILITY_KIND_UTILITY.getDescription());
			final boolean isDisutility = OSDiNames.DataPropertyRange.UTILITY_KIND_DISUTILITY.getDescription().equals(strType);
			// Default value for utilities is 1; 0 for disutilities
			final String strValue = OSDiNames.DataProperty.HAS_VALUE.getValue(utilityName, isDisutility ? "0.0" : "1.0");
			final ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strValue);
			if (probDistribution == null)
				throw new TranspilerException(OSDiNames.Class.MANIFESTATION, manifestation.name(), OSDiNames.DataProperty.HAS_VALUE, strValue);
			UtilityParamDescriptions paramUtility = null;
			if (acute) {
				paramUtility = isDisutility ? UtilityParamDescriptions.ONE_TIME_DISUTILITY : UtilityParamDescriptions.ONE_TIME_UTILITY;
			}
			else {
				// If defined to be applied one time
				final boolean isOneTime = OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ONETIME.getDescription().equals(strTempBehavior);
				if (isOneTime) {
					paramUtility = isDisutility ? UtilityParamDescriptions.ONE_TIME_DISUTILITY : UtilityParamDescriptions.ONE_TIME_UTILITY;
				}
				else {
					paramUtility = isDisutility ? UtilityParamDescriptions.DISUTILITY : UtilityParamDescriptions.UTILITY;
				}
			}
			paramUtility.addParameter(secParams, manifestation,	OSDiNames.getSource(utilityName), 
					probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());

		}
	}
	
	/**
	 * 
	 */
	private static void createMortalityParams(SecondOrderParamsRepository secParams, Manifestation manifestation) {
		final String mortalityFactor = OSDiNames.DataProperty.HAS_MORTALITY_FACTOR.getValue(manifestation.name());
		if (mortalityFactor != null) {
			ProbabilityDistribution probabilityDistribution = ValueParser.splitProbabilityDistribution(mortalityFactor);
			if (probabilityDistribution != null) {
				// Chronic manifestations involve a mortality factor (increased risk of death) or a reduction of life expectancy
				if (Manifestation.Type.CHRONIC.equals(manifestation.getType())) { 
					if (probabilityDistribution.getDeterministicValue() > 0) {
						OtherParamDescriptions.INCREASED_MORTALITY_RATE.addParameter(secParams, manifestation, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
					} else {
						// FIXME: Inconsistent value: positive for deterministic, negative for probabilistic. Best to define different data properties for each
						OtherParamDescriptions.LIFE_EXPECTANCY_REDUCTION.addParameter(secParams, manifestation, Constants.CONSTANT_EMPTY_STRING, Math.abs(probabilityDistribution.getDeterministicValue()), probabilityDistribution.getProbabilisticValue());
					}
				// Acute manifestations involve a probability of death
				} else if (Manifestation.Type.ACUTE == manifestation.getType()) {
					ProbabilityParamDescriptions.PROBABILITY_DEATH.addParameter(manifestation.getParamsRepository(), manifestation, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
				}
			}
		}
	}

	/**
	 * 
	 */
	private static void createProbabilityDiagnosisParam(SecondOrderParamsRepository secParams, Manifestation manifestation) {
		final String pDiagnosis = OSDiNames.DataProperty.HAS_PROBABILITY_OF_DIAGNOSIS.getValue(manifestation.name());
		if (pDiagnosis != null) {
			ProbabilityDistribution probabilityDistribution = ValueParser.splitProbabilityDistribution(pDiagnosis);
			ProbabilityParamDescriptions.PROBABILITY_DIAGNOSIS.addParameter(manifestation.getParamsRepository(), manifestation, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
		}
	}
}
