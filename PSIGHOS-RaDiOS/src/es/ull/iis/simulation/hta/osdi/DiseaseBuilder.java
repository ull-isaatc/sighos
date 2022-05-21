package es.ull.iis.simulation.hta.osdi;

import java.util.List;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.StandardDisease;

public class DiseaseBuilder {
	public static StandardDisease getDiseaseInstance(SecondOrderParamsRepository secParams, String diseaseName) {
		
		StandardDisease disease = new StandardDisease(secParams, diseaseName, OwlHelper.getDataPropertyValue(diseaseName, OSDiNames.DataProperty.HAS_DESCRIPTION.getDescription(), "")) {

			@Override
			public void registerSecondOrderParameters() {
				
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
		// Build manifestations
		List<String> manifestations = OwlHelper.getChildsByClassName(diseaseName, OSDiNames.Class.MANIFESTATION.getDescription());
		for (String manifestationName: manifestations) {
			ManifestationBuilder.getManifestationInstance(secParams, disease, manifestationName);
		}
		// Build manifestation pathways after creating all the manifestations
		for (String manifestationName: manifestations) {
			List<String> pathways = OwlHelper.getChildsByClassName(manifestationName, OSDiNames.Class.MANIFESTATION_PATHWAY.getDescription());
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
}
