package es.ull.iis.simulation.hta.radios.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
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
import es.ull.iis.simulation.hta.radios.transforms.ValueTransform;
import es.ull.iis.simulation.hta.radios.wrappers.CostMatrixElement;
import es.ull.iis.simulation.hta.radios.wrappers.Matrix;
import es.ull.iis.simulation.hta.radios.wrappers.ProbabilityDistribution;
import es.ull.iis.simulation.hta.radios.wrappers.TimeCostEvent;
import simkit.random.RandomVariate;

/**
 * @author David Prieto González
 *
 */
public class CostUtils {
	private static final String REGEXP_RANGE = "^([0-9]+)([udmy])(-([0-9]+)([dmy]))$|^([0-9]+)([udmy])(-(\\*))$|^([&@])([0-9]+)([udmy])";
	private static final String REGEXP_FRECUENCY = "^([0-9]+)([dmy])$";
	private static final String REGEXP_DOSE = "^(([0-9]+)(\\.[0-9]+)?)[a-zA-Z]*(/kg)?$";

	private static Pattern rangePattern = Pattern.compile(REGEXP_RANGE);
	private static Pattern frequencyPattern = Pattern.compile(REGEXP_FRECUENCY);
	private static Pattern dosePattern = Pattern.compile(REGEXP_DOSE);
	
	public static Predicate<TimeCostEvent> searchTimeEventPredicate(Double timeEvent) {
		return (TimeCostEvent tce) -> {
			return tce.getTimeEvent() == timeEvent;
		};
	}
	
	/**
	 * @param range
	 * @param splitIntoAnnualRanges
	 * @return
	 */
	private static Boolean normalizeRange(CostMatrixElement value, String range, String frequency, Integer timeHorizont) {
		Matcher matcher = rangePattern.matcher(range.trim());
		if (matcher.find()) {
			List<String> floorLimitUnitAndPreffix = getFloorLimitRangeUnitAndPreffix(matcher);
			String preffixRange = floorLimitUnitAndPreffix.get(0);
			String floorLimitUnit = floorLimitUnitAndPreffix.get(1);
			Double floorLimit = getFloorLimitRange(matcher);
			Double floorLimitInYears = toYears(getFloorLimitRange(matcher), floorLimitUnit.toLowerCase());
			value.setFloorLimitRange(floorLimitInYears);
			
			if (preffixRange != null) {
				if ("&".equals(preffixRange)) {
					value.getTimesCostsEvents().add(TimeCostEvent.fromTimeEvent(floorLimitInYears));
					value.setCeilLimitRange(floorLimitInYears);
				} else if ("@".equals(preffixRange)) {
					if (value.getCostExpression() == null) {
						value.setCost(value.getCost() * floorLimit);
					} else {
						Double f = normalizeFrequency(frequency, false);
						value.setCostExpression(String.format("(%s * %s) / %s", value.getCostExpression(), floorLimit, f));
					}
				}
			} else {
				Double ceilLimitInYears = 1000.0;
				if (matcher.group(4) != null) {
					String ceilLimitUnit = matcher.group(5);
					ceilLimitInYears = toYears(Double.valueOf(matcher.group(4)), ceilLimitUnit);
				}
				value.setCeilLimitRange(ceilLimitInYears);

				value.getTimesCostsEvents().add(TimeCostEvent.fromTimeEvent(floorLimitInYears));
				Double f = normalizeFrequency(frequency, true);
				value.setFrequency(f);
				Double newLimit = floorLimitInYears + f;
				while (newLimit <= ceilLimitInYears) {
					boolean existEvent = false;
					for (TimeCostEvent tce : value.getTimesCostsEvents()) {
						if (tce.getTimeEvent() == newLimit) {
							existEvent = true;
							break;
						}
					}
					if (!existEvent) {
						value.getTimesCostsEvents().add(TimeCostEvent.fromTimeEvent(newLimit));
					}
					newLimit = newLimit + f;
				}
			}
		}

		return true;
	}

	/**
	 * @param matcher
	 * @return
	 */
	private static List<String> getFloorLimitRangeUnitAndPreffix(Matcher matcher) {
		List<String> result = new ArrayList<>();
		result.add(matcher.group(10));
		if (matcher.group(2) != null) {
			result.add(matcher.group(2));
		} else if (matcher.group(7) != null) {
			result.add(matcher.group(7));
		} else {
			result.add(matcher.group(12));
		}
		return result;
	}

