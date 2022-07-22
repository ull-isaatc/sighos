package es.ull.iis.simulation.hta.osdi;

import java.util.List;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.utils.Constants;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.StandardDisease;
import es.ull.iis.simulation.hta.radios.utils.CostUtils;
import es.ull.iis.simulation.hta.radios.wrappers.Matrix;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * Allows the creation of a {@link StandardDisease} based on the information stored in the ontology
 * @author Iv�n Castilla Rodr�guez
 * @author David Prieto Gonz�lez
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

			@Override
			public double getDiagnosisCost(Patient pat) {
				return 0;
			}

			@Override
			public double getAnnualTreatmentAndFollowUpCosts(Patient pat, double initAge, double endAge) {
				return 0;
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
			// Assumes a default calculation method specified in Constants if not specified
			final String strCalcMethod = OSDiNames.DataProperty.HAS_CALCULATION_METHOD.getValue(utilityName, Constants.UTILITY_DEFAULT_CALCULATION_METHOD);
			final ProbabilityDistribution probDistribution = ValueParser.splitProbabilityDistribution(strValue);
			if (probDistribution == null)
				throw new TranspilerException("Error parsing regular expression \"" + strValue + "\" for instance \"" + disease.name() + "\"");
			secParams.addUtilityParam(disease, 
					OSDiNames.DataProperty.HAS_DESCRIPTION.getValue(utilityName, "Utility for " + disease.name() + " calculated using " + strCalcMethod),  
					OSDiNames.getSource(utilityName), 
					probDistribution.getDeterministicValue(), probDistribution.getProbabilisticValueInitializedForCost(), isDisutility);			
		}
	}

}
