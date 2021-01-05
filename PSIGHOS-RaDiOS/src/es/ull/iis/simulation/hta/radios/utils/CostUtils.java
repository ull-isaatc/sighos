package es.ull.iis.simulation.hta.radios.utils;

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

public class CostUtils {
	private static String REGEXP_RANGE = "^([0-9]+)([my])(-([0-9]+)([my]))?$|^([0-9]+)([my])(-(\\*))?$";
	private static String REGEXP_FRECUENCY = "^([0-9]+)([my])$";
	private static Double MAX_VALUE = 7777.7;
	
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
				Double ceilLimit = MAX_VALUE; 
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
	 * @param cost
	 * @param patientAge
	 * @param range
	 * @param frequency
	 * @return
	 */
	private Double calculateAnnualCostByFrequncy (Double cost, Double patientAge, RangeWrapper range, String frequency) {
		Double result = 0.0;

		Pattern pattern = Pattern.compile(REGEXP_FRECUENCY);
		Matcher matcher = pattern.matcher(frequency.trim());
		if (matcher.find()) {
			
		}
		
		// result += cost * 
		return result;		
	}
	
	/**
	 * Calculate the annual cost of a follow-up strategy
	 * @param intervention
	 * @param temporalBehavior
	 * @param patientAge
	 * @return
	 */
	public static Double calculateCostOfFollowStrategies (Intervention intervention, String temporalBehavior, Double patientAge, Boolean showResult) {
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
													String range = guideline.getRange().split(",")[rangeWrapper.getIndex()];
													String frecuency = guideline.getFrequency().split(",")[rangeWrapper.getIndex()];
													if (showResult) {
														System.err.println(String.format("\tFollowUp [%s] - Range [%s] - Frecuency [%s]", followUp.getName(), range, frecuency));
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
		System.out.println("\tFinded position: " + isAgeIntoRange("6y-*", 7.0, false));
		System.out.println("\tFinded position: " + isAgeIntoRange("8m,10y", 8.0/12.0, false));
		System.out.println("\tFinded position: " + isAgeIntoRange("5y", 5.0, false));
		System.out.println("\tFinded position: " + isAgeIntoRange("0y-1y,1y-5y,6y-*", 2.0, false));
		
		Double coste = 10.0;
		Double edad = 3.0;
		Double rango = 7.0 - 0.0;
		Double frecuencia = 4.0;
		System.out.println(rango/frecuencia);
	}
}
