/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.List;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.interventions.ScreeningStrategy;
import es.ull.iis.simulation.hta.osdi.OSDiNames.DataProperty;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.utils.OwlHelper;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * @author Iván Castilla
 *
 */
public interface InterventionBuilder {

	public static Intervention getInterventionInstance(SecondOrderParamsRepository secParams, String interventionName) throws TranspilerException {
		final String description = OwlHelper.getDataPropertyValue(interventionName, OSDiNames.DataProperty.HAS_DESCRIPTION.getDescription(), "");
		final String kind = OwlHelper.getDataPropertyValue(interventionName, OSDiNames.DataProperty.HAS_INTERVENTION_KIND.getDescription(), OSDiNames.DataPropertyRange.INTERVENTION_KIND_NOSCREENING.getDescription());
		Intervention intervention = null;
		if (OSDiNames.DataPropertyRange.INTERVENTION_KIND_SCREENING.getDescription().equals(kind)) {			
			// TODO: Move to createSecondOrderParams when ScreeningIntervention be modified accordingly
			String strSensitivity = OwlHelper.getDataPropertyValue(interventionName, DataProperty.HAS_SENSITIVITY.getDescription(), "1.0");
			final ProbabilityDistribution probSensitivity = ValueParser.splitProbabilityDistribution(strSensitivity);
			if (probSensitivity == null)
				throw new TranspilerException("Error parsing regular expression \"" + strSensitivity + "\" for instance \"" + interventionName + "\"");
			String strSpecificity = OwlHelper.getDataPropertyValue(interventionName, DataProperty.HAS_SPECIFICITY.getDescription(), "1.0");
			final ProbabilityDistribution probSpecificity = ValueParser.splitProbabilityDistribution(strSpecificity);
			if (probSpecificity == null)
				throw new TranspilerException("Error parsing regular expression \"" + strSpecificity + "\" for instance \"" + interventionName + "\"");

			intervention = new ScreeningStrategy(secParams, interventionName, description, probSpecificity.getDeterministicValue(), probSpecificity.getDeterministicValue()) {
				
				@Override
				public void registerSecondOrderParameters() {
					createParams(secParams, this);										
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
	
	private static void createModificationParams(SecondOrderParamsRepository secParams, Intervention intervention) throws TranspilerException {
		// Collects the modifications associated to the specified intervention
		List<String> modifications = OwlHelper.getObjectPropertiesByName(intervention.name(), OSDiNames.ObjectProperty.INVOLVES_MODIFICATION.getDescription());
		for (String modificationName : modifications) {			
			// Parse the modification kind
			final String strKind = OwlHelper.getDataPropertyValue(modificationName, OSDiNames.DataProperty.HAS_MODIFICATION_KIND.getDescription(), OSDiNames.DataPropertyRange.MODIFICATION_KIND_SET.getDescription());
			// I assume that modification kinds are equivalent to those defined in Modificaton.Type
			Modification.Type kind = null;
			try {
				kind = Modification.Type.valueOf(strKind);
			} catch(IllegalArgumentException ex) {
				throw new TranspilerException("Error parsing modification kind. Unexpected value: " + strKind); 				
			}
			// Get the source, if specified
			final String strSource = OwlHelper.getDataPropertyValue(modificationName, OSDiNames.DataProperty.HAS_SOURCE.getDescription(), "");
			// Parse the value
			final String strValue = OwlHelper.getDataPropertyValue(modificationName, OSDiNames.DataProperty.HAS_VALUE.getDescription());
			final ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strValue);
			if (probDistribution == null)
				throw new TranspilerException("Error parsing regular expression \"" + strValue + "\" for data property 'has_value' in instance \"" + modificationName + "\"");
			// Parse the property which is modified
			final List<String> strProperties = OwlHelper.getDataPropertyValues(modificationName, OSDiNames.DataProperty.HAS_DATA_PROPERTY_MODIFIED.getDescription());
			for (String strProperty : strProperties) {
				final OSDiNames.DataProperty property = OSDiNames.DATA_PROPERTY_MAP.get(strProperty);
				if (property == null) {
					throw new TranspilerException("Error parsing the name of the modified property. Unexpected value: " + strProperty); 				
				}
				// Process modifications that affect the development
				List<String> modifiedItems = OwlHelper.getObjectPropertiesByName(modificationName, OSDiNames.ObjectProperty.MODIFIES_DEVELOPMENT.getDescription());
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
				modifiedItems = OwlHelper.getObjectPropertiesByName(modificationName, OSDiNames.ObjectProperty.MODIFIES_MANIFESTATION.getDescription());
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
				modifiedItems = OwlHelper.getObjectPropertiesByName(modificationName, OSDiNames.ObjectProperty.MODIFIES_MANIFESTATION_PATHWAY.getDescription());
				for (String manifPathwayName : modifiedItems) {
					final List<String> manifestationNames = OwlHelper.getObjectPropertiesByName(manifPathwayName, OSDiNames.ObjectProperty.IS_PATHWAY_TO.getDescription());
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
