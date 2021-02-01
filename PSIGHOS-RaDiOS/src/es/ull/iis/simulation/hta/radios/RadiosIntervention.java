package es.ull.iis.simulation.hta.radios;

import static java.lang.String.format;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.Intervention;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.radios.utils.CostUtils;
import es.ull.iis.simulation.hta.radios.wrappers.CostMatrixElement;
import es.ull.iis.simulation.hta.radios.wrappers.Matrix;

/**
 * @author David Prieto González
 *
 */
public class RadiosIntervention extends es.ull.iis.simulation.hta.interventions.Intervention {
	private Intervention intervention;
	private Double timeHorizont;
	private Matrix costTreatments;
	private Matrix costFollowUps;
	private Matrix costScreenings;
	private Matrix costClinicalDiagnosis;

	private boolean debug = true;
	
	public RadiosIntervention(SecondOrderParamsRepository secParams, Intervention intervention, Double timeHorizont, Matrix baseCostTreatments, Matrix baseCostFollowUps, Matrix baseCostScreenings, Matrix baseCostClinicalDiagnosis) {
		super(secParams, intervention.getName(), Constants.CONSTANT_EMPTY_STRING);
		this.intervention = intervention; 
		this.costTreatments = baseCostTreatments.clone();
		this.costFollowUps = baseCostFollowUps.clone();
		this.costScreenings = baseCostScreenings.clone();
		this.costClinicalDiagnosis = baseCostClinicalDiagnosis.clone();
		this.timeHorizont = timeHorizont;
		
		// TODO: las intervenciones al definirlas en Radios, puede llevar vinculadas modificaciones de las manifestaciones. Es aquí donde las daremos de alta.
		
		initializeCostMatrix();
	}

	/**
	 * Initializes the cost matrix for the follow-up tests associated with the intervention
	 */
	private void initializeCostMatrix() {
		CostUtils.loadCostFromTreatmentStrategies(this.costTreatments, intervention.getName(), intervention.getTreatmentStrategies(), timeHorizont);
		CostUtils.loadCostFromFollowUpStrategies(this.costFollowUps, intervention.getName(), intervention.getFollowUpStrategies(), timeHorizont);
		CostUtils.loadCostFromScreeningStrategies(this.costScreenings, intervention.getScreeningStrategies(), timeHorizont);
		CostUtils.loadCostFromClinicalDiagnosisStrategies(this.costClinicalDiagnosis, intervention.getClinicalDiagnosisStrategies(), timeHorizont);
		
		if (debug) {
			System.out.println(format("\nIntervention [%s]", this.intervention.getName()));
			System.out.println("\n\tCost matrix for Treatments:\n");
			CostUtils.showCostMatrix(this.costTreatments, "\t\t");
			System.out.println("\n\tCost matrix for FollowUps:\n");
			CostUtils.showCostMatrix(this.costFollowUps, "\t\t");
			System.out.println("\n\tCost matrix for Screenings:\n");
			CostUtils.showCostMatrix(this.costScreenings, "\t\t");
			System.out.println("\n\tCost matrix for Clinical Diagnosis:\n");
			CostUtils.showCostMatrix(this.costClinicalDiagnosis, "\t\t");
		}
	}

	public Intervention getIntervention() {
		return intervention;
	}
	
	public void setIntervention(Intervention intervention) {
		this.intervention = intervention;
	}
	
	public Matrix getCostTreatments() {
		return costTreatments;
	}

	public void setCostTreatments(Matrix costTreatments) {
		this.costTreatments = costTreatments;
	}

	public Matrix getCostFollowUps() {
		return costFollowUps;
	}

	public void setCostFollowUps(Matrix costFollowUps) {
		this.costFollowUps = costFollowUps;
	}

	public Matrix getCostScreenings() {
		return costScreenings;
	}

	public void setCostScreenings(Matrix costScreenings) {
		this.costScreenings = costScreenings;
	}

	public Matrix getCostClinicalDiagnosis() {
		return costClinicalDiagnosis;
	}

	public void setCostClinicalDiagnosis(Matrix costClinicalDiagnosis) {
		this.costClinicalDiagnosis = costClinicalDiagnosis;
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
		Double cummulativeCost = 0.0;
		
		JexlEngine jexl = new JexlBuilder().create();		

		JexlContext jc = new MapContext();
		jc.set("weight", 12.9);
		jc.set("splenectomy", false);

		Matrix costs = this.costTreatments;
		for (String manifestacion : costs.keySetR()) {
			// TODO: en este punto habría que comprobar que el paciente en su historia ha sufrido esta manifestación: keyR
			for (String treatment : costs.keySetC(manifestacion)) {
				for (CostMatrixElement e : costs.get(manifestacion, treatment)) {
					Boolean applyCost = true; 
					if (e.getCondition() != null) {
						JexlExpression exprToEvaluate = jexl.createExpression(e.getCondition());
						applyCost = (Boolean) exprToEvaluate.evaluate(jc);
					}
					
					if (applyCost) {
						cummulativeCost += e.getCost() * e.calculateNTimesInRange(pat.getInitAge(), pat.getInitAge()); 
					}
				}
			}
		}
		
		return cummulativeCost;
	}
}
