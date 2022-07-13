package es.ull.iis.simulation.hta.osdi.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.w3c.xsd.owl2.Axiom;
import org.w3c.xsd.owl2.ClassAssertion;
import org.w3c.xsd.owl2.NamedIndividual;
import org.w3c.xsd.owl2.ObjectFactory;
import org.w3c.xsd.owl2.ObjectPropertyAssertion;
import org.w3c.xsd.owl2.Ontology;

import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;
import es.ull.iis.simulation.hta.osdi.service.DataStoreService;
import es.ull.iis.simulation.hta.osdi.wrappers.ValueDistributionWrapper;

/**
 * @author davidpg
 *
 */
public class OntologyUtils {
	/**
	 * @param path
	 * @return
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Ontology loadOntology(String path) throws JAXBException, FileNotFoundException, IOException {
		Ontology ontology = null;

		try (InputStream xmlOwl = new FileInputStream(path)) {
			ontology = loadOntology(xmlOwl);
		}
		return ontology;
	}

	/**
	 * @param xmlOwl
	 * @return
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Ontology loadOntology(InputStream xmlOwl) throws JAXBException, FileNotFoundException, IOException {
		Ontology ontology = null;

		JAXBContext jc = JAXBContext.newInstance("org.w3c.xsd.owl2");
		Unmarshaller unmarshaller = jc.createUnmarshaller();

		JAXBElement<Ontology> jaxbOntology = unmarshaller.unmarshal(new StreamSource(xmlOwl), Ontology.class);
		ontology = jaxbOntology.getValue();

		return ontology;
	}

	/**
	 * @param ontology
	 * @return
	 * @throws JAXBException
	 */
	public static String getRadiosAsString(Ontology ontology) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("org.w3c.xsd.owl2");
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		ByteArrayOutputStream radiosAsOutpuStream = new ByteArrayOutputStream();
		
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<Ontology> jaxbOntology = objectFactory.createOntology(ontology);
		marshaller.marshal(jaxbOntology, radiosAsOutpuStream);

