package es.ull.iis.simulation.hta.radios;

import static java.lang.String.format;

import java.util.Random;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.Cost;
import es.ull.iis.ontology.radios.json.schema4simulation.Intervention;
import es.ull.iis.ontology.radios.json.schema4simulation.ScreeningTechnique;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.ScreeningStrategy;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.radios.utils.CostUtils;
import es.ull.iis.simulation.hta.radios.wrappers.CostMatrixElement;
import es.ull.iis.simulation.hta.radios.wrappers.Matrix;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author David Prieto González
 */
public class RadiosScreeningIntervention extends ScreeningStrategy {
	private boolean debug = false;
	
	private static final JexlEngine jexl = new JexlBuilder().create();		

	private Intervention intervention;
	private String naturalDevelopmentName;
	private Integer timeHorizont;
	private Matrix costTreatments;
	private Matrix costFollowUps;
	private Matrix costScreenings;
	private Matrix costClinicalDiagnosis;

	public RadiosScreeningIntervention(SecondOrderParamsRepository secParams, Intervention intervention, String naturalDevelopmentName, Integer timeHorizont, Matrix baseCostTreatments, Matrix baseCostFollowUps, Matrix baseCostScreenings, Matrix baseCostClinicalDiagnosis) {
		super(secParams, intervention.getName(), Constants.CONSTANT_EMPTY_STRING, calculateSpecificityScreeningTechnique(intervention), calculateSensitivityScreeningTechnique(intervention));
		this.intervention = intervention; 
		this.naturalDevelopmentName = naturalDevelopmentName;
		this.costTreatments = baseCostTreatments.clone();
		this.costFollowUps = baseCostFollowUps.clone();
		this.costScreenings = baseCostScreenings.clone();
		this.costClinicalDiagnosis = baseCostClinicalDiagnosis.clone();
		this.timeHorizont = timeHorizont;
		
		// TODO: las intervenciones al definirlas en Radios, puede llevar vinculadas modificaciones de las manifestaciones. Es aquí donde las daremos de alta.
		
		initializeCostMatrix();
	}

	/**
	 * @param intervention
	 * @return
	 */
	private static Double calculateDataPropertyValueFromScreeningTechnique(Intervention intervention, String propertyName) {
		if (intervention.getScreeningStrategies() != null && !intervention.getScreeningStrategies().isEmpty()) {
			for (es.ull.iis.ontology.radios.json.schema4simulation.ScreeningStrategy screeningStrategy : intervention.getScreeningStrategies()) {
				if (screeningStrategy.getScreeningTechniques() != null && !screeningStrategy.getScreeningTechniques().isEmpty()) {
					for (ScreeningTechnique screeningTechnique : screeningStrategy.getScreeningTechniques()) {
						if ("SPECIFICITY".equals(propertyName)) {
							return Double.valueOf(screeningTechnique.getEspecificity());
						} else if ("SENSITIVITY".equals(propertyName)) {
							return Double.valueOf(screeningTechnique.getSensitivity());
						} else if ("COSTS".equals(propertyName)) {
							Double cost = 0.0;
							if (screeningTechnique.getCosts() != null && !screeningTechnique.getCosts().isEmpty()) {
								for (Cost costTechnique : screeningTechnique.getCosts()) {
									cost += Double.valueOf(costTechnique.getAmount());
								}
							}
							return cost;
						}
					}
				}
			}
		}
		return null;		
	}
	
	/**
	 * @param intervention
	 * @return
	 */
	private static Double calculateSpecificityScreeningTechnique(Intervention intervention) {
		return calculateDataPropertyValueFromScreeningTechnique(intervention, "SPECIFICITY");		
	}
	
