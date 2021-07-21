package es.ull.iis.simulation.hta.radios;

import static java.lang.String.format;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.lang3.StringUtils;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.Cost;
import es.ull.iis.ontology.radios.json.schema4simulation.Intervention;
import es.ull.iis.ontology.radios.json.schema4simulation.ManifestationModification;
import es.ull.iis.ontology.radios.json.schema4simulation.ScreeningTechnique;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.ScreeningStrategy;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.Modification;
import es.ull.iis.simulation.hta.radios.transforms.ValueTransform;
import es.ull.iis.simulation.hta.radios.utils.CostUtils;
import es.ull.iis.simulation.hta.radios.utils.NumberUtils;
import es.ull.iis.simulation.hta.radios.wrappers.CostMatrixElement;
import es.ull.iis.simulation.hta.radios.wrappers.Matrix;
import es.ull.iis.simulation.hta.radios.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author David Prieto González
 */
public class RadiosScreeningIntervention extends ScreeningStrategy {
	private boolean debug = false;
	private boolean fine = false;
	
	private static final JexlEngine jexl = new JexlBuilder().create();		

	private static final String REGEXP = "^([*+-/])([0-9]+(\\.[0-9]+)?)$";
	private static Pattern pattern = Pattern.compile(REGEXP);	
	
	private Intervention intervention;
	private String naturalDevelopmentName;
	private Integer timeHorizont;
	private Matrix costTreatments;
	private Matrix costFollowUps;
	private Matrix costScreenings;
	private Matrix costClinicalDiagnosis;
	private Disease disease;

	public RadiosScreeningIntervention(SecondOrderParamsRepository secParams, Intervention intervention, String naturalDevelopmentName, 
			Integer timeHorizont, Matrix baseCostTreatments, Matrix baseCostFollowUps, Matrix baseCostScreenings, Matrix baseCostClinicalDiagnosis, Disease disease) {
		super(secParams, intervention.getName(), Constants.CONSTANT_EMPTY_STRING, calculateSpecificityScreeningTechnique(intervention), calculateSensitivityScreeningTechnique(intervention));
		this.intervention = intervention; 
		this.naturalDevelopmentName = naturalDevelopmentName;
		this.costTreatments = baseCostTreatments.clone();
		this.costFollowUps = baseCostFollowUps.clone();
		this.costScreenings = baseCostScreenings.clone();
		this.costClinicalDiagnosis = baseCostClinicalDiagnosis.clone();
		this.timeHorizont = timeHorizont;
		this.disease = disease;
		
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
									ProbabilityDistribution costProbabilityDistribution = ValueTransform.splitProbabilityDistribution(costTechnique.getAmount());
									cost += costProbabilityDistribution.getDeterministicValue();
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
			StringBuilder sb = new StringBuilder(format("\tIntervention [%s]", this.intervention.getName())).append("\n")
			.append("\t\tCost matrix for Treatments:\n").append(CostUtils.showCostMatrix(this.costTreatments, "\t\t\t")).append("\n")
			.append("\t\tCost matrix for FollowUps:\n").append(CostUtils.showCostMatrix(this.costFollowUps, "\t\t\t")).append("\n")
			.append("\t\tCost matrix for Screenings:\n").append(CostUtils.showCostMatrix(this.costScreenings, "\t\t\t")).append("\n")
			.append("\t\tCost matrix for Clinical Diagnosis:\n").append(CostUtils.showCostMatrix(this.costClinicalDiagnosis, "\t\t\t")).append("\n");
			System.out.println(sb.toString());
		}
	}

	public double getFullLifeCost (Patient pat) {
		Double cummulativeCost = 0.0;
		if (fine) {
			System.out.println("\nCalculando costes derivados de los tratamientos para las manifestaciones sufridas o genéricos para la enfermedad...");
		}
		cummulativeCost += calculateCostsFromTreatments(pat, cummulativeCost);
		if (fine) {
			System.out.println("\nCalculando costes derivados de las pruebas de seguimiento asociadas a las manifestaciones sufridas o de la propia enfermedad...");
		}
		cummulativeCost += calculateCostsFromFollowUps(pat, cummulativeCost);
		return cummulativeCost;
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
							if (fine) {
								if (e.getCondition() != null) {
									System.out.println(format("\t\tSe aplicará el coste de [%s] al paciente por cumplir la condición [%s]. Coste parcial añadido [%s].", item, e.getCondition(), partialCummulativeCost));
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
		if (manifestacion.equalsIgnoreCase(this.naturalDevelopmentName)) {
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
	
	/**
	 * @param registerManifestation
	 * @param paramModification
	 */
	private void addInterventionParamModification(Manifestation registerManifestation, String paramModification) {
		if (!StringUtils.isEmpty(paramModification)) {
			Matcher matcher = pattern.matcher(paramModification.trim());
			if (matcher.find()) {
				Double modificatorValue = NumberUtils.asDouble(matcher.group(2));
				if (modificatorValue != null && modificatorValue == 0.0) {
					// This is the only case implemented at the moment and it is in which the modification is to set the parameter to zero.
					if ("*".equals(matcher.group(1))) {
						secParams.addModificationParam(this, Modification.Type.SET, disease.getAsymptomaticManifestation(), registerManifestation,  
								Constants.CONSTANT_EMPTY_STRING, modificatorValue, RandomVariateFactory.getInstance("ConstantVariate", modificatorValue));
					}
				}
			}
		}
	}

	/*******************************************************************************************************************************************************************************************
	 *******************************************************************************************************************************************************************************************
	 * Override methods
	 *******************************************************************************************************************************************************************************************
	*******************************************************************************************************************************************************************************************/	
	
	@Override
	public double getStartingCost(Patient pat) {
		Boolean useCalculatedCostMatrix = false;
		if (useCalculatedCostMatrix) {
			return calculateInitialCosts(pat, 0.0);
		} else {
			return calculateDataPropertyValueFromScreeningTechnique(intervention, "COSTS");
		}
	}

	@Override
	public double getAnnualCost(Patient pat) {
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
	public void registerSecondOrderParameters() {
		if (CollectionUtils.notIsEmptyAndOnlyOneElement(this.intervention.getScreeningStrategies())) {
			Object[] calculatedCost = CostUtils.calculateOnetimeCostFromMatrix(this.costScreenings);
			RandomVariate distribution = RandomVariateFactory.getInstance("ConstantVariate", (Double) calculatedCost[1]);
			if (calculatedCost[2] != null) {
				distribution = (RandomVariate)calculatedCost[2];
			}
			secParams.addCostParam(this, "Cost of screening", "", (Integer)calculatedCost[0], (Double)calculatedCost[1], distribution);
		}
		
		// Register the modifications of the manifestations caused by the intervention		
		if (this.intervention.getManifestationModifications() != null) {
			for (ManifestationModification manifestationModification : this.intervention.getManifestationModifications()) {
				if (manifestationModification.getManifestations() != null) {
					for (Manifestation registerManifestation : secParams.getRegisteredManifestations()) {
						for (String manifestation : manifestationModification.getManifestations()) {
							if (registerManifestation.getName().equals(manifestation)) {
								addInterventionParamModification(registerManifestation, manifestationModification.getProbabilityModification());
							}
						}
					}
				}
			}
		}
	}

}
