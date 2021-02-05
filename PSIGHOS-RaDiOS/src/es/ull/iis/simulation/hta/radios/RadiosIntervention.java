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
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.radios.utils.CostUtils;
import es.ull.iis.simulation.hta.radios.wrappers.CostMatrixElement;
import es.ull.iis.simulation.hta.radios.wrappers.Matrix;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author David Prieto Gonz�lez
 *
 */
public class RadiosIntervention extends es.ull.iis.simulation.hta.interventions.Intervention {
	private boolean debug = true;
	
	private static final JexlEngine jexl = new JexlBuilder().create();		

	private Intervention intervention;
	private String naturalDevelopmentName;
	private Double timeHorizont;
	private Matrix costTreatments;
	private Matrix costFollowUps;
	private Matrix costScreenings;
	private Matrix costClinicalDiagnosis;


	public RadiosIntervention(SecondOrderParamsRepository secParams, Intervention intervention, String naturalDevelopmentName, Double timeHorizont, Matrix baseCostTreatments, Matrix baseCostFollowUps, Matrix baseCostScreenings, Matrix baseCostClinicalDiagnosis) {
		super(secParams, intervention.getName(), Constants.CONSTANT_EMPTY_STRING);
		this.intervention = intervention; 
		this.naturalDevelopmentName = naturalDevelopmentName;
		this.costTreatments = baseCostTreatments.clone();
		this.costFollowUps = baseCostFollowUps.clone();
		this.costScreenings = baseCostScreenings.clone();
		this.costClinicalDiagnosis = baseCostClinicalDiagnosis.clone();
		this.timeHorizont = timeHorizont;
		
		// TODO: las intervenciones al definirlas en Radios, puede llevar vinculadas modificaciones de las manifestaciones. Es aqu� donde las daremos de alta.
		
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

	public String getNaturalDevelopmentName() {
		return naturalDevelopmentName;
	}
	
	public void setNaturalDevelopmentName(String naturalDevelopmentName) {
		this.naturalDevelopmentName = naturalDevelopmentName;
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

	public double getFullLifeCost (Patient pat) {
		/* 
		 * TODO: para calcular el coste total para la intervenci�n, es necesario calcular los costes parciales de:
		 * 	- Estrategias de cribado
		 * 	- Estrategias de diagn�stico
		 * 	- Estrategias de tratamiento
		 * 	- Estrategias de seguimiento
		 * 	- Modificaciones de las manifestaciones
		*/
		
		Double cummulativeCost = 0.0;
		cummulativeCost += calculateCostsFromTreatments(pat, cummulativeCost);
		return cummulativeCost;
	}
	
	@Override
	public double getAnnualCost(Patient pat) {
		/* 
		 * TODO: para calcular el coste anual para la intervenci�n, es necesario calcular los costes parciales de:
		 * 	- Estrategias de cribado
		 * 	- Estrategias de diagn�stico
		 * 	- Estrategias de tratamiento
		 * 	- Estrategias de seguimiento
		 * 	- Modificaciones de las manifestaciones
		*/
		
		Double cummulativeCost = 0.0;
		return cummulativeCost;
	}

	@Override
	public double getStartingCost(Patient pat) {
		Double cummulativeCost = 0.0;
		return cummulativeCost;
	}

	private Double calculateCostsFromTreatments(Patient pat, Double cummulativeCost) {
		JexlContext jc = generatePatientContext(pat);
		Matrix costs = this.costTreatments;

		for (String manifestacion : costs.keySetR()) {
			Integer nTimesManifestations = calculateNTimesManifestationPatientLife(pat, manifestacion);
			//if (getNaturalDevelopmentName().equalsIgnoreCase(manifestacion)) {
			if (nTimesManifestations > 0) {
				for (String treatment : costs.keySetC(manifestacion)) {
					for (CostMatrixElement e : costs.get(manifestacion, treatment)) {
						Boolean applyCost = true; 
						if (e.getCondition() != null) {
							JexlExpression exprToEvaluate = jexl.createExpression(e.getCondition());
							applyCost = (Boolean) exprToEvaluate.evaluate(jc);
						}
						
						if (applyCost) {
							Double partialCummulativeCost = 0.0;
							if (e.getCostExpression() != null) {
								jc.set("cost", e.getCost());
								JexlExpression exprToEvaluate = jexl.createExpression(e.getCostExpression());
								partialCummulativeCost = (((Double) exprToEvaluate.evaluate(jc)) * e.calculateNTimesInRange(null, null) * nTimesManifestations);
							} else {
								partialCummulativeCost = (e.getCost() * e.calculateNTimesInRange(null, null) * nTimesManifestations); 
							}
							cummulativeCost += partialCummulativeCost;
							if (debug) {
								System.out.println(format("\tSe aplicar� el coste de [%s] al paciente por cumplir la condici�n [%s]. Coste parcial a�adido [%s].", 
										treatment, e.getCondition(), partialCummulativeCost));
							}
						}
					}
				}
			}
		}
		return cummulativeCost;
	}

	/**
	 * @param pat
	 * @return
	 */
	@SuppressWarnings("unused")
	private Double calculateAgeToYears(Double age, TimeUnit defaultTimeUnit) {
		Double result = 0.0;
		if (defaultTimeUnit == TimeUnit.MINUTE) {
			result = age / (365.0 * 24.0 * 60.0);
		} else if (defaultTimeUnit == TimeUnit.HOUR) {			
			result = age / (365.0 * 24.0);
		} else if (defaultTimeUnit == TimeUnit.DAY) {
			result = age / 365.0;
		} else if (defaultTimeUnit == TimeUnit.MONTH) {
			result = age / 12.0;
		} else if (defaultTimeUnit == TimeUnit.YEAR) {
			result = age;
		}		
		return result;
	}

	/**
	 * @param pat
	 * @return
	 */
	private JexlContext generatePatientContext(Patient pat) {
		JexlContext jcPatient = new MapContext();
		jcPatient.set("disease", true);
		jcPatient.set("weight", 26);
		jcPatient.set("splenectomy", false);
		return jcPatient;
	}

	/**
	 * @param pat
	 * @param manifestacion
	 * @return
	 */
	private Integer calculateNTimesManifestationPatientLife(Patient pat, String manifestacion) {
		Integer nTimesManifestations = 0;
		if (manifestacion.equalsIgnoreCase(getNaturalDevelopmentName())) {
			nTimesManifestations = 1;
		} else {
			for (Manifestation manif : pat.getDetailedState()) {
				if (manif.name().equalsIgnoreCase(manifestacion)) {
					nTimesManifestations++;
				}
			}
		}

		if (debug) {
			System.out.println(format("N�mero de veces que el paciente ha padecido la manifestaci�n [%s] = %s ", manifestacion, nTimesManifestations));
		}
		return nTimesManifestations;
	}
}
