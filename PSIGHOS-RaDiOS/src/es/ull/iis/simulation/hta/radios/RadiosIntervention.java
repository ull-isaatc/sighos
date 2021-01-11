package es.ull.iis.simulation.hta.radios;

import java.util.Map;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.ClinicalDiagnosisStrategy;
import es.ull.iis.ontology.radios.json.schema4simulation.ClinicalDiagnosisTechnique;
import es.ull.iis.ontology.radios.json.schema4simulation.FollowUp;
import es.ull.iis.ontology.radios.json.schema4simulation.FollowUpStrategy;
import es.ull.iis.ontology.radios.json.schema4simulation.Intervention;
import es.ull.iis.ontology.radios.json.schema4simulation.ScreeningStrategy;
import es.ull.iis.ontology.radios.json.schema4simulation.ScreeningTechnique;
import es.ull.iis.ontology.radios.json.schema4simulation.Treatment;
import es.ull.iis.ontology.radios.json.schema4simulation.TreatmentStrategy;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.radios.utils.CostUtils;

/**
 * @author David Prieto González
 *
 */
public class RadiosIntervention extends es.ull.iis.simulation.hta.interventions.Intervention {
	private Intervention intervention;
	private Double timeHorizont;
	private Map<String, Double[][]> costs;

	private boolean debug = true;
	
	public RadiosIntervention(SecondOrderParamsRepository secParams, Intervention intervention, Double timeHorizont, Map<String, Double[][]> baseCosts) {
		super(secParams, intervention.getName(), Constants.CONSTANT_EMPTY_STRING);
		this.intervention = intervention; 
		this.costs = baseCosts;
		this.timeHorizont = timeHorizont;
		
		// TODO: las intervenciones al definirlas en Radios, puede llevar vinculadas modificaciones de las manifestaciones. Es aquí donde las daremos de alta.
		
		initializeCostMatrix(intervention);
	}

	/**
	 * Initializes the cost matrix for the follow-up tests associated with the intervention
	 * @param intervention Intervention
	 */
	private void initializeCostMatrix(Intervention intervention) {
		// Screening Strategies
		if (CollectionUtils.notIsEmpty(intervention.getScreeningStrategies())) {
			for (ScreeningStrategy strategy : intervention.getScreeningStrategies()) {
				for (ScreeningTechnique item : strategy.getScreeningTechniques()) {
					CostUtils.updateMatrixWithCostAndGuidelines(costs, item.getName(), item.getCosts(), NewbornGuideline.getInstance(), timeHorizont);
				}
			}
		}
		// ClinicalDiagnosis Strategies 
		if (CollectionUtils.notIsEmpty(intervention.getClinicalDiagnosisStrategies())) {
			for (ClinicalDiagnosisStrategy strategy : intervention.getClinicalDiagnosisStrategies()) {
				for (ClinicalDiagnosisTechnique item : strategy.getClinicalDiagnosisTechniques()) {
					CostUtils.updateMatrixWithCostAndGuidelines(costs, item.getName(), item.getCosts(), NewbornGuideline.getInstance(), timeHorizont);
				}
			}
		}
		// Treatments Strategies
		if (CollectionUtils.notIsEmpty(intervention.getTreatmentStrategies())) {
			for (TreatmentStrategy strategy : intervention.getTreatmentStrategies()) {
				for (Treatment item : strategy.getTreatments()) {
					CostUtils.updateMatrixWithCostAndGuidelines(costs, item.getName(), item.getCosts(), item.getGuidelines(), timeHorizont);
				}
			}
		}
		// Follow-Ups Strategies
		if (CollectionUtils.notIsEmpty(intervention.getFollowUpStrategies())) {
			for (FollowUpStrategy strategy : intervention.getFollowUpStrategies()) {
				for (FollowUp item : strategy.getFollowUps()) {
					CostUtils.updateMatrixWithCostAndGuidelines(costs, item.getName(), item.getCosts(), item.getGuidelines(), timeHorizont);
				}
			}
		}

		if (debug) {
			CostUtils.showCostMatrix(costs);
		}
	}

	public Intervention getIntervention() {
		return intervention;
	}
	
	public void setIntervention(Intervention intervention) {
		this.intervention = intervention;
	}

	public void setCosts(Map<String, Double[][]> costs) {
		this.costs = costs;
	}
	
	public Map<String, Double[][]> getCosts() {
		return costs;
	}
	
	public void setTimeHorizont(Double timeHorizont) {
		this.timeHorizont = timeHorizont;
	}
	
	public Double getTimeHorizont() {
		return timeHorizont;
	}
	
	@Override
	public void registerSecondOrderParameters() {
	}

	@Override
	public double getAnnualCost(Patient pat) {
		// String annualBehavior = Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE;
		// String lifetimeBehavior = Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_LIFETIME_VALUE;
		/* 
		 * TODO: para calcular el coste anual para la intervención, es necesario calcular los costes parciales de:
		 * 	- Estrategias de cribado
		 * 	- Estrategias de diagnóstico
		 * 	- Estrategias de tratamiento
		 * 	- Estrategias de seguimiento
		 * 	- Modificaciones de las manifestaciones
		*/
		
		double annualCost = 0.0; 
		double lifetimeCost = 0.0; 		
		return annualCost + (lifetimeCost / (pat != null ? pat.getAgeAtDeath() : Double.MAX_VALUE));
	}

	@Override
	public double getStartingCost(Patient pat) {
		// Como en la ontología no se recoge el momento exacto en el cual se realiza el gasto de coste ONETIME, asumiremos el sumatorio de este tipo de costes como coste inicial de la intervención.
		// String onetimeBehavior = Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE;
		double lifetimeCost = 0.0; 
		return lifetimeCost;
	}
}
