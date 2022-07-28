package es.ull.iis.simulation.hta.radios;

import static java.lang.String.format;

import java.util.Random;
import java.util.TreeSet;
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
import es.ull.iis.ontology.radios.json.schema4simulation.Intervention;
import es.ull.iis.ontology.radios.json.schema4simulation.ManifestationModification;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.ManifestationPathway;
import es.ull.iis.simulation.hta.progression.condition.PreviousManifestationCondition;
import es.ull.iis.simulation.hta.radios.transforms.ValueTransform;
import es.ull.iis.simulation.hta.radios.utils.CostUtils;
import es.ull.iis.simulation.hta.radios.wrappers.CostMatrixElement;
import es.ull.iis.simulation.hta.radios.wrappers.Matrix;
import es.ull.iis.simulation.hta.radios.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author David Prieto González
 */
public class RadiosBasicIntervention extends es.ull.iis.simulation.hta.interventions.Intervention {
	private final boolean debug = false;	
	private final boolean fine = false;	
	private static final JexlEngine jexl = new JexlBuilder().create();

	private static final String REGEXP = "^([*+-/])?(.+)$";
	private static Pattern pattern = Pattern.compile(REGEXP);	
	
	private Intervention intervention;
	private String naturalDevelopmentName;
	private Integer timeHorizont;
	private Matrix costTreatments;
	private Matrix costFollowUps;
	private Matrix costScreenings;
	private Matrix costClinicalDiagnosis;
	private Disease disease;

