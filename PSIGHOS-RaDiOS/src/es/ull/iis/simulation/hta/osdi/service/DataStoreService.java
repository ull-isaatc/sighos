package es.ull.iis.simulation.hta.osdi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.xsd.owl2.Axiom;
import org.w3c.xsd.owl2.ClassAssertion;
import org.w3c.xsd.owl2.DataPropertyAssertion;
import org.w3c.xsd.owl2.NamedIndividual;
import org.w3c.xsd.owl2.ObjectProperty;
import org.w3c.xsd.owl2.ObjectPropertyAssertion;
import org.w3c.xsd.owl2.Ontology;

import es.ull.iis.simulation.hta.osdi.utils.Constants;
import es.ull.iis.simulation.hta.osdi.utils.PropertyData;

public class DataStoreService {
	private static Map<String, String> classIndividualsInstance = null;
	private static Map<String, List<String>> classIndividualsByClassInstance = null;
	private static Map<String, Map<String, List<PropertyData>>> dataPropertyValuesInstance = null;
	private static Map<String, Map<String, List<PropertyData>>> diseasesDatasheetInstance = null;
	
	public static void initializeDiseaseDatasheet (String diseaseName) {
		getDiseasesDatasheetInstance().put(diseaseName, new HashMap<String, List<PropertyData>>());		
	}
	
	/**
	 * @param diseaseName
	 * @param propertyName
	 * @param subPropertyName
	 * @return
	 */
	public static String searchPropertyDatasheet(String diseaseName, String propertyName, String subPropertyName) {
		String result = null;
		List<PropertyData> properties = getDiseasesDatasheetInstance().get(diseaseName).get(propertyName);
		if (properties != null) {
			for (PropertyData property : properties) {
				if (subPropertyName.equals(property.getName())) {
					result = property.getValue();
				}
			}
		}		
		return result;
	}
	
	/**
	 * @param diseaseName
	 * @param propertyName
	 * @return
	 */
	public static String getPropertyDatasheet(String diseaseName, String propertyName) {
		return searchPropertyDatasheet(diseaseName, propertyName, propertyName);		
	}

	/**
	 * @param diseaseName
	 * @param propertyName
	 * @param subPropertyName
	 * @return
	 */
	public static String getPropertyDatasheet(String diseaseName, String propertyName, String subPropertyName) {
		return searchPropertyDatasheet(diseaseName, propertyName, subPropertyName);		
	}
	
	/**
	 * @param disease
	 * @param propertyData
	 */
	public static void storePropertyDataIntoDiseaseDatacheet (String disease, PropertyData propertyData) {
		Map<String, Map<String, List<PropertyData>>> diseasesDatasheetInstance = getDiseasesDatasheetInstance();
		if (!diseasesDatasheetInstance.containsKey(disease)) {
			diseasesDatasheetInstance.put(disease, new HashMap<String, List<PropertyData>>());
		}
		String key = propertyData.getName();
		List<PropertyData> properties = diseasesDatasheetInstance.get(disease).get(key);
		if (properties == null) {			
			diseasesDatasheetInstance.get(disease).put(key, new ArrayList<PropertyData>());
		}
		diseasesDatasheetInstance.get(disease).get(key).add(propertyData);
	}
	
	/**
	 * @param disease
	 * @param key
	 * @param propertyData
	 */
	public static void storePropertyDataIntoDiseaseDatacheet (String disease, String key, PropertyData propertyData) {
		Map<String, Map<String, List<PropertyData>>> diseasesDatasheetInstance = getDiseasesDatasheetInstance();
		if (!diseasesDatasheetInstance.containsKey(disease)) {
			diseasesDatasheetInstance.put(disease, new HashMap<String, List<PropertyData>>());
		}
		List<PropertyData> properties = diseasesDatasheetInstance.get(disease).get(key);
		if (properties == null) {			
			diseasesDatasheetInstance.get(disease).put(key, new ArrayList<PropertyData>());
		}
		diseasesDatasheetInstance.get(disease).get(key).add(propertyData);
	}
	
