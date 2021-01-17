package es.ull.iis.simulation.hta.radios.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.ontology.radios.json.schema4simulation.ClinicalDiagnosisStrategy;
import es.ull.iis.ontology.radios.json.schema4simulation.ClinicalDiagnosisTechnique;
import es.ull.iis.ontology.radios.json.schema4simulation.Cost;
import es.ull.iis.ontology.radios.json.schema4simulation.Drug;
import es.ull.iis.ontology.radios.json.schema4simulation.FollowUp;
import es.ull.iis.ontology.radios.json.schema4simulation.FollowUpStrategy;
import es.ull.iis.ontology.radios.json.schema4simulation.Guideline;
import es.ull.iis.ontology.radios.json.schema4simulation.ScreeningStrategy;
import es.ull.iis.ontology.radios.json.schema4simulation.ScreeningTechnique;
import es.ull.iis.ontology.radios.json.schema4simulation.Treatment;
import es.ull.iis.ontology.radios.json.schema4simulation.TreatmentStrategy;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.radios.NewbornGuideline;
import es.ull.iis.simulation.hta.radios.wrappers.RangeWrapper;

/**
 * @author David Prieto González
 *
 */
public class CostUtils2 {
	private static final String REGEXP_RANGE = "^([0-9]+)([udmy])(-([0-9]+)([dmy]))?$|^([0-9]+)([udmy])(-(\\*))?$";
	private static final String REGEXP_FRECUENCY = "^([0-9]+)([dmy])$";
	
	private static final boolean debug = true;
	
