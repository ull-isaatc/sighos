/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.List;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.interventions.ScreeningIntervention;
import es.ull.iis.simulation.hta.osdi.OSDiNames.DataProperty;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * @author Iván Castilla
 *
 */
public interface InterventionBuilder {

	public static Intervention getInterventionInstance(SecondOrderParamsRepository secParams, String interventionName) throws TranspilerException {
		final String description = OSDiNames.DataProperty.HAS_DESCRIPTION.getValue(interventionName, "");
		final String kind = OSDiNames.DataProperty.HAS_INTERVENTION_KIND.getValue(interventionName, OSDiNames.DataPropertyRange.INTERVENTION_KIND_NOSCREENING.getDescription());
		Intervention intervention = null;
		if (OSDiNames.DataPropertyRange.INTERVENTION_KIND_SCREENING.getDescription().equals(kind)) {			

			intervention = new ScreeningIntervention(secParams, interventionName, description) {
				
				@Override
				public void registerSecondOrderParameters() {
					createParamsForScreening(secParams, this);										
				}
				
				@Override
				public double getStartingCost(Patient pat) {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public double getAnnualCost(Patient pat) {
					// TODO Auto-generated method stub
					return 0;
				}
			};
		}
		else {
			intervention = new Intervention(secParams, interventionName, description) {

				@Override
				public void registerSecondOrderParameters() {
					createParams(secParams, this);					
				}

				@Override
				public double getAnnualCost(Patient pat) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public double getStartingCost(Patient pat) {
					// TODO Auto-generated method stub
					return 0;
				}
			};
		}
		return intervention;
	}
	
	private static void createParams(SecondOrderParamsRepository secParams, Intervention intervention) {
		try {
			createModificationParams(secParams, intervention);
		} catch(TranspilerException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	private static void createParamsForScreening(SecondOrderParamsRepository secParams, ScreeningIntervention intervention) {
		try {
			createModificationParams(secParams, intervention);
			createSpecificityAndSensitivity(secParams, intervention);
		} catch(TranspilerException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	private static void createSpecificityAndSensitivity(SecondOrderParamsRepository secParams, ScreeningIntervention intervention) throws TranspilerException {
		String strSensitivity = DataProperty.HAS_SENSITIVITY.getValue(intervention.name(), "1.0");
		final ProbabilityDistribution probSensitivity = ValueParser.splitProbabilityDistribution(strSensitivity);
		if (probSensitivity == null)
			throw new TranspilerException("Error parsing regular expression \"" + strSensitivity + "\" for instance \"" + intervention.name() + "\"");
		secParams.addProbParam(new SecondOrderParam(secParams, intervention.getSensitivityParameterString(false), intervention.getSensitivityParameterString(true), "", 
				probSensitivity.getDeterministicValue(), probSensitivity.getProbabilisticValue()));
		String strSpecificity = DataProperty.HAS_SPECIFICITY.getValue(intervention.name(), "1.0");
		final ProbabilityDistribution probSpecificity = ValueParser.splitProbabilityDistribution(strSpecificity);
		if (probSpecificity == null)
			throw new TranspilerException("Error parsing regular expression \"" + strSpecificity + "\" for instance \"" + intervention.name() + "\"");
		secParams.addProbParam(new SecondOrderParam(secParams, intervention.getSpecificityParameterString(false), intervention.getSpecificityParameterString(true), "", 
				probSpecificity.getDeterministicValue(), probSpecificity.getProbabilisticValue()));
	}
	
	private static void createModificationParams(SecondOrderParamsRepository secParams, Intervention intervention) throws TranspilerException {
		// Collects the modifications associated to the specified intervention
		List<String> modifications = OSDiNames.ObjectProperty.INVOLVES_MODIFICATION.getValues(intervention.name());
		for (String modificationName : modifications) {			
			// Parse the modification kind
			final String strKind = OSDiNames.DataProperty.HAS_MODIFICATION_KIND.getValue(modificationName, OSDiNames.DataPropertyRange.MODIFICATION_KIND_SET.getDescription());
			// I assume that modification kinds are equivalent to those defined in Modificaton.Type
			Modification.Type kind = null;
			try {
				kind = Modification.Type.valueOf(strKind);
			} catch(IllegalArgumentException ex) {
				throw new TranspilerException("Error parsing modification kind. Unexpected value: " + strKind); 				
			}
			// Get the source, if specified
			final String strSource = OSDiNames.DataProperty.HAS_SOURCE.getValue(modificationName, "");
			// Parse the value
			final String strValue = OSDiNames.DataProperty.HAS_VALUE.getValue(modificationName);
			final ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strValue);
			if (probDistribution == null)
				throw new TranspilerException("Error parsing regular expression \"" + strValue + "\" for data property 'has_value' in instance \"" + modificationName + "\"");
			// Parse the property which is modified
			final List<String> strProperties = OSDiNames.DataProperty.HAS_DATA_PROPERTY_MODIFIED.getValues(modificationName);
			for (String strProperty : strProperties) {
				final OSDiNames.DataProperty property = OSDiNames.DATA_PROPERTY_MAP.get(strProperty);
				if (property == null) {
					throw new TranspilerException("Error parsing the name of the modified property. Unexpected value: " + strProperty); 				
				}
				// Process modifications that affect the development
				List<String> modifiedItems = OSDiNames.ObjectProperty.MODIFIES_DEVELOPMENT.getValues(modificationName);
				for (String developmentName : modifiedItems) {
					switch(property) {
					case HAS_LIFE_EXPECTANCY:
						// TODO
					case HAS_MODIFICATION_FOR_ALL_PARAMS:
						// TODO: Probably useless
					default:
						throw new TranspilerException("The data property " + strProperty + " cannot be handled to create a modification of a parameter of a manifestation pathway. Error in instance \"" + modificationName + "\"");
					}
				}
				// Then process modifications that affect to specific manifestations
				modifiedItems = OSDiNames.ObjectProperty.MODIFIES_MANIFESTATION.getValues(modificationName);
				for (String manifestationName : modifiedItems) {
					final Manifestation manif = secParams.getManifestationByName(manifestationName);
					switch(property) {
					case HAS_DURATION:
						// TODO
					case HAS_END_AGE:
						// TODO
					case HAS_FREQUENCY:
						// TODO
					case HAS_MORTALITY_FACTOR:
						// TODO
					case HAS_ONSET_AGE:					
						// TODO
					case HAS_PROBABILITY_OF_DIAGNOSIS:
						// TODO
					default:
						throw new TranspilerException("The data property " + strProperty + " cannot be handled to create a modification of a parameter of a manifestation pathway. Error in instance \"" + modificationName + "\"");
					}
				}
				// And finally process modifications that affect to specific manifestation pathways
				modifiedItems = OSDiNames.ObjectProperty.MODIFIES_MANIFESTATION_PATHWAY.getValues(modificationName);
				for (String manifPathwayName : modifiedItems) {
					final List<String> manifestationNames = OSDiNames.ObjectProperty.IS_PATHWAY_TO.getValues(manifPathwayName);
					switch(property) {
					case HAS_PROBABILITY:
						for (String manifestationName : manifestationNames) {
							final Manifestation manif = secParams.getManifestationByName(manifestationName);
							secParams.addModificationParam(intervention, kind, ManifestationPathwayBuilder.getProbString(manif, manifPathwayName), strSource, probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValue());
						}
						break;
					case HAS_RELATIVE_RISK:
						// TODO
					case HAS_TIME_TO:
						// TODO
					default:
						throw new TranspilerException("The data property " + strProperty + " cannot be handled to create a modification of a parameter of a manifestation pathway. Error in instance \"" + modificationName + "\"");
					}
				}
			}
		}
		// TODO: Check what type of data property is going to be modified and create the appropriate modification
	}
}
