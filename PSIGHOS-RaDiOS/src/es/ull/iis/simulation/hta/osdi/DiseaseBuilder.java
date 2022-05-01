package es.ull.iis.simulation.hta.osdi;

import org.w3c.xsd.owl2.Ontology;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.Disease;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.StandardDisease;

public class DiseaseBuilder {
	public static StandardDisease getDiseaseInstance(Ontology ontology, SecondOrderParamsRepository secParams, String diseaseName) {
		
		StandardDisease disease = new StandardDisease(secParams, diseaseName,OwlHelper.getDataPropertyValue(diseaseName, OSDiNames.DataProperty.HAS_DESCRIPTION.getName())) {

			@Override
			public void registerSecondOrderParameters() {
				// TODO Auto-generated method stub
				
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
		disease.setBirthPrevalence(OwlHelper.getDataPropertyValue(diseaseName, Constants.DATAPROPERTY_BIRTH_PREVALENCE));

		disease.setScreeningStrategies(null);
		disease.setClinicalDiagnosisStrategies(null);
		disease.setDevelopments(null);
		disease.setInterventions(null);
		disease.setFollowUpStrategies(null);				
		disease.setTreatmentStrategies(null);

		disease.setScreeningStrategies(ScreeningBuilder.getScreeningStrategies(diseaseName));
		disease.setClinicalDiagnosisStrategies(ClinicalDiagnosisBuilder.getClinicalDiagnosisStrategies(diseaseName));
		disease.setDevelopments(DevelopmentBuilder.getDevelopments(diseaseName));
		disease.setInterventions(InterventionBuilder.getInterventions(diseaseName));
		disease.setFollowUpStrategies(FollowUpBuilder.getFollowUpStrategies(diseaseName));				
		disease.setTreatmentStrategies(TreatmentBuilder.getTreatmentStrategies(diseaseName));

		return disease;
	}
}