	/**
	 * @param range
	 * @param splitIntoAnnualRanges
	 * @return
	 */
	private static Double[] normalizeRange (String range, Double timeHorizont, boolean splitIntoAnnualRanges) {
		List<Double> result = new ArrayList<>();
		Pattern pattern = Pattern.compile(REGEXP_RANGE);
		Matcher matcher = pattern.matcher(range.trim());
		if (matcher.find()) {
			Double floorLimit = Double.valueOf(matcher.group(1) != null ? matcher.group(1) : matcher.group(6));
			String floorLimitUnit = matcher.group(2) != null ? matcher.group(2) : matcher.group(7);
			Double nTimes = 1.0;
			switch (floorLimitUnit.toLowerCase()) {
			case "m":
				floorLimit = floorLimit/12.0; break;
			case "d":
				floorLimit = floorLimit/365.0; break;
			case "u":	
				nTimes = floorLimit;
				floorLimit = 0.0; break;
			default: break;
			}
			
			Double ceilLimit = timeHorizont;
			if (!"u".equalsIgnoreCase(floorLimitUnit)) {
				if (matcher.group(4) != null) {
					ceilLimit = Double.valueOf(matcher.group(4)) > timeHorizont ? timeHorizont : Double.valueOf(matcher.group(4));
					String ceilLimitUnit = matcher.group(5); 
					switch (ceilLimitUnit.toLowerCase()) {
					case "m":
						ceilLimit = ceilLimit/12.0; break;
					case "d":
						ceilLimit = floorLimit/365.0; break;
					case "u":				
						ceilLimit = timeHorizont; break;
					default: break;
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
				result.add(nTimes);
			}
			
			return result.toArray(new Double [0]);
		}
		return null;
	}

	/**
	 * @param range
	 * @param splitIntoAnnualRanges
	 * @return
	 */
	private static List<Double> normalizeRange2 (String range, String frequency, Double timeHorizont) {
		List<Double> result = new ArrayList<>();
		Pattern pattern = Pattern.compile(REGEXP_RANGE);
		Matcher matcher = pattern.matcher(range.trim());
		if (matcher.find()) {
			Double floorLimit = Double.valueOf(matcher.group(1) != null ? matcher.group(1) : matcher.group(6));
			String floorLimitUnit = matcher.group(2) != null ? matcher.group(2) : matcher.group(7);
			switch (floorLimitUnit.toLowerCase()) {
			case "m":
				floorLimit = floorLimit/12.0; break;
			case "d":
				floorLimit = floorLimit/365.0; break;
			default: break;
			}
			
			Double ceilLimit = timeHorizont;
			if (matcher.group(4) != null) {
				ceilLimit = Double.valueOf(matcher.group(4)) > timeHorizont ? timeHorizont : Double.valueOf(matcher.group(4));
				String ceilLimitUnit = matcher.group(5); 
				switch (ceilLimitUnit.toLowerCase()) {
				case "m":
					ceilLimit = ceilLimit/12.0; break;
				case "d":
					ceilLimit = floorLimit/365.0; break;
				default: break;
				}
			} else if (matcher.group(9) == null) {
				ceilLimit = null;
			}
			
			result.add(floorLimit);
			if (ceilLimit != null) {
				Double f = normalizeFrequency(frequency);
				Double newLimit = floorLimit + f;
				while (newLimit < ceilLimit) {
					result.add(newLimit);
					newLimit = newLimit + f;
				}
				result.add(ceilLimit);
			}
		}
		return result;
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
	private static Double[] normalizeFrequencyAndCalculateNumberTimesInRange (Double[] range, String frequency) {		
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
	 * @param value
	 * @param step
	 * @return
	 */
	private static Double toYears (Double value, String step) {
		Double result = value;
		switch (step.toLowerCase()) {
		case "m":
			result /= 12.0; break;
		case "d":
			result /= 365.0; break;
		default: break;
		}
		return result;		
	}
	
	/**
	 * @return Returns the normalized frequency and the number of times that, according to that frequency, an event will occur.
	 */
	private static Double normalizeFrequency (String frequency) {		
		if (frequency == null) {
			return 1.0;
		}
		Double result = 1.0;
		Pattern pattern = Pattern.compile(REGEXP_FRECUENCY);
		Matcher matcher = pattern.matcher(frequency.trim());
		if (matcher.find()) {
			result = toYears(Double.valueOf(matcher.group(1)), matcher.group(2));
		}
		return result;
	}
	
	/**
	 * @param numFrequencies
	 * @return
	 */
	private static String[] initializeFrequencies(Integer numFrequencies, Double timeHorizont) {
		String[] frequencies = new String[numFrequencies];
		for (int i = 0; i < numFrequencies; i++) {
			frequencies[i] = Math.round(timeHorizont) + "y";
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
	 * @param itemName
	 * @param cost
	 * @param guideline
	 * @return
	 */
	private static Map<String, Double[][]> updateCostMatrix(Map<String, Double[][]> costsMatrix, String itemName, String cost, Guideline guideline, Double timeHorizont) {
		if (!costsMatrix.keySet().contains(itemName)) {
			if (debug) {
				String debugItemName = "CefotaximaIV";
				if (itemName.contains(debugItemName)) {
					System.out.println();
				}
			}
			
			String[] ranges = guideline.getRange().split(",");				
			String[] frequencies = initializeFrequencies(ranges.length, timeHorizont);
			Double dose = guideline.getDose() != null ? Double.valueOf(guideline.getDose()) : 1.0;
			Double nTimesToDay = guideline.getHoursIntervals() != null ? 24.0 / Double.valueOf(guideline.getHoursIntervals()) : 1.0;
			if (guideline.getFrequency() != null) {
				frequencies = guideline.getFrequency().split(",");
				if (ranges == null || frequencies == null || (ranges != null && frequencies != null && ranges.length != frequencies.length)) {
					throw new RuntimeException(String.format("Error en la correlacion de rango y frecuencia para %s", itemName));
				}
			}
			
			Double[][] costValues = new Double[ranges.length][7];
			for (int i = 0; i < ranges.length; i++) {
				Double[] normalizedRange = normalizeRange(ranges[i], timeHorizont, false);
				Double nTimes = normalizedRange[2];
				normalizedRange = Arrays.copyOf(normalizedRange, normalizedRange.length-1); 
				Double[] frequencyNormalized = normalizeFrequencyAndCalculateNumberTimesInRange(normalizedRange, frequencies[i]);
				Double costValue = Double.valueOf(cost);
				costValues[i][0] = normalizedRange[0];
				costValues[i][1] = normalizedRange[1];
				costValues[i][2] = frequencyNormalized[0];
				costValues[i][3] = frequencyNormalized[1];
				costValues[i][4] = costValue; // TODO: caso de la prueba de cribado que se ejecuta dos veces, no se está teniendo en cuenta porque se sobreescribe el hashmap
				costValues[i][5] = nTimes; 
				if (normalizedRange[1].equals(normalizedRange[0])) {
					costValues[i][6] = frequencyNormalized[1] * costValue;
				} else {
					// Prorated Cost 
					costValues[i][6] = (nTimes * dose * costValue * nTimesToDay * frequencyNormalized[1]) / (normalizedRange[1] - normalizedRange[0]);
				}
			}
			costsMatrix.put(itemName, costValues);
		}
		return costsMatrix;
	}

	@SuppressWarnings("unused")
	public static Map<String, List<Double>> updateCostMatrix2(Map<String, List<Double>> costsMatrix, String itemName, String cost, Guideline guideline, Double timeHorizont) {
		String[] ranges = guideline.getRange().split(",");				
		String[] frequencies = initializeFrequencies(ranges.length, timeHorizont);
		Double dose = guideline.getDose() != null ? Double.valueOf(guideline.getDose()) : 1.0;
		Double nTimesToDay = guideline.getHoursIntervals() != null ? 24.0 / Double.valueOf(guideline.getHoursIntervals()) : 1.0;
		if (guideline.getFrequency() != null) {
			frequencies = guideline.getFrequency().split(",");
			if (ranges == null || frequencies == null || (ranges != null && frequencies != null && ranges.length != frequencies.length)) {
				throw new RuntimeException(String.format("Error en la correlacion de rango y frecuencia para %s", itemName));
			}
		}
		
		List<Double> result = new ArrayList<>();
		for (int i = 0; i < ranges.length; i++) {
			result.addAll(normalizeRange2(ranges[i], frequencies[i], timeHorizont));
		}
		result.add(0, Double.valueOf(cost));
		costsMatrix.put(itemName, result);
		return costsMatrix;
	}
	
	/**
	 * Show intervention cost matrix 
	 */
	public static void showCostMatrix(Map<String, Double[][]> costs) {
		for (String key: costs.keySet()) {
			System.out.println(String.format("%s", key));
			for (int i = 0; i < costs.get(key).length ; i++) {
				System.out.println(String.format("\trange {%.3fy - %.3fy}, frequency=%.3fy, numberTimesInRange=%.3f, cost=%.3f, multiplicativeCostFactor=%.3f, annualCost=%.3f", 
						costs.get(key)[i][0], costs.get(key)[i][1], costs.get(key)[i][2], costs.get(key)[i][3], costs.get(key)[i][4], costs.get(key)[i][5], costs.get(key)[i][6]));
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
	 * @param costs
	 * @param screeningStrategies
	 * @param timeHorizont
	 */
	public static void loadCostFromScreeningStrategies(Map<String, Double[][]> costs, List<ScreeningStrategy> screeningStrategies, Double timeHorizont) {
		if (CollectionUtils.notIsEmpty(screeningStrategies)) {
			for (ScreeningStrategy strategy : screeningStrategies) {
				for (ScreeningTechnique item : strategy.getScreeningTechniques()) {
					updateMatrixWithCostAndGuidelines(costs, item.getName(), item.getCosts(), NewbornGuideline.getInstance(), timeHorizont);
				}
			}
		}
	}
	
	/**
	 * @param costs
	 * @param clinicalDiagnosisStrategies
	 * @param timeHorizont
	 */
	public static void loadCostFromClinicalDiagnosisStrategies(Map<String, Double[][]> costs, List<ClinicalDiagnosisStrategy> clinicalDiagnosisStrategies, Double timeHorizont) {
		if (CollectionUtils.notIsEmpty(clinicalDiagnosisStrategies)) {
			for (ClinicalDiagnosisStrategy strategy : clinicalDiagnosisStrategies) {
				for (ClinicalDiagnosisTechnique item : strategy.getClinicalDiagnosisTechniques()) {
					updateMatrixWithCostAndGuidelines(costs, item.getName(), item.getCosts(), NewbornGuideline.getInstance(), timeHorizont);
				}
			}
		}
	}
	
	/**
	 * @param costs
	 * @param treatmentStrategies
	 * @param timeHorizont
	 */
	public static void loadCostFromTreatmentStrategies(Map<String, Double[][]> costs, List<TreatmentStrategy> treatmentStrategies, Double timeHorizont) {
		if (CollectionUtils.notIsEmpty(treatmentStrategies)) {
			for (TreatmentStrategy strategy : treatmentStrategies) {
				for (Treatment item : strategy.getTreatments()) {
					updateMatrixWithCostAndGuidelines(costs, item.getName(), item.getCosts(), item.getGuidelines(), timeHorizont);
					for (Drug drug : item.getDrugs()) {
						updateMatrixWithCostAndGuidelines(costs, drug.getName(), drug.getCosts(), drug.getGuidelines(), timeHorizont);
					}
				}
			}
		}
	}

	/**
	 * @param costs
	 * @param followUpStrategies
	 * @param timeHorizont
	 */
	public static void loadCostFromFollowUpStrategies(Map<String, Double[][]> costs, List<FollowUpStrategy> followUpStrategies, Double timeHorizont) {
		if (CollectionUtils.notIsEmpty(followUpStrategies)) {
			for (FollowUpStrategy strategy : followUpStrategies) {
				for (FollowUp item : strategy.getFollowUps()) {
					updateMatrixWithCostAndGuidelines(costs, item.getName(), item.getCosts(), item.getGuidelines(), timeHorizont);
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean showResults = false;
		
		System.out.println("\tFinded position: " + isAgeIntoRange("6y-*", 7.0, showResults));
		System.out.println("\tFinded position: " + isAgeIntoRange("8m,10y", 8.0/12.0, showResults));
		System.out.println("\tFinded position: " + isAgeIntoRange("5y", 5.0, showResults));
		System.out.println("\tFinded position: " + isAgeIntoRange("0y-1y,1y-5y,6y-*", 2.0, showResults));

		System.out.println(calculateAnnualTimes(new RangeWrapper(0, 0.0, 5.0), "7m"));
		
		List<Double> result = null;
		String range = "3y-*";
		String frequency = null; 
		result = normalizeRange2(range, frequency, 10.0);
		for (Double d : result) {
			System.out.print(String.format("%.3f ", d));
		}
	}
}