	/**
	 * @return
	 */
	public static Map<String, Map<String, List<PropertyData>>> getDiseasesDatasheetInstance() {
		if (diseasesDatasheetInstance == null) {
			diseasesDatasheetInstance = new HashMap<String, Map<String, List<PropertyData>>>();
		}
		return diseasesDatasheetInstance;
	}

	/**
	 * @param ontology
	 * @return
	 */
	public static Map<String, Map<String, List<PropertyData>>> getDataPropertyValuesInstance(Ontology ontology) {
		if (dataPropertyValuesInstance == null) {
			dataPropertyValuesInstance = eTLDataPropertyValues(ontology);
		}
		return dataPropertyValuesInstance;
	}

	/**
	 * @param ontology
	 * @return
	 */
	public static Map<String, String> getClassIndividualsInstance(Ontology ontology) {
		if (classIndividualsInstance == null) {
			classIndividualsInstance = eTLClassIndividuals(ontology);
		}
		return classIndividualsInstance;
	}

	/**
	 * @param ontology
	 * @return
	 */
	public static Map<String, List<String>> getIndividualsByClassInstance(Ontology ontology) {
		if (classIndividualsByClassInstance == null) {
			classIndividualsByClassInstance = eTLIndividualsByClass(ontology);
		}
		return classIndividualsByClassInstance;
	}

