package es.ull.iis.simulation.hta.radios.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.Cost;
import es.ull.iis.ontology.radios.json.schema4simulation.FollowUp;
import es.ull.iis.ontology.radios.json.schema4simulation.FollowUpStrategy;
import es.ull.iis.ontology.radios.json.schema4simulation.Guideline;
import es.ull.iis.ontology.radios.json.schema4simulation.Intervention;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.radios.wrappers.RangeWrapper;

/**
 * @author David Prieto González
 *
 */
public class CostUtils {
	private static final String REGEXP_RANGE = "^([0-9]+)([udmy])(-([0-9]+)([dmy]))?$|^([0-9]+)([udmy])(-(\\*))?$";
	private static final String REGEXP_FRECUENCY = "^([0-9]+)([dmy])$";
	
	private static final boolean debug = true;
	
	/**
	 * @param range
	 * @param splitIntoAnnualRanges
	 * @return
	 */
	private static Double[] normalizeRange (String range, boolean splitIntoAnnualRanges, Double timeHorizont) {
		List<Double> result = new ArrayList<>();
		Pattern pattern = Pattern.compile(REGEXP_RANGE);
		Matcher matcher = pattern.matcher(range.trim());
		if (matcher.find()) {
			Double floorLimit = Double.valueOf(matcher.group(1) != null ? matcher.group(1) : matcher.group(6));
			String floorLimitUnit = matcher.group(2) != null ? matcher.group(2) : matcher.group(7); 
			if ("m".equalsIgnoreCase(floorLimitUnit)) {
				floorLimit = floorLimit/12.0;
			} else if ("d".equalsIgnoreCase(floorLimitUnit)) {
				floorLimit = floorLimit/365.0;
			} else if ("u".equalsIgnoreCase(floorLimitUnit)) {
				floorLimit = 0.0;
			}
			Double ceilLimit = timeHorizont;
			if (!"u".equalsIgnoreCase(floorLimitUnit)) {
				if (matcher.group(4) != null) {				
					ceilLimit = Double.valueOf(matcher.group(4)) > timeHorizont ? timeHorizont : Double.valueOf(matcher.group(4));
					String ceilLimitUnit = matcher.group(5); 
					if ("m".equalsIgnoreCase(ceilLimitUnit)) {
						ceilLimit = ceilLimit/12.0; 
					} else if ("d".equalsIgnoreCase(ceilLimitUnit)) {
						ceilLimit = floorLimit/365.0;
					} else if ("u".equalsIgnoreCase(ceilLimitUnit)) {
						ceilLimit = timeHorizont;
					}
				} else if (matcher.group(9) == null) {
					ceilLimit = null;
				}
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
	private static List<Double> splitFullRangeIntoAnnualRanges(List<Double> result, Double floorLimit, Double ceilLimit) {
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
	private static Double[] normalizeFrequencyAndCalculateAnnualTimes (Double[] range, String frequency) {		
		List<Double> result = new ArrayList<>();

		Pattern pattern = Pattern.compile(REGEXP_FRECUENCY);
		Matcher matcher = pattern.matcher(frequency.trim());
		if (matcher.find()) {
			Double frequencyValue = Double.valueOf(matcher.group(1));
			if ("m".equalsIgnoreCase(matcher.group(2))) {
				frequencyValue /= 12.0; 
			} else if ("d".equalsIgnoreCase(matcher.group(2))) {
				frequencyValue /= 365.0; 
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
	
	/**
	 * @param numFrequencies
	 * @return
	 */
	private static String[] initializeFrequencies(Integer numFrequencies) {
		String[] frequencies = new String[numFrequencies];
		for (int i = 0; i < numFrequencies; i++) {
			frequencies[i] = "1y";
		}
		return frequencies;
	}
	
	/**
	 * @param costsMatrix
	 * @param itemName
	 * @param costs
	 * @param guidelines
	 * @return
	 */
	public static Map<String, Double[][]> updateMatrixWithCostAndGuidelines(Map<String, Double[][]> costsMatrix, String itemName, List<Cost> costs, List<Guideline> guidelines, Double timeHorizont) {
		if (CollectionUtils.notIsEmpty(costs)) {
			for (Cost cost : costs) {
				if (CollectionUtils.notIsEmpty(guidelines)) {
					for (Guideline guideline : guidelines) {
						updateCostMatrix(costsMatrix, itemName, cost.getAmount(), guideline, timeHorizont);
					}
				} else {
					if (Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE.equalsIgnoreCase(cost.getTemporalBehavior())) {
					// TODO	
					}
				}
			}
		}
		
		return costsMatrix;
	}

	/**
	 * @param costsMatrix
	 * @param eventName
	 * @param cost
	 * @param guideline
	 * @return
	 */
	private static Map<String, Double[][]> updateCostMatrix(Map<String, Double[][]> costsMatrix, String eventName, String cost, Guideline guideline, Double timeHorizont) {
		if (debug) {
			System.out.println(String.format("Calculando costes para %s", eventName));
		}				
		if (costsMatrix.keySet().contains(eventName)) {
			if (costsMatrix.get(eventName) != null) {
				for (int i = 0; i < costsMatrix.get(eventName).length; i++) {
					costsMatrix.get(eventName)[i][2] *= 2.0;
					costsMatrix.get(eventName)[i][3] *= 2.0;
					costsMatrix.get(eventName)[i][5] *= 2.0;
				}
			}
		} else {
			String[] ranges = guideline.getRange().split(",");				
			// FIXME: ver la bondad de inicializar las frecuencias. Para el caso de rango=1u y sin frecuencia especificada no va bien.
			String[] frequencies = initializeFrequencies(ranges.length);
			if (guideline.getFrequency() != null) {
				frequencies = guideline.getFrequency().split(",");
			}
			if (ranges == null || frequencies == null || (ranges != null && frequencies != null && ranges.length != frequencies.length)) {
				throw new RuntimeException(String.format("Error en la correlacion de rango y frecuencia para %s", eventName));
			}
			Double[][] costValues = new Double[ranges.length][6];
			for (int i = 0; i < ranges.length; i++) {
				Double[] normalizedRange = normalizeRange(ranges[i], false, timeHorizont);
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
			costsMatrix.put(eventName, costValues);
		}
		return costsMatrix;
	}

	/**
	 * Show intervention cost matrix 
	 */
	public static void showCostMatrix(Map<String, Double[][]> costs) {
		for (String key: costs.keySet()) {
			System.out.println(String.format("%s", key));
			for (int i = 0; i < costs.get(key).length ; i++) {
				System.out.println(String.format("\trange.floor=%s, range.ceil=%s, frequency=%s, rangeTimes=%s, cost=%S, annualCost=%s", 
						costs.get(key)[i][0], costs.get(key)[i][1], costs.get(key)[i][2], costs.get(key)[i][3], costs.get(key)[i][4], costs.get(key)[i][5]));
			}
		}
	}

	/**
	 * @param range
	 * @param patientAge
	 * @param showResult
	 * @return
	 */
	private static RangeWrapper isAgeIntoRange (String range, Double patientAge, Boolean showResult) {
		RangeWrapper result = new RangeWrapper(-1, -1.0, -1.0);
		Boolean finded = false;
		Pattern pattern = Pattern.compile(REGEXP_RANGE);
		String[] rangeSplitted = range.split(",");
		for (int i = 0; i < rangeSplitted.length; i++) {
			Matcher matcher = pattern.matcher(rangeSplitted[i].trim());
			if (matcher.find()) {
				Double floorLimit = Double.valueOf(matcher.group(1) != null ? matcher.group(1) : matcher.group(6));
				String floorLimitStep = matcher.group(2) != null ? matcher.group(2) : matcher.group(7); 
				if ("m".equalsIgnoreCase(floorLimitStep)) {
					floorLimit = floorLimit/12.0;
				}
				Double ceilLimit = 500.0; 
				if (matcher.group(4) != null) {
					ceilLimit = Double.valueOf(matcher.group(4));
					String ceilLimitStep = matcher.group(5); 
					if ("m".equalsIgnoreCase(ceilLimitStep)) {
						ceilLimit = ceilLimit/12.0; 
					}
				} else if (matcher.group(9) == null) {
					ceilLimit = null;
				}
				if (ceilLimit != null) {
					if (floorLimit <= patientAge && patientAge <= ceilLimit) {
						result.setIndex(i).setFloorLimit(floorLimit).setCeilLimit(ceilLimit);
						if (showResult) {
							System.out.println(String.format("\tLimits: %.4f - %.4f [Patient Age = %.4f] ==> Result = %s", floorLimit, ceilLimit, patientAge, finded));
						}
						break;
					}
					
				} else {
					if (floorLimit.equals(patientAge)) {
						result.setIndex(i).setFloorLimit(floorLimit).setCeilLimit(ceilLimit);
						if (showResult) {
							System.out.println(String.format("\tLimits: %.4f [Patient Age = %.4f] ==> Result = %s", floorLimit, patientAge, finded));
						}
						break;
					}
				}
			}
		}
		return result.getIndex() >= 0 ? result : null;
	}
	
	/**
	 * @param range
	 * @param frequency
	 * @return
	 */
	public static Double calculateAnnualTimes (RangeWrapper range, String frequency) {		
		Double result = 0.0;

		Pattern pattern = Pattern.compile(REGEXP_FRECUENCY);
		Matcher matcher = pattern.matcher(frequency.trim());
		if (matcher.find()) {
			Double frequencyValue = Double.valueOf(matcher.group(1));
			if ("m".equalsIgnoreCase(matcher.group(2))) {
				frequencyValue /= 12.0; 
			}
			result = (range.getCeilLimit() - range.getFloorLimit()) / frequencyValue;  
		}
		
		return Math.floor(result);		
	}
	
	/**
	 * Calculate the annual cost of a follow-up strategy
	 * @param intervention
	 * @param temporalBehavior
	 * @param patientAge
	 * @return
	 */
	public static Double calculateCostOfFollowStrategies (Intervention intervention, String temporalBehavior, Double patientAge, Map<String, Double[][]> costs, Double timeHorizonts, Boolean showResult) {
		double result = 0.0;
		if (CollectionUtils.notIsEmpty(intervention.getFollowUpStrategies())) {
			for (FollowUpStrategy followUpStrategy : intervention.getFollowUpStrategies()) {
				if (CollectionUtils.notIsEmpty(followUpStrategy.getCosts())) {
					for (Cost cost : followUpStrategy.getCosts()) {
						if (temporalBehavior.equalsIgnoreCase(cost.getTemporalBehavior())) {
							result += Double.parseDouble(cost.getAmount());
						}
					}
				} else {
					for (FollowUp followUp : followUpStrategy.getFollowUps()) {
						if (CollectionUtils.notIsEmpty(followUp.getCosts())) {
							for (Cost cost : followUp.getCosts()) {
								if (temporalBehavior.equalsIgnoreCase(cost.getTemporalBehavior())) {
									if (Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE.equalsIgnoreCase(temporalBehavior)) {
										if (CollectionUtils.notIsEmpty(followUp.getGuidelines())) {
											for (Guideline guideline : followUp.getGuidelines()) {
												RangeWrapper rangeWrapper = isAgeIntoRange(guideline.getRange(), patientAge, false);
												if (rangeWrapper != null) {
													String frecuency = guideline.getFrequency().split(",")[rangeWrapper.getIndex()];
													Double annualTimes = calculateAnnualTimes(rangeWrapper, frecuency);
													if (showResult) {
														String range = guideline.getRange().split(",")[rangeWrapper.getIndex()];
														System.err.println(String.format("\t\tFollowUp [%s] - Range [%s] - Frecuency [%s] - AnnualTimes [%s]", followUp.getName(), range, frecuency, annualTimes));
													}
												}																								
											}
										}
									} else {
										result += Double.parseDouble(cost.getAmount());	
									}									
								}
							}
						}
					}
				}
			}
		}		
		return result;
	}
	
	public static void main(String[] args) {
		boolean showResults = false;
		
		System.out.println("\tFinded position: " + isAgeIntoRange("6y-*", 7.0, showResults));
		System.out.println("\tFinded position: " + isAgeIntoRange("8m,10y", 8.0/12.0, showResults));
		System.out.println("\tFinded position: " + isAgeIntoRange("5y", 5.0, showResults));
		System.out.println("\tFinded position: " + isAgeIntoRange("0y-1y,1y-5y,6y-*", 2.0, showResults));

		RangeWrapper range = new RangeWrapper(0, 0.0, 5.0);
		System.out.println(calculateAnnualTimes(range, "7m"));
	}
}
