package es.ull.iis.simulation.hta.osdi;

import java.util.List;

import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.StandardDisease;

/**
 * Allows the creation of a {@link StandardDisease} based on the information stored in the ontology
 * @author Iván Castilla Rodríguez
 * @author David Prieto González
 */
public interface DiseaseBuilder {
	public static StandardDisease getDiseaseInstance(SecondOrderParamsRepository secParams, String diseaseName) throws TranspilerException {
		
		StandardDisease disease = new StandardDisease(secParams, diseaseName, OSDiNames.DataProperty.HAS_DESCRIPTION.getValue(diseaseName, "")) {

			@Override
			public void registerSecondOrderParameters() {
				try {
					createUtilityParam(secParams, this);
				} catch (TranspilerException e) {
					System.err.println(e.getMessage());
				}
			}
			
		};
		// Build developments
		List<String> developments = OSDiNames.Class.DEVELOPMENT.getDescendantsOf(diseaseName);
		for (String developmentName : developments) {
			DevelopmentBuilder.getDevelopmentInstance(developmentName, disease);
		}
		
		// Build manifestations
		List<String> manifestations = OSDiNames.Class.MANIFESTATION.getDescendantsOf(diseaseName);
		for (String manifestationName: manifestations) {
			ManifestationBuilder.getManifestationInstance(secParams, disease, manifestationName);
		}
		// Build manifestation pathways after creating all the manifestations
		for (String manifestationName: manifestations) {
			List<String> pathways = OSDiNames.Class.MANIFESTATION_PATHWAY.getDescendantsOf(manifestationName);
			for (String pathwayName : pathways)
				ManifestationPathwayBuilder.getManifestationPathwayInstance(secParams, disease.getManifestation(manifestationName), pathwayName);
		}
		
//		disease.setScreeningStrategies(ScreeningBuilder.getScreeningStrategies(diseaseName));
//		disease.setClinicalDiagnosisStrategies(ClinicalDiagnosisBuilder.getClinicalDiagnosisStrategies(diseaseName));
//		disease.setInterventions(InterventionBuilder.getInterventions(diseaseName));
//		disease.setFollowUpStrategies(FollowUpBuilder.getFollowUpStrategies(diseaseName));				
//		disease.setTreatmentStrategies(TreatmentBuilder.getTreatmentStrategies(diseaseName));

		return disease;
	}

	/**
	 * Creates the utilities associated to a specific disease by extracting the information from the ontology. Only one annual (dis)utility should be defined.
	 * @param secParams Repository
	 * @param disease A disease
	 * @throws TranspilerException When there was a problem parsing the ontology
	 */
	public static void createUtilityParam(SecondOrderParamsRepository secParams, StandardDisease disease) throws TranspilerException {
		List<String> utilities = OSDiNames.ObjectProperty.HAS_UTILITY.getValues(disease.name());
		if (utilities.size() > 1)
			throw new TranspilerException("A maximum of one annual (dis)utility should be associated to the disease \"" + disease.name() + "\". Instead, " + utilities.size() + " found");
		for (String utilityName : utilities) {
			// Assumes annual behavior if not specified
			final String strTempBehavior = OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getValue(utilityName, OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription());
			if (!OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription().equals(strTempBehavior))
				throw new TranspilerException("Only annual (dis)utilities should be associated to the disease \"" + disease.name() + "\". Instead, " + strTempBehavior + " found");
			// Assumes that it is a utility (not a disutility) if not specified
			final String strType = OSDiNames.DataProperty.HAS_UTILITY_KIND.getValue(utilityName, OSDiNames.DataPropertyRange.UTILITY_KIND_UTILITY.getDescription());
			final boolean isDisutility = OSDiNames.DataPropertyRange.UTILITY_KIND_DISUTILITY.getDescription().equals(strType);
			// Default value for utilities is 1; 0 for disutilities
			final String strValue = OSDiNames.DataProperty.HAS_VALUE.getValue(utilityName, isDisutility ? "0.0" : "1.0");
			final ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strValue);
			if (probDistribution == null)
				throw new TranspilerException(OSDiNames.Class.UTILITY, utilityName, OSDiNames.DataProperty.HAS_VALUE, strValue);
			if (isDisutility) {
				UtilityParamDescriptions.DISUTILITY.addParameter(secParams, disease,  
						OSDiNames.getSource(utilityName), 
						probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
			}
			else {
				UtilityParamDescriptions.UTILITY.addParameter(secParams, disease,  
						OSDiNames.getSource(utilityName), 
						probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
			}
		}
	}

}
