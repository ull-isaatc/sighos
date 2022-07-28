/**
 * 
 */
package es.ull.iis.simulation.hta.radios;

import java.util.TreeMap;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.ClinicalDiagnosisStrategy;
import es.ull.iis.ontology.radios.json.schema4simulation.FollowUpStrategy;
import es.ull.iis.ontology.radios.json.schema4simulation.TreatmentStrategy;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.StandardDisease;
import es.ull.iis.simulation.hta.radios.exceptions.TransformException;
import es.ull.iis.simulation.hta.radios.utils.CostUtils;
import es.ull.iis.simulation.hta.radios.wrappers.Matrix;
import javax.xml.bind.JAXBException;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author masbe
 *
 */
public class DiseaseFactory {
	private static TreeMap<Disease, es.ull.iis.ontology.radios.json.schema4simulation.Disease> mappings = new TreeMap<>();

	private DiseaseFactory() {		
	}
	
	public static Disease getDiseaseInstance(SecondOrderParamsRepository secParams, es.ull.iis.ontology.radios.json.schema4simulation.Disease diseaseJSON, Integer timeHorizon) throws TransformException, JAXBException {
		Disease disease = new Disease(secParams, diseaseJSON.getName(), Constants.CONSTANT_EMPTY_STRING) {
			
			@Override
			public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
				final es.ull.iis.ontology.radios.json.schema4simulation.Disease diseaseJSON = mappings.get(this); 
				String diseaseName = diseaseJSON.getName();
				if (CollectionUtils.notIsEmptyAndOnlyOneElement(diseaseJSON.getClinicalDiagnosisStrategies())) {
					String paramName = SecondOrderParamsRepository.STR_COST_PREFIX + diseaseJSON.getClinicalDiagnosisStrategies().get(0).getName();
					calculateDiseaseStrategyCost(secParams, paramName, "Cost of diagnosing for " + diseaseName, ((RadiosRepository)secParams).getCostClinicalDiagnosis(), Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE);
				}
				
				if (CollectionUtils.notIsEmptyAndOnlyOneElement(diseaseJSON.getTreatmentStrategies())) {
					String paramName = SecondOrderParamsRepository.STR_COST_PREFIX + diseaseJSON.getTreatmentStrategies().get(0).getName();
					calculateDiseaseStrategyCost(secParams, paramName, "Cost of treatment for " + diseaseName, ((RadiosRepository)secParams).getCostTreatments(), Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE);
				}
				
				if (CollectionUtils.notIsEmptyAndOnlyOneElement(diseaseJSON.getFollowUpStrategies())) {
					String paramName = SecondOrderParamsRepository.STR_COST_PREFIX + diseaseJSON.getFollowUpStrategies().get(0).getName();
					calculateDiseaseStrategyCost(secParams, paramName, "Cost of following up for " + diseaseName, ((RadiosRepository)secParams).getCostFollowUps(), Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE);
				}
			}
			
			@Override
			public double getDiagnosisCost(Patient pat) {
				if (CollectionUtils.notIsEmptyAndOnlyOneElement(mappings.get(this).getClinicalDiagnosisStrategies())) {
					ClinicalDiagnosisStrategy strategy = mappings.get(this).getClinicalDiagnosisStrategies().get(0);
					String paramName = SecondOrderParamsRepository.STR_COST_PREFIX + strategy.getName();
					return secParams.getCostParam(paramName, pat.getSimulation());
				}
				return 0;
			}
			
			@Override
			public double getAnnualTreatmentAndFollowUpCosts(Patient pat, double initAge, double endAge) {
				final es.ull.iis.ontology.radios.json.schema4simulation.Disease diseaseJSON = mappings.get(this); 
				if (CollectionUtils.notIsEmptyAndOnlyOneElement(diseaseJSON.getTreatmentStrategies()) && CollectionUtils.notIsEmptyAndOnlyOneElement(diseaseJSON.getFollowUpStrategies())) {
					TreatmentStrategy treatmentStrategy = diseaseJSON.getTreatmentStrategies().get(0);
					String treatmentStrategyCostParamName = SecondOrderParamsRepository.STR_COST_PREFIX + treatmentStrategy.getName();
					FollowUpStrategy followUpStrategy = diseaseJSON.getFollowUpStrategies().get(0);
					String followUpStrategyCostParamName = SecondOrderParamsRepository.STR_COST_PREFIX + followUpStrategy.getName();
					return secParams.getCostParam(treatmentStrategyCostParamName, pat.getSimulation()) + secParams.getCostParam(followUpStrategyCostParamName, pat.getSimulation());
				}
				return 0;
			}
		};
		
		mappings.put(disease, diseaseJSON);
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
