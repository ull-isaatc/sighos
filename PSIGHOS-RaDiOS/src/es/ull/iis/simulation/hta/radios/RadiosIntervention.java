package es.ull.iis.simulation.hta.radios;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.Intervention;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.radios.utils.CostUtils;
import es.ull.iis.simulation.hta.radios.wrappers.Matrix;

/**
 * @author David Prieto González
 *
 */
public class RadiosIntervention extends es.ull.iis.simulation.hta.interventions.Intervention {
	private Intervention intervention;
	private Double timeHorizont;
	private Matrix costs;

	private boolean debug = true;
	
	public RadiosIntervention(SecondOrderParamsRepository secParams, Intervention intervention, Double timeHorizont, Matrix baseCosts) {
		super(secParams, intervention.getName(), Constants.CONSTANT_EMPTY_STRING);
		this.intervention = intervention; 
		this.costs = baseCosts.clone();
		this.timeHorizont = timeHorizont;
		
		// TODO: las intervenciones al definirlas en Radios, puede llevar vinculadas modificaciones de las manifestaciones. Es aquí donde las daremos de alta.
		
		initializeCostMatrix();
	}

	/**
	 * Initializes the cost matrix for the follow-up tests associated with the intervention
	 */
	private void initializeCostMatrix() {
		CostUtils.loadCostFromScreeningStrategies(costs, intervention.getScreeningStrategies(), timeHorizont);
		CostUtils.loadCostFromClinicalDiagnosisStrategies(costs, intervention.getClinicalDiagnosisStrategies(), timeHorizont);
		CostUtils.loadCostFromTreatmentStrategies(costs, intervention.getName(), intervention.getTreatmentStrategies(), timeHorizont);
		CostUtils.loadCostFromFollowUpStrategies(costs, intervention.getName(), intervention.getFollowUpStrategies(), timeHorizont);
		
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

	public Matrix getCosts() {
		return costs;
	}
	
	public void setCosts(Matrix costs) {
		this.costs = costs;
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
