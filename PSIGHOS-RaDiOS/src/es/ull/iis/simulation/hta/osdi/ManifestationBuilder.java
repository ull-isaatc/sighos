/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.GregorianCalendar;
import java.util.List;

import org.w3c.xsd.owl2.Ontology;

import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.AcuteManifestation;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.StandardDisease;

/**
 * @author David Prieto González
 * @author Iván Castilla Rodríguez
 */
public class ManifestationBuilder {

	/**
	 * 
	 */
	private ManifestationBuilder() {
	}

	public static Manifestation getManifestationInstance(Ontology ontology, SecondOrderParamsRepository secParams, StandardDisease disease, String manifestationName) {
		Manifestation manifestation = null;
		final String type = OwlHelper.getDataPropertyValue(manifestationName, OSDiNames.DataProperty.HAS_MANIFESTATION_KIND.getDescription(), OSDiNames.DataPropertyRange.KIND_MANIFESTATION_CHRONIC.getDescription());
		final Double onsetAge = ValueParser.toDoubleValue(OwlHelper.getDataPropertyValue(manifestationName, OSDiNames.DataProperty.HAS_ONSET_AGE.getDescription(), "0.0"));
		final Double endAge = ValueParser.toDoubleValue(OwlHelper.getDataPropertyValue(manifestationName, OSDiNames.DataProperty.HAS_END_AGE.getDescription(), "" + BasicConfigParams.DEF_MAX_AGE));
		final String description = OwlHelper.getDataPropertyValue(manifestationName, OSDiNames.DataProperty.HAS_DESCRIPTION.getDescription(), "");
		if (OSDiNames.DataPropertyRange.KIND_MANIFESTATION_CHRONIC.getDescription().equals(type)) {
			manifestation = new ChronicManifestation(secParams, manifestationName, description,	disease, onsetAge, endAge) {
					@Override
					public void registerSecondOrderParameters() {
						createParams(secParams, this);
					}
			};
		}
		else {
			manifestation = new AcuteManifestation(secParams, manifestationName, description, disease, onsetAge, endAge) {
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
		createCostParams(secParams, manifestation);
		createUtilityParams(secParams, manifestation);
		createMortalityParams(secParams, manifestation);
		createProbabilityDiagnosisParam(secParams, manifestation);
		} catch(TranspilerException ex) {
			System.err.println(ex.getStackTrace());
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
		List<String> costs = OwlHelper.getChildsByClassName(manifestation.name(), OSDiNames.Class.COST.getDescription());
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
			final int year = Integer.parseInt(OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_YEAR.getDescription(), "" + (new GregorianCalendar()).get(GregorianCalendar.YEAR)));
			// Assumes cost to be 0 if not defined
			final String strValue = OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_VALUE.getDescription(), "0.0");
			// Assumes annual behavior if not specified
			final String strTempBehavior = OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getDescription(), OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL_VALUE.getDescription());
			final ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strValue);
			if (probDistribution == null)
				throw new TranspilerException("Error parsing regular expression \"" + strValue + "\" for instance \"" + manifestation.name() + "\"");
			if (acute) {
					secParams.addCostParam((AcuteManifestation)manifestation, 
							OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_DESCRIPTION.getDescription(), ""),  
							OSDiNames.getSource(costName), 
							year, probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
			}
			else {
				// If defined to be applied one time
				final boolean isOneTime = OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ONETIME_VALUE.getDescription().equals(strTempBehavior);
				secParams.addCostParam((ChronicManifestation)manifestation, 
						OwlHelper.getDataPropertyValue(costName, OSDiNames.DataProperty.HAS_DESCRIPTION.getDescription(), ""),  
						OSDiNames.getSource(costName), 
						year, probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost(), isOneTime);			
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
		List<String> utilities = OwlHelper.getChildsByClassName(manifestation.name(), OSDiNames.Class.UTILITY.getDescription());
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
			// Assumes cost to be 0 if not defined
			final String strValue = OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_VALUE.getDescription(), "0.0");
			// Assumes annual behavior if not specified
			final String strTempBehavior = OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getDescription(), OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL_VALUE.getDescription());
			// Assumes that it is a utility (not a disutility) if not specified
			final String strType = OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_UTILITY_KIND.getDescription(), OSDiNames.DataPropertyRange.KIND_UTILITY_UTILITY.getDescription());
			final boolean isDisutility = OSDiNames.DataPropertyRange.KIND_UTILITY_DISUTILITY.getDescription().equals(strType);
			// Assumes a default calculation method specified in Constants if not specified
			final String strCalcMethod = OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_CALCULATION_METHOD.getDescription(), Constants.UTILITY_DEFAULT_CALCULATION_METHOD);
			final ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strValue);
			if (probDistribution == null)
				throw new TranspilerException("Error parsing regular expression \"" + strValue + "\" for instance \"" + manifestation.name() + "\"");
			if (acute) {
					secParams.addUtilityParam((AcuteManifestation)manifestation, 
							OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_DESCRIPTION.getDescription(), "Utility for " + manifestation.name() + " calculated using " + strCalcMethod),  
							OSDiNames.getSource(utilityName), 
							probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost(), isDisutility);
			}
			else {
				// If defined to be applied one time
				final boolean isOneTime = OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ONETIME_VALUE.getDescription().equals(strTempBehavior);
				secParams.addUtilityParam((ChronicManifestation)manifestation, 
						OwlHelper.getDataPropertyValue(utilityName, OSDiNames.DataProperty.HAS_DESCRIPTION.getDescription(), "Utility for " + manifestation.name() + " calculated using " + strCalcMethod),  
						OSDiNames.getSource(utilityName), 
						probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost(), isDisutility, isOneTime);			
			}
		}
	}
	
	/**
	 * 
	 */
	private static void createMortalityParams(SecondOrderParamsRepository secParams, Manifestation manifestation) {
		final String mortalityFactor = OwlHelper.getDataPropertyValue(manifestation.name(), OSDiNames.DataProperty.HAS_MORTALITY_FACTOR.getDescription());
		if (mortalityFactor != null) {
			ProbabilityDistribution probabilityDistribution = ValueParser.splitProbabilityDistribution(mortalityFactor);
			if (probabilityDistribution != null) {
				// Chronic manifestations involve a mortality factor (increased risk of death) or a reduction of life expectancy
				if (Manifestation.Type.CHRONIC.equals(manifestation.getType())) { 
					if (probabilityDistribution.getDeterministicValue() > 0) {
						manifestation.getParamsRepository().addIMRParam(manifestation, "Mortality factor for " + manifestation, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValue());
					} else {
						manifestation.getParamsRepository().addLERParam(manifestation, "Life expectancy reduction for " + manifestation, Constants.CONSTANT_EMPTY_STRING, Math.abs(probabilityDistribution.getDeterministicValue()), probabilityDistribution.getProbabilisticValue());
					}
				// Acute manifestations involve a probability of death
				} else if (Manifestation.Type.ACUTE == manifestation.getType()) {
					manifestation.getParamsRepository().addDeathProbParam(manifestation, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
				}
			}
		}
	}

	/**
	 * 
	 */
	private static void createProbabilityDiagnosisParam(SecondOrderParamsRepository secParams, Manifestation manifestation) {
		final String pDiagnosis = OwlHelper.getDataPropertyValue(manifestation.name(), OSDiNames.DataProperty.HAS_PROBABILITY_OF_DIAGNOSIS.getDescription());
		if (pDiagnosis != null) {
			ProbabilityDistribution probabilityDistribution = ValueParser.splitProbabilityDistribution(pDiagnosis);
			manifestation.getParamsRepository().addDiagnosisProbParam(manifestation, Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
		}
	}
}
