package es.ull.iis.simulation.hta.osdi;

import java.util.List;

import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * Allows the creation of a {@link StandardDisease} based on the information stored in the ontology
 * @author Iván Castilla Rodríguez
 * @author David Prieto González
 */
public interface DiseaseBuilder {
	public static Disease getDiseaseInstance(OSDiGenericRepository secParams, String diseaseName) throws TranspilerException {
		final OwlHelper helper = secParams.getOwlHelper();		
		
		Disease disease = new Disease(secParams, diseaseName, OSDiNames.DataProperty.HAS_DESCRIPTION.getValue(helper, diseaseName, "")) {

			@Override
			public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
				try {
					createUtilityParam((OSDiGenericRepository) secParams, this);
				} catch (TranspilerException e) {
					System.err.println(e.getMessage());
				}
			}
			
		};
		// Build developments
		List<String> developments = OSDiNames.Class.DEVELOPMENT.getDescendantsOf(helper, diseaseName);
		for (String developmentName : developments) {
			DevelopmentBuilder.getDevelopmentInstance(secParams, developmentName, disease);
		}
		
		// Build manifestations
		List<String> manifestations = OSDiNames.Class.MANIFESTATION.getDescendantsOf(helper, diseaseName);
		for (String manifestationName: manifestations) {
			ManifestationBuilder.getManifestationInstance(secParams, disease, manifestationName);
		}
		// Build manifestation pathways after creating all the manifestations
		for (String manifestationName: manifestations) {
			final Manifestation manif = disease.getManifestation(manifestationName);
			final List<String> pathways = OSDiNames.Class.MANIFESTATION_PATHWAY.getDescendantsOf(helper, manifestationName);
			for (String pathwayName : pathways)
				ManifestationPathwayBuilder.getManifestationPathwayInstance(secParams, manif, pathwayName);
			// Also include exclusions among manifestations
			final List<String> exclusions = OSDiNames.ObjectProperty.EXCLUDES_MANIFESTATION.getValues(helper, manifestationName);
			for (String excludedManif : exclusions) {
				disease.addExclusion(manif, disease.getManifestation(excludedManif));
			}
		}
		
		// TODO: Process costs related to follow-up or treatment strategies 
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
	public static void createUtilityParam(OSDiGenericRepository secParams, Disease disease) throws TranspilerException {
		final OwlHelper helper = secParams.getOwlHelper();		
		List<String> utilities = OSDiNames.ObjectProperty.HAS_UTILITY.getValues(helper, disease.name());
		if (utilities.size() > 1)
			throw new TranspilerException("A maximum of one annual (dis)utility should be associated to the disease \"" + disease.name() + "\". Instead, " + utilities.size() + " found");
		for (String utilityName : utilities) {
			// Assumes annual behavior if not specified
			final String strTempBehavior = OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getValue(helper, utilityName, OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription());
			if (!OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL.getDescription().equals(strTempBehavior))
				throw new TranspilerException("Only annual (dis)utilities should be associated to the disease \"" + disease.name() + "\". Instead, " + strTempBehavior + " found");
			// Assumes that it is a utility (not a disutility) if not specified
			final String strType = OSDiNames.DataProperty.HAS_UTILITY_KIND.getValue(helper, utilityName, OSDiNames.DataPropertyRange.UTILITY_KIND_UTILITY.getDescription());
			final boolean isDisutility = OSDiNames.DataPropertyRange.UTILITY_KIND_DISUTILITY.getDescription().equals(strType);
			// Default value for utilities is 1; 0 for disutilities
			final String strValue = OSDiNames.DataProperty.HAS_VALUE.getValue(helper, utilityName, isDisutility ? "0.0" : "1.0");
			try {
				final ProbabilityDistribution probDistribution = new ProbabilityDistribution(strValue);
				if (isDisutility) {
					UtilityParamDescriptions.DISUTILITY.addParameter(secParams, disease,  
							OSDiNames.getSource(helper, utilityName), 
							probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
				}
				else {
					UtilityParamDescriptions.UTILITY.addParameter(secParams, disease,  
							OSDiNames.getSource(helper, utilityName), 
							probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost());
				}
			} catch(TranspilerException ex) {
				throw new TranspilerException(OSDiNames.Class.UTILITY, utilityName, OSDiNames.DataProperty.HAS_VALUE, strValue);
			}
		}
	}

}