	/**
	 * @param intervention
	 * @return
	 */
	private static Double calculateSensitivityScreeningTechnique(Intervention intervention) {
		return calculateDataPropertyValueFromScreeningTechnique(intervention, "SENSITIVITY");		
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
	
	public void setTimeHorizont(Integer timeHorizont) {
		this.timeHorizont = timeHorizont;
	}
	
	public Integer getTimeHorizont() {
		return timeHorizont;
	}
	
	@Override
	public void registerSecondOrderParameters() {
	}

	public double getFullLifeCost (Patient pat) {
		/* 
		 * TODO: para calcular el coste total para la intervención, es necesario calcular los costes parciales de:
		 * 	- [ ] Estrategias de cribado
		 * 	- [ ] Estrategias de diagnóstico
		 * 	- [V] Estrategias de tratamiento
		 * 	- [V] Estrategias de seguimiento
		 * 	- [ ] Modificaciones de las manifestaciones
		 */
		
		Double cummulativeCost = 0.0;
		if (debug) {
			System.out.println("\nCalculando costes derivados de los tratamientos para las manifestaciones sufridas o genéricos para la enfermedad...");
		}
		cummulativeCost += calculateCostsFromTreatments(pat, cummulativeCost);
		if (debug) {
			System.out.println("\nCalculando costes derivados de las pruebas de seguimiento asociadas a las manifestaciones sufridas o de la propia enfermedad...");
		}
		cummulativeCost += calculateCostsFromFollowUps(pat, cummulativeCost);
		return cummulativeCost;
	}
	
	@Override
	public double getAnnualCost(Patient pat) {
		/* 
		 * TODO: para calcular el coste total para la intervención, es necesario calcular los costes parciales de:
		 * 	- [ ] Estrategias de cribado
		 * 	- [ ] Estrategias de diagnóstico
		 * 	- [V] Estrategias de tratamiento
		 * 	- [V] Estrategias de seguimiento
		 * 	- [ ] Modificaciones de las manifestaciones
		 */
				
		Boolean calculateCummulativeCost = false;
		Double cummulativeCost = 0.0;
		if (calculateCummulativeCost) {
			Double floorLimit = Math.floor(pat.getAge());
			Double ceilLimit = (Math.ceil(pat.getAge()) == Math.floor(pat.getAge())) ? Math.ceil(pat.getAge()) + 1.0 : Math.ceil(pat.getAge());
			return calculateCostsByRange(pat, cummulativeCost, floorLimit, ceilLimit);
		} else {
			return cummulativeCost;
		}
	}

	@Override
	public double getStartingCost(Patient pat) {
		Boolean useCalculatedCostMatrix = true;
		if (useCalculatedCostMatrix) {
			return calculateInitialCosts(pat, 0.0);
		} else {
			return calculateDataPropertyValueFromScreeningTechnique(intervention, "COSTS");
		}
	}

	/**
	 * @param pat
	 * @param cummulativeCost
	 * @return
	 */
	private Double calculateCostsFromTreatments(Patient pat, Double cummulativeCost) {
		return evaluateRanges(pat, cummulativeCost, this.costTreatments);
	}

	/**
	 * @param pat
	 * @param cummulativeCost
	 * @return
	 */
	private Double calculateCostsFromFollowUps(Patient pat, Double cummulativeCost) {
		return evaluateRanges(pat, cummulativeCost, this.costFollowUps);
	}

	/**
	 * @param pat
	 * @param cummulativeCost
	 * @return
	 */
	public Double calculateInitialCosts(Patient pat, Double cummulativeCost) {
		double result = evaluateRangesFromSpecificLimits(0.0, 1.0/12.0, 0.0, this.costScreenings, new MapContext());
		result += evaluateRangesFromSpecificLimits(0.0, 1.0/12.0, 0.0, this.costClinicalDiagnosis, new MapContext());
		result += evaluateRangesFromSpecificLimits(0.0, 1.0/12.0, 0.0, this.costTreatments, new MapContext());
		result += evaluateRangesFromSpecificLimits(0.0, 1.0/12.0, 0.0, this.costFollowUps, new MapContext());
		return result;
	}

	/**
	 * @param pat
	 * @param cummulativeCost
	 * @return
	 */
	public Double calculateCostsByRange(Patient pat, Double cummulativeCost, Double initTimeRange, Double finalTimeRange) {
		MapContext jc = new MapContext();
		jc.set("weight", 50);
		
		double result = evaluateRangesFromSpecificLimits(initTimeRange, finalTimeRange, 0.0, this.costScreenings, jc);
		result += evaluateRangesFromSpecificLimits(initTimeRange, finalTimeRange, 0.0, this.costClinicalDiagnosis, jc);
		result += evaluateRangesFromSpecificLimits(initTimeRange, finalTimeRange, 0.0, this.costTreatments, jc);
		result += evaluateRangesFromSpecificLimits(initTimeRange, finalTimeRange, 0.0, this.costFollowUps, jc);
		return result;
	}

	/**
	 * @param initTimeRange
	 * @param finalTimeRange
	 * @param cummulativeCost
	 * @param costs
	 * @param jc
	 * @return
	 */
	private Double evaluateRangesFromSpecificLimits(Double initTimeRange, Double finalTimeRange, Double cummulativeCost, Matrix costs, JexlContext jc) {
		for (String manifestacion : costs.keySetR()) {
			for (String item : costs.keySetC(manifestacion)) {
				for (CostMatrixElement e : costs.get(manifestacion, item)) {
					if (initTimeRange <= e.getFloorLimitRange() && (e.getCeilLimitRange() == null || e.getCeilLimitRange() <= finalTimeRange)) {
						Integer nTimesManifestations = 1;
						Boolean applyCost = true; 
						if (e.getCondition() != null) {
							JexlExpression exprToEvaluate = jexl.createExpression(e.getCondition());
							try {
								applyCost = (Boolean) exprToEvaluate.evaluate(jc);
							} catch (JexlException ex) {
								System.err.println(ex.getMessage());
								applyCost = false;
							}
						}
						
						if (applyCost) {
							Double partialCummulativeCost = 0.0;
							if (e.getCostExpression() != null) {
								jc.set("cost", e.getCost());
								JexlExpression exprToEvaluate = jexl.createExpression(e.getCostExpression());
								try {
									partialCummulativeCost = (((Double) exprToEvaluate.evaluate(jc)) * e.calculateNTimesInRange(null, null) * nTimesManifestations);
								} catch (JexlException ex) {
									System.err.println(ex.getMessage());
									partialCummulativeCost = 0.0;
								}
							} else {
								partialCummulativeCost = (e.getCost() * e.calculateNTimesInRange(null, null) * nTimesManifestations); 
							}
							cummulativeCost += partialCummulativeCost;
							if (debug) {
								if (e.getCondition() != null) {
									System.out.println(format("\t\tSe aplicará el coste de [%s] al paciente por cumplir la condición [%s]. Coste parcial añadido [%s].", item, e.getCondition(), partialCummulativeCost));
								} else {
								}
								System.out.println(format("\t\tSe aplicará el coste de [%s] al paciente. Coste parcial añadido [%s].", item, partialCummulativeCost));
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
	 * @param cummulativeCost
	 * @param costs
	 * @return
	 */
	private Double evaluateRanges(Patient pat, Double cummulativeCost, Matrix costs) {
		JexlContext jc = generatePatientContext(pat);
		for (String manifestacion : costs.keySetR()) {
			Integer nTimesManifestations = calculateNTimesManifestationPatientLife(pat, manifestacion);
			if (nTimesManifestations > 0) {
				for (String item : costs.keySetC(manifestacion)) {
					for (CostMatrixElement e : costs.get(manifestacion, item)) {
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
								if (e.getCondition() != null) {
									System.out.println(format("\t\tSe aplicará el coste de [%s] al paciente por cumplir la condición [%s]. Coste parcial añadido [%s].", item, e.getCondition(), partialCummulativeCost));
								} else {
								}
								System.out.println(format("\t\tSe aplicará el coste de [%s] al paciente. Coste parcial añadido [%s].", item, partialCummulativeCost));
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
		// FIXME: asignar correctamente los parámetros del context del paciente, extrayendo la información de parámetro "pat".
		Random r = new Random();
		JexlContext jcPatient = new MapContext();
		jcPatient.set("disease", true);
		jcPatient.set("weight", pat.getVar("weight"));
		jcPatient.set("splenectomy", r.nextBoolean());
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
			System.out.println(format("\tNúmero de veces que el paciente ha padecido la manifestación [%s] = %s ", manifestacion, nTimesManifestations));
		}
		return nTimesManifestations;
	}
}