		return (radiosAsOutpuStream != null) ? new String(radiosAsOutpuStream.toByteArray()) : "";
	}

	/**
	 * @param ontology
	 * @param instanceToClazz
	 * @param disease
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static TreeNode buildNArioTreeFromDisease(Ontology ontology, Map<String, String> instanceToClazz, ClassAssertion disease) {
		TreeNode nArioTree;

		String diseaseName = disease.getNamedIndividual().getIRI();
		nArioTree = new TreeNode<NodeData>(new NodeData(diseaseName, instanceToClazz.get(diseaseName)));
		Queue<TreeNode> queue = new LinkedList<TreeNode>();
		queue.add(nArioTree);

		while (!queue.isEmpty()) {
			TreeNode searchElement = queue.poll();
			for (Axiom axiom : ontology.getAxiom()) {
				if (axiom instanceof ObjectPropertyAssertion) {
					ObjectPropertyAssertion assertion = (ObjectPropertyAssertion) axiom;
					String assertionLeftSide = ((NamedIndividual) assertion.getRest().get(1).getValue()).getIRI();
					String assertionRightSide = ((NamedIndividual) assertion.getRest().get(2).getValue()).getIRI();
					if (((NodeData) searchElement.getData()).getName().equals(assertionLeftSide)) {
						queue.add(searchElement.addChild(new NodeData(assertionRightSide, instanceToClazz.get(assertionRightSide))));
					}
				}
			}
		}
		return nArioTree;
	}

	/**
	 * @param node
	 * @throws TranspilerException
	 */
	public static void loadManifestationsFromNaturalDevelopment(Ontology ontology, String diseaseName, String intervention, TreeNode<NodeData> node, boolean includeManifestationModificacions) throws TranspilerException {
		Map<String, Map<String, PropertyData>> manifestationsResult = new HashMap<String, Map<String, PropertyData>>();

		// Step 1: we load the natural development of the disease.
		loadNaturalDevelopmentManifestations(ontology, diseaseName, manifestationsResult);

		// Step 2: we analyze those modifications of the manifestations derived directly from [Intervention -> ManifestationModification]
		if (includeManifestationModificacions) {
			manifestationsModificationsOperations(ontology, intervention, manifestationsResult);

			// TODO: Step 3: we analyze those modifications derived directly from [Intervention -> DevelopmentModification]
			if (DataStoreService.eTLObjectProperties(ontology).get(intervention).get(Constants.OBJECTPROPERTY_DEVELOPMENT_MODIFICATION) != null) {
				throw new TranspilerException("The possibility of working with development modifications from interventions is not yet available.");
			}
		}

		// Step 4: After the calculation of new probabilities, we create the combinatorial tree of those manifestations that have not been affected
		// by a zero modification of their probability. 
		removeManifestationsWithZeroProbability(manifestationsResult);
		
		List<TreeNode<NodeData>> nodeLeafs = TreeUtils.getTreeNodeLeafs(node);		
		for (TreeNode<NodeData> nodeLeaf : nodeLeafs) {
			calculateAlternatives(ontology, diseaseName, nodeLeaf, manifestationsResult);
		}
	}
	
	/**
		If we pass an intervention as a parameter, we will analyze it to see if it has associated any modification of manifestations 
		that means that it should not be included in the combination of manifestations.
	 * @param ontology
	 * @param intervention
	 * @param manifestationsResult
	 * @throws TranspilerException
	 */
	private static void manifestationsModificationsOperations(Ontology ontology, String intervention, Map<String, Map<String, PropertyData>> manifestationsResult) throws TranspilerException {
		if (intervention != null) {
			if (DataStoreService.eTLObjectProperties(ontology).get(intervention).containsKey(Constants.OBJECTPROPERTY_MANIFESTATION_MODIFICATION)) {
				List<String> manifestationsModificationList = DataStoreService.eTLObjectProperties(ontology).get(intervention).get(Constants.OBJECTPROPERTY_MANIFESTATION_MODIFICATION);
				for (String manifestationModification : manifestationsModificationList) {
					if (DataStoreService.eTLObjectProperties(ontology).get(manifestationModification).containsKey(Constants.OBJECTPROPERTY_MANIFESTATION)) {
						Map<String, PropertyData> manifestationModificationDataProperties = DataStoreService.eTLDataPropertyValues(ontology).get(manifestationModification);
						for (String manifestationModificationDataProperty : manifestationModificationDataProperties.keySet()) {
							switch (manifestationModificationDataProperty) {
							case Constants.DATAPROPERTY_PROBABILITY_MODIFICATION:
								precalculateDataForUpdateManifestation(ontology, manifestationsResult, manifestationModification,  
										Constants.DATAPROPERTY_PROBABILITY_MODIFICATION, Constants.CUSTOM_PROPERTY_PROBABILITY_MODIFICATION_DISTRIBUTION, Constants.DATAPROPERTY_PROBABILITY);
								break;
							case Constants.DATAPROPERTY_FREQUENCY_MODIFICATION:
								precalculateDataForUpdateManifestation(ontology, manifestationsResult, manifestationModification,  
										Constants.DATAPROPERTY_FREQUENCY_MODIFICATION, Constants.CUSTOM_PROPERTY_FREQUENCY_MODIFICATION_DISTRIBUTION, Constants.DATAPROPERTY_FREQUENCY);
								break;
							case Constants.DATAPROPERTY_RELATIVE_RISK_MODIFICATION:
								precalculateDataForUpdateManifestation(ontology, manifestationsResult, manifestationModification,  
										Constants.DATAPROPERTY_RELATIVE_RISK_MODIFICATION, Constants.CUSTOM_PROPERTY_RELATIVE_RISK_MODIFICATION_DISTRIBUTION, Constants.DATAPROPERTY_RELATIVE_RISK);
								break;
							case Constants.DATAPROPERTY_MORTALITY_FACTOR_MODIFICATION:
								precalculateDataForUpdateManifestation(ontology, manifestationsResult, manifestationModification,  
										Constants.DATAPROPERTY_MORTALITY_FACTOR_MODIFICATION, Constants.CUSTOM_PROPERTY_MORTALITY_FACTOR_MODIFICATION_DISTRIBUTION, Constants.DATAPROPERTY_MORTALITY_FACTOR);
								break;
							default:
								break;
							}
						}					
					}
				}
			}
		}
	}

	/**
	 * @param ontology
	 * @param manifestationsResult
	 * @param manifestationModification
	 * @param dataPropertyModification
	 * @param dataPropertyModificationDistribution
	 * @param modifyDataProperty
	 */
	private static void precalculateDataForUpdateManifestation(Ontology ontology, Map<String, Map<String, PropertyData>> manifestationsResult, String manifestationModification,
			String dataPropertyModification, String dataPropertyModificationDistribution, String modifyDataProperty) {

		String operation = null;
		String dataPropertyValueDistribution = null;
		String dataPropertyValue = null;
		if (DataStoreService.eTLDataPropertyValues(ontology).get(manifestationModification).get(dataPropertyModification) != null) {
			dataPropertyValue = DataStoreService.eTLDataPropertyValues(ontology).get(manifestationModification).get(dataPropertyModification).getValue();
		}

		if (DataStoreService.eTLDataPropertyValues(ontology).get(manifestationModification).containsKey(dataPropertyModificationDistribution)) {
			dataPropertyValueDistribution = DataStoreService.eTLDataPropertyValues(ontology).get(manifestationModification).get(dataPropertyModificationDistribution).getValue();
		}

		if (dataPropertyValue != null) {
			if (dataPropertyValue.matches(Constants.REGEX_OPERATION_NUMERICVALUE)) {
				operation = dataPropertyValue.substring(0,1);
				dataPropertyValue = dataPropertyValue.substring(1);
			}
			
			updateManifestations(ontology, manifestationsResult, manifestationModification, operation, dataPropertyValue, dataPropertyValueDistribution, modifyDataProperty);
		}
	}

	/**
	 * @param ontology
	 * @param manifestations
	 * @param manifestationModification
	 * @param operation
	 * @param value
	 * @param valueDistribution
	 * @param modifyDataProperty
	 */
	private static void updateManifestations(Ontology ontology, Map<String, Map<String, PropertyData>> manifestations, String manifestationModification,
			String operation, String value, String valueDistribution, String modifyDataProperty) {
		List<String> manifestationsToModify = DataStoreService.eTLObjectProperties(ontology).get(manifestationModification).get(Constants.OBJECTPROPERTY_MANIFESTATION);
		for (String manifestationToModify : manifestationsToModify) {
			String resultOperationStr = value;
			if (operation != null) {
				String valueOfManifestationToModify = manifestations.get(manifestationToModify).get(modifyDataProperty).getValue();
				Double resultOperation = null;
				if ("*".equals(operation)) {
					resultOperation = Double.valueOf(valueOfManifestationToModify) * Double.valueOf(value);
				} else if ("/".equals(operation)) {
					resultOperation = Double.valueOf(valueOfManifestationToModify) / Double.valueOf(value);
				} else if ("+".equals(operation)) {
					resultOperation = Double.valueOf(valueOfManifestationToModify) + Double.valueOf(value);
				} else if ("-".equals(operation)) {
					resultOperation = Double.valueOf(valueOfManifestationToModify) - Double.valueOf(value);
				}
				
				if (resultOperation < 0.0) {
					resultOperation = 0.0; 
				} else if (resultOperation > 0.0) {
					resultOperation = 1.0; 
				}
				
				resultOperationStr = String.valueOf(resultOperation);
			}
			manifestations.get(manifestationToModify).get(modifyDataProperty).setValue(resultOperationStr);
			if (valueDistribution != null) {
				manifestations.get(manifestationToModify).get(modifyDataProperty + Constants.CONSTANT_DISTRUBUTION_SUFFIX).setValue(valueDistribution);
			}
		}
	}

	/**
	 * We assume that it makes no sense for a disease or screening strategy to apply more than one clinical diagnosis to try to see if the patient has the disease or not. What I will do is first look
	 * for a clinical diagnosis in the intervention, and if I did not find any, I would look for one associated with the disease.
	 * 
	 * Evaluate if a clinical diagnosis is effective. For this, it is valued: hasSpecificity == 1.0 hasSensitivity == 1.0 Only if the two previous parameters are equal to one then the diagnosis is
	 * considered effective.
	 * 
	 * @param ontology
	 * @param node Parent node
	 * @param clinicalDiagnosisStrategy Clinical diagnostic strategy selected as input parameter.
	 * @return Node of the sub-tree with the development of the clinical diagnosis or null in case of not having been able to calculate it.
	 * @throws TranspilerException
	 */
	public static void loadClinicalDiagnosisStrategy(Ontology ontology, String clinicalDiagnosisStrategy, TreeNode<NodeData> node) throws TranspilerException {
		if (checkIfExistClinicalDiagnosisStrategy(ontology, clinicalDiagnosisStrategy)) {
			if (!checkIfClinicalDiagnosisIsEffectiveness(ontology, clinicalDiagnosisStrategy)) {
				throw new TranspilerException("The possibility of working with clinical diagnoses that are not 100% effective is not yet available.");
			}
			else {
				Double clinicalDiagnosisStrategyCost = CostUtils.calculateStrategyCost(ontology, clinicalDiagnosisStrategy);
				Double actualNodeCost = 0.0;
				if (node.getData().getProperties().containsKey(Constants.DATAPROPERTY_COST)) {
					actualNodeCost = Double.valueOf(node.getData().getProperties().get(Constants.DATAPROPERTY_COST).getValue());	
				}
				CostUtils.addCostToNode(node, actualNodeCost + clinicalDiagnosisStrategyCost, Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_LIFETIME_VALUE);
			}
		}
	}

	/**
	 * @param ontology
	 * @param cumulativeVariable
	 * @param methodCost
	 * @return
	 */
	public static ValueDistributionWrapper parseValueOrValueDistributionExpression(Ontology ontology, String classInstance, String property) {
		ValueDistributionWrapper result = new ValueDistributionWrapper();
		if (DataStoreService.eTLDataPropertyValues(ontology).get(classInstance).containsKey(property)) {
			String amountExpression = DataStoreService.eTLDataPropertyValues(ontology).get(classInstance).get(property).getValue().replaceAll("\\s+", "");
			if (amountExpression.matches(Constants.REGEX_NUMERICVALUE_DISTRO)) {
				String [] splitValue = amountExpression.split(Constants.CONSTANT_HASHTAG);
				result.setValue(Double.valueOf(splitValue[0]));
				result.setDistribution(splitValue[1]);				
			} else if (amountExpression.matches(Constants.REGEX_NUMERICVALUE)) {
				result.setValue(Double.valueOf(Double.valueOf(amountExpression)));
			}
		}
		return result;
	}

	/**
	 * @param ontology
	 * @param clinicalDiagnosisStrategy
	 * @return
	 */
	private static boolean checkIfClinicalDiagnosisIsEffectiveness(Ontology ontology, String clinicalDiagnosisStrategy) {
		boolean result = true;
		List<String> clinicalDiagnosisStepsStrategy = new ArrayList<String>();
		for (String key : DataStoreService.eTLObjectProperties(ontology).get(clinicalDiagnosisStrategy).keySet()) {
			clinicalDiagnosisStepsStrategy.addAll(DataStoreService.eTLObjectProperties(ontology).get(clinicalDiagnosisStrategy).get(key));
		}

		List<String> clinicalDiagnosis = new ArrayList<String>();
		for (String clinicalDiagnosisStepStrategy : clinicalDiagnosisStepsStrategy) {
			for (String key : DataStoreService.eTLObjectProperties(ontology).get(clinicalDiagnosisStepStrategy).keySet()) {
				clinicalDiagnosis.addAll(DataStoreService.eTLObjectProperties(ontology).get(clinicalDiagnosisStepStrategy).get(key));
			}
		}

		for (String cd : clinicalDiagnosis) {
			if (!Constants.CONSTANT_SENSITIVITY_100.equals(DataStoreService.getDataPropertyValuesInstance(ontology).get(cd).get(Constants.DATAPROPERTY_SENSITIVITY).getValue().split(Constants.CONSTANT_SPLIT_TYPE)[0])
					|| !Constants.CONSTANT_SPECIFICITY_100.equals(DataStoreService.getDataPropertyValuesInstance(ontology).get(cd).get(Constants.DATAPROPERTY_SPECIFICITY).getValue().split(Constants.CONSTANT_SPLIT_TYPE)[0])) {
				result = false;
				break;
			}
		}
		return result;
	}

	/**
	 * @param ontology
	 * @param clinicalDiagnosisStrategy
	 * @return
	 */
	private static boolean checkIfExistClinicalDiagnosisStrategy(Ontology ontology, String clinicalDiagnosisStrategy) {
		// I'm looking for the father of clinicalDiagnosisEstretegy. With this we validate that it exists in ongology.
		boolean existClinicalDiagnosisStrategy = false;
		if (clinicalDiagnosisStrategy != null && !clinicalDiagnosisStrategy.isEmpty()) {
			for (String keyA : DataStoreService.eTLObjectProperties(ontology).keySet()) {
				for (String keyB : DataStoreService.eTLObjectProperties(ontology).get(keyA).keySet()) {
					List<String> alternatives = DataStoreService.eTLObjectProperties(ontology).get(keyA).get(keyB);
					if (alternatives.contains(clinicalDiagnosisStrategy)) {
						existClinicalDiagnosisStrategy = true;
					}
				}
			}
		}
		return existClinicalDiagnosisStrategy;
	}

	/**
	 * @param ontology
	 * @param manifestations
	 */
	public static Double calculateDiseaseLifeExpectancy(Ontology ontology, String disease) {
		Double lifeExpectancy = 0.0;

		if (DataStoreService.eTLObjectProperties(ontology).get(disease).containsKey(Constants.OBJECTPROPERTY_DISEASE_DEVELOPMENTS)) {
			List<String> developments = DataStoreService.eTLObjectProperties(ontology).get(disease).get(Constants.OBJECTPROPERTY_DISEASE_DEVELOPMENTS);		
			for (String development : developments) {
				String kindDevelopment = DataStoreService.eTLDataPropertyValues(ontology).get(development).get(Constants.DATAPROPERTY_KIND_DEVELOPMENT).getValue().split(Constants.CONSTANT_SPLIT_TYPE)[0];
				if (Constants.DATAPROPERTYVALUE_KIND_DEVELOPMENT_NATURAL_VALUE.equals(kindDevelopment)) {
					lifeExpectancy = parseValueOrValueDistributionExpression(ontology, development, Constants.DATAPROPERTY_LIFE_EXPECTANCY).getValue();
					break;
				}
			}
		}
		
		return lifeExpectancy;
	}
	
	/**
	 * @param ontology
	 * @param manifestations
	 */
	public static Double[] calculateDiseaseStrategySensitivityAndSpecificity(Ontology ontology, String disease, String intervention, String strategyName) {
		Double[] result = {1.0, 1.0};
		
		if (DataStoreService.eTLObjectProperties(ontology).get(intervention).containsKey(strategyName)) {
			List<String> strategies = DataStoreService.eTLObjectProperties(ontology).get(intervention).get(strategyName);
			for (String strategy : strategies) {
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
												Map<String, PropertyData> dataProperties = DataStoreService.eTLDataPropertyValues(ontology).get(methodName);
												for (String dataPropertyName : dataProperties.keySet()) {
													if (Constants.DATAPROPERTY_SENSITIVITY.equals(dataPropertyName)) {
														Double dataPropertyValue = Double.valueOf(dataProperties.get(dataPropertyName).getValue()); 
														result[0] = (result[0] == null) ? dataPropertyValue : result[0] * dataPropertyValue;
													} else if (Constants.DATAPROPERTY_SPECIFICITY.equals(dataPropertyName)) {
														Double dataPropertyValue = Double.valueOf(dataProperties.get(dataPropertyName).getValue()); 
														result[1] = (result[1] == null) ? dataPropertyValue : result[1] * dataPropertyValue;
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
		
		return result;
	}

	/**
	 * Given a set of values, calculate the combinatorial tree of all possible combinations of its elements.
	 * 
	 * @param ontology
	 * @param node Root node for the creation of the combinatorial tree.
	 * @param tmp  Elements participating in the combinatorial.
	 * @throws TranspilerException
	 */
	public static void calculateAlternatives(Ontology ontology, String diseaseName, TreeNode<NodeData> node, Map<String, Map<String, PropertyData>> tmp) throws TranspilerException {
		if (!tmp.keySet().isEmpty()) {
			String x = (new ArrayList<String>(tmp.keySet())).get(0);
			
			Map<String, PropertyData> dataProperties = MapUtils.cloneSimpleHashMap(DataStoreService.eTLDataPropertyValues(ontology).get(x));
			calculateAlternativeDataProperties(ontology, diseaseName, x, dataProperties);

			TreeNode<NodeData> n = node.addChild((new NodeData(x, DataStoreService.eTLClassIndividuals(ontology).get(x))).addProperties(dataProperties));
			calculateAlternatives(ontology, diseaseName, n, MapUtils.subSetComplexHashMap(tmp, Arrays.asList(x)));

			TreeNode<NodeData> m = node.addChild((new NodeData("!" + x, DataStoreService.eTLClassIndividuals(ontology).get(x))).addProperties(dataProperties));
			calculateAlternatives(ontology, diseaseName, m, MapUtils.subSetComplexHashMap(tmp, Arrays.asList(x)));
		}
	}

	/**
	 * @param ontology
	 * @param x
	 * @param dataProperties
	 */
	private static void calculateAlternativeDataProperties(Ontology ontology, String diseaseName, String x, Map<String, PropertyData> dataProperties) {
		if (DataStoreService.eTLObjectProperties(ontology).get(x).containsKey(Constants.OBJECTPROPERTY_MANIFESTATION_COST)) {
			List<String> manifestionCosts = DataStoreService.eTLObjectProperties(ontology).get(x).get(Constants.OBJECTPROPERTY_MANIFESTATION_COST);
			Double cumulativeAnnualCost = 0.0;
			Double cumulativeOnetimeCost = 0.0;
			Double cumulativeLifetimeCost = 0.0;
			for (String manifestationCost : manifestionCosts) {				
				Map<String, PropertyData> manifestationCostDataProperties = DataStoreService.getDataPropertyValues(ontology, manifestationCost);
				if (manifestationCostDataProperties.containsKey(Constants.DATAPROPERTY_TEMPORAL_BEHAVIOR)) {
					String temporalBehaviorValue = manifestationCostDataProperties.get(Constants.DATAPROPERTY_TEMPORAL_BEHAVIOR).getValue();
					if (Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ANNUAL_VALUE.equals(temporalBehaviorValue)) {
						cumulativeAnnualCost += parseValueOrValueDistributionExpression(ontology, manifestationCost, Constants.CUSTOM_PROPERTY_AMOUNT).getValue();
					} else if (Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_ONETIME_VALUE.equals(temporalBehaviorValue)) {
						cumulativeOnetimeCost += parseValueOrValueDistributionExpression(ontology, manifestationCost, Constants.CUSTOM_PROPERTY_AMOUNT).getValue();
					} else if (Constants.DATAPROPERTYVALUE_TEMPORAL_BEHAVIOR_LIFETIME_VALUE.equals(temporalBehaviorValue)) {
						cumulativeLifetimeCost += parseValueOrValueDistributionExpression(ontology, manifestationCost, Constants.CUSTOM_PROPERTY_AMOUNT).getValue();
					}
				} else {
					cumulativeAnnualCost += parseValueOrValueDistributionExpression(ontology, manifestationCost, Constants.CUSTOM_PROPERTY_AMOUNT).getValue();
				}
			}
			dataProperties.put(Constants.CUSTOM_PROPERTY_ANNUAL_COST, new PropertyData(Constants.CUSTOM_PROPERTY_ANNUAL_COST, String.valueOf(cumulativeAnnualCost), Constants.CONSTANT_DOUBLE_TYPE));
			dataProperties.put(Constants.CUSTOM_PROPERTY_ONETIME_COST, new PropertyData(Constants.CUSTOM_PROPERTY_ONETIME_COST, String.valueOf(cumulativeOnetimeCost), Constants.CONSTANT_DOUBLE_TYPE));
			dataProperties.put(Constants.CUSTOM_PROPERTY_LIFETIME_COST, new PropertyData(Constants.CUSTOM_PROPERTY_LIFETIME_COST, String.valueOf(cumulativeLifetimeCost), Constants.CONSTANT_DOUBLE_TYPE));
		}
		
		if (DataStoreService.eTLObjectProperties(ontology).get(x).containsKey(Constants.OBJECTPROPERTY_UTILITY)) {
			List<String> manifestionUtilities = DataStoreService.eTLObjectProperties(ontology).get(x).get(Constants.OBJECTPROPERTY_UTILITY);
			Double utilityValue = null;
			String utilityDistribution = null;
			String utilityKind = null;
			for (String manifestionUtility : manifestionUtilities) {
				ValueDistributionWrapper valueDistributionWrapper = parseValueOrValueDistributionExpression(ontology, manifestionUtility, Constants.DATAPROPERTY_VALUE);
				utilityValue = valueDistributionWrapper.getValue();
				utilityDistribution = valueDistributionWrapper.getDistribution();
				if (DataStoreService.eTLDataPropertyValues(ontology).get(manifestionUtility).containsKey(Constants.DATAPROPERTY_KIND_UTILITY)) {
					utilityKind = DataStoreService.eTLDataPropertyValues(ontology).get(manifestionUtility).get(Constants.DATAPROPERTY_KIND_UTILITY).getValue();
				}					 
			}
			
			String key = null; 
			if (utilityValue != null) {
				key = Constants.CUSTOM_PROPERTY_UTILITY_VALUE;
				dataProperties.put(key, new PropertyData(key, String.valueOf(utilityValue), Constants.CONSTANT_DOUBLE_TYPE));
			}
			if (utilityDistribution != null) {
				key = Constants.CUSTOM_PROPERTY_UTILITY_VALUE + Constants.CONSTANT_DISTRUBUTION_SUFFIX;
				dataProperties.put(key, new PropertyData(key, utilityDistribution, Constants.CONSTANT_STRING_TYPE));
			}
			if (utilityKind != null) {
				key = Constants.CUSTOM_PROPERTY_UTILITY_KIND;
				dataProperties.put(key, new PropertyData(key, utilityKind, Constants.CONSTANT_STRING_TYPE));
			}
		}
		
		if (DataStoreService.eTLDataPropertyValues(ontology).get(x).containsKey(Constants.DATAPROPERTY_ONSET_AGE)) {
			ValueDistributionWrapper value = parseValueOrValueDistributionExpression(ontology, x, Constants.DATAPROPERTY_ONSET_AGE);
			dataProperties.put(Constants.DATAPROPERTY_ONSET_AGE, new PropertyData(Constants.DATAPROPERTY_ONSET_AGE, String.valueOf(value.getValue()), Constants.CONSTANT_DOUBLE_TYPE));
		} else {
			dataProperties.put(Constants.DATAPROPERTY_ONSET_AGE, new PropertyData(Constants.DATAPROPERTY_ONSET_AGE, Constants.CONSTANT_VALUE_ZERO, Constants.CONSTANT_DOUBLE_TYPE));
		}
		
		if (DataStoreService.eTLDataPropertyValues(ontology).get(x).containsKey(Constants.DATAPROPERTY_END_AGE)) {
			ValueDistributionWrapper value = parseValueOrValueDistributionExpression(ontology, x, Constants.DATAPROPERTY_END_AGE);
			dataProperties.put(Constants.DATAPROPERTY_END_AGE, new PropertyData(Constants.DATAPROPERTY_END_AGE, String.valueOf(value.getValue()), Constants.CONSTANT_DOUBLE_TYPE));
		} else {
			Double lifeExpectancy = Double.valueOf(DataStoreService.getPropertyDatasheet(diseaseName, Constants.DATASHEET_NATURAL_DEVELOPMENT_LIFE_EXPECTANCY));
			Double mortalityFactor = 0.0;
			if (DataStoreService.eTLDataPropertyValues(ontology).get(x).containsKey(Constants.DATAPROPERTY_MORTALITY_FACTOR)) {
				mortalityFactor = Double.valueOf(DataStoreService.eTLDataPropertyValues(ontology).get(x).get(Constants.DATAPROPERTY_MORTALITY_FACTOR).getValue());
			}
			dataProperties.put(Constants.DATAPROPERTY_END_AGE, new PropertyData(Constants.DATAPROPERTY_END_AGE, 
					String.format(Locale.US, Constants.CONSTANT_DOUBLE_FORMAT_STRING_6DEC, lifeExpectancy + mortalityFactor), Constants.CONSTANT_DOUBLE_TYPE));
		}
	}

	/**
	 * @param manifestations
	 * @throws TranspilerException
	 */
	private static void removeManifestationsWithZeroProbability (Map<String, Map<String, PropertyData>> manifestations) throws TranspilerException {
		List<String> manifestationsToRemove = new ArrayList<>();
		for (String manifestation : manifestations.keySet()) {
			if (manifestations.get(manifestation).containsKey(Constants.DATAPROPERTY_PROBABILITY)) {
				String dataPropertyValue = manifestations.get(manifestation).get(Constants.DATAPROPERTY_PROBABILITY).getValue();
				if (Double.valueOf(dataPropertyValue) == 0.0) {
					manifestationsToRemove.add(manifestation);
				}
			}
		}
		
		for (String manifestationToRemove : manifestationsToRemove) {
			manifestations.remove(manifestationToRemove);
		}
	}

}