	public RadiosBasicIntervention(SecondOrderParamsRepository secParams, Intervention intervention, String naturalDevelopmentName, Integer timeHorizont, 
			Matrix baseCostTreatments, Matrix baseCostFollowUps, Matrix baseCostScreenings, Matrix baseCostClinicalDiagnosis, Disease disease) {
		super(secParams, intervention.getName(), Constants.CONSTANT_EMPTY_STRING);
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
	 * Initializes the cost matrix for the follow-up tests associated with the intervention
	 */
	private void initializeCostMatrix() {
		CostUtils.loadCostFromTreatmentStrategies(this.costTreatments, intervention.getName(), intervention.getTreatmentStrategies(), timeHorizont);
		CostUtils.loadCostFromFollowUpStrategies(this.costFollowUps, intervention.getName(), intervention.getFollowUpStrategies(), timeHorizont);
		CostUtils.loadCostFromScreeningStrategies(this.costScreenings, intervention.getScreeningStrategies(), timeHorizont);
		CostUtils.loadCostFromClinicalDiagnosisStrategies(this.costClinicalDiagnosis, intervention.getClinicalDiagnosisStrategies(), timeHorizont);
		
		if (debug) {
			StringBuilder sb = new StringBuilder(format("\tIntervention [%s]\n", this.intervention.getName())).append("\n")
			.append("\t\tCost matrix for Treatments:\n").append(CostUtils.showCostMatrix(this.costTreatments, "\t\t\t")).append("\n")
			.append("\t\tCost matrix for FollowUps:\n").append(CostUtils.showCostMatrix(this.costFollowUps, "\t\t\t")).append("\n")
			.append("\t\tCost matrix for Screenings:\n").append(CostUtils.showCostMatrix(this.costScreenings, "\t\t\t")).append("\n")
			.append("\t\tCost matrix for Clinical Diagnosis:\n").append(CostUtils.showCostMatrix(this.costClinicalDiagnosis, "\t\t\t")).append("\n");
			System.out.println(sb.toString());
		}
	}

	public double getFullLifeCost (Patient pat) {
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
					cummulativeCost = calculateCostForSpecificRange(initTimeRange, finalTimeRange, cummulativeCost, jc, item, e);
				}
			}
		}
		return cummulativeCost;
	}

	private Double calculateCostForSpecificRange(Double initTimeRange, Double finalTimeRange, Double cummulativeCost, JexlContext jc, String item, CostMatrixElement e) {
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
					partialCummulativeCost = (((Double) exprToEvaluate.evaluate(jc)) * e.calculateNTimesInRange(initTimeRange, finalTimeRange) * nTimesManifestations);
				} catch (JexlException ex) {
					System.err.println(ex.getMessage());
					partialCummulativeCost = 0.0;
				}
			} else {
				partialCummulativeCost = (e.getCost() * e.calculateNTimesInRange(initTimeRange, finalTimeRange) * nTimesManifestations); 
			}
			cummulativeCost += partialCummulativeCost;
			if (fine) {
				if (e.getCondition() != null) {
					System.out.println(format("\t\tSe aplicará el coste de [%s] al paciente por cumplir la condición [%s]. Coste parcial añadido [%s].", item, e.getCondition(), partialCummulativeCost));
				} else {
				}
				System.out.println(format("\t\tSe aplicará el coste de [%s] al paciente. Coste parcial añadido [%s].", item, partialCummulativeCost));
			}
		}
		return cummulativeCost;
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

	private Double calculateCostsFromTreatments(Patient pat, Double cummulativeCost) {
		return evaluateRanges(pat, cummulativeCost, this.costTreatments);
	}

	private Double calculateCostsFromFollowUps(Patient pat, Double cummulativeCost) {
		return evaluateRanges(pat, cummulativeCost, this.costFollowUps);
	}

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
		jcPatient.set("weight", r.nextInt(40) + 1);
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
			for (Manifestation manif : pat.getState()) {
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
	private void addInterventionParamModification(String registerManifestation, String paramModification) {
		if (!StringUtils.isEmpty(paramModification)) {
			Matcher matcher = pattern.matcher(paramModification.trim());
			if (matcher.find()) {
				ProbabilityDistribution probabilityDistribution = ValueTransform.splitProbabilityDistribution(matcher.group(2));
				if (probabilityDistribution != null) {
					if ("*".equals(matcher.group(1))) {
						for (ManifestationPathway path : disease.getManifestation(registerManifestation).getPathways()) {							
							if (path.getCondition() instanceof PreviousManifestationCondition) {
								final TreeSet<Manifestation> previousManif = ((PreviousManifestationCondition)path.getCondition()).getPreviousManifestationsList();
								for (Manifestation srcManif : previousManif) {
									secParams.addModificationParam(this, Modification.Type.RR, srcManif, disease.getManifestation(registerManifestation),  
										Constants.CONSTANT_EMPTY_STRING, probabilityDistribution.getDeterministicValue(), probabilityDistribution.getProbabilisticValueInitializedForProbability());
								}
							}
						}
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
	public double getCostWithinPeriod(Patient pat) {
		Boolean calculateCummulativeCost = true;
		Double cummulativeCost = 0.0;
		if (calculateCummulativeCost) {
			double floorLimit = pat.getAge();
			double ceilLimit = Math.ceil(pat.getAge());
			if (ceilLimit == floorLimit) {
				ceilLimit = ceilLimit + 1.0;
			}
			cummulativeCost = calculateCostsByRange(pat, cummulativeCost, floorLimit, ceilLimit);
			if (debug) {
				System.out.println(String.format("Patient {%s} ==> Range[%s - %s] ==> Cost = %s", pat.getIdentifier(), floorLimit, ceilLimit, cummulativeCost));
			}				
		}
		return cummulativeCost;
	}

	@Override
	public double getStartingCost(Patient pat) {
		Double cummulativeCost = 0.0;
		return cummulativeCost;
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		// Register the modifications of the manifestations caused by the intervention		
		if (this.intervention.getManifestationModifications() != null) {
			for (ManifestationModification manifestationModification : this.intervention.getManifestationModifications()) {
				if (manifestationModification.getManifestations() != null) {
					for (String manifestation : manifestationModification.getManifestations()) {
						addInterventionParamModification(manifestation, manifestationModification.getProbabilityModification());
					}
				}
			}
		}
	}
}