	/**
	 * @param ontology
	 * @return
	 */
	public static Map<String, String> eTLClassIndividuals(Ontology ontology) {
		Map<String, String> result = new HashMap<String, String>();
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof ClassAssertion) {
				ClassAssertion classAssertion = (ClassAssertion) axiom;
				String clazzName = classAssertion.getNamedIndividual().getIRI();
				String clazzType = classAssertion.getClazz().getIRI();

				result.put(clazzName, clazzType);
			}
		}
		return result;
	}

	/**
	 * @param ontology
	 * @return
	 */
	public static Map<String, List<String>> eTLIndividualsByClass(Ontology ontology) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof ClassAssertion) {
				ClassAssertion classAssertion = (ClassAssertion) axiom;
				String clazzName = classAssertion.getNamedIndividual().getIRI();
				String clazzType = classAssertion.getClazz().getIRI();

				List<String> tmp = result.get(clazzType);
				if (tmp == null) {
					tmp = new ArrayList<String>();
				}
				tmp.add(clazzName);
				result.put(clazzType, tmp);
			}
		}
		return result;
	}

	/**
	 * @param ontology
	 * @return
	 */
	public static Map<String, Map<String, List<String>>> eTLObjectProperties(Ontology ontology) {
		Map<String, Map<String, List<String>>> result = new HashMap<String, Map<String, List<String>>>();
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof ObjectPropertyAssertion) {
				ObjectPropertyAssertion assertion = (ObjectPropertyAssertion) axiom;
				String assertionLink = ((ObjectProperty) assertion.getRest().get(0).getValue()).getIRI();
				String assertionLeftSide = ((NamedIndividual) assertion.getRest().get(1).getValue()).getIRI();
				String assertionRightSide = ((NamedIndividual) assertion.getRest().get(2).getValue()).getIRI();

				if (result.get(assertionLeftSide) == null) {
					result.put(assertionLeftSide, new HashMap<String, List<String>>());
				}
				if (result.get(assertionLeftSide).get(assertionLink) == null) {
					result.get(assertionLeftSide).put(assertionLink, new ArrayList<String>());
				}
				result.get(assertionLeftSide).get(assertionLink).add(assertionRightSide);
			}
		}
		return result;
	}

	/**
	 * @param ontology
	 * @return
	 */
	public static Map<String, Map<String, List<PropertyData>>> eTLDataPropertyValues(Ontology ontology) {
		Map<String, Map<String, List<PropertyData>>> result = new HashMap<String, Map<String, List<PropertyData>>>();
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof DataPropertyAssertion) {
				DataPropertyAssertion dataPropertyAssertion = (DataPropertyAssertion) axiom;
				String clazzName = dataPropertyAssertion.getNamedIndividual().getIRI();
				Map<String, List<PropertyData>> dataProperties = result.get(clazzName);
				if (dataProperties == null) {
					dataProperties = new HashMap<String, List<PropertyData>>();
				}
				
				String dataPropertyName = dataPropertyAssertion.getDataProperty().getIRI();
				if (!dataProperties.containsKey(dataPropertyName))
					dataProperties.put(dataPropertyName, new ArrayList<PropertyData>());
				
				String dataPropertyType = Constants.CONSTANT_UNDEFINED_TYPE;
				if (dataPropertyAssertion.getLiteral().getDatatypeIRI() != null) {
					dataPropertyType = Constants.CONSTANT_HASHTAG + dataPropertyAssertion.getLiteral().getDatatypeIRI().split(Constants.CONSTANT_HASHTAG)[1];
				}
				
				String dataPropertyValue = dataPropertyAssertion.getLiteral().getValue();
				if (dataPropertyValue.matches(Constants.REGEX_NUMERICVALUE_DISTRO)) {
					String valueSplitted[] = dataPropertyValue.split(Constants.CONSTANT_HASHTAG); 
					dataProperties.get(dataPropertyName).add(new PropertyData(dataPropertyName, valueSplitted[0], Constants.CONSTANT_DOUBLE_TYPE));
					if (!dataProperties.containsKey(dataPropertyName + Constants.CONSTANT_DISTRUBUTION_SUFFIX))
						dataProperties.put(dataPropertyName + Constants.CONSTANT_DISTRUBUTION_SUFFIX, new ArrayList<PropertyData>());
					dataProperties.get(dataPropertyName + Constants.CONSTANT_DISTRUBUTION_SUFFIX).add(new PropertyData(dataPropertyName + Constants.CONSTANT_DISTRUBUTION_SUFFIX, valueSplitted[1], Constants.CONSTANT_STRING_TYPE));
				} else {
					dataProperties.get(dataPropertyName).add(new PropertyData(dataPropertyName, dataPropertyValue, dataPropertyType));
				}

				result.put(clazzName, dataProperties);
			}
		}
		return result;
	}

	/**
	 * @param instancesByClazz
	 * @param instanceToClazz
	 * @param radios
	 */
	public static List<ClassAssertion> eTLClassIndividuals(Ontology ontology, Map<String, List<String>> instancesByClazz, Map<String, String> instanceToClazz, String rootClassType) {
		List<ClassAssertion> result = new ArrayList<>();
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof ClassAssertion) {
				ClassAssertion classAssertion = (ClassAssertion) axiom;
				String clazzName = classAssertion.getNamedIndividual().getIRI();
				String clazzType = classAssertion.getClazz().getIRI();

				if (rootClassType.equals(clazzType)) {
					result.add(classAssertion);
				}

				if (instancesByClazz != null) {
					if (instancesByClazz.get(clazzType) != null && !instancesByClazz.get(clazzType).isEmpty()) {
						instancesByClazz.get(clazzType).add(clazzName);
					} else {
						List<String> tmp = new ArrayList<>();
						tmp.add(clazzName);
						instancesByClazz.put(clazzType, tmp);
					}
				}

				if (instanceToClazz != null) {
					instanceToClazz.put(clazzName, clazzType);
				}
			}
		}
		return result;
	}
	
	/**
	 * @param ontology
	 * @param classInstance
	 * @return
	 */
	public static Map<String, List<PropertyData>> getDataPropertyValues(Ontology ontology, String classInstance) {
		if (eTLDataPropertyValues(ontology).containsKey(classInstance)) {
			return eTLDataPropertyValues(ontology).get(classInstance);
		}
		return new HashMap<String, List<PropertyData>> ();		
	}
}
