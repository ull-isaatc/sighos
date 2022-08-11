package es.ull.iis.simulation.hta.osdi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.w3c.xsd.owl2.Axiom;
import org.w3c.xsd.owl2.ClassAssertion;
import org.w3c.xsd.owl2.DataPropertyAssertion;
import org.w3c.xsd.owl2.NamedIndividual;
import org.w3c.xsd.owl2.ObjectProperty;
import org.w3c.xsd.owl2.ObjectPropertyAssertion;
import org.w3c.xsd.owl2.Ontology;


public class OwlHelper {
	private static Map<String, Map<String, List<String>>> dataPropertyValues = null;
	private static Map<String, Map<String, List<String>>> objectPropertyValues = null;
	private static Map<String, String> instanceToClazz = new HashMap<String, String>();
	
	public static void initilize (Ontology radios) {
		eTLClassIndividuals(radios, null, instanceToClazz, "#Disease");
		dataPropertyValues = eTLDataPropertyValues(radios);
		objectPropertyValues = eTLObjectProperties(radios);
	}

	/**
	 * Returns the string corresponding to the specified data property defined in the specified instance in the ontology.
	 * If there are is than one value for that property, returns the first one. 
	 * If the data property or the instance are not defined, returns the default value. Be aware that the instance might be not defined simply because 
	 * it does not define any data property, and not because it does not exists.
	 * @param instanceName Name of the instance that defines the data property
	 * @param propertyName Name of the data property
	 * @param defaultValue Value that is returned when not defined 
	 * @return the string corresponding to the specified data property defined in the specified instance in the ontology.
	 */
	public static String getDataPropertyValue (String instanceName, String propertyName, String defaultValue) {
		final Map<String, List<String>> data = dataPropertyValues.get(instanceName);
		if (data == null)
			return defaultValue;
		if (data.get(propertyName) == null)
			return defaultValue;
		return data.get(propertyName).get(0);
	}
	
	/**
	 * Returns the string values corresponding to the specified data property defined in the specified instance in the ontology.
	 * If the data instance is not defined, returns null. If the data property is not defined, returns an empty list.
	 * @param instanceName Name of the instance that defines the data property
	 * @param propertyName Name of the data property
	 * @return the string values corresponding to the specified data property defined in the specified instance in the ontology.
	 */
	public static List<String> getDataPropertyValues(String instanceName, String propertyName) {
		final Map<String, List<String>> data = dataPropertyValues.get(instanceName);
		if (data == null)
			return null;
		final ArrayList<String> values = new ArrayList<>();
		if (data.get(propertyName) != null)
			for (String dataItem : data.get(propertyName)) {
				values.add(dataItem);
			}
		return values;
	}
	
	/**
	 * Returns the string corresponding to the specified data property defined in the specified instance in the ontology. 
	 * If the data property or the instance are not defined, returns null.
	 * @param instanceName Name of the instance that defines the data property
	 * @param propertyName Name of the data property
	 * @return the string corresponding to the specified data property defined in the specified instance in the ontology.
	 */
	public static String getDataPropertyValue (String instanceName, String propertyName) {
		return getDataPropertyValue(instanceName, propertyName, null);
	}
	
	/**
	 * Returns the object associated to another object by means of the specified object property. If there are more than 
	 * one, returns the first one.  
	 * @param instanceName Name of the original instance
	 * @param objectPropertyName Name of the property
	 * @return the object associated to another object by means of the specified object property
	 */
	public static String getObjectPropertyByName(String instanceName, String objectPropertyName) {
		final Map<String, List<String>> map = objectPropertyValues.get(instanceName);
		if (map == null)
			return null;
		List<String> list = map.get(objectPropertyName);
		if (list == null)
			return null;
		return list.get(0);
	}
	
	/**
	 * Returns the list of objects associated to another object by means of the specified object property  
	 * @param instanceName Name of the original instance
	 * @param objectPropertyName Name of the property
	 * @return the list of objects associated to another object by means of the specified object property
	 */
	public static List<String> getObjectPropertiesByName(String instanceName, String objectPropertyName) {
		final Map<String, List<String>> map = objectPropertyValues.get(instanceName);
		if (map == null)
			return new ArrayList<>();
		List<String> list = map.get(objectPropertyName);
		if (list == null)
			return new ArrayList<>();
		return list;
	}
	
	public static List<String> getChilds(String objectName) {
		List<String> result = null;
		Map<String, List<String>> objectProperties = objectPropertyValues.get(objectName);
		if (objectProperties != null && !objectProperties.keySet().isEmpty()) {
			result = new ArrayList<>();
			for (String key: objectProperties.keySet()) {
				result.addAll(objectProperties.get(key));
			}
		}
		return result;
	}

	public static List<String> getChildsByClassName(String objectName, String className) {
		List<String> result = new ArrayList<>();
		Map<String, List<String>> objectProperties = objectPropertyValues.get(objectName);
		if (objectProperties != null && !objectProperties.keySet().isEmpty()) {
			result = new ArrayList<>();
			for (String key: objectProperties.keySet()) {
				List<String> values = objectProperties.get(key);
				if (values != null && !values.isEmpty()) {
					for (String value: values) {
						if (className.equals(instanceToClazz.get(value))) {
							result.add(value);
						}
					}
				}
			}
		}
		return result;
	}

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
			ontology = OwlHelper.loadOntology(xmlOwl);
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
	public static Map<String, Map<String, List<String>>> eTLDataPropertyValues(Ontology ontology) {
		Map<String, Map<String, List<String>>> result = new HashMap<>();
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof DataPropertyAssertion) {
				DataPropertyAssertion dataPropertyAssertion = (DataPropertyAssertion) axiom;
				String clazzName = dataPropertyAssertion.getNamedIndividual().getIRI();
				Map<String, List<String>> dataProperties = result.get(clazzName);
				if (dataProperties == null) {
					dataProperties = new HashMap<>();
				}
				
				String dataPropertyName = dataPropertyAssertion.getDataProperty().getIRI();
				if (!dataProperties.containsKey(dataPropertyName))
					dataProperties.put(dataPropertyName, new ArrayList<>());
				
				String dataPropertyValue = dataPropertyAssertion.getLiteral().getValue();
				dataProperties.get(dataPropertyName).add(dataPropertyValue);

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
	
}