	/**
	 * @param matcher
	 * @return
	 */
	private static Double getFloorLimitRange(Matcher matcher) {
		Double floorLimit = 0.0;
		if (matcher.group(1) != null) {
			floorLimit = Double.valueOf(matcher.group(1));
		} else if (matcher.group(6) != null) {
			floorLimit = Double.valueOf(matcher.group(6));
		} else {
			floorLimit = Double.valueOf(matcher.group(11));
		}
		return floorLimit;
	}

	/**
	 * @param dose
	 * @return
	 */
	private static String normalizeDose(String dose) {
		if (dose == null) {
			return null;
		}
		String result = null;
		Matcher matcher = dosePattern.matcher(dose.toLowerCase().replace(" ", ""));
		if (matcher.find()) {
			result = Double.valueOf(matcher.group(1)).toString();
			if (matcher.group(4) != null) {
				result += " * weight"; 
			}
		}
		return result;
	}
	
	/**
	 * @return Returns the normalized frequency and the number of times that, according to that frequency, an event will occur.
	 */
	private static Double normalizeFrequency(String frequency, Boolean toYear) {
		if (frequency == null) {
			return 1.0;
		}
		Double result = 1.0;
		Matcher matcher = frequencyPattern.matcher(frequency.trim());
		if (matcher.find()) {
			if (toYear) {
				result = toYears(Double.valueOf(matcher.group(1)), matcher.group(2));
			} else {
				result = Double.valueOf(matcher.group(1));
			}
		}
		return result;
	}

