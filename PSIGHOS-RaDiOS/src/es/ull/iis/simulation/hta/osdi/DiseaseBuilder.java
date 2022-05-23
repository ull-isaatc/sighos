package es.ull.iis.simulation.hta.osdi;

import java.util.List;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.StandardDisease;
import es.ull.iis.simulation.hta.radios.utils.CostUtils;
import es.ull.iis.simulation.hta.radios.wrappers.Matrix;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

public interface DiseaseBuilder {
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


	private static void calculateDiseaseStrategyCost(SecondOrderParamsRepository secParams, String paramName, String paramDescription, Matrix costs, String costType) {
		Object[] calculatedCost = null;
		if (Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE.equalsIgnoreCase(costType)) {
			calculatedCost = CostUtils.calculateOnetimeCostFromMatrix(costs);
		} else if (Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE.equalsIgnoreCase(costType)) {
			calculatedCost = CostUtils.calculateAnnualCostFromMatrix(costs);
		}
		RandomVariate distribution = RandomVariateFactory.getInstance("ConstantVariate", (Double) calculatedCost[1]);
		if (calculatedCost[2] != null) {
			distribution = (RandomVariate) calculatedCost[2];
		}
		secParams.addCostParam(new SecondOrderCostParam(secParams, paramName, paramDescription, "", (Integer) calculatedCost[0], (Double) calculatedCost[1], distribution));
	}

}
