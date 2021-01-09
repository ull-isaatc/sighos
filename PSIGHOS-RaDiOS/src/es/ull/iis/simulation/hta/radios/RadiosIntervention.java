package es.ull.iis.simulation.hta.radios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.ClinicalDiagnosisStrategy;
import es.ull.iis.ontology.radios.json.schema4simulation.ClinicalDiagnosisTechnique;
import es.ull.iis.ontology.radios.json.schema4simulation.Cost;
import es.ull.iis.ontology.radios.json.schema4simulation.FollowUp;
import es.ull.iis.ontology.radios.json.schema4simulation.FollowUpStrategy;
import es.ull.iis.ontology.radios.json.schema4simulation.Guideline;
import es.ull.iis.ontology.radios.json.schema4simulation.Intervention;
import es.ull.iis.ontology.radios.json.schema4simulation.ScreeningStrategy;
import es.ull.iis.ontology.radios.json.schema4simulation.ScreeningTechnique;
import es.ull.iis.ontology.radios.json.schema4simulation.Treatment;
import es.ull.iis.ontology.radios.json.schema4simulation.TreatmentStrategy;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author David Prieto González
 *
 */
public class RadiosIntervention extends es.ull.iis.simulation.hta.interventions.Intervention {
	private final static String REGEXP_RANGE = "^([0-9]+)([my])(-([0-9]+)([my]))?$|^([0-9]+)([my])(-(\\*))?$";
	private final static String REGEXP_FRECUENCY = "^([0-9]+)([my])$";

	private boolean debug = true;
	
	private Intervention intervention;
	private Double timeHorizont;
	private Map<String, Double[][]> costs;

	public RadiosIntervention(SecondOrderParamsRepository secParams, Intervention intervention, Double timeHorizont) {
		super(secParams, intervention.getName(), Constants.CONSTANT_EMPTY_STRING);
		this.intervention = intervention; 
		this.costs = new HashMap<>();
		this.timeHorizont = timeHorizont;
		
		// TODO: las intervenciones al definirlas en Radios, puede llevar vinculadas modificaciones de las manifestaciones. Es aquí donde las daremos de alta.
		
		initializeCostMatrix(intervention);

		if (debug) {
			showCostMatrix();
		}
	}

