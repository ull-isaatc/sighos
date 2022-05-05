package es.ull.iis.simulation.hta.osdi;

import java.util.ArrayList;
import java.util.List;

import org.w3c.xsd.owl2.Ontology;

import es.ull.iis.ontology.radios.json.schema4simulation.Disease;
import es.ull.iis.ontology.radios.json.schema4simulation.PrecedingManifestation;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.osdi.utils.ValueParser;
import es.ull.iis.simulation.hta.osdi.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.AgeBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.ManifestationPathway;
import es.ull.iis.simulation.hta.progression.PathwayCondition;
import es.ull.iis.simulation.hta.progression.PreviousManifestationCondition;
import es.ull.iis.simulation.hta.progression.ProportionBasedTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.StandardDisease;
import es.ull.iis.simulation.hta.progression.TimeToEventCalculator;
import es.ull.iis.simulation.hta.radios.RadiosRangeAgeMatrixRRCalculator;
import es.ull.iis.simulation.hta.radios.transforms.ValueTransform;
import es.ull.iis.simulation.hta.radios.transforms.XmlTransform;

public class DiseaseBuilder {
	public static StandardDisease getDiseaseInstance(Ontology ontology, SecondOrderParamsRepository secParams, String diseaseName) {
		
		StandardDisease disease = new StandardDisease(secParams, diseaseName, OwlHelper.getDataPropertyValue(diseaseName, OSDiNames.DataProperty.HAS_DESCRIPTION.getDescription(), "")) {

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
		// Build manifestations
		List<String> manifestations = OwlHelper.getChildsByClassName(diseaseName, OSDiNames.Class.MANIFESTATION.getDescription());
		for (String manifestationName: manifestations) {
			ManifestationBuilder.getManifestationInstance(ontology, secParams, disease, manifestationName);
		}
		// Build manifestation pathways after creating all the manifestations
		for (String manifestationName: manifestations) {
			List<String> pathways = OwlHelper.getChildsByClassName(manifestationName, OSDiNames.Class.MANIFESTATION_PATHWAY.getDescription());
			for (String pathwayName : pathways)
				ManifestationPathwayBuilder.getManifestationPathwayInstance(ontology, secParams, disease.getManifestation(manifestationName), pathwayName);
		}
		
		disease.setBirthPrevalence(OwlHelper.getDataPropertyValue(diseaseName, Constants.DATAPROPERTY_BIRTH_PREVALENCE));

		disease.setScreeningStrategies(null);
		disease.setClinicalDiagnosisStrategies(null);
		disease.setInterventions(null);
		disease.setFollowUpStrategies(null);				
		disease.setTreatmentStrategies(null);

		disease.setScreeningStrategies(ScreeningBuilder.getScreeningStrategies(diseaseName));
		disease.setClinicalDiagnosisStrategies(ClinicalDiagnosisBuilder.getClinicalDiagnosisStrategies(diseaseName));
		disease.setInterventions(InterventionBuilder.getInterventions(diseaseName));
		disease.setFollowUpStrategies(FollowUpBuilder.getFollowUpStrategies(diseaseName));				
		disease.setTreatmentStrategies(TreatmentBuilder.getTreatmentStrategies(diseaseName));

		return disease;
	}

	private static void registerPathwayParameters() {
		List<String> manifestations = OwlHelper.getChildsByClassName(diseaseName, OSDiNames.Class.MANIFESTATION.getDescription());
		for (String manifestationName: manifestations) {
			List<String> pathways = OwlHelper.getChildsByClassName(manifestationName, OSDiNames.Class.MANIFESTATION_PATHWAY.getDescription());
			for (String pathwayName : pathways) {
				ManifestationPathwayBuilder.getManifestationPathwayInstance(ontology, secParams, disease.getManifestation(manifestationName), pathwayName);
				final String strPManif = OwlHelper.getDataPropertyValue(pathwayName, OSDiNames.DataProperty.HAS_PROBABILITY.getDescription());
				if (strPManif != null) {
					ProbabilityDistribution probabilityDistribution = ValueParser.splitProbabilityDistribution(strPManif);
					if (probabilityDistribution != null) {
						secParams.addProbParam(manifestation, Constants.CONSTANT_EMPTY_STRING, 
								probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
						tte = new ProportionBasedTimeToEventCalculator(SecondOrderParamsRepository.getProbString(manifestation), secParams, manifestation);
					} else {
						Object[][] datatableMatrix = ValueTransform.rangeDatatableToMatrix(XmlTransform.getDataTable(manifestationProbability), secParams);
						tte = new AgeBasedTimeToEventCalculator(datatableMatrix, manifestation, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix));
					}
					new ManifestationPathway(secParams, manifestation, tte);
				}

				if (CollectionUtils.notIsEmpty(manifJSON.getPrecedingManifestations())) {
					for (PrecedingManifestation precedingManifestation : manifJSON.getPrecedingManifestations()) {
						// Looks for the preceding manifestation
						Manifestation precManif = null;
						for (Manifestation mm : mappings.keySet()) {
							if (mm.getDescription().equals(precedingManifestation.getName())) {
								precManif = mm;
							}
						}
						String transitionProbability = precedingManifestation.getProbability();
						if (transitionProbability != null) {
							ProbabilityDistribution probabilityDistributionForTransition = ValueTransform.splitProbabilityDistribution(transitionProbability);
							TimeToEventCalculator tte;
							if (probabilityDistributionForTransition != null) {
								secParams.addProbParam(precManif, manifestation, Constants.CONSTANT_EMPTY_STRING, probabilityDistributionForTransition.getDeterministicValue(), probabilityDistributionForTransition.getProbabilisticValueInitializedForProbability());
								tte = new ProportionBasedTimeToEventCalculator(SecondOrderParamsRepository.getProbString(precManif, manifestation), secParams, manifestation);
							} else {
								Object[][] datatableMatrix = ValueTransform.rangeDatatableToMatrix(XmlTransform.getDataTable(transitionProbability), secParams);
								tte = new AgeBasedTimeToEventCalculator(datatableMatrix, manifestation, new RadiosRangeAgeMatrixRRCalculator(datatableMatrix));
							}
							final PathwayCondition cond = new PreviousManifestationCondition(precManif);
							new ManifestationPathway(secParams, manifestation, cond, tte);
						}
					}
				}
			}
		}
		
		
	}
}
