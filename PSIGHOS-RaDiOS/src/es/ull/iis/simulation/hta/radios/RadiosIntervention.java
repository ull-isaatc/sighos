package es.ull.iis.simulation.hta.radios;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.ClinicalDiagnosisStrategy;
import es.ull.iis.ontology.radios.json.schema4simulation.ClinicalDiagnosisTechnique;
import es.ull.iis.ontology.radios.json.schema4simulation.Cost;
import es.ull.iis.ontology.radios.json.schema4simulation.Intervention;
import es.ull.iis.ontology.radios.json.schema4simulation.ScreeningStrategy;
import es.ull.iis.ontology.radios.json.schema4simulation.ScreeningTechnique;
import es.ull.iis.ontology.radios.json.schema4simulation.Treatment;
import es.ull.iis.ontology.radios.json.schema4simulation.TreatmentStrategy;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.radios.utils.CostUtils;

public class RadiosIntervention extends es.ull.iis.simulation.hta.interventions.Intervention {
	private Intervention intervention;

	public RadiosIntervention(SecondOrderParamsRepository secParams, Intervention intervention) {
		super(secParams, intervention.getName(), Constants.CONSTANT_EMPTY_STRING);
		this.intervention = intervention; 
		
		// TODO: las intervenciones al definirlas en Radios, puede llevar vinculadas modificaciones de las manifestaciones. Es aquí donde las daremos de alta.
	}

	public Intervention getIntervention() {
		return intervention;
	}
	
	public void setIntervention(Intervention intervention) {
		this.intervention = intervention;
	}
	
	@Override
	public void registerSecondOrderParameters() {
	}

	@Override
	public double getAnnualCost(Patient pat) {
		String annualBehavior = Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE;
		String lifetimeBehavior = Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_LIFETIME_VALUE;
		/* 
		 * TODO: para calcular el coste anual para la intervención, es necesario calcular los costes parciales de:
		 * 	- Estrategias de cribado
		 * 	- Estrategias de diagnóstico
		 * 	- Estrategias de tratamiento
		 * 	- Estrategias de seguimiento
		 * 	- Modificaciones de las manifestaciones
		*/
		
		double annualCost = calculateCostOfClinicalDiagnosisStrategies(annualBehavior, pat.getAge()) + 
				 CostUtils.calculateCostOfFollowStrategies(getIntervention(), annualBehavior, pat.getAge(), false) + 
				 calculateCostOfScreeningStrategies(annualBehavior, pat.getAge()) + 
				 calculateCostOfTreatmentStrategies(annualBehavior, pat.getAge()); 

		double lifetimeCost = calculateCostOfClinicalDiagnosisStrategies(lifetimeBehavior, pat.getAge()) + 
				CostUtils.calculateCostOfFollowStrategies(getIntervention(), lifetimeBehavior, pat.getAge(), false) + 
				 calculateCostOfScreeningStrategies(lifetimeBehavior, pat.getAge()) + 
				 calculateCostOfTreatmentStrategies(lifetimeBehavior, pat.getAge()); 
		
		return annualCost + (lifetimeCost / (pat != null ? pat.getAgeAtDeath() : Double.MAX_VALUE));
	}

	@Override
	public double getStartingCost(Patient pat) {
		// Como en la ontología no se recoge el momento exacto en el cual se realiza el gasto de coste ONETIME, asumiremos el sumatorio de este tipo de costes como coste inicial de la intervención.
		String onetimeBehavior = Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE;
		double lifetimeCost = calculateCostOfClinicalDiagnosisStrategies(onetimeBehavior, pat.getAge()) + 
				CostUtils.calculateCostOfFollowStrategies(getIntervention(), onetimeBehavior, pat.getAge(), false) + 
				 calculateCostOfScreeningStrategies(onetimeBehavior, pat.getAge()) + 
				 calculateCostOfTreatmentStrategies(onetimeBehavior, pat.getAge()); 
		return lifetimeCost;
	}

	/** 
	 * Support methods
	 */
	private double calculateCostOfScreeningStrategies (String temporalBehavior, Double patientAge) {
		double result = 0.0;
		if (CollectionUtils.notIsEmpty(getIntervention().getClinicalDiagnosisStrategies())) {
			for (ScreeningStrategy screeningStrategy : getIntervention().getScreeningStrategies()) {
				if (CollectionUtils.notIsEmpty(screeningStrategy.getCosts())) {
					for (Cost cost : screeningStrategy.getCosts()) {
						if (temporalBehavior.equalsIgnoreCase(cost.getTemporalBehavior())) {
							result += Double.parseDouble(cost.getAmount());
						}
					}
				} else {
					for (ScreeningTechnique technique : screeningStrategy.getScreeningTechniques()) {
						if (CollectionUtils.notIsEmpty(technique.getCosts())) {
							for (Cost cost : technique.getCosts()) {
								if (temporalBehavior.equalsIgnoreCase(cost.getTemporalBehavior())) {
									result += Double.parseDouble(cost.getAmount());
								}
							}
						}
					}
				}
			}
		}		
		return result;				
	}
	
	private double calculateCostOfClinicalDiagnosisStrategies (String temporalBehavior, Double patientAge) {
		double result = 0.0;
		if (CollectionUtils.notIsEmpty(getIntervention().getClinicalDiagnosisStrategies())) {
			for (ClinicalDiagnosisStrategy clinicalDiagnosisStrategy : getIntervention().getClinicalDiagnosisStrategies()) {
				if (CollectionUtils.notIsEmpty(clinicalDiagnosisStrategy.getCosts())) {
					for (Cost cost : clinicalDiagnosisStrategy.getCosts()) {
						if (temporalBehavior.equalsIgnoreCase(cost.getTemporalBehavior())) {
							result += Double.parseDouble(cost.getAmount());
						}
					}
				} else {
					for (ClinicalDiagnosisTechnique technique : clinicalDiagnosisStrategy.getClinicalDiagnosisTechniques()) {
						if (CollectionUtils.notIsEmpty(technique.getCosts())) {
							for (Cost cost : technique.getCosts()) {
								if (temporalBehavior.equalsIgnoreCase(cost.getTemporalBehavior())) {
									result += Double.parseDouble(cost.getAmount());
								}
							}
						}
					}
				}
			}
		}		
		return result;				
	}
	
	private double calculateCostOfTreatmentStrategies (String temporalBehavior, Double patientAge) {
		double result = 0.0;
		if (CollectionUtils.notIsEmpty(getIntervention().getTreatmentStrategies())) {
			for (TreatmentStrategy treatmentStrategy : getIntervention().getTreatmentStrategies()) {
				if (CollectionUtils.notIsEmpty(treatmentStrategy.getCosts())) {
					for (Cost cost : treatmentStrategy.getCosts()) {
						if (temporalBehavior.equalsIgnoreCase(cost.getTemporalBehavior())) {
							result += Double.parseDouble(cost.getAmount());
						}
					}
				} else {
					for (Treatment treatment : treatmentStrategy.getTreatments()) {
						if (CollectionUtils.notIsEmpty(treatment.getCosts())) {
							for (Cost cost : treatment.getCosts()) {
								if (temporalBehavior.equalsIgnoreCase(cost.getTemporalBehavior())) {
									result += Double.parseDouble(cost.getAmount());
								}
							}
						}
					}
				}
			}
		}		
		return result;				
	}
}