	/**
	 * Show intervention cost matrix 
	 */
	private void showCostMatrix() {
		for (String key: costs.keySet()) {
			System.out.println(String.format("%s", key));
			for (int i = 0; i < costs.get(key).length ; i++) {
				System.out.println(String.format("\trange.floor=%s, range.ceil=%s, frequency=%s, rangeTimes=%s, cost=%S, annualCost=%s", 
						costs.get(key)[i][0], costs.get(key)[i][1], costs.get(key)[i][2], costs.get(key)[i][3], costs.get(key)[i][4], costs.get(key)[i][5]));
			}
		}
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
					updateMatrixWithCostAndGuidelines(item.getName(), item.getCosts(), NewbornGuideline.getInstance());
				}
			}
		}
		// ClinicalDiagnosis Strategies 
		if (CollectionUtils.notIsEmpty(intervention.getClinicalDiagnosisStrategies())) {
			for (ClinicalDiagnosisStrategy strategy : intervention.getClinicalDiagnosisStrategies()) {
				for (ClinicalDiagnosisTechnique item : strategy.getClinicalDiagnosisTechniques()) {
					updateMatrixWithCostAndGuidelines(item.getName(), item.getCosts(), NewbornGuideline.getInstance());
				}
			}
		}
		// Treatments Strategies
		if (CollectionUtils.notIsEmpty(intervention.getTreatmentStrategies())) {
			for (TreatmentStrategy strategy : intervention.getTreatmentStrategies()) {
				for (Treatment item : strategy.getTreatments()) {
					updateMatrixWithCostAndGuidelines(item.getName(), item.getCosts(), item.getGuidelines());
				}
			}
		}
		// Follow-Ups Strategies
		if (CollectionUtils.notIsEmpty(intervention.getFollowUpStrategies())) {
			for (FollowUpStrategy strategy : intervention.getFollowUpStrategies()) {
				for (FollowUp item : strategy.getFollowUps()) {
					updateMatrixWithCostAndGuidelines(item.getName(), item.getCosts(), item.getGuidelines());
				}
			}
		}
	}

	private void updateMatrixWithCostAndGuidelines(String itemName, List<Cost> costs, List<Guideline> guidelines) {
		if (CollectionUtils.notIsEmpty(costs)) {
			for (Cost cost : costs) {
				if (Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE.equalsIgnoreCase(cost.getTemporalBehavior())) {
					if (CollectionUtils.notIsEmpty(guidelines)) {
						for (Guideline guideline : guidelines) {
							updateCostMatrix(itemName, cost.getAmount(), guideline);
						}
					}
				}									
			}
		}
	}

	private void updateCostMatrix(String eventName, String cost, Guideline guideline) {
		if (costs.keySet().contains(eventName)) {
			if (costs.get(eventName) != null) {
				for (int i = 0; i < costs.get(eventName).length; i++) {
					costs.get(eventName)[i][2] *= 2.0;
					costs.get(eventName)[i][3] *= 2.0;
					costs.get(eventName)[i][5] *= 2.0;
				}
			}			
		} else {
			String[] ranges = guideline.getRange().split(",");										
			String[] frequencies = initializeFrequencies(ranges.length);
			if (guideline.getFrequency() != null) {
				frequencies = guideline.getFrequency().split(",");
			}
			if (ranges == null || frequencies == null || (ranges != null && frequencies != null && ranges.length != frequencies.length)) {
				throw new RuntimeException(String.format("Error en la correlacion de rango y frecuencia para %s", eventName));
			}
			Double[][] costValues = new Double[ranges.length][6];
			for (int i = 0; i < ranges.length; i++) {
				Double[] normalizedRange = normalizeRange(ranges[i], false);
				Double[] frequencyAndAnnualTimes = normalizeFrequencyAndCalculateAnnualTimes(normalizedRange, frequencies[i]);
				Double costValue = Double.valueOf(cost);
				costValues[i][0] = normalizedRange[0];
				costValues[i][1] = normalizedRange[1];
				costValues[i][2] = frequencyAndAnnualTimes[0];
				costValues[i][3] = frequencyAndAnnualTimes[1];
				costValues[i][4] = costValue; // TODO: caso de la prueba de cribado que se ejecuta dos veces, no se está teniendo en cuenta porque se sobreescribe el hashmap
				if (normalizedRange[1].equals(normalizedRange[0])) {
					costValues[i][5] = frequencyAndAnnualTimes[1] * costValue;
				} else {
					costValues[i][5] = (frequencyAndAnnualTimes[1] * costValue) / (normalizedRange[1] - normalizedRange[0]);
				}
			}
			costs.put(eventName, costValues);
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
		
		double annualCost = 0.0; 
		double lifetimeCost = 0.0; 		
		return annualCost + (lifetimeCost / (pat != null ? pat.getAgeAtDeath() : Double.MAX_VALUE));
	}

	@Override
	public double getStartingCost(Patient pat) {
		// Como en la ontología no se recoge el momento exacto en el cual se realiza el gasto de coste ONETIME, asumiremos el sumatorio de este tipo de costes como coste inicial de la intervención.
		String onetimeBehavior = Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE;
		double lifetimeCost = 0.0; 
		return lifetimeCost;
	}

	/**************************************************************************************************************************************************************************
	 * Support methods
	 **************************************************************************************************************************************************************************/
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
	
	/**
	 * @param range
	 * @return
	 */
	private Double[] normalizeRange (String range, boolean splitIntoAnnualRanges) {
		List<Double> result = new ArrayList<>();
		Pattern pattern = Pattern.compile(REGEXP_RANGE);
		Matcher matcher = pattern.matcher(range.trim());
		if (matcher.find()) {
			Double floorLimit = Double.valueOf(matcher.group(1) != null ? matcher.group(1) : matcher.group(6));
			String floorLimitUnit = matcher.group(2) != null ? matcher.group(2) : matcher.group(7); 
			if ("m".equalsIgnoreCase(floorLimitUnit)) {
				floorLimit = floorLimit/12.0;
			}
			Double ceilLimit = timeHorizont; 
			if (matcher.group(4) != null) {				
				ceilLimit = Double.valueOf(matcher.group(4)) > timeHorizont ? timeHorizont : Double.valueOf(matcher.group(4));
				String ceilLimitStep = matcher.group(5); 
				if ("m".equalsIgnoreCase(ceilLimitStep)) {
					ceilLimit = ceilLimit/12.0; 
				}
			} else if (matcher.group(9) == null) {
				ceilLimit = null;
			}
			
			if (splitIntoAnnualRanges) {
				splitFullRangeIntoAnnualRanges(result, floorLimit, ceilLimit);	
			} else {
				result.add(floorLimit);
				if (ceilLimit == null) {
					result.add(floorLimit);	
				} else {
					result.add(ceilLimit);	
				}
			}
			
			return result.toArray(new Double [0]);
		}
		return null;
	}

	/**
	 * @param result
	 * @param floorLimit
	 * @param ceilLimit
	 */
	private List<Double> splitFullRangeIntoAnnualRanges(List<Double> result, Double floorLimit, Double ceilLimit) {
		result.add(floorLimit);
		if (ceilLimit != null) {
			if (ceilLimit - Math.floor(floorLimit) < 1.0) {
				result.add(ceilLimit);
			} else {
				result.add(Math.ceil(floorLimit));
				Double tmp = ceilLimit - Math.ceil(floorLimit);
				while (tmp > 0.0) {
					if (tmp > 1.0) {
						result.add(result.get(result.size()-1) + 1.0);
						tmp -= 1.0;
					} else {
						result.add(result.get(result.size()-1) + tmp);
					}						
				}
			}
		}
		return result;
	}
	
	/**
	 * @return Returns the normalized frequency and the number of times that, according to that frequency, an event will occur.
	 */
	public static Double[] normalizeFrequencyAndCalculateAnnualTimes (Double[] range, String frequency) {		
		List<Double> result = new ArrayList<>();

		Pattern pattern = Pattern.compile(REGEXP_FRECUENCY);
		Matcher matcher = pattern.matcher(frequency.trim());
		if (matcher.find()) {
			Double frequencyValue = Double.valueOf(matcher.group(1));
			if ("m".equalsIgnoreCase(matcher.group(2))) {
				frequencyValue /= 12.0; 
			}
			
			for (int i = 0; i < range.length-1; i++) {
				if (range[i].equals(range[i+1])) {
					result.add(1.0);
					result.add(1.0);
				} else {
					result.add(frequencyValue);
					result.add(Math.floor((range[i+1] - range[i]) / frequencyValue));
				}			  
			}
			return result.toArray(new Double [0]);
		}
		
		return null;
	}
	
	private String[] initializeFrequencies(Integer numFrequencies) {
		String[] frequencies = new String[numFrequencies];
		for (int i = 0; i < numFrequencies; i++) {
			frequencies[i] = "1y";
		}
		return frequencies;
	}
}
