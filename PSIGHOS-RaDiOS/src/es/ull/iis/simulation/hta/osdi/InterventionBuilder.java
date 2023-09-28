/**
 * 
 */
package es.ull.iis.simulation.hta.osdi;

import java.util.List;
import java.util.Set;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.interventions.ScreeningIntervention;
import es.ull.iis.simulation.hta.osdi.OSDiNames.DataProperty;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.wrappers.OSDiWrapper;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * @author Iván Castilla
 *
 */
public interface InterventionBuilder {
	public static String DO_NOTHING = "DO_NOTHING";

	public static Intervention getInterventionInstance(OSDiGenericRepository secParams, String interventionName) throws TranspilerException {
		final OSDiWrapper wrap = secParams.getOwlWrapper();
		if (DO_NOTHING.equals(interventionName))
			return new DoNothingIntervention(secParams);
		final String description = OSDiWrapper.DataProperty.HAS_DESCRIPTION.getValue(wrap, interventionName, "");
		final Set<String> superclasses = wrap.getClassesForIndividual(interventionName);
		Intervention intervention = null;
		// TODO: Populate different methods for different interventions
		if (superclasses.contains(OSDiWrapper.InterventionType.SCREENING.getClazz().getShortName())) {			

			intervention = new ScreeningIntervention(secParams, interventionName, description) {
				
				@Override
				public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
					createParamsForScreening((OSDiGenericRepository) secParams, this);										
				}
				
				@Override
				public double getStartingCost(Patient pat, double time, Discount discountRate) {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public double[] getAnnualizedCostWithinPeriod(Patient pat, double initT, double endT,
						Discount discountRate) {
					// TODO Auto-generated method stub
					return discountRate.applyAnnualDiscount(0.0, initT, endT);
				}

				@Override
				public double getTreatmentAndFollowUpCosts(Patient pat, double initT, double endT,
						Discount discountRate) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT,
						Discount discountRate) {
					// TODO Auto-generated method stub
					return discountRate.applyAnnualDiscount(0.0, initT, endT);
				}
			};
		}
		else {
			intervention = new Intervention(secParams, interventionName, description) {

				@Override
				public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
					createParams((OSDiGenericRepository) secParams, this);					
				}

				@Override
				public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public double getStartingCost(Patient pat, double time, Discount discountRate) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public double[] getAnnualizedCostWithinPeriod(Patient pat, double initT, double endT,
						Discount discountRate) {
					// TODO Auto-generated method stub
					return discountRate.applyAnnualDiscount(0.0, initT, endT);
				}

				@Override
				public double getTreatmentAndFollowUpCosts(Patient pat, double initT, double endT,
						Discount discountRate) {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT,
						Discount discountRate) {
					// TODO Auto-generated method stub
					return discountRate.applyAnnualDiscount(0.0, initT, endT);
				}
			};
		}
		return intervention;
	}
	
	private static void createParams(OSDiGenericRepository secParams, Intervention intervention) {
		try {
			createModificationParams(secParams, intervention);
		} catch(TranspilerException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	private static void createParamsForScreening(OSDiGenericRepository secParams, ScreeningIntervention intervention) {
		try {
			createModificationParams(secParams, intervention);
			createSpecificityAndSensitivity(secParams, intervention);
		} catch(TranspilerException ex) {
			System.err.println(ex.getMessage());
		}
	}
	
	private static void createSpecificityAndSensitivity(OSDiGenericRepository secParams, ScreeningIntervention intervention) throws TranspilerException {
		final OSDiWrapper wrap = secParams.getOwlWrapper();
		String strSensitivity = DataProperty.HAS_SENSITIVITY.getValue(helper, intervention.name(), "1.0");
		try {
			final ProbabilityDistribution probSensitivity = new ProbabilityDistribution(strSensitivity);
			ProbabilityParamDescriptions.SENSITIVITY.addParameter(secParams, intervention, "",  
					probSensitivity.getDeterministicValue(), probSensitivity.getProbabilisticValue());
		} catch(TranspilerException ex) {
			throw new TranspilerException(OSDiNames.Class.INTERVENTION, intervention.name(), OSDiNames.DataProperty.HAS_SENSITIVITY, strSensitivity, ex);
		}

		String strSpecificity = DataProperty.HAS_SPECIFICITY.getValue(helper, intervention.name(), "1.0");
		try {
			final ProbabilityDistribution probSpecificity = new ProbabilityDistribution(strSpecificity);
			ProbabilityParamDescriptions.SPECIFICTY.addParameter(secParams, intervention, "",  
					probSpecificity.getDeterministicValue(), probSpecificity.getProbabilisticValue());
		} catch(TranspilerException ex) {
			throw new TranspilerException(OSDiNames.Class.INTERVENTION, intervention.name(), OSDiNames.DataProperty.HAS_SPECIFICITY, strSpecificity, ex);
		}
	}
	
	private static void createModificationParams(OSDiGenericRepository secParams, Intervention intervention) throws TranspilerException {
		final OwlHelper helper = secParams.getOwlHelper();		
		// Collects the modifications associated to the specified intervention
		List<String> modifications = OSDiNames.ObjectProperty.INVOLVES_MODIFICATION.getValues(helper, intervention.name());
		for (String modificationName : modifications) {			
			// Parse the modification kind
			final String strKind = OSDiNames.DataProperty.HAS_MODIFICATION_KIND.getValue(helper, modificationName, OSDiNames.DataPropertyRange.MODIFICATION_KIND_SET.getDescription());
			// I assume that modification kinds are equivalent to those defined in Modificaton.Type
			Modification.Type kind = null;
			try {
				kind = Modification.Type.valueOf(strKind);
			} catch(IllegalArgumentException ex) {
				throw new TranspilerException(OSDiNames.Class.MODIFICATION, modificationName, OSDiNames.DataProperty.HAS_MODIFICATION_KIND, strKind);
			}
			final String strDescription = OSDiNames.DataProperty.HAS_DESCRIPTION.getValue(helper, modificationName, "");			
			// Get the source, if specified
			final String strSource = OSDiNames.DataProperty.HAS_SOURCE.getValue(helper, modificationName, "");
			// Parse the value
			final String strValue = OSDiNames.DataProperty.HAS_VALUE.getValue(helper, modificationName);
			ProbabilityDistribution probDistribution;
			try {
				probDistribution = new ProbabilityDistribution(strValue);
			} catch(TranspilerException ex) {
				throw new TranspilerException(OSDiNames.Class.MODIFICATION, modificationName, OSDiNames.DataProperty.HAS_VALUE, strValue, ex);
			}
			
			// If it is modifying an individual parameter, the processing is different
			List<String> modifiedItems = OSDiNames.ObjectProperty.MODIFIES_INDIVIDUAL_PARAMETER.getValues(helper, modificationName);
			for (String indParamName : modifiedItems) {
				final String name = OSDiNames.DataProperty.HAS_NAME.getValue(helper, indParamName);
				intervention.addClinicalParameterModification(name, new Modification(secParams, kind, SecondOrderParamsRepository.getModificationString(intervention, name), strDescription,
						strSource, probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValue()));
			}
			
			// Parse the property which is modified
			final List<String> strProperties = OSDiNames.DataProperty.HAS_DATA_PROPERTY_MODIFIED.getValues(helper, modificationName);
			for (String strProperty : strProperties) {
				final OSDiNames.DataProperty property = OSDiNames.DATA_PROPERTY_MAP.get(strProperty);
				if (property == null) {
					throw new TranspilerException(OSDiNames.Class.MODIFICATION, modificationName, OSDiNames.DataProperty.HAS_DATA_PROPERTY_MODIFIED, strProperty);
				}
				// Process modifications that affect the development
				modifiedItems = OSDiNames.ObjectProperty.MODIFIES_DEVELOPMENT.getValues(helper, modificationName);
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
				modifiedItems = OSDiNames.ObjectProperty.MODIFIES_MANIFESTATION.getValues(helper, modificationName);
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
				modifiedItems = OSDiNames.ObjectProperty.MODIFIES_MANIFESTATION_PATHWAY.getValues(helper, modificationName);
				for (String manifPathwayName : modifiedItems) {
					final List<String> manifestationNames = OSDiNames.ObjectProperty.IS_PATHWAY_TO.getValues(helper, manifPathwayName);
					switch(property) {
					case HAS_PROBABILITY:
						for (String manifestationName : manifestationNames) {
							final Manifestation manif = secParams.getManifestationByName(manifestationName);
							secParams.addModificationParam(intervention, kind, ProbabilityParamDescriptions.PROBABILITY.getParameterName(ManifestationPathwayBuilder.getProbString(manif, manifPathwayName)), strSource, probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValue());
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