	/**
	 * @param value
	 * @param step
	 * @return
	 */
	private static Double toYears(Double value, String step) {
		Double result = value;
		switch (step.toLowerCase()) {
		case "m":
			result /= 12.0;
			break;
		case "d":
			result /= 365.0;
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * @param numFrequencies
	 * @return
	 */
	private static String[] initializeFrequencies(Integer numFrequencies, Integer timeHorizont) {
		String[] frequencies = new String[numFrequencies];
		for (int i = 0; i < numFrequencies; i++) {
			frequencies[i] = Math.round(timeHorizont) + "y";
		}
		return frequencies;
	}

	/**
	 * @param costs
	 * @param itemName
	 * @param costsItem
	 * @param guidelines
	 * @return
	 */
	public static Matrix updateMatrixWithCostAndGuidelines(Matrix costs, String strategyName, String itemName, List<Cost> costsItem, List<Guideline> guidelines, Integer timeHorizont) {
		if (CollectionUtils.notIsEmpty(costsItem)) {
			for (Cost cost : costsItem) {
				if (CollectionUtils.notIsEmpty(guidelines)) {
					for (Guideline guideline : guidelines) {
						updateCostMatrix(costs, strategyName, itemName, cost.getAmount(), guideline, timeHorizont, Integer.parseInt(cost.getYear()));
					}
				} else if (Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE.equalsIgnoreCase(cost.getTemporalBehavior())) {
					// TODO
				} else if (Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE.equalsIgnoreCase(cost.getTemporalBehavior())) {
					List<CostMatrixElement> costList = costs.get(strategyName, itemName);
					if (costList == null) {
						costList = new ArrayList<>();
					}
					costList.add(new CostMatrixElement(0.0, null, 1.0, null, null, Double.valueOf(cost.getAmount()), null, Integer.parseInt(cost.getYear()), null));
					costs.put(strategyName, itemName, costList);
				}
			}
		}

		return costs;
	}

	/**
	 * @param costs
	 * @param itemName
	 * @param costItem
	 * @param guideline
	 * @param timeHorizont
	 * @return
	 */
	public static Matrix updateCostMatrix(Matrix costs, String strategyName, String itemName, String costItem, Guideline guideline, Integer timeHorizont, Integer costYear) {
		Boolean parseRange = true;
		CostMatrixElement value = new CostMatrixElement();

		value.setYear(costYear);		
		
		ProbabilityDistribution probabilityDistributionForCost = ValueTransform.splitProbabilityDistribution(costItem);
		value.setCost(probabilityDistributionForCost.getDeterministicValue());		
		value.setDistribution(probabilityDistributionForCost.getProbabilisticValue());
		String[] ranges = getRangesSplitted(guideline);
		String[] frequencies = getFrequenciesSplitted(guideline, timeHorizont, ranges);
		Double nTimesToDay = getNumberOfDaysForDose(guideline);

		value.setCostExpression(normalizeDose(guideline.getDose()));
		if (value.getCostExpression() != null) {
			value.setCostExpression(value.getCostExpression() + " * " + nTimesToDay);		
		}
		value.setCondition(guideline.getConditions() != null ? guideline.getConditions().toLowerCase() : null);

		if (parseRange) {
			for (int i = 0; i < ranges.length; i++) {
				normalizeRange(value, ranges[i], frequencies[i], timeHorizont);
			}
		}

		if (value.getCostExpression() != null) {
			value.setCostExpression("(" + value.getCostExpression() + ") * cost");
		}
		
		if (costs.get(strategyName, itemName) != null) {
			List<CostMatrixElement> tmp = new ArrayList<>();
			tmp.addAll(costs.get(strategyName, itemName));
			tmp.add(value);
			costs.put(strategyName, itemName, tmp);
		} else {
			costs.put(strategyName, itemName, Arrays.asList(value));
		}
		
		return costs;
	}

	/**
	 * @param guideline
	 * @return
	 */
	private static Double getNumberOfDaysForDose(Guideline guideline) {
		Double nTimesToDay = guideline.getHoursIntervals() != null ? 24.0 / Double.valueOf(guideline.getHoursIntervals()) : 1.0;
		return nTimesToDay;
	}

	/**
	 * @param guideline
	 * @param timeHorizont
	 * @param ranges
	 * @return
	 */
	private static String[] getFrequenciesSplitted(Guideline guideline, Integer timeHorizont, String[] ranges) {
		String[] frequencies = (guideline.getFrequency() != null) ? frequencies = guideline.getFrequency().split(",") : initializeFrequencies(ranges.length, timeHorizont);
		return frequencies;
	}

	/**
	 * @param guideline
	 * @return
	 */
	private static String[] getRangesSplitted(Guideline guideline) {
		String[] ranges = (guideline.getRange() != null) ? guideline.getRange().split(",") : null;
		return ranges;
	}

	/**
	 * @param costs
	 * @param screeningStrategies
	 * @param timeHorizont
	 */
	public static void loadCostFromScreeningStrategies(Matrix costs, List<ScreeningStrategy> screeningStrategies, Integer timeHorizont) {
		if (CollectionUtils.notIsEmpty(screeningStrategies)) {
			for (ScreeningStrategy strategy : screeningStrategies) {
				for (ScreeningTechnique item : strategy.getScreeningTechniques()) {
					updateMatrixWithCostAndGuidelines(costs, strategy.getName(), item.getName(), item.getCosts(), NewbornGuideline.getInstance(), timeHorizont);
				}
			}
		}
	}

	/**
	 * @param costs
	 * @param clinicalDiagnosisStrategies
	 * @param timeHorizont
	 */
	public static void loadCostFromClinicalDiagnosisStrategies(Matrix costs, List<ClinicalDiagnosisStrategy> clinicalDiagnosisStrategies, Integer timeHorizont) {
		if (CollectionUtils.notIsEmpty(clinicalDiagnosisStrategies)) {
			for (ClinicalDiagnosisStrategy strategy : clinicalDiagnosisStrategies) {
				for (ClinicalDiagnosisTechnique item : strategy.getClinicalDiagnosisTechniques()) {
					updateMatrixWithCostAndGuidelines(costs, strategy.getName(), item.getName(), item.getCosts(), NewbornGuideline.getInstance(), timeHorizont);
				}
			}
		}
	}

	/**
	 * @param costs
	 * @param treatmentStrategies
	 * @param timeHorizont
	 */
	public static void loadCostFromTreatmentStrategies(Matrix costs, String manifestationName, List<TreatmentStrategy> treatmentStrategies, Integer timeHorizont) {
		if (CollectionUtils.notIsEmpty(treatmentStrategies)) {
			for (TreatmentStrategy strategy : treatmentStrategies) {
				for (Treatment item : strategy.getTreatments()) {
					updateMatrixWithCostAndGuidelines(costs, manifestationName, item.getName(), item.getCosts(), item.getGuidelines(), timeHorizont);
					for (Drug drug : item.getDrugs()) {
						updateMatrixWithCostAndGuidelines(costs, manifestationName, drug.getName(), drug.getCosts(), drug.getGuidelines(), timeHorizont);
					}
				}
				if (strategy.getCosts() != null) {
					updateMatrixWithCostAndGuidelines(costs, manifestationName, strategy.getName(), strategy.getCosts(), strategy.getGuidelines(), timeHorizont);
				}
			}
		}
	}

	/**
	 * @param costs
	 * @param followUpStrategies
	 * @param timeHorizont
	 */
	public static void loadCostFromFollowUpStrategies(Matrix costs, String manifestationName, List<FollowUpStrategy> followUpStrategies, Integer timeHorizont) {
		if (CollectionUtils.notIsEmpty(followUpStrategies)) {
			for (FollowUpStrategy strategy : followUpStrategies) {
				for (FollowUp item : strategy.getFollowUps()) {
					updateMatrixWithCostAndGuidelines(costs, manifestationName, item.getName(), item.getCosts(), item.getGuidelines(), timeHorizont);
				}
				if (strategy.getCosts() != null) {
					updateMatrixWithCostAndGuidelines(costs, manifestationName, strategy.getName(), strategy.getCosts(), strategy.getGuidelines(), timeHorizont);
				}
			}
		}
	}

	/**
	 * Show intervention cost matrix
	 */
	public static String showCostMatrix(Matrix costs, String prefix) {
		StringBuffer sb = new StringBuffer();
		for (String keyR : costs.keySetR()) {
			sb.append(String.format("%s%s:\n", prefix, keyR));
			for (String keyC : costs.keySetC(keyR)) {
				sb.append(String.format("%s%s:\n", prefix + "\t", keyC));
				for (CostMatrixElement e : costs.get(keyR, keyC)) {
					sb.append(e.toString(prefix + "\t\t"));
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * For the biotidinase example, the estrada data are normalized to the same year. This method should discount the costs to pass 
	 * them all to the same year if there are discrepancies.
	 * 
	 * @param costs
	 * @return
	 */
	public static Object[] calculateAnnualCostFromMatrix (Matrix costs) {		
		Integer year = 1900;
		Double amount = 0.0;
		List<RandomVariate> distributions = new ArrayList<>();
		
		for (String keyR : costs.keySetR()) {
			for (String keyC : costs.keySetC(keyR)) {
				for (CostMatrixElement e : costs.get(keyR, keyC)) {
					// FIXME: for the example of biotidinase it would be valid but both things should be taken into account
					if (e.getCondition() == null && e.getCostExpression() == null) { 
						if (e.getFrequency() == 1.0) {
							amount += e.getCost();
							if (year == 1900) {
								year = e.getYear();
							}
							distributions.add(e.getDistribution());
						} else {
							// TODO: the calculation would have to be made for when the frequency is not once a year.
						}
					}
				}
			}
		}
		
		Object [] result = {year, amount, (distributions.size() == 1) ? distributions.get(0) : null};
		return result;
	}		

	/**
	 * @param costs
	 * @return
	 */
	public static Object[] calculateOnetimeCostFromMatrix (Matrix costs) {		
		Integer year = 1900;
		Double amount = 0.0;
		List<RandomVariate> distributions = new ArrayList<>();
		
		for (String keyR : costs.keySetR()) {
			for (String keyC : costs.keySetC(keyR)) {
				for (CostMatrixElement e : costs.get(keyR, keyC)) {
					// FIXME: for the example of biotidinase it would be valid but both things should be taken into account
					if (e.getCondition() == null && e.getCostExpression() == null) {
						Double tmp = e.calculateNTimesInRange(0.0, 1000.0); 
						if (tmp == 1.0) {							
							amount += e.getCost();
							if (year == 1900) {
								year = e.getYear();
							}
							distributions.add(e.getDistribution());
						}
					}
				}
			}
		}
		
		Object [] result = {year, amount, (distributions.size() == 1) ? distributions.get(0) : null};
		return result;
	}		

}
