package es.ull.iis.simulation.hta.osdi.utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.w3c.xsd.owl2.Ontology;

import es.ull.iis.simulation.hta.osdi.Constants;
import es.ull.iis.simulation.hta.osdi.NodeData;
import es.ull.iis.simulation.hta.osdi.OSDiNames;
import es.ull.iis.simulation.hta.osdi.TreeNode;
import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.service.DataStoreService;

public class CostsUtils {

	/**
	 * This formula is applied to costs as well as to profits.
	 * 
	 * @param discountRate Example: 0.03
	 * @param age          Example: 0
	 * @param endAge       Example: lifeExpectancy
	 * @return
	 */
	public static Double calculateDiscount2(Double value, Double discountRate, Double age) {
		Double result = value / Math.pow(1.0 + discountRate, age);
		return result;
	}

	/**
	 * This formula is applied to costs as well as to profits.
	 * 
	 * @param discountRate Example: 0.03
	 * @param initAge      Example: 0
	 * @param endAge       Example: lifeExpectancy
	 * @return
	 */
	public static Double calculateDiscount(Double anualValue, Double discountRate, Double initAge, Double endAge) {
		Double result = anualValue * (endAge - initAge);
		if (discountRate != 0) {
			Double a = Math.log(1.0 + discountRate);
			Double b = -1.0 / a;
			Double c = Math.pow(1.0 + discountRate, -endAge);
			Double d = Math.pow(1.0 + discountRate, -initAge);
			result = anualValue * b * (c - d);
		}
		return result;
	}

	/**
	 * @param ontology
	 * @param intervention
	 * @param followUpStrategyCostTotal
	 * @throws TranspilerException
	 */
	public static Double calculateInterventionStrategyCost(Ontology ontology, String intervention, String strategyName) throws TranspilerException {
		Double strategyCostTotal = 0.0;
		if (DataStoreService.eTLObjectProperties(ontology).get(intervention).containsKey(strategyName)) {
			List<String> strategies = DataStoreService.eTLObjectProperties(ontology).get(intervention).get(strategyName);
			for (String strategy : strategies) {
				Double strategyCostPartial = calculateStrategyCost(ontology, strategy, Constants.CONSTANT_COST_CALCULATION_STRATEGY_ALTERNATIVE_GLOBAL);
				if (strategyCostPartial == 0.0) {
					strategyCostPartial = calculateStrategyCost(ontology, strategy, Constants.CONSTANT_COST_CALCULATION_STRATEGY_ALTERNATIVE_SPECIFIC);
				}
				if (strategyCostPartial != null) {
					strategyCostTotal += strategyCostPartial;
				}
			}
		}
		return strategyCostTotal;
	}

	/**
	 * @param ontology
	 * @param strategy
	 * @return
	 * @throws TranspilerException
	 */
	public static Double calculateStrategyCost(Ontology ontology, String strategy) throws TranspilerException {
		Double partialCost = CostsUtils.calculateStrategyCost(ontology, strategy, Constants.CONSTANT_COST_CALCULATION_STRATEGY_ALTERNATIVE_GLOBAL);
		Double finalCost = (partialCost > 0.0) ? partialCost : CostsUtils.calculateStrategyCost(ontology, strategy, Constants.CONSTANT_COST_CALCULATION_STRATEGY_ALTERNATIVE_SPECIFIC);
		return finalCost;
	}

	/**
	 * @param ontology
	 * @param strategy
	 * @return
	 * @throws TranspilerException
	 */
	public static Double calculateStrategyCost(Ontology ontology, String strategy, int globalOrSpecificCost) throws TranspilerException {
		Double cumulativeCost = 0.0;

		if (globalOrSpecificCost == 0) {
			Map<String, List<String>> strategyObjectProperties = DataStoreService.eTLObjectProperties(ontology).get(strategy);
			if (strategyObjectProperties != null) {
				for (String strategyObjectProperty : strategyObjectProperties.keySet()) {
					List<String> strategyChilds = strategyObjectProperties.get(strategyObjectProperty);
					if (strategyChilds != null) {
						for (String strategyChild : strategyChilds) {
							if (DataStoreService.eTLClassIndividuals(ontology).get(strategyChild).equals(OSDiNames.Class.COST.getName())) {
								cumulativeCost += OntologyUtils.parseValueOrValueDistributionExpression(ontology, strategyChild, Constants.CUSTOM_PROPERTY_AMOUNT).getValue();
							}
						}
					}
				}
			}
		} else if (globalOrSpecificCost == 1) {
			Map<String, List<String>> stepsStrategy = DataStoreService.eTLObjectProperties(ontology).get(strategy);
			if (stepsStrategy != null) {
				for (String stepsStrategyKey : stepsStrategy.keySet()) {
					List<String> stepsStrategyValues = stepsStrategy.get(stepsStrategyKey);
					if (stepsStrategyValues != null) {
						for (String stepStrategyName : stepsStrategyValues) {
							Map<String, List<String>> methods = DataStoreService.eTLObjectProperties(ontology).get(stepStrategyName);
							if (methods != null) {
								for (String methodsKey : methods.keySet()) {
									List<String> methodsNames = methods.get(methodsKey);
									if (methodsNames != null) {
										for (String methodName : methodsNames) {
											Map<String, List<String>> methodObjectProperties = DataStoreService.eTLObjectProperties(ontology).get(methodName);
											if (methodObjectProperties != null) {
												for (String methodObjectProperty : methodObjectProperties.keySet()) {
													List<String> methodObjectPropertyChilds = methodObjectProperties.get(methodObjectProperty);
													if (methodObjectPropertyChilds != null) {
														for (String methodObjectPropertyChild : methodObjectPropertyChilds) {
															if (DataStoreService.eTLClassIndividuals(ontology).get(methodObjectPropertyChild).equals(OSDiNames.Class.COST.getName())) {
																cumulativeCost += OntologyUtils.parseValueOrValueDistributionExpression(ontology, methodObjectPropertyChild, Constants.CUSTOM_PROPERTY_AMOUNT)
																		.getValue();
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} else {
			throw new TranspilerException(String.format("Cost calculation alternative [%d] for the [%s] strategy not known", globalOrSpecificCost, strategy));
		}

		return cumulativeCost;
	}

	/**
	 * @param node
	 * @param cost
	 * @param costType
	 * @throws TranspilerException
	 */
	public static void addCostToNode(TreeNode<NodeData> node, Double cost, String costType) throws TranspilerException {
		node.getData().addProperty(OSDiNames.ObjectProperty.HAS_COST.getName(), String.format(Locale.US, Constants.CONSTANT_DOUBLE_FORMAT_STRING_3DEC, cost), Constants.CONSTANT_DOUBLE_TYPE);
		node.getData().addProperty(OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getName(), costType, Constants.CONSTANT_STRING_TYPE);
	}

	/**
	 * @param node
	 * @param cost
	 * @throws TranspilerException
	 */
	public static void addCummulativeCostToNode(TreeNode<NodeData> node, Double cost) throws TranspilerException {
		node.getData().addProperty(Constants.CUSTOM_PROPERTY_CUMULATIVE_COST, String.format(Locale.US, Constants.CONSTANT_DOUBLE_FORMAT_STRING_3DEC, cost), Constants.CONSTANT_DOUBLE_TYPE);
	}

	/**
	 * @param values
	 * @param avg
	 * @return
	 */
	public static double standardDeviation(List<Double> values, Double avg) {
		Double sum = 0.0;
		for (int i = 0; i < values.size(); i++) {
			sum += Math.pow(values.get(i) - avg, 2);
		}
		return Math.sqrt(sum / (double) values.size());
	}
}